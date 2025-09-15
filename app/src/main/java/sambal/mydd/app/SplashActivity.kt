package sambal.mydd.app

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.iid.FirebaseInstanceId
import sambal.mydd.app.activity.PromoCode
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.beans.User
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.ActivitySplashBinding
import sambal.mydd.app.runtimePermission.PermissionsManager
import sambal.mydd.app.runtimePermission.PermissionsResultAction
import sambal.mydd.app.utils.*
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.*


class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private val SPLASH_DISPLAY_LENGTH = 2000
    private var context: Context? = null
    private var user: User? = null
    private var isLogin = false
    private var myLog: MyLog? = null
    var currentVersion: String? = null
    var latestVersion: String? = null
    var updateDialog: Dialog? = null
    var gpsDialog: Dialog? = null
    private var userLat = 0.0
    private var userLang = 0.0
    private var cityNameStr = ""
    private var PromoCodeLink = ""
    private var new_PromoCodeLink = ""
    private val PERMISSION_REQUEST_CODE = 1111
    var gpsAlertDialog: Dialog? = null
    var permissionsManager: PermissionsManager? = null
    private var mHandler: Handler? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private val mPermission =
        arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    private var mLocationManager: LocationManager? = null
    var location_permission = false
    var check_redirection = true
    private var permissionDialog: Dialog? = null
    var countStoragePermission = 0
    var checkForBluthoothPopUpVisible = true

    val versionChecker = GetLatestVersion()


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Fabric.with(this, new Crashlytics());


        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        context = this@SplashActivity
        SavedData.saveLocationPermission("true")


//        countStoragePermission= SavedData.getCountNearByDevicePermission()

        val permissionCountString = SavedData.getCountNearByDevicePermission()
        countStoragePermission = permissionCountString?.toIntOrNull() ?: 0

        // AppUtil.printKeyHash(this);
        permissionsManager = PermissionsManager.instance
        deleteCache(this)
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        myLog = MyLog()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Log.e("slish", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result.token
                // Log and toast
                PreferenceHelper.getInstance(context)?.fcmToken = token
                val deviceId = PreferenceHelper.getInstance(this@SplashActivity)?.deviceId

                // updateDeviceToken(deviceId, token);
                // String msg = getString(R.string.msg_token_fmt, token);
                // Log.e("token", token)

                //Toast.makeText(Splash.this, token, Toast.LENGTH_SHORT).show();
            })
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                //  Log.e("deepLink", deepLink.toString() + "")
                if (deepLink != null) {
                    try {
                        var afterDecode = URLDecoder.decode(deepLink.toString(), "UTF-8")
                        //  Log.e("deepLink afterDecode>>", "" + afterDecode);
                        afterDecode = afterDecode.substring(afterDecode.lastIndexOf("/"))
                        //  Log.e("afterDecode>>>>", "" + afterDecode);
                        val separated = afterDecode.split("/".toRegex()).toTypedArray()
                        new_PromoCodeLink = separated[1]
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            PromoCodeLink = separated[1]
                            (context as SplashActivity).startActivity(
                                Intent(
                                    context,
                                    PromoCode::class.java
                                )
                                    .putExtra("Code", separated[1])
                                    .putExtra("check", "Refer")
                            )
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.e("Exce", e.toString()) }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val userDeviceID =
            Settings.Secure.getString(
                (context as SplashActivity).getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
        SharedPreferenceVariable.savePreferences(context, KeyConstant.Shar_DeviceID, userDeviceID)
        PreferenceHelper.getInstance(context)?.setUserDeviceId(userDeviceID)
        user = PreferenceHelper.getInstance(context)?.userDetail
        isLogin = PreferenceHelper.getInstance(context)!!.isLogin
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    if (PromoCodeLink == "") {
                        if (isLocationEnable) {
                            getCurrentVersion()
                        }
                    }
                } else {
                    ///requestPermission();

                    ErrorMessage.E("isLocationEnable<><><>" + isLocationEnable)
                    if (isLocationEnable) {
                        alertPopup()
                    }
                }
            } else {
                if (isLocationEnable) {
                    currentLocation
                    if (PromoCodeLink == "") {
                        if (!isLocationEnable) {
                            alertPopupForGps()
                        } else {
                            getCurrentVersion()
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
//        requestLocationPermissions()
    }

    private val location: Unit
        private get() {
            locationRequest = LocationRequest()
                .setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        try {
                            //  Log.e("Location on Splash is ", location.toString())
                            userLat = location.latitude
                            userLang = location.longitude
                            SavedData.saveLatitude(userLat.toString())
                            SavedData.saveLongitude(userLang.toString())
                            PreferenceHelper.getInstance(context)?.lat = userLat.toString() + ""
                            PreferenceHelper.getInstance(context)?.lng = userLang.toString() + ""
                            address
                        } catch (ew: Exception) {
                        }
                    }
                }
            }
        }

    public override fun onResume() {
        try {
            ErrorMessage.E("onResume<><><>" + permissionDialog)

            if (!isLocationEnable && SavedData.getLocationPermission() == "true") {
                alertPopupForGps()
            } else {
                if (checkPermission()) {
                    getCurrentVersion()
                } else {

                    ///requestPermission();
                    /*     if (permissionDialog != null && permissionDialog!!.isShowing == false) {
                             alertPopup()
                         }*/
                    checkForBluthoothPopUpVisible = true
                    alertPopup()


//                    else{
//                        ErrorMessage.E("qwers"+permissionDialog!!.isShowing)
//                        alertPopup()
//                    }
                }
            }
        } catch (e: Exception) {
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (updateDialog != null) {
            updateDialog!!.dismiss()
        }
        if (gpsDialog != null) {
            gpsDialog!!.dismiss()
        }
    }

    override fun onStop() {
        try {
            mHandler!!.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
        }
        super.onStop()
        deleteCache(this)
    }

    override fun onDestroy() {
        try {
            mHandler!!.removeCallbacksAndMessages(null)
            if (updateDialog != null) {
                updateDialog!!.dismiss()
            }
            if (gpsDialog != null) {
                gpsDialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    private fun openNewScreen() {
        //PreferenceHelper.getInstance(context).setAccessToken("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        if (check_redirection) {
            check_redirection = false
            try {

                if (permissionDialog != null) {
                    permissionDialog!!.dismiss()
                }
                startService(Intent(this@SplashActivity, GPSTracker::class.java))

            } catch (e: Exception) {
            }
            PreferenceHelper.getInstance(context)?.openAppFirstTime = true
            if (intent.getStringExtra("title") != null) {
                try {
                    val i = intent
                    val onlineStoreURL = i.getStringExtra("onlineStoreURL")
                    val dealId = i.getStringExtra("dealId")
                    val agentId = i.getStringExtra("agentId")
                    val agentCompanyName = i.getStringExtra("agentCompanyName")
                    val body = i.getStringExtra("body")
                    val ntype = i.getStringExtra("ntype")
                    val title = i.getStringExtra("title")
                    val dealName = i.getStringExtra("dealName")
                    val message = i.getStringExtra("message")
                    val notificationType = i.getStringExtra("notificationType")
                    val type = i.getStringExtra("type")
                    val text = i.getStringExtra("text")
                    val imageUrl = i.getStringExtra("imageUrl")
                    val id = i.getStringExtra("id")

                    val isAdmin = i.getStringExtra("isAdmin")
                    val subscribeKey = i.getStringExtra("subscribeKey")
                    val publishKey = i.getStringExtra("publishKey")
                    val followingStatus = i.getStringExtra("followingStatus")
                    val time = i.getStringExtra("time")


                    intent.removeExtra("title")
                    intent.removeExtra("text")
                    intent.removeExtra("imageUrl")
                    intent.removeExtra("type")
                    intent.removeExtra("id")

                    if (permissionDialog != null) {
                        permissionDialog!!.dismiss()
                    }


                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("text", text)
                    intent.putExtra("imageUrl", imageUrl)
                    intent.putExtra("id", id)
                    intent.putExtra("deals", "33")
                    intent.putExtra("favLat", userLat)
                    intent.putExtra("favLng", userLang)
                    intent.putExtra("favCityName", cityNameStr)
                    intent.putExtra("agentCompanyName", agentCompanyName)
                    intent.putExtra("onlineStoreURL", onlineStoreURL)
                    intent.putExtra("dealId", dealId)
                    intent.putExtra("agentId", agentId)
                    intent.putExtra("body", body)
                    intent.putExtra("dealName", dealName)
                    intent.putExtra("message", message)
                    intent.putExtra("notificationType", notificationType)
                    intent.putExtra("ntype", ntype)
                    intent.putExtra("title", title)
                    intent.putExtra("type", type)

                    intent.putExtra("isAdmin", isAdmin)
                    intent.putExtra("subscribeKey", subscribeKey)
                    intent.putExtra("publishKey", publishKey)
                    intent.putExtra("followingStatus", followingStatus)
                    intent.putExtra("time", time)


                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                }
            } else if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                if (permissionDialog != null) {
                    permissionDialog!!.dismiss()
                }
                val intent = Intent(context, MainActivity::class.java)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("favLat", userLat)
                intent.putExtra("favLng", userLang)
                ErrorMessage.E("favCityName" + if (cityNameStr != null) cityNameStr else "")
                intent.putExtra("favCityName", if (cityNameStr != null) cityNameStr else "")

                intent.putExtra("deals", "3")



                startActivity(intent)
                finish()
            } else {
//                permissionDialog!!.dismiss()
                if (permissionDialog != null) {
                    permissionDialog!!.dismiss()
                }
                val intent = Intent(context, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("favLat", userLat)
                intent.putExtra("favLng", userLang)
                intent.putExtra("deals", "3")
                intent.putExtra("true", "false")
                intent.putExtra("favCityName", cityNameStr)
                intent.putExtra("PromoCodeLink", new_PromoCodeLink)
                // Log.e("PromoCodeLink", ">>$new_PromoCodeLink")
                startActivity(intent)
                finish()
            }
        }
    }


    private fun getCurrentVersion() {
        val pm = this.packageManager
        var pInfo: PackageInfo? = null
        try {
            pInfo = pm.getPackageInfo(this.packageName, 0)
        } catch (e1: PackageManager.NameNotFoundException) {
            e1.printStackTrace()
        }
        currentVersion = pInfo!!.versionName

        if (AppUtil.isNetworkAvailable(context)) {
            try {
//                GetLatestVersion().execute()
                versionChecker.execute()

            } catch (e: Exception) {
            }
        } else {
            AppUtil.showMsgAlert(
                binding!!.ivSplashImage,
                MessageConstant.MESSAGE_INTERNET_CONNECTION
            )

            val handler = Handler()
            handler.postDelayed({
                try {
                    openNewScreen()
                } catch (e: Exception) {
                }
            }, 2000)


        }

    }

//    @SuppressLint("StaticFieldLeak")
//    private inner class GetLatestVersion : AsyncTask<Void?, String?, String?>() {
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//
//        protected override fun doInBackground(vararg p0: Void?): String? {
//            try {
//                val urlOfAppFromPlayStore =
//                    "https://play.google.com/store/apps/details?id=sambal.mydd.app&hl=en_GB"
//                //It retrieves the latest version by scraping the content of current version from play store at runtime
//                val connection = Jsoup.connect(urlOfAppFromPlayStore)
//
//                //Log.e("hello 123123", connection + "");
//                if (connection != null) {
//                    val doc = connection.get()
//                    if (doc!=null && doc.getElementsByClass("htlgb").size >= 5) {
//                        latestVersion = doc.getElementsByClass("htlgb")[6].text()
//                    }
//                }
//            }
//            catch (e: Exception) {
//                e.printStackTrace()
//                latestVersion = currentVersion
//                return null
//            }
//            catch (e: NoClassDefFoundError) {
//            }
//            return latestVersion
//        }
//
//        override fun onPostExecute(version: String?) {
//            super.onPostExecute(version)
//            try {
//                if (latestVersion != null) {
//                    val current = java.lang.Double.valueOf(currentVersion)
//                    val currentVersion = current.toString()
//                    val latest = java.lang.Double.valueOf(latestVersion)
//                    myLog!!.logE("currentVersion", "$currentVersion latestVersion $latestVersion")
//                    if (current >= latest) {
//                        appExecute()
//                    } else {
//                        showUpdateDialog()
//                        myLog!!.logE("currentVersion",
//                            "$currentVersion latestVersion $latestVersion")
//                    }
//                } else {
//                    myLog!!.logD("latestVersion", "latestVersion null")
//                    appExecute()
//                }
//            } catch (e: Exception) {
//            }
//        }
//    }


    @SuppressLint("StaticFieldLeak")
    inner class GetLatestVersion : AsyncTask<Void?, String?, String?>() {
        // Flag to keep track of whether the task is canceled
        private var isCancelled = false

        fun cancelTask() {
            isCancelled = true
            cancel(true)
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val urlOfAppFromPlayStore =
                    "https://play.google.com/store/apps/details?id=sambal.mydd.app&hl=en_GB"
                val connection = Jsoup.connect(urlOfAppFromPlayStore)

                if (isCancelled) {
                    return null // Task is canceled
                }

                if (connection != null) {
                    val doc = connection.get()
                    if (isCancelled) {
                        return null // Task is canceled
                    }

                    if (doc != null && doc.getElementsByClass("htlgb").size >= 5) {
                        latestVersion = doc.getElementsByClass("htlgb")[6].text()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
//                latestVersion = currentVersion
//                return null
            } catch (e: NoClassDefFoundError) {
            }
//            ErrorMessage.E("latestVersi"+latestVersion)

//            return latestVersion

            ErrorMessage.E("latestVersi" + if (latestVersion != null) latestVersion else "")

//                                versionChecker.cancelTask()

            return if (latestVersion != null) latestVersion else ""
        }

        override fun onPostExecute(version: String?) {
            super.onPostExecute(version)
            // Check if the task is canceled before proceeding
            if (isCancelled) {
                return
            }

            try {
                if (latestVersion != null && latestVersion!!.isNotEmpty() && latestVersion != "") {
                    val current = java.lang.Double.valueOf(currentVersion)
                    val currentVersion = current.toString()
                    val latest = java.lang.Double.valueOf(latestVersion)
                    myLog!!.logE("currentVersion", "$currentVersion latestVersion $latestVersion")
                    if (current >= latest) {
                        appExecute()
                    } else {
                        showUpdateDialog()
                        myLog!!.logE(
                            "currentVersion",
                            "$currentVersion latestVersion $latestVersion"
                        )
                    }
                } else {
                    myLog!!.logD("latestVersion", "latestVersion null")

                    appExecute()
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Show Dialog....
     */
    private fun showUpdateDialog() {
        updateDialog = Dialog(this)
        updateDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        updateDialog!!.setContentView(R.layout.popup_update_app)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(updateDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        updateDialog!!.window!!.attributes = lp
        val contentText = updateDialog!!.findViewById<TextView>(R.id.popup_content)
        contentText.text = "A new version of DD Points is available, please update."
        val btnSkip = updateDialog!!.findViewById<TextView>(R.id.popup_skip_btn)
        val btnUpate = updateDialog!!.findViewById<TextView>(R.id.popup_update_btn)
        updateDialog!!.setCancelable(false)
        updateDialog!!.show()
        try {
            btnUpate.setOnClickListener { // launch new intent instead of loading fragment
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=sambal.mydd.app")
                    )
                )
                updateDialog!!.dismiss()
            }
            btnSkip.setOnClickListener {
                updateDialog!!.dismiss()
                appExecute()
            }
        } catch (e: Exception) {
        }
    }

    private fun appExecute() {
        // TODO : code to login screen...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                location
            } else {
                //Request Location Permission
                if (!isLocationEnable) {
                    // checkLocationPermission();
                    ErrorMessage.E("else is working>>>")
                }
            }
        } else {
            location
        }
        try {
            mHandler = Handler()
            mHandler!!.postDelayed({
                if (PromoCodeLink == "") {
                    openNewScreen()
                }
            }, SPLASH_DISPLAY_LENGTH.toLong())
        } catch (w: Exception) {
        }
    }

    private fun requestPermission() {

        ErrorMessage.E("countStoragePermission" + countStoragePermission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_MEDIA_IMAGES,
                    permission.BLUETOOTH_SCAN,
                    permission.POST_NOTIFICATIONS,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.BLUETOOTH_SCAN,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result3 = ContextCompat.checkSelfPermission(
                applicationContext, permission.READ_MEDIA_IMAGES
            )
            val result5 = ContextCompat.checkSelfPermission(
                applicationContext, permission.BLUETOOTH_SCAN
            )

            val result6 = ContextCompat.checkSelfPermission(
                applicationContext, permission.POST_NOTIFICATIONS
            )


            result3 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED &&
                    result6 == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val result3 = ContextCompat.checkSelfPermission(
                applicationContext, permission.READ_EXTERNAL_STORAGE
            )
            val result4 = ContextCompat.checkSelfPermission(
                applicationContext, permission.WRITE_EXTERNAL_STORAGE
            )
            val result5 = ContextCompat.checkSelfPermission(
                applicationContext, permission.BLUETOOTH_SCAN
            )

            result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED

        } else {
            val result3 = ContextCompat.checkSelfPermission(
                applicationContext, permission.READ_EXTERNAL_STORAGE
            )
            val result4 = ContextCompat.checkSelfPermission(
                applicationContext, permission.WRITE_EXTERNAL_STORAGE
            )
            result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        location
                    }
                    if (ContextCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_BACKGROUND_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                    } else {
                        requestLocationPermissions()
                    }

                    try {
                        if (permissionDialog != null) {
                            permissionDialog!!.dismiss()
                        }
                        startService(Intent(this@SplashActivity, GPSTracker::class.java))
                    } catch (e: Exception) {
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return
            }

            PERMISSION_REQUEST_CODE -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    ErrorMessage.E("PERMISSION_REQUEST_CODE>>>>>if<><><>1")
                    try {
                        if (!isLocationEnable) {
//                            alertPopupForGps()
                        } else {
                            currentLocation
                            if (checkPermission()) {
                                if (PromoCodeLink == "") {
                                    if (!isLocationEnable) {
                                        alertPopupForGps()
                                    } else {
                                        getCurrentVersion()
                                    }
                                }
                            } else {
                                ///requestPermission();
                                alertPopup()
                            }
                        }
                    } catch (e: Exception) {
                    }
                } else {
                    openNewScreen()
//                    alertPopup()
                }
            } else {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    ErrorMessage.E("PERMISSION_REQUEST_CODE>>>>>if")
                    try {
                        if (!isLocationEnable) {
//                            alertPopupForGps()
                        } else {
                            currentLocation
                            if (checkPermission()) {
                                if (PromoCodeLink == "") {
                                    if (!isLocationEnable) {
                                        alertPopupForGps()
                                    } else {
                                        getCurrentVersion()
                                    }
                                }
                            } else {
                                ///requestPermission();
                                alertPopup()
                            }
                        }
                    } catch (e: Exception) {
                    }
                } else {

                    openNewScreen()

//                    alertPopup()
                }
            }

            REQUEST_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start scanning here
                    ErrorMessage.E("if is working on that >>>>>")

                } else {
                    ErrorMessage.E("if is not working on that >>>>>")
                    // Permission denied, handle accordingly (e.g., show a message or disable Bluetooth-related features)
                }


            }


        }


        /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                   ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
               }
           }
   */

    }


    private val address: Unit
        private get() {
            val geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address>? = null
            try {
                addresses = geocoder.getFromLocation(userLat, userLang, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                if (addresses != null && addresses.size > 0) {
                    val cityName = addresses[0].getAddressLine(0)
                    val stateName = addresses[0].getAddressLine(1)
                    val countryName = addresses[0].getAddressLine(2)
                    val city = addresses[0].locality
                    cityNameStr = city ?: cityName
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    /*=================================================*/
    private val isLocationEnable: Boolean
        private get() {
            val manager = this@SplashActivity.getSystemService(LOCATION_SERVICE) as LocationManager
            var statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!statusOfGPS) {
                statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
            return statusOfGPS
        }

    private fun alertPopupForGps() {

        try {
            if (gpsAlertDialog != null) {
                gpsAlertDialog!!.dismiss()
            }

//            ErrorMessage.E("mayu"+gpsAlertDialog!!.isShowing)

            gpsAlertDialog = Dialog(this@SplashActivity)
            gpsAlertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            gpsAlertDialog!!.setContentView(R.layout.popup_common)
            gpsAlertDialog!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(gpsAlertDialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            gpsAlertDialog!!.window!!.attributes = lp
            val tvTitle = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_content_inbold)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Location Access Disabled"
            val contentText = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_content)
            contentText.text = "In order to search nearby deals we need your location"
            val btnNo = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_no_btn)
            btnNo.text = "Cancel"
            val btnOk = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_yes_btn)
            btnOk.text = "Open Settings"
            val view = gpsAlertDialog!!.findViewById<View>(R.id.view_btw_btn)
            view.visibility = View.VISIBLE
            //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
            gpsAlertDialog!!.setCancelable(true)
            gpsAlertDialog!!.show()
            try {
                btnOk.setOnClickListener {
                    try {
                        gpsAlertDialog!!.dismiss()
                    } catch (e: Exception) {
                    }
                    //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        MY_PERMISSIONS_REQUEST_LOCATION
                    )
                }
                btnNo.setOnClickListener {
                    try {
                        gpsAlertDialog!!.dismiss()
                        // after Grager discussion it's comment.
                        // alertPopupForGps();
                        SavedData.saveLocationPermission("false")

                        // changrs //
                        alertPopup()

                        // changes 00
//                        getCurrentVersion()
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }

        } catch (e: Exception) {
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        imageReturnedIntent: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        ErrorMessage.E("gfgfdgfgfdf<><><>" + requestCode + "<><><><>" + MY_PERMISSIONS_REQUEST_LOCATION + "<><><><>" + RESULT_OK)
        if (resultCode == RESULT_OK && requestCode != 85) {

            if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

                if (isLocationEnable) {
//                    permission()
                    // changrs //
                    alertPopup()
                } else {
                    alertPopupForGps()
                }
            }
        }

    }

    private fun permission() {
        try {
            if (permissionsManager!!.hasAllPermissions(this@SplashActivity, mPermission)) {
                if (isLocationEnable) {
                    location
                    //  getCurrentVersion();
                    if (checkPermission()) {
                        if (PromoCodeLink == "") {
                            if (!isLocationEnable) {
                                alertPopupForGps()
                            } else {
                                getCurrentVersion()
                            }
                        }
                    } else {
                        ///requestPermission();
                        alertPopup()
                    }
                } else {
                    alertPopupForGps()
                }
            } else {
                permissionsManager!!.requestPermissionsIfNecessaryForResult(this,
                    mPermission,
                    object : PermissionsResultAction() {
                        override fun onGranted() {
                            if (isLocationEnable) {
                                location
                                //getCurrentVersion();
                                if (checkPermission()) {
                                    if (PromoCodeLink == "") {
                                        if (!isLocationEnable) {
                                            alertPopupForGps()
                                        } else {
                                            getCurrentVersion()
                                        }
                                    }
                                } else {
                                    ///requestPermission();
                                    alertPopup()
                                }
                            } else {
                                alertPopupForGps()
                            }
                        }

                        override fun onDenied(permission: String?) {}
                    })
            }
        } catch (e: Exception) {
        }
    }

    // getCurrentVersion();
    private val currentLocation: Unit
        private get() {
            try {
                val locationRequest = LocationRequest()
                locationRequest.interval = 10000
                locationRequest.fastestInterval = 3000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                location_permission = if (ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //  Log.e("getCurrentLocation", "<><>")
                    true
                } else {
                    true
                }
                LocationServices.getFusedLocationProviderClient(this@SplashActivity)
                    .requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            LocationServices.getFusedLocationProviderClient(this@SplashActivity)
                                .removeLocationUpdates(this)
                            if (locationResult != null && locationResult.locations.size > 0) {
                                val latestlocIndex = locationResult.locations.size - 1
                                userLat = locationResult.locations[latestlocIndex].latitude
                                userLang = locationResult.locations[latestlocIndex].longitude
                                SavedData.saveLatitude(userLat.toString())
                                SavedData.saveLongitude(userLang.toString())
                                PreferenceHelper.getInstance(context)?.lat = userLat.toString() + ""
                                PreferenceHelper.getInstance(context)?.lng =
                                    userLang.toString() + ""
                                getCurrentVersion()
                                address
                            } else {
                                getCurrentVersion()
                            }
                        }
                    }, Looper.getMainLooper())
                // getCurrentVersion();
            } catch (e: Exception) {
                ErrorMessage.E("Exception>>$e")
            }
        }

    private fun alertPopup() {

        ErrorMessage.E("checkForBluthoothPopUpVisible<><><><>" + checkForBluthoothPopUpVisible + "<><><><>" + countStoragePermission)


        if (checkForBluthoothPopUpVisible) {
//        if(!SavedData.getCountNearByDevicePermission().equals("3")){
            if (countStoragePermission < 3) {

                checkForBluthoothPopUpVisible = false;

                try {
                    if (permissionDialog != null) {
                        permissionDialog!!.dismiss()
                    }

                    permissionDialog = Dialog(this)
                    permissionDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            permissionDialog!!.setContentView(R.layout.popup_common)

                    permissionDialog!!.setContentView(R.layout.near_by_devices_permission_popup)

                    permissionDialog!!.setCanceledOnTouchOutside(false)
                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(permissionDialog!!.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                    permissionDialog!!.window!!.attributes = lp
                    val tvTitle =
                        permissionDialog!!.findViewById<TextView>(R.id.popup_content_inbold)
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = "Allow Permissions"
                    val contentText = permissionDialog!!.findViewById<TextView>(R.id.popup_content)
                    contentText.setTextColor(resources.getColor(R.color.black))

                    val msgPart1 = getString(R.string.near_by_permission_msg1)
                    val appName = getString(R.string.app_name)
                    val msgPart2 = getString(R.string.near_by_permission_msg2)

                    val popupMsg = "$msgPart1 $appName $msgPart2"
                    contentText.setText(popupMsg)

//            contentText.setText(R.string.near_by_permission_msg1)
                    val btnNo = permissionDialog!!.findViewById<Button>(R.id.popup_no_btn)
//            btnNo.text = "Cancel"
                    val btnOk = permissionDialog!!.findViewById<Button>(R.id.popup_yes_btn)
//            btnOk.text = "OK"
//            val view = permissionDialog!!.findViewById<View>(R.id.view_btw_btn)
//            view.visibility = View.VISIBLE
                    val do_not_allow =
                        permissionDialog!!.findViewById<Button>(R.id.popup_do_not_allow_btn)

                    try {
                        btnOk.setOnClickListener {
                            try {
                                if (permissionDialog != null) {
                                    permissionDialog!!.dismiss()
                                }
                            } catch (e: Exception) {
                            }


//                    checkForBluthoothPopUpVisible=true;
                            if (countStoragePermission < 3) {
//                        ErrorMessage.E("qwert"+countStoragePermission)

                                requestPermission()
                            } else {
//                        ErrorMessage.E("qwer"+countStoragePermission)

                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                        }
                        btnNo.setOnClickListener {
                            try {
                                //AppUtil.showMsgAlert(binding.ivSplashImage,"Please Allow Permissions To Open The App");

                                // update code
                                //                     ErrorMessage.T(context, "Please Allow Permissions To Open The App")
//                        checkForBluthoothPopUpVisible=true;

                                if (permissionDialog != null) {
                                    permissionDialog!!.dismiss()
                                }
                                permissionRequestRuntime();

//                        openNewScreen()


                            } catch (e: Exception) {
                            }
                        }

                        do_not_allow.setOnClickListener {
                            try {

                                SavedData.saveCountNearByDevicePermission("3")
//                        openNewScreen()

                                if (permissionDialog != null) {
                                    permissionDialog!!.dismiss()
                                }
                                permissionRequestRuntime();

                            } catch (e: Exception) {
                            }
                        }
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }
                countStoragePermission++
                SavedData.saveCountNearByDevicePermission(countStoragePermission.toString())


                if (permissionDialog != null) {
                    permissionDialog!!.setCancelable(false)
                    permissionDialog!!.show()
                }
            } else {
                openNewScreen()
            }
        } else {
            openNewScreen()
        }

    }


    private fun permissionRequestRuntime() {

        ErrorMessage.E("countStoragePermission" + countStoragePermission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_MEDIA_IMAGES,
                    permission.POST_NOTIFICATIONS,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.ACCESS_FINE_LOCATION,
                    " "
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private fun deleteCache(context: Context) {
            try {
                val dir = context.cacheDir
                deleteDir(dir)
            } catch (e: Exception) {
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }

        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    private val REQUEST_PERMISSIONS = 123

    private fun requestLocationPermissions() {
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             if (ContextCompat.checkSelfPermission(this, permission.ACCESS_BACKGROUND_LOCATION)
                 != PackageManager.PERMISSION_GRANTED
             ) {
                 ActivityCompat.requestPermissions(
                     this, arrayOf(
                         permission.ACCESS_FINE_LOCATION,
                         permission.ACCESS_BACKGROUND_LOCATION
                     ),
                     REQUEST_PERMISSIONS
                 )
             } else {
                 // Permissions already granted, start scanning here
             }
         }
         else {*/
        // For devices below API 29, you only need ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS
            )
        } else {
            // Permissions already granted, start scanning here
        }
        /* }*/
    }

    /* override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<String?>,
         grantResults: IntArray
     ) {
         if (requestCode == REQUEST_PERMISSIONS) {
             if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 // Permission granted, start scanning here
             } else {
                 // Permission denied, handle accordingly (e.g., show a message or disable Bluetooth-related features)
             }
         }
     }*/

// commented line for custum for gps enable is after request permission issue
//    749
//    776
}