package sambal.mydd.app.fragment.chat

import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode
import android.os.Build
import android.content.Intent
import kotlin.Throws
import android.graphics.BitmapFactory
import android.content.Context
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import sambal.mydd.app.R
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.asyncTask.Chat_Image_Task
import sambal.mydd.app.utils.AsyncCallback
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.databinding.ChatimageuiBinding
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.lang.Exception

class ChatImage : AppCompatActivity() {
    private var binding: ChatimageuiBinding? = null
    private var picturePath: String? = null
    private var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.chatimageui)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        intentData
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding!!.ivBack.setOnClickListener { finish() }
        binding!!.ivCrop.setOnClickListener {
            startActivityForResult(Intent(this@ChatImage, CropImage::class.java)
                .putExtra("image", picturePath), 80)
        }
        binding!!.ivSend.setOnClickListener {
            try {
                val imgFile = File(picturePath)
                bitmap =
                    handleSamplingAndRotationBitmap(this@ChatImage, Uri.fromFile(File(picturePath)))
                sendMessage(bitmap)
            } catch (e: Exception) {
            }
        }
    }

    private val intentData: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                try {
                    picturePath = bundle.getString("image")
                    val bitmap =
                        handleSamplingAndRotationBitmap(this, Uri.fromFile(File(picturePath)))
                    binding!!.iv.setImageBitmap(bitmap)
                } catch (e: Exception) {
                }
            }
        }

    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img = rotateImageIfRequired(context, img, selectedImage)
        return img
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap?, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(picturePath!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img,
                90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img,
                180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img,
                270)
            else -> img
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 80) {
            if (data != null) {
                try {
                    picturePath = data.getStringExtra("CroppedImage")
                    val imgFile = File(picturePath)
                    if (imgFile.exists()) {
                        bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding!!.iv.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun sendMessage(userPicture: Bitmap?) {
        val dialogManager = DialogManager()
        dialogManager.showProcessDialog(this, "", false, null)
        Chat_Image_Task(this@ChatImage, userPicture, object : AsyncCallback {
            override fun setResponse(responseCode: Int?, responseStr: String?) {
                if (responseStr != null) {
                    try {
                        Log.e("IMAgeChat", responseStr)
                        val resp = JSONObject(responseStr)
                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                        Log.e("Error", errorType)
                        //String message = resp.optString(KeyConstant.KEY_MESSAGE);
                        if (errorType.equals("200", ignoreCase = true)) {
                            dialogManager.stopProcessDialog()
                            val imageURL = resp.optString("imageURL")
                            val msg = binding!!.exMsg.text.toString().trim { it <= ' ' }
                            val `in` = Intent()
                            `in`.putExtra("url", imageURL)
                            `in`.putExtra("msg", msg)
                            setResult(RESULT_OK, `in`)
                            finish()
                        } else {
                        }
                    } catch (e: JSONException) {
                        dialogManager.stopProcessDialog()
                        e.printStackTrace()
                    }
                    dialogManager.stopProcessDialog()
                    //ProgressDialogUtils.hideProgressDialog();
                }
            }

            override fun setException(e: String?) {}
        }).execute()
    }

    companion object {
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                // Calculate ratios of height and width to requested height and width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
                val totalPixels = (width * height).toFloat()
                val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize++
                }
            }
            return inSampleSize
        }

        private fun rotateImage(img: Bitmap?, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(img!!, 0, 0, img.width, img.height, matrix, true)
            img.recycle()
            return rotatedImg
        }
    }
}