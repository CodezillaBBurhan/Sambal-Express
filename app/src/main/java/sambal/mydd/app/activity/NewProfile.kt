package sambal.mydd.app.activity

import android.Manifest
import android.graphics.Bitmap
import sambal.mydd.app.adapter.AdapterNewProfileCards
import android.content.pm.PackageManager
import org.json.JSONArray
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.MainActivity
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import com.bumptech.glide.Glide
import android.text.TextUtils
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import android.widget.Toast
import android.content.Intent
import sambal.mydd.app.familyaccount.FamilyMemberList
import sambal.mydd.app.familyaccount.FamilyRequest
import sambal.mydd.app.familyaccount.AddFamily
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import sambal.mydd.app.asyncTask.UpdateProfileImage
import sambal.mydd.app.fragment.HomeFragment
import android.widget.TextView
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import sambal.mydd.app.R
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.databinding.MyprofileBinding
import sambal.mydd.app.utils.*
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception

class NewProfile : BaseActivity(), View.OnClickListener {
    var binding: MyprofileBinding? = null
    var bitmap: Bitmap? = null
    var adap: AdapterNewProfileCards? = null
    var dialogManager: DialogManager? = null
    var packageManager1: PackageManager? = null
    var picturePath: String? = null
    var userChoosenTask: String? = null
    var mPermission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION)
    var arrQrCode: JSONArray? = null
    var doorNo = ""
    var StreetName = ""
    var city = ""
    var zipCode = ""
    var adress = ""
    var mainAdd = ""
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    var removeAccount = 0

    override val contentResId: Int
        get() = R.layout.myprofile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.myprofile)
        setToolbarWithBackButton_colorprimary("My Profile")
        packageManager1 = getPackageManager()
        initWidgets()
        adap = AdapterNewProfileCards(this@NewProfile, arrQrCode)
        val linearLayoutManager =
            LinearLayoutManager(this@NewProfile, LinearLayoutManager.VERTICAL, false)
        binding!!.rvQrCode.layoutManager = linearLayoutManager
        dialogManager = DialogManager()
        dialogManager!!.showProcessDialog(this@NewProfile, "", false, null)
    }//AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);//AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));

    // will load image
    private val myProfile: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(this)) {
                val lat = MainActivity.userLat.toString() + ""
                val lng = MainActivity.userLang.toString() + ""
                val call = AppConfig.api_Interface().getMyProfileV1(lat, lng)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                dialogManager!!.stopProcessDialog()
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("ProfileResponse", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    val arrDetails = responseObj.optJSONArray("userDetails")
                                    for (i in 0 until arrDetails.length()) {
                                        val ob = arrDetails.optJSONObject(i)
                                        runOnUiThread {
                                            binding!!.tvName.text =
                                                ob.optString("userName") + " " + ob.optString("userLastName")
                                            binding!!.tvQrCode.text =
                                                "Qr code " + ob.optString("userQRuuid")
                                            binding!!.tvEmailid.text = ob.optString("userEmail")
                                            binding!!.tvEmail.text = ob.optString("userEmail")
                                            binding!!.tvMobileNo.text = ob.optString("userMobile")
                                            binding!!.tvCountry.text = ob.optString("userCountry")
                                            binding!!.tvWallet.text =
                                                "  " + ob.optString("storeAvailablePoints") + "  "
                                            binding!!.tvStorePoints.text =
                                                "  " + ob.optString("storeVouchers") + "  "
                                            binding!!.tvBonusVoucher.text =
                                                "  " + ob.optString("bonusVouchers") + "  "
                                            binding!!.tvCurrency.text =
                                                "  " + ob.optString("currency") + "  "
                                            binding!!.tvAvailablePoints.text =
                                                "   " + ob.optString("bonusPoints") + "   "
                                            binding!!.tvLanguage.text = ob.optString("language")
                                            try {
                                                Log.e("Pshs", ob.optString("userPhoto"))
                                                Glide.with(this@NewProfile)
                                                    .load(ob.optString("userPhoto"))
                                                    .dontAnimate() // will load image
                                                    .placeholder(R.drawable.userplaceholder)
                                                    .error(R.drawable.userplaceholder)
                                                    .into(binding!!.ivProfile)
                                            } catch (e: Exception) {
                                                Log.e("NEwProfilePhoto", e.toString())
                                            }
                                            if (!TextUtils.isEmpty(ob.optString("userDoorNumber"))) {
                                                doorNo = ob.optString("userDoorNumber") + ", "
                                                Log.e("D", doorNo)
                                            }
                                            if (!TextUtils.isEmpty(ob.optString("userStreetName"))) {
                                                StreetName = ob.optString("userStreetName") + ", "
                                                Log.e("S", StreetName)
                                            }
                                            if (!TextUtils.isEmpty(ob.optString("userCity"))) {
                                                city = ob.optString("userCity") + ", "
                                                Log.e("C", city)
                                            } else {
                                            }
                                            if (!TextUtils.isEmpty(ob.optString("userZipCode"))) {
                                                zipCode = ob.optString("userZipCode")
                                                Log.e("Z", zipCode)
                                            }
                                            try {
                                                adress = doorNo + StreetName + city + zipCode
                                            } catch (e: Exception) {
                                            }
                                            if (adress.endsWith(", ")) {
                                                mainAdd = adress.substring(0, adress.length - 2)
                                                Log.e("a", mainAdd)
                                                binding!!.tvAddress.text = mainAdd
                                            } else {
                                                binding!!.tvAddress.text = adress
                                            }
                                            try {
                                                val dobs =
                                                    ob.optString("userDOB").split("-".toRegex())
                                                        .toTypedArray()
                                                binding!!.tvDOB.text =
                                                    dobs[2] + "-" + dobs[1] + "-" + dobs[0]
                                            } catch (e: Exception) {
                                            }
                                            bitmap = QRCode.from(ob.optString("userQRuuid"))
                                                .bitmap()
                                            arrQrCode = responseObj.optJSONArray("DDcardList")
                                            if (arrQrCode != null && arrQrCode!!.length() > 0) {
                                                Log.e("aaaaa", arrQrCode.toString() + "")
                                                adap = AdapterNewProfileCards(this@NewProfile,
                                                    arrQrCode)
                                                val linearLayoutManager =
                                                    LinearLayoutManager(this@NewProfile,
                                                        LinearLayoutManager.VERTICAL,
                                                        false)
                                                binding!!.rvQrCode.layoutManager =
                                                    linearLayoutManager
                                                binding!!.rvQrCode.adapter = adap
                                                adap!!.notifyDataSetChanged()
                                            } else {
                                                adap = AdapterNewProfileCards(this@NewProfile, null)
                                                val linearLayoutManager =
                                                    LinearLayoutManager(this@NewProfile,
                                                        LinearLayoutManager.VERTICAL,
                                                        false)
                                                binding!!.rvQrCode.layoutManager =
                                                    linearLayoutManager
                                                binding!!.rvQrCode.adapter = adap
                                                adap!!.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                            KeyConstant.KEY_STATUS), ignoreCase = true)
                                    ) {
                                        dialogManager!!.stopProcessDialog()
                                        //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("eeee", e.toString())
                                dialogManager!!.stopProcessDialog()
                                //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Log.e("eeee", e.toString())
                                dialogManager!!.stopProcessDialog()
                            }
                        } else {
                            dialogManager!!.stopProcessDialog()
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvAddress, t.message)
                    }
                })
            } else {
                AppUtil.showMsgAlert(binding!!.tvAddress,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

    private fun initWidgets() {
        binding!!.llFamilyMember.setOnClickListener(this)
        binding!!.llLogout.setOnClickListener(this)
        binding!!.llCloseAccount.setOnClickListener(this)
        binding!!.tvEdit.setOnClickListener(this)
        binding!!.llQR.setOnClickListener(this)
        binding!!.ivEdit.setOnClickListener(this)
        dialogManager = DialogManager()
    }

    override fun onResume() {
        super.onResume()
        doorNo = ""
        StreetName = ""
        city = ""
        zipCode = ""
        adress = ""
        mainAdd = ""
        if (!AppUtil.isNetworkAvailable(this@NewProfile)) {
            Toast.makeText(this@NewProfile, "No Internet", Toast.LENGTH_SHORT).show()
            return
        } else {
            myProfile
        }
        try {
            StatusBarcolor.setStatusbarColor(this@NewProfile, "")
        } catch (e: Exception) {
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivEdit -> try {
                if ((ActivityCompat.checkSelfPermission(this@NewProfile, mPermission[0])
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                        this@NewProfile,
                        mPermission[1])
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                        this@NewProfile,
                        mPermission[2])
                            != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(this, mPermission, REQUEST_CODE_PERMISSION)
                } else {
                    selectImage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            R.id.llLogout -> {
                removeAccount = 0
                exitPopup("Are you sure want to Log out?")
            }
            R.id.ll_close_account -> {
                removeAccount = 1
                exitPopup("Are you sure want to Close your Account?")
            }
            R.id.tvEdit -> startActivity(Intent(this@NewProfile, EditProfile::class.java))
            R.id.llFamilyMember -> if (!AppUtil.isNetworkAvailable(this@NewProfile)) {
                AppUtil.showMsgAlert(binding!!.tvAddress,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
                return
            } else {
                myFamilyMember
            }
            R.id.llQR -> showDialogs(this@NewProfile, bitmap)
        }
    }

    private val myFamilyMember: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(this)) {
                dialogManager!!.showProcessDialog(this, "", false, null)
                val call = AppConfig.api_Interface().myFamilyMember
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("GetMemeberList", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString(
                                        "status").equals("true", ignoreCase = true)
                                ) {
                                    val objRe = resp.optJSONObject("response")
                                    runOnUiThread {
                                        dialogManager!!.stopProcessDialog()
                                        if (objRe.optString("responseType")
                                                .equals("1", ignoreCase = true)
                                        ) {
                                            if (!AppUtil.isNetworkAvailable(this@NewProfile)) {
                                                AppUtil.showMsgAlert(binding!!.tvAddress,
                                                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
                                            } else {
                                                startActivity(Intent(this@NewProfile,
                                                    FamilyMemberList::class.java)
                                                    .putExtra("addMoreUserEnabled",
                                                        objRe.optString("addMoreUserEnabled"))
                                                    .putExtra("addMoreUserText",
                                                        objRe.optString("addMoreUserText")))
                                            }
                                        } else if (objRe.optString("responseType")
                                                .equals("2", ignoreCase = true)
                                        ) {
                                            startActivity(Intent(this@NewProfile,
                                                FamilyRequest::class.java)
                                                .putExtra("familyHeadId",
                                                    objRe.optString("familyHeadId"))
                                                .putExtra("note", objRe.optString("note")))
                                        }
                                    }
                                } else {
                                    if (resp.optString("status")
                                            .equals("false", ignoreCase = true)
                                    ) {
                                        dialogManager!!.stopProcessDialog()
                                        runOnUiThread {
                                            AppUtil.showMsgAlert(binding!!.tvAddress,
                                                resp.optString(KeyConstant.KEY_MESSAGE))
                                            startActivity(Intent(this@NewProfile,
                                                AddFamily::class.java))
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                dialogManager!!.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tvAddress,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                dialogManager!!.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tvAddress,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            }
                        } else {
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvAddress,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvAddress, t.message)
                    }
                })
            } else {
                AppUtil.showMsgAlert(binding!!.tvAddress,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery",
            "Cancel")
        val builder = AlertDialog.Builder(this@NewProfile, R.style.MyDialogTheme)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            val result = Utility.checkPermission(this@NewProfile)
            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"
                if (result) cameraIntent()
            } else if (items[item] == "Choose from Gallery") {
                userChoosenTask = "Choose from Gallery"
                if (result) galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // Start the Intent
        startActivityForResult(galleryIntent, SELECT_FILE)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        try {
            val selectedImage = data!!.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!,
                filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            picturePath = cursor.getString(columnIndex)
            cursor.close()
            decodeFile(picturePath)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun decodeFile(filePath: String?) {
        // Decode image size
        try {
            val original = BitmapFactory.decodeStream(assets.open(filePath!!))
            Log.e("Original   dimensions", original.width.toString() + " " + original.height)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, o)

        // The new size we want to scale to
        val REQUIRED_SIZE = 1024

        // Find the correct scale value. It should be the power of 2.
        var width_tmp = o.outWidth
        var height_tmp = o.outHeight
        var scale = 1
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) break
            width_tmp /= 2
            height_tmp /= 2
            scale *= 2
        }

        // Decode with inSampleSize
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        bitmap = BitmapFactory.decodeFile(filePath, o2)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        try {
            val exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }
            bitmap = Bitmap.createBitmap(bitmap!!,
                0,
                0,
                bitmap!!.getWidth(),
                bitmap!!.getHeight(),
                matrix,
                true) // rotating bitmap
        } catch (e: Exception) {
        }
        ConfirmDialogBox(bitmap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("Req Code", "" + requestCode)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(this@NewProfile,
                    "Please allow permissions to update userprofile pic",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data) else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(
                data)
        }
    }

    private fun onCaptureImageResult(data: Intent?) {
        try {
            bitmap = data!!.extras!!["data"] as Bitmap?
            val bytes = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val destination: File
            destination = if (Build.VERSION.SDK_INT >= 29) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    System.currentTimeMillis().toString() + ".jpg")
            } else {
                File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".jpg")
            }
            val fo: FileOutputStream
            try {
                destination.createNewFile()
                fo = FileOutputStream(destination)
                fo.write(bytes.toByteArray())
                fo.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ConfirmDialogBox(bitmap)
        } catch (e: Exception) {
        }
    }

    private fun ConfirmDialogBox(bitmap: Bitmap?) {
        val builder = AlertDialog.Builder(this@NewProfile)
        builder.setTitle("Are you sure want to update this image ?")
        builder.setPositiveButton("Yes") { dialog, id ->
            dialog.dismiss()
            updateProfileImage(bitmap)
        }
        builder.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun updateProfileImage(bitmap: Bitmap?) {
        if (AppUtil.isNetworkAvailable(this@NewProfile)) {
            Log.e("E", bitmap.toString() + "")
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this@NewProfile, "", false, null)
            Log.e("E", "2")
            //User user = PreferenceHelper.getInstance(context).getUserDetail();
            UpdateProfileImage(this@NewProfile, bitmap, true, object : AsyncCallback {
                override fun setResponse(responseCode: Int?, responseStr: String?) {
                    if (responseStr != null) {
                        try {
                            Log.e("UpdateImageRe", responseStr)
                            val resp = JSONObject(responseStr)
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            val message = resp.optString(KeyConstant.KEY_MESSAGE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_201,
                                    ignoreCase = true)
                            ) {
                                try {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    runOnUiThread {
                                        dialogManager.stopProcessDialog()
                                        try {
                                            binding!!.ivProfile.setImageBitmap(bitmap)
                                            MainActivity.ivImage!!.setImageBitmap(bitmap)
                                            HomeFragment.iv!!.setImageBitmap(bitmap)
                                        } catch (e: Exception) {
                                        }
                                    }
                                } catch (e: Exception) {
                                    dialogManager.stopProcessDialog()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvAddress,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvAddress,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvAddress,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                    }
                }

                override fun setException(e: String?) {
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAddress,
                        MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            }).execute()
        } else {
            AppUtil.showMsgAlert(binding!!.tvAddress, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun exitPopup(msg: String) {
        val dialog1 = Dialog(this@NewProfile)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = msg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"

        //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                if (AppUtil.isNetworkAvailable(this@NewProfile)) {
                    dialog1.dismiss()
                    doLogout()
                } else {
                    Toast.makeText(this@NewProfile, "No Internet", Toast.LENGTH_SHORT).show()
                }
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun showDialogs(context: Context, bitmap: Bitmap?) {
        val dialog1 = Dialog(context, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.profileqr)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { dialog1.dismiss() }
        val ivQrCode = dialog1.findViewById<ImageView>(R.id.iv_qr_code)
        ivQrCode.setImageBitmap(bitmap)
        try {
            dialog1.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    private fun doLogout() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().logout(removeAccount.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Toast.makeText(this@NewProfile,
                                obj.optString("message"),
                                Toast.LENGTH_SHORT).show()
                            SharedPreferenceVariable.ClearSharePref(this@NewProfile)
                            PreferenceHelper.getInstance(this@NewProfile)?.logout
                            PreferenceHelper.getInstance(this@NewProfile)?.isLogin = false
                            startActivity(Intent(this@NewProfile, SplashActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            finish()
                        } catch (e: Exception) {
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    AppUtil.showMsgAlert(binding!!.tvAddress, t.message)
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    fun deleteCard(id: String) {
        val dialog1 = Dialog(this@NewProfile)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = "Are you sure want to Delete?"
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"

        //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
        dialog1.setCanceledOnTouchOutside(false)
        try {
            btnOk.setOnClickListener {
                dialog1.dismiss()
                removeDDCard(id)
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
        dialog1.show()
    }

    private fun removeDDCard(id: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().removeDDcard(id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            dialogManager.stopProcessDialog()
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("DeleteCardNo", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                AppUtil.showMsgAlert(binding!!.tvAddress,
                                    resp.optString(KeyConstant.KEY_MESSAGE))
                                myProfile
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("eeee", e.toString())
                            dialogManager.stopProcessDialog()
                            //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("eeee", e.toString())
                            dialogManager.stopProcessDialog()
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvAddress, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 2
    }
}