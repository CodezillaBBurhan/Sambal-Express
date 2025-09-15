package sambal.mydd.app.fragment.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.content.Intent
import android.app.Activity
import android.content.Context
import kotlin.Throws
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import sambal.mydd.app.R
import sambal.mydd.app.databinding.CropimageBinding
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CropImage : AppCompatActivity() {
    private var binding: CropimageBinding? = null
    private val uri: Uri? = null
    var picturePath: String? = null
    private var rotationAngle = 0
    private var fileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.cropimage)
        val bundle = intent.extras
        if (bundle != null) {
            picturePath = bundle.getString("image")
            try {
                val imgFile = File(picturePath)
                if (imgFile.exists()) {
                    val bitmap = handleSamplingAndRotationBitmap(this@CropImage, Uri.fromFile(
                        File(picturePath)))
                    binding!!.iv.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
            }
        }
        binding!!.actCropLlCancel.setOnClickListener { finish() }
        binding!!.llDone.setOnClickListener {
            fileUri = getOutputMediaFileUri(1)
            val cropped = binding!!.iv.croppedImage
            val f = File(fileUri!!.path)
            val bos = ByteArrayOutputStream()
            cropped?.compress(Bitmap.CompressFormat.JPEG, 90, bos)
            val bitmapdata = bos.toByteArray()
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(f)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
                Log.e("1", "tes")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.e("E1", e.localizedMessage)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("E2", e.localizedMessage)
            }
            val returnIntent = Intent()
            returnIntent.putExtra("CroppedImage", f.path)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
        binding!!.actCropLlRotate.setOnClickListener {
            rotationAngle += 90
            binding!!.iv.rotatedDegrees = rotationAngle
        }
    }

    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
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
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                img,
                90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                img,
                180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                img,
                270)
            else -> img
        }
    }

    companion object {
        private fun getOutputMediaFile(type: Int): File? {

            // External sdcard location
            //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/HouseViewing");
            val mediaStorageDir: File
            mediaStorageDir = if (Build.VERSION.SDK_INT >= 29) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/HouseViewing")
            } else {
                File(Environment.getExternalStorageDirectory(), "/HouseViewing")
            }

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Hello Camera", "Oops Failed create Hello Camera directory")
                    return null
                }
            }

            // Create a media file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
            val mediaFile: File
            mediaFile = File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg")
            return mediaFile
        }

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
                // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
                // with both dimensions larger than or equal to the requested height and width.
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).
                val totalPixels = (width * height).toFloat()
                // Anything more than 2x the requested pixels we'll sample down further
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