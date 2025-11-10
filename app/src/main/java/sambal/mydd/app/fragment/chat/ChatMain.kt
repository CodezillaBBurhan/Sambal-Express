package sambal.mydd.app.fragment.chat

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.squareup.picasso.Picasso
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.adapter.AdapterMainChat
import sambal.mydd.app.adapter.AdapterReportChat
import sambal.mydd.app.apiResponse.ApiResponse
import sambal.mydd.app.beans.ChatMainPubNubBean
import sambal.mydd.app.beans.ReportedChatListBean
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.ChatscreenBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.MimeTypeMap


class ChatMain : AppCompatActivity(), ChatHistoryCallback, View.OnClickListener {
    var pubNubChat: PubNub? = null
    var context: Context? = null
    var id: String? = ""
    var mChatList = ArrayList<ChatMainPubNubBean>()
    var mReportList = ArrayList<ReportedChatListBean>()
    var adapterReportChat: AdapterReportChat? = null
    var adap: AdapterMainChat? = null
    var userId = ""
    var isadmin: String? = ""
    var dialogManager: DialogManager? = null
    var packageManager1: PackageManager? = null
    var picturePath: String? = null
    var userChoosenTask: String? = null
    var mPermission = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    var bitmap: Bitmap? = null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    var fileUri: Uri? = null
    var dialog: Dialog? = null
    var timeToken = ""
    var subskey: String? = ""
    var pubskey: String? = ""
    var type: String? = ""
    var followingStatus: String? = ""
    var clickcount = 0
    var mainTime = "0"
    var notificationStatus = "0"
    private var binding: ChatscreenBinding? = null
    private var permissionDialog: Dialog? = null
    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                handlePickedMedia(uri)
            } else {
                Log.d("ChatMain", "No media selected from picker")
            }
        }


    //position = bundle.getInt("position");
    private val intentData: Unit
        private get() {
            binding!!.ivSend.setOnClickListener(this)
            val bundle = intent.extras
            if (bundle != null) {
                if (bundle.getString("id") != null) {
                    id = bundle.getString("id")
                }
                if (bundle.getString("isAdmin") != null) {
                    isadmin = bundle.getString("isAdmin")
                }
                if (bundle.getString("name") != null) {
                    binding!!.tvTitle.text = bundle.getString("name")
                }
                if (bundle.getString("subskey") != null) {
                    subskey = bundle.getString("subskey")
                }
                if (bundle.getString("pubskey") != null) {
                    pubskey = bundle.getString("pubskey")
                }
                if (bundle.getString("followingstatus") != null) {
                    followingStatus = bundle.getString("followingstatus")
                }
                //position = bundle.getInt("position");
                if (bundle.getString("type") != null) {
                    type = bundle.getString("type")
                }
                binding!!.ivOFF.setOnClickListener(this)
                binding!!.ivON.setOnClickListener(this)
                Log.e("isadmin", ">>$isadmin")
                if (isadmin == "0") {
                    binding!!.ivSettings.visibility = View.GONE
                    binding!!.swich.visibility = View.GONE
                    binding!!.tvNotify.visibility = View.GONE
                } else if (isadmin == "1") {
                    binding!!.ivSettings.visibility = View.VISIBLE
                    binding!!.swich.visibility = View.VISIBLE
                    binding!!.tvNotify.visibility = View.VISIBLE
                    binding!!.ivOFF.visibility = View.VISIBLE
                    binding!!.ivON.visibility = View.GONE
                }
            }
        }

    private fun initPubNub() {
        pubNubChat = PubNub(context!!, this)
        pubNubChat!!.initPubNub(
            SharedPreferenceVariable.loadSavedPreferences(
                this@ChatMain,
                KeyConstant.KEY_USER_UUID
            ), subskey, pubskey
        )
        pubNubChat!!.subscribePubNubChannel(id + "")
        pubNubChat!!.subscribePubNubListener()
        pubNubChat!!.fetchPubNubHistory(id, 10000000)
        //  PubNub pubnub = new PubNub(pubNubChat);
    }

    override fun clearData() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.chatscreen)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setWindowInsetsAnimationCallback(
            binding!!.chatLayout,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                    // Calculate translation only when keyboard is visible or animating
                    if (imeInsets.bottom > 0) {
                        // Keyboard is visible or animating in
                        binding!!.chatLayout.translationY = -(imeInsets.bottom - sysInsets.bottom).toFloat()
                    } else {
                        // Keyboard is fully hidden - reset translation to maintain bottom margin
                        binding!!.chatLayout.translationY = 0f
                    }

                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    // Ensure translation is reset when animation completes
                    val currentInsets = ViewCompat.getRootWindowInsets(binding!!.chatLayout)
                    val imeInsets = currentInsets?.getInsets(WindowInsetsCompat.Type.ime()) ?: Insets.NONE

                    if (imeInsets.bottom == 0) {
                        // Keyboard is fully closed, reset translation
                        binding!!.chatLayout.translationY = 0f
                    }
                }
            }
        )

        try {


            context = this@ChatMain
            packageManager1 = getPackageManager()
            dialogManager = DialogManager()
            dialogManager!!.showProcessDialog(this, "", false, null)
            init()
            userId = PreferenceHelper.getInstance(this@ChatMain)?.userDetail?.userId.toString()
            intentData
            mChatList.clear()
            initPubNub()
            binding!!.ivSend.setOnClickListener(this)
            binding!!.ivBack.setOnClickListener(this)
            if (Build.VERSION.SDK_INT >= 11) {
                binding!!.rv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    if (bottom < oldBottom) {
                        binding!!.rv.postDelayed({
                            try {
                                binding!!.rv.smoothScrollToPosition(
                                    binding!!.rv.adapter!!.itemCount - 1
                                )
                            } catch (e: Exception) {
                            }
                        }, 100)
                    }
                }
            }
            binding!!.exMessge.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        binding!!.ivSend.setImageResource(R.drawable.sendchat)
                    } else if (s.length > 0) {
                        binding!!.ivSend.setImageResource(R.drawable.sendactive)
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun init() {
        binding!!.ivCamera.setOnClickListener {
            try {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {

                    ErrorMessage.E("mpermission"+mPermission.size)

                    if ((ActivityCompat.checkSelfPermission(this@ChatMain, mPermission[0])
                                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@ChatMain,
                            mPermission[1])
                                != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                            this@ChatMain,
                            mPermission,
                            REQUEST_CODE_PERMISSION
                        )
                    } else {
                        ErrorMessage.E("mayurra")
                        selectImage()
                    }
                }
                else {
                    if ((ActivityCompat.checkSelfPermission(this@ChatMain, mPermission[0])
                                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@ChatMain,
                            mPermission[1]
                        )
                                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@ChatMain,
                            mPermission[2]
                        )
                                != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                            this@ChatMain,
                            mPermission,
                            REQUEST_CODE_PERMISSION
                        )
                    } else {
                        selectImage()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding!!.ivSettings.setOnClickListener {
            mReportList.clear()
            AppUtil.hideSoftKeyboard(this@ChatMain)
            reportBusinessChatList()
        }
    }

    private fun reportBusinessChatList() {
        dialogManager!!.showProcessDialog(this@ChatMain, "", false, null)
        if (AppUtil.isNetworkAvailable(context)) {
            val call = AppConfig.api_Interface().reportBusinessChatList(id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("RespoReport", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                runOnUiThread {
                                    val response = resp.optJSONObject("response")
                                    val arr = response.optJSONArray("reportList")
                                    for (i in 0 until arr.length()) {
                                        val obj = arr.optJSONObject(i)
                                        val reportId = obj.optString("reportId")
                                        val agentId = obj.optString("agentId")
                                        val userId = obj.optString("userId")
                                        val userName = obj.optString("userName")
                                        val reportedUserId = obj.optString("reportedUserId")
                                        val reportedUserName = obj.optString("reportedUserName")
                                        val message = obj.optString("message")
                                        val imageURL = obj.optString("imageURL")
                                        val timeToken = obj.optString("timeToken")
                                        val timeTokenEnd = obj.optString("timeTokenEnd")
                                        val comments = obj.optString("comments")
                                        val reportDate = obj.optString("reportDate")
                                        val rc = ReportedChatListBean(
                                            reportId,
                                            agentId,
                                            userId,
                                            userName,
                                            reportedUserId,
                                            reportedUserName,
                                            message,
                                            imageURL,
                                            timeToken,
                                            timeTokenEnd,
                                            comments,
                                            reportDate
                                        )
                                        mReportList.add(rc)
                                    }
                                    dialogManager!!.stopProcessDialog()
                                    if (dialog != null) {
                                        dialog!!.dismiss()
                                    }
                                    dialog = Dialog(this@ChatMain)
                                    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                                    dialog!!.setContentView(R.layout.reportedchat)
                                    val lp = WindowManager.LayoutParams()
                                    lp.copyFrom(dialog!!.window!!.attributes)
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT
                                    dialog!!.window!!.attributes = lp
                                    dialog!!.setOnShowListener {
                                        val imm =
                                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                        imm.showSoftInput(
                                            binding!!.exMessge,
                                            InputMethodManager.SHOW_IMPLICIT
                                        )
                                    }
                                    val rvDialog = dialog!!.findViewById<RecyclerView>(R.id.rv)
                                    val ivClose = dialog!!.findViewById<ImageView>(R.id.ivClose)
                                    rvDialog.isNestedScrollingEnabled = true
                                    adapterReportChat =
                                        AdapterReportChat(this@ChatMain, mReportList)
                                    rvDialog.layoutManager = LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                    rvDialog.adapter = adapterReportChat
                                    adapterReportChat!!.notifyDataSetChanged()
                                    ivClose.setOnClickListener { dialog!!.dismiss() }
                                    dialog!!.setCancelable(true)
                                    dialog!!.show()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding!!.tvTitle,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(
                            binding!!.tvTitle,
                            MessageConstant.MESSAGE_SOMETHING_WRONG
                        )
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvTitle, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvTitle, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            try {
                pubNubChat!!.getTimeToken(this@ChatMain, id!!)
            } catch (e: Exception) {
            }
            /* if (mChatList.size() > 0)
            {
                checkDataInSQLITE();
            }*/pubNubChat!!.unSubscribePubNubChannel(id!!)
        } catch (e: Exception) {
        }
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Gallery",
            "Cancel"
        )
        val builder = AlertDialog.Builder(this@ChatMain, R.style.MyDialogTheme)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            when (items[item]) {
                "Take Photo" -> {
                    userChoosenTask = "Take Photo"
                    try {
                        cameraIntent()
                    } catch (e: Exception) {
                        ErrorMessage.E("Exception$e")
                    }
                }
                "Choose from Gallery" -> {
                    userChoosenTask = "Choose from Gallery"
                    launchPhotoPicker()
                }
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun launchPhotoPicker() {
        try {
            pickMediaLauncher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
            )
        } catch (e: Exception) {
            Log.e("ChatMain", "Unable to launch photo picker", e)
            Toast.makeText(this, "Unable to open gallery", Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePickedMedia(uri: Uri) {
        try {
            val localPath = copyUriToCache(uri)
            picturePath = localPath
            decodeFile(localPath)
        } catch (e: Exception) {
            Log.e("ChatMain", "Failed to handle picked media", e)
            Toast.makeText(this, "Unable to process selected image", Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun copyUriToCache(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.takeIf { it.isNotBlank() } ?: "jpg"
        val fileName = "picker_${System.currentTimeMillis()}.$extension"
        val destination = File(cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: throw IOException("Unable to open selected image stream")
        return destination.absolutePath
    }

    override fun onRefreshHistoryList(list: List<PNHistoryItemResult>) {
        mChatList.clear()
        for (i in list.indices) {
            Log.e("List", list[i].entry.toString() + "")
            try {
                val obj = JSONObject(list[i].entry.toString())
                if (!obj.has("nameValuePairs")) {
                    val userName = obj.optString("userName")
                    val image = obj.optString("image")
                    val userId = obj.optString("userId")
                    val date = obj.optString("date")
                    val message = obj.optString("message")
                    val isAdmin = obj.optString("isAdmin")
                    val timeToken = obj.optString("timeToken")
                    val cm = ChatMainPubNubBean(
                        userId,
                        userName,
                        image,
                        message,
                        date,
                        isAdmin,
                        timeToken
                    )
                    mChatList.add(cm)
                }
            } catch (e: Exception) {
            }
        }
        adap = AdapterMainChat(this@ChatMain, mChatList, isadmin!!)
        binding!!.rv.layoutManager = WrapContentLinearLayoutManager(this@ChatMain)
        binding!!.rv.adapter = adap
        adap!!.notifyDataSetChanged()
        try {
            binding!!.rv.scrollToPosition(
                binding!!.rv.adapter!!.itemCount - 1
            )
        } catch (e: Exception) {
        }
        dialogManager!!.stopProcessDialog()
    }

    override fun onRefreshChatList(jsonObject: JsonObject) {
        try {
            val obj = JSONObject(jsonObject.toString())
            if (!obj.has("nameValuePairs")) {
                val userName = obj.optString("userName")
                val image = obj.optString("image")
                val userId = obj.optString("userId")
                val date = obj.optString("date")
                val message = obj.optString("message")
                val isAdmin = obj.optString("isAdmin")
                val timeToken = obj.optString("timeToken")
                val cm =
                    ChatMainPubNubBean(userId, userName, image, message, date, isAdmin, timeToken)
                mChatList.add(cm)
                adap!!.notifyItemInserted(mChatList.size)
                try {
                    binding!!.rv.scrollToPosition(mChatList.size - 1)
                    binding!!.rv.smoothScrollToPosition(
                        binding!!.rv.adapter!!.itemCount - 1
                    )
                } catch (e: Exception) {
                }
            }
            try {
                binding!!.rv.scrollToPosition(mChatList.size - 1)
                binding!!.rv.smoothScrollToPosition(
                    binding!!.rv.adapter!!.itemCount - 1
                )
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= 11) {
            binding!!.rv.postDelayed({
                try {
                    binding!!.rv.smoothScrollToPosition(
                        binding!!.rv.adapter!!.itemCount - 1
                    )
                } catch (e: Exception) {
                }
            }, 100)
        }
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        val uri = data?.data
        if (uri != null) {
            handlePickedMedia(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivOFF -> {
                binding!!.ivON.visibility = View.VISIBLE
                binding!!.ivOFF.visibility = View.GONE
                notificationStatus = "1"
            }
            R.id.ivON -> {
                binding!!.ivON.visibility = View.GONE
                binding!!.ivOFF.visibility = View.VISIBLE
                notificationStatus = "0"
            }
            R.id.ivBack -> if (type.equals("direct", ignoreCase = true)) {
                startActivity(Intent(this@ChatMain, MainActivity::class.java))
            } else {
                finish()
            }
            R.id.ivSend -> if (TextUtils.isEmpty(
                    binding!!.exMessge.text.toString().trim { it <= ' ' })
            ) {
                AppUtil.showMsgAlert(binding!!.exMessge, "Enter message")
                return
            } else {
                clickcount = clickcount + 1
                if (clickcount == 1) {
                    if (followingStatus.equals("0", ignoreCase = true)) {
                        doFollow()
                    }
                } else {
                    //check how many times clicked and so on
                }
                try {
                    AppUtil.hideSoftKeyboard(this@ChatMain)
                    val date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(
                        Date()
                    )
                    //val obj: HashMap<*, *> = HashMap<Any?, Any?>()
                    val obj: HashMap<String, String> = HashMap()
                    obj["userId"] = userId
                    if (isadmin.equals("1", ignoreCase = true)) {
                        obj["userName"] = binding!!.tvTitle.text.toString().trim { it <= ' ' }
                    } else {
                        obj["userName"] = SharedPreferenceVariable.loadSavedPreferences(
                            this@ChatMain,
                            KeyConstant.Shar_Name
                        ).toString()
                    }
                    obj["image"] = ""
                    obj["message"] = binding!!.exMessge.text.toString().trim { it <= ' ' }
                    obj["date"] = DateUtil.localToUTC(date).toString()
                    obj["isAdmin"] = isadmin.toString()
                    //obj.put("timeToken",time);
                    Log.e("obj", obj.toString() + "")
                    pubNubChat!!.publishPubNub(obj, id)

                    /* if (isadmin.equalsIgnoreCase("1")) {
                        }*/sendChatNotification()
                    binding!!.exMessge.setText("")
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@ChatMain, "")
        } catch (e: Exception) {
        }
    }

    private fun doFollow() {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.followAgent(this, id, binding!!.tvTitle, object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    Log.e("follwi", resp.toString())
                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                        runOnUiThread { }
                    } else {
                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                resp.optString(KeyConstant.KEY_STATUS),
                                ignoreCase = true
                            )
                        ) {
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                resp.optString(KeyConstant.KEY_MESSAGE)
                            )
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(
                        binding!!.tvTitle,
                        MessageConstant.MESSAGE_SOMETHING_WRONG
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(
                        binding!!.tvTitle,
                        MessageConstant.MESSAGE_SOMETHING_WRONG
                    )
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > " + text)
                AppUtil.showMsgAlert(binding!!.tvTitle, text)
            }
        })
    }

    private fun cameraIntent() {

        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            fileUri = getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }
        catch (e : Exception){
            ErrorMessage.E(e.toString())
        }

    }




    //        @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("Req Code", "" + requestCode)

//        ErrorMessage.E("rahul" + requestCode + grantResults!!.size + grantResults[0]!!.toString() + grantResults[1]!!.toString() + grantResults[2].toString() + grantResults[3].toString())

//            ErrorMessage.E("rahul" +permissions.size+  requestCode + grantResults!!.size + grantResults[0]!!.toString() + grantResults[1]!!.toString() + REQUEST_CODE_PERMISSION)

        ErrorMessage.E("rahul " + grantResults.size + "  " + REQUEST_CODE_PERMISSION + grantResults[0]!!.toString()+" "+permissions[0])
        if (requestCode == REQUEST_CODE_PERMISSION) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
//                if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                }
                else {

                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        alertPopup()
                    }
                }
            }


            else {
                if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else {

                    // show toast for please allow the permission for the media and camera
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        alertPopup()
                    }
                }
            }
        }
    }

    private fun alertPopup() {

        try {
            if (permissionDialog != null) {
                permissionDialog!!.dismiss()
            }


            permissionDialog = Dialog(this)
            permissionDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            permissionDialog!!.setContentView(R.layout.popup_common)
            permissionDialog!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(permissionDialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            permissionDialog!!.window!!.attributes = lp
            val tvTitle = permissionDialog!!.findViewById<TextView>(R.id.popup_content_inbold)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Allow Permissions"
            val contentText = permissionDialog!!.findViewById<TextView>(R.id.popup_content)
            contentText.setTextColor(resources.getColor(R.color.colorPrimary))
            contentText.setText(R.string.PermissionMessageCamera)
            val btnNo = permissionDialog!!.findViewById<TextView>(R.id.popup_no_btn)
            btnNo.text = "Cancel"
            val btnOk = permissionDialog!!.findViewById<TextView>(R.id.popup_yes_btn)
            btnOk.text = "OK"
            val view = permissionDialog!!.findViewById<View>(R.id.view_btw_btn)
            view.visibility = View.VISIBLE
            if (permissionDialog != null) {
                permissionDialog!!.setCancelable(false)
                permissionDialog!!.show()
            }
            try {
                btnOk.setOnClickListener {
                    try {
                        if (permissionDialog != null) {
                            permissionDialog!!.dismiss()
                        }
                    } catch (e: Exception) {
                    } catch (e: Exception) {
                    }


                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                btnNo.setOnClickListener {
                    try {
                        permissionDialog!!.dismiss()

                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
        }

    }


    fun getOutputMediaFileUri(type: Int): Uri {
        //return Uri.fromFile(getOutputMediaFile(type));
        picturePath = getOutputMediaFile(type)!!.path
        Log.e("fileUriPath", picturePath!!)
        return FileProvider.getUriForFile(this, "$packageName.provider", File(picturePath))
    }

    fun decodeFile(filePath: String?) {
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
        bitmap = BitmapFactory.decodeFile(picturePath, o2)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
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
            bitmap = Bitmap.createBitmap(
                bitmap!!,
                0,
                0,
                bitmap!!.getWidth(),
                bitmap!!.getHeight(),
                matrix,
                true
            ) // rotating bitmap
            Log.e("Image", bitmap.toString() + "")
            startActivityForResult(
                Intent(this@ChatMain, ChatImage::class.java)
                    .putExtra("image", picturePath), 80
            )
        } catch (e: Exception) {
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        ErrorMessage.E("Data"+data!!.toString())
        if (resultCode == RESULT_OK && requestCode == 80) {
            try {
                val url = data!!.getStringExtra("url")
                val msg = data.getStringExtra("msg")
                AppUtil.hideSoftKeyboard(this@ChatMain)
                val date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
                //val obj: HashMap<*, *> = HashMap<Any?, Any?>()
                val obj: HashMap<String, String> = HashMap()
                obj["userId"] = userId
                if (isadmin.equals("1", ignoreCase = true)) {
                    obj["userName"] = binding!!.tvTitle.text.toString().trim { it <= ' ' }
                } else {
                    obj["userName"] = SharedPreferenceVariable.loadSavedPreferences(
                        this@ChatMain,
                        KeyConstant.Shar_Name
                    ).toString()
                }
                obj["image"] = url.toString()
                obj["message"] = msg.toString()
                obj["date"] = DateUtil.localToUTC(date).toString()
                obj["isAdmin"] = isadmin.toString()
                Log.e("obj", obj.toString() + "")
                pubNubChat!!.publishPubNub(obj, id)
                if (isadmin.equals("1", ignoreCase = true)) {
                    sendChatNotification()
                }
                if (Build.VERSION.SDK_INT >= 11) {
                    binding!!.rv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                        if (bottom < oldBottom) {
                            binding!!.rv.postDelayed({
                                try {
                                    binding!!.rv.smoothScrollToPosition(
                                        binding!!.rv.adapter!!.itemCount - 1
                                    )
                                } catch (e: Exception) {
                                }
                            }, 100)
                        }
                    }
                }
                try {
                    binding!!.exMessge.setText("")
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
            }
        }
        else if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data)
            else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data)
        }
    }

    private fun onCaptureImageResult(data: Intent?) {
        try {
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", File(picturePath))
            try {
                val bitmap = handleSamplingAndRotationBitmap(this, uri)
                startActivityForResult(
                    Intent(this@ChatMain, ChatImage::class.java)
                        .putExtra("image", picturePath), 80
                )
            } catch (e: IOException) {
                Log.e("Exception", e.toString())
                e.printStackTrace()
            }
        } catch (e: Exception) {
        }

//        ivImage.setImageBitmap(thumbnail);
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
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                img,
                180
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                img,
                270
            )
            else -> img
        }
    }

    fun fullscreenImage(image: String?) {
        val dialog = Dialog(this@ChatMain, R.style.full_screen_dialog)
        // Include dialog.xml file
        dialog.setContentView(R.layout.fullscreendialog)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val iv = dialog.findViewById<ImageView>(R.id.iv)
        iv.setOnTouchListener(ImageMatrixTouchHandler(dialog.context))
        try {
            Glide.with(context!!)
                .load(image)
                .dontAnimate() // will load image
                .placeholder(R.drawable.mainimageplaceholder)
                .error(R.drawable.mainimageplaceholder)
                .dontAnimate()
                .into(iv)
        } catch (e: Exception) {
        }
        ivClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun deleteMessage(timeToken: String, pos: Int, endTime: String) {
        try {
            AppUtil.hideSoftKeyboard(this@ChatMain)
            if (!TextUtils.isEmpty(timeToken)) {
                pubNubChat!!.deleteMsg(
                    id!!,
                    timeToken.toLong(),
                    endTime.toLong(),
                    mChatList,
                    adap!!,
                    pos,
                    binding!!.rv
                )
                mReportList.clear()
                /* getRepotrdChatList(timeToken);*/deleteReportedChat("", 0, timeToken)
            } else {
                pubNubChat!!.deleteMsg(
                    id!!,
                    null,
                    endTime.toLong(),
                    mChatList,
                    adap!!,
                    pos,
                    binding!!.rv
                )
                mReportList.clear()
                deleteReportedChat("", 0, endTime)

                // getRepotrdChatList(endTime);
            }
        } catch (e: Exception) {
        }
    }

    fun reportToAdmin(
        name: String?,
        msg: String?,
        img: String?,
        timeToken: String,
        userId: String?,
    ) {
        if (dialog != null) {
            dialog!!.dismiss()
        }
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog!!.setContentView(R.layout.reportadmin)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = lp
        dialog!!.setOnShowListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding!!.exMessge, InputMethodManager.SHOW_IMPLICIT)
        }
        val tvName = dialog!!.findViewById<TextView>(R.id.tvName)
        val tvUserMsg = dialog!!.findViewById<TextView>(R.id.tvUserMsg)
        val exMsg = dialog!!.findViewById<EditText>(R.id.etMsg)
        val btnSubmit = dialog!!.findViewById<Button>(R.id.btnSubmit)
        val iv = dialog!!.findViewById<ImageView>(R.id.iv)
        val ivClose = dialog!!.findViewById<ImageView>(R.id.ivClose)
        tvName.text = name
        tvUserMsg.text = msg
        ivClose.setOnClickListener { dialog!!.dismiss() }
        if (TextUtils.isEmpty(msg)) {
            tvUserMsg.visibility = View.GONE
        } else {
            tvUserMsg.visibility = View.VISIBLE
        }
        if (TextUtils.isEmpty(img)) {
            iv.visibility = View.GONE
        } else {
            iv.visibility = View.VISIBLE
            Picasso.with(this@ChatMain)
                .load(img)
                .placeholder(R.drawable.mainimageplaceholder)
                .error(R.drawable.mainimageplaceholder)
                .into(iv)
        }
        btnSubmit.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(exMsg.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@ChatMain, "Enter Message", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                AppUtil.hideSoftKeyboard(this@ChatMain)
                reportBusinessChat(
                    msg,
                    img,
                    exMsg.text.toString().trim { it <= ' ' },
                    tvName,
                    timeToken
                )
            }
        })
        dialog!!.setCancelable(true)
        dialog!!.show()
    }

    private fun reportBusinessChat(
        msg: String?,
        img: String?,
        exMsg: String,
        tvName: TextView,
        timeToken: String,
    ) {
        dialogManager!!.showProcessDialog(this@ChatMain, "", false, null)
        if (AppUtil.isNetworkAvailable(context)) {
            val call = AppConfig.api_Interface()
                .reportBusinessChat(id, msg, img, this.timeToken, userId, exMsg, "")
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("asssaasas", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || errorType == "202") {
                                runOnUiThread {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        tvName,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                    dialog!!.dismiss()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        tvName,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(tvName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(tvName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(tvName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(tvName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun deleteReportedChat(reportId: String?, position: Int, timeTokens: String?) {
        AppUtil.hideSoftKeyboard(this@ChatMain)
        if (AppUtil.isNetworkAvailable(context)) {
            val call = AppConfig.api_Interface().deleteBusinessChatReport(reportId, timeTokens, id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("DeleteReport", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || errorType == "202") {
                                runOnUiThread {
                                    try {
                                        mReportList.removeAt(position)
                                        adapterReportChat!!.notifyItemRemoved(position)
                                        adapterReportChat!!.notifyItemRangeChanged(
                                            position,
                                            mReportList.size
                                        )
                                        for (i in mChatList.indices) {
                                            if (mChatList[i].timeToken.equals(
                                                    timeTokens,
                                                    ignoreCase = true
                                                )
                                            ) {
                                                try {
                                                    mainTime = mChatList[i + 1].timeToken
                                                } catch (e: Exception) {
                                                }
                                                if (!mainTime.equals("0", ignoreCase = true)) {
                                                    pubNubChat!!.deleteMsg(
                                                        id!!,
                                                        mChatList[i].timeToken.toLong(),
                                                        mChatList[i + 1].timeToken.toLong(),
                                                        mChatList,
                                                        adap!!,
                                                        i,
                                                        binding!!.rv
                                                    )
                                                    break
                                                } else {
                                                    pubNubChat!!.deleteMsg(
                                                        id!!,
                                                        null,
                                                        mChatList[i].timeToken.toLong(),
                                                        mChatList,
                                                        adap!!,
                                                        i,
                                                        binding!!.rv
                                                    )
                                                    break
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding!!.tvTitle,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(
                            binding!!.tvTitle,
                            MessageConstant.MESSAGE_SOMETHING_WRONG
                        )
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvTitle, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvTitle, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun keepReportedChat(reportId: String?, position: Int, timeTokens: String?) {
        AppUtil.hideSoftKeyboard(this@ChatMain)
        if (AppUtil.isNetworkAvailable(context)) {
            val call = AppConfig.api_Interface().deleteBusinessChatReport(reportId, timeToken, "")
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("asssaasas", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || errorType == "202") {
                                runOnUiThread {
                                    try {
                                        mReportList.removeAt(position)
                                        adapterReportChat!!.notifyItemRemoved(position)
                                        adapterReportChat!!.notifyItemRangeChanged(
                                            position,
                                            mReportList.size
                                        )
                                    } catch (e: Exception) {
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding!!.tvTitle,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("Exxx", e.toString())
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvTitle,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(
                            binding!!.tvTitle,
                            MessageConstant.MESSAGE_SOMETHING_WRONG
                        )
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvTitle, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvTitle, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun sendChatNotification() {
        if (AppUtil.isNetworkAvailable(context)) {
            val call = AppConfig.api_Interface().sendChatNotification(
                id,
                id,
                binding!!.exMessge.text.toString().trim { it <= ' ' },
                notificationStatus
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ChatNotifcation", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                // final JSONObject responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE);
                                // dialogManager.stopProcessDialog();
                                binding!!.ivON.visibility = View.GONE
                                binding!!.ivOFF.visibility = View.VISIBLE
                                notificationStatus = "0"
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    // dialogManager.stopProcessDialog();
                                    //   AppUtil.showMsgAlert(binding.tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            //dialogManager.stopProcessDialog();
                            // AppUtil.showMsgAlert(binding.tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        //dialogManager.stopProcessDialog();
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvTitle, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (type.equals("direct", ignoreCase = true)) {
            startActivity(Intent(this@ChatMain, MainActivity::class.java))
        } else {
            finish()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 2
        private fun getOutputMediaFile(type: Int): File? {
            // External sdcard location
            val mediaStorageDir: File
            mediaStorageDir = if (Build.VERSION.SDK_INT >= 29) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/DD"
                )
            } else {
                File(Environment.getExternalStorageDirectory(), "/DD")
            }

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e("Hello Camera", "Oops! Failed create Hello Camera directory")
                    return null
                }
            }
            // Create a media file name
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val mediaFile: File
            mediaFile = File(
                mediaStorageDir.path + File.separator
                        + "IMG_" + timeStamp + ".jpg"
            )
            return mediaFile
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int,
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