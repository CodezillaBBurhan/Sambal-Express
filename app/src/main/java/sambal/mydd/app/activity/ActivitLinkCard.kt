package sambal.mydd.app.activity

import android.Manifest.permission
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.adapter.AdapterDdCardList
import sambal.mydd.app.beans.DDCardList
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.LinkcardBinding
import sambal.mydd.app.utils.*
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ActivitLinkCard : AppCompatActivity(), ChatHistoryCallback {

    private lateinit var binding: LinkcardBinding
    private lateinit var userQRuuid: String
    private lateinit var userQRCode: String
    private lateinit var bitmap: Bitmap
    private var mList = ArrayList<DDCardList>()
    private lateinit var adap: AdapterDdCardList

    var dialog1: Dialog? = null

    lateinit var ivDialogTick: ImageView
    private var pubNubChat: PubNubChat? = null

    val PERMISSION_REQUEST_CODE = 1111
    var camPermDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@ActivitLinkCard, R.layout.linkcard)
        binding.titleTv.setText(resources.getString(R.string.app_name) + " Card");
        binding.firstContentTv.setText("* Link your "+resources.getString(R.string.app_name) + " Card");

        getMyProfile()

        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.llLink.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
                startActivityForResult(Intent(this@ActivitLinkCard, ScanQr::class.java), 80)
            } else {
                AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

        binding.cvScan.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
                checkPermissions()
            } else {
                AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

        binding.tapToQrImg.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
                checkPermissions()
            } else {
                AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

        /*binding.cvScan.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
                startActivityForResult(
                    Intent(this@ActivitLinkCard, ScanQRReceiptCode::class.java),
                    60
                )
            } else {
                AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }*/


        binding.ivQR.setOnClickListener {
            showQr(bitmap, "")
        }

//        if(!AppUtil.isNetworkAvailable(this)){
////            ivQR.background(R.drawable.app_icon_new)
//            ErrorMessage.E("qwertrt")
//            binding.ivQR.setBackgroundResource(R.drawable.appicon)
//        }


    }

    fun showQr(bitmap: Bitmap?, desc: String?) {
        dialog1 = Dialog(this@ActivitLinkCard, R.style.NewDialogQR)
        dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1!!.setContentView(R.layout.homeqr)
        dialog1!!.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1!!.window!!.attributes = lp
        val ivClose = dialog1!!.findViewById<ImageView>(R.id.ivClose)
        val ticket_title = dialog1!!.findViewById<TextView>(R.id.ticket_title)
        val ll = dialog1!!.findViewById<LinearLayout>(R.id.ll)
        ivDialogTick = dialog1!!.findViewById(R.id.ivTick)

        ivClose.setOnClickListener {
            if (dialog1 != null) {
                dialog1!!.dismiss()
            }
        }
        ticket_title.text = "Your QR Code"
        ll.setOnClickListener {
            if (dialog1 != null) {
                dialog1!!.dismiss()
            }
        }

        val ivQrCode = dialog1!!.findViewById<ImageView>(R.id.iv_qr_code)
        if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
            ivQrCode.setImageBitmap(bitmap)
        } else {
            try {
                val previouslyEncodedImage: String = SharedPreferenceVariable.loadSavedPreferences(
                    this@ActivitLinkCard,
                    KeyConstant.QRCode_bitmap
                ).toString()
                if (!previouslyEncodedImage.equals("")) {
                    val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                    //imageConvertResult.setImageBitmap(bitmap)
                    ivQrCode.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
            }
        }
        try {
            dialog1!!.show()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.yellow)
            }
        } catch (e: java.lang.Exception) {
        }

    }


    private fun initPubnub() {
        runOnUiThread {
            binding.tvPoints.text = userQRCode.toString()

            pubNubChat = PubNubChat(this@ActivitLinkCard, this)
            pubNubChat!!.initPubNub()

            pubNubChat!!.subscribePubNubChannel(userQRuuid)
            pubNubChat!!.subscribePubNubListener()
        }
    }

    private fun getMyProfile() {

        if (AppUtil.isNetworkAvailable(this@ActivitLinkCard)) {
            ErrorMessage.E("internetcheck")

            val lat = MainActivity.userLat.toString() + ""
            val lng = MainActivity.userLang.toString() + ""

            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this@ActivitLinkCard, "", false, null)
            val call = AppConfig.api_Interface().getMyProfileV1(lat, lng)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Log.e("Respos", obj.toString())

                            if (obj.optString("error_type").equals("200")) {
                                var res = obj.optJSONObject("response")

                                var arr = res.optJSONArray("userDetails")

                                if (arr.length() > 0) {
                                    for (i in 0 until arr.length()) {

                                        var o = arr.optJSONObject(i)

                                        userQRuuid = o.optString("userQRuuid")
                                        userQRCode = o.optString("userQRCode")
                                        initPubnub()
                                    }
                                }

                                var arrGift = res.optJSONArray("DDcardList")
                                mList.clear()
                                for (j in 0 until arrGift.length()) {
                                    var objGift = arrGift.optJSONObject(j)
                                    var userDDCardId = objGift.optString("userDDCardId")
                                    var userDDCardNo = objGift.optString("userDDCardNo")
                                    var ll = DDCardList(userDDCardId, userDDCardNo)
                                    mList.add(ll)
                                }

                                this@ActivitLinkCard.runOnUiThread(java.lang.Runnable {

                                    try {
                                        bitmap = QRCode.from(userQRuuid)
                                            .bitmap()
                                        binding.ivQR.setImageBitmap(bitmap)

                                        val baos = ByteArrayOutputStream()
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                        val b: ByteArray = baos.toByteArray()

                                        val encodedImage: String =
                                            Base64.encodeToString(b, Base64.DEFAULT)

                                        SharedPreferenceVariable.savePreferences(
                                            this@ActivitLinkCard,
                                            KeyConstant.QRCode_bitmap,
                                            encodedImage
                                        )

                                        binding.tvCardNo.text = userQRCode

                                        adap = AdapterDdCardList(this@ActivitLinkCard, mList)
                                        binding.rv.layoutManager =
                                            LinearLayoutManager(this@ActivitLinkCard)
                                        binding.rv.adapter = adap
                                        adap.notifyDataSetChanged()
                                    } catch (e: java.lang.Exception) {
                                    }

                                })
                            }

                            dialogManager.stopProcessDialog()

                        } catch (e: Exception) {
                            Log.e("EXXx", e.toString())
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tvCard, t.message)
                }
            })
        }
        else {

//            ErrorMessage.E("nointernetcheck")
            AppUtil.showMsgAlert(binding.tvCard, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            if (!SavedData.getAllData().equals("")) {
//                ErrorMessage.E("nternetcheck")

                try {

                    binding.ivQR.setImageDrawable(resources.getDrawable(R.drawable.place_holder))

                    var obj = JSONObject(SavedData.getAllData())


                    if (obj.optString("error_type").equals("200")) {
                        var res = obj.optJSONObject("response")

                        var arr = res.optJSONArray("userDetails")

                        if (arr.length() > 0) {
                            for (i in 0 until arr.length()) {

                                var o = arr.optJSONObject(i)

                                userQRuuid = o.optString("userQRuuid")
                                userQRCode = o.optString("userQRCode")
                                initPubnub()
                            }
                        }

                        var arrGift = res.optJSONArray("DDcardList")
                        mList.clear()
                        for (j in 0 until arrGift.length()) {
                            var objGift = arrGift.optJSONObject(j)

                            var userDDCardId = objGift.optString("userDDCardId")
                            var userDDCardNo = objGift.optString("userDDCardNo")

                            var ll = DDCardList(userDDCardId, userDDCardNo)
                            mList.add(ll)

                        }

                        this@ActivitLinkCard.runOnUiThread(java.lang.Runnable {

                            try {
                                bitmap = QRCode.from(userQRuuid)
                                    .bitmap()

                                // binding.ivQR.setImageBitmap(bitmap)
                                try {
                                    val previouslyEncodedImage: String =
                                        SharedPreferenceVariable.loadSavedPreferences(
                                            this@ActivitLinkCard,
                                            KeyConstant.QRCode_bitmap
                                        ).toString()
                                    if (!previouslyEncodedImage.equals("")) {
                                        val b: ByteArray =
                                            Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                                        val bitmap: Bitmap =
                                            BitmapFactory.decodeByteArray(b, 0, b.size)
                                        //imageConvertResult.setImageBitmap(bitmap)
                                        binding.ivQR.setImageBitmap(bitmap)
                                    }else {
                                        ErrorMessage.E("qrcode")
                                        binding.ivQR.setImageDrawable(resources.getDrawable(R.drawable.place_holder))
                                    }
                                } catch (e: Exception) {
                                }
                                binding.tvCardNo.text = userQRCode

                                adap = AdapterDdCardList(this@ActivitLinkCard, mList)
                                binding.rv.layoutManager = LinearLayoutManager(this@ActivitLinkCard)
                                binding.rv.adapter = adap
                                adap.notifyDataSetChanged()
                            } catch (e: java.lang.Exception) {
                            }

                        })
                    }

                }

                catch (e: Exception) {
                    Log.e("EXXx", e.toString())
                }
            }

            else{
//                ErrorMessage.E("ternetcheck")

                binding.ivQR.setImageDrawable(resources.getDrawable(R.drawable.place_holder))
            }
        }
    }


    fun params(): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        return params
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 80) {
//                adap == null
                getMyProfile()
            }
        }
    }

    override fun clearData() {
    }

    override fun onRefreshChatList(jsonObject: JsonObject?) {

        runOnUiThread(java.lang.Runnable {
            binding.ivTick.visibility = View.VISIBLE
            try {
                if (dialog1!!.isShowing) {
                    ivDialogTick.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
            }
            binding.ivQR.visibility = View.GONE
            binding.ivTickImg.visibility = View.VISIBLE
        })
    }

    override fun onRefreshHistoryList(list: MutableList<PNHistoryItemResult>?) {
        Log.e("Tt", "11")
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.isCameraPermissionGranted(this)) {
                startActivityForResult(Intent(this@ActivitLinkCard, ScanQr::class.java), 80)
            } else {
                alertPopup()
            }
        } else {
            startActivityForResult(Intent(this@ActivitLinkCard, ScanQr::class.java), 80)
        }
    }

    private fun requestPermission() {
        MainActivity.countCamPermission++
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.CAMERA, " "),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun alertPopup() {
        try {
            if (camPermDialog != null) {
                camPermDialog!!.dismiss()
            }
            camPermDialog = Dialog(this@ActivitLinkCard)
            camPermDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            camPermDialog!!.setContentView(R.layout.popup_common)
            camPermDialog!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(camPermDialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            camPermDialog!!.window!!.attributes = lp
            val tvTitle = camPermDialog!!.findViewById<TextView>(R.id.popup_content_inbold)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Camera Permission"
            val contentText = camPermDialog!!.findViewById<TextView>(R.id.popup_content)
            contentText.text = "In order to scan please allow camera permissions"
            val btnNo = camPermDialog!!.findViewById<
                    TextView>(R.id.popup_no_btn)
            btnNo.text = "Cancel"
            val btnOk = camPermDialog!!.findViewById<TextView>(R.id.popup_yes_btn)
            btnOk.text = "OK"
            val view = camPermDialog!!.findViewById<View>(R.id.view_btw_btn)
            view.visibility = View.VISIBLE
            camPermDialog!!.setCancelable(true)
            camPermDialog!!.show()
            try {
                btnOk.setOnClickListener {
                    try {
                        if (camPermDialog != null) {
                            camPermDialog!!.dismiss()
                        }
                    } catch (e: java.lang.Exception) {
                    }

                    if (MainActivity.countCamPermission < 2) {
                        requestPermission()
                    } else {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", getPackageName(), null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
                btnNo.setOnClickListener {
                    try {
                        if (camPermDialog != null) {
                            camPermDialog!!.dismiss()
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }
            } catch (e: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> try {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(Intent(this@ActivitLinkCard, ScanQr::class.java), 80)
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        "Please allow permission in order to scan",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } catch (e: java.lang.Exception) {
            }
        }
    }

}