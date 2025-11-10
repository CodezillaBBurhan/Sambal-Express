package sambal.mydd.app

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.MTPeripheral
import com.minew.beaconplus.sdk.enums.BluetoothState
import com.minew.beaconplus.sdk.enums.ConnectionStatus
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.exception.MTException
import com.minew.beaconplus.sdk.frames.IBeaconFrame
import com.minew.beaconplus.sdk.interfaces.ConnectionStatueListener
import com.minew.beaconplus.sdk.interfaces.GetPasswordListener
import com.minew.beaconplus.sdk.interfaces.OnBluetoothStateChangedListener
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import sambal.mydd.app.FCMUtils.MyFirebaseMessagingService
import sambal.mydd.app.activity.*
import sambal.mydd.app.activity.reward_club.RewardClubActivity
import sambal.mydd.app.activity.viewAndEarn.ViewAndEarnActivity
import sambal.mydd.app.adapter.AdapterLeftMenu
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.beans.MenuList
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.callback.RefreshFragmentCallback
import sambal.mydd.app.check_internet.ConnectivityReceiver.ConnectivityReceiverListener
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.constant.UrlConstant
import sambal.mydd.app.databinding.ActivitynavigationaldrawerBinding
import sambal.mydd.app.fragment.HomeFragment
import sambal.mydd.app.fragment.chat.ChatLocationFavourite
import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.models.iBeaconModel.iBeacon
import sambal.mydd.app.runtimePermission.PermissionsManager
import sambal.mydd.app.runtimePermission.PermissionsResultAction
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


//class MainActivity : AppCompatActivity(),LifecycleObserver, View.OnClickListener, RefreshFragmentCallback,
class MainActivity : AppCompatActivity(), View.OnClickListener, RefreshFragmentCallback,
    Animation.AnimationListener, ChatHistoryCallback, ConnectivityReceiverListener {
    private var mMtCentralManager: MTCentralManager? = null
    private var showSingleTimePopup = true
    private var binding: ActivitynavigationaldrawerBinding? = null
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val mPermission = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION,
        permission.BLUETOOTH
    )
    var isLocationClicked = false
    var animSlideDown: Animation? = null
    var animSlideUp: Animation? = null
    var gpsAlertDialog: Dialog? = null
    var permissionsManager: PermissionsManager? = null
    var id: String? = null
    var mList: ArrayList<MenuList>? = ArrayList()
    var intent1: Intent? = null
    private var context: Context? = null
    private var myLog: MyLog? = null
    private val PERMISSION_REQUEST_CODE = 1111
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var camPermDialog: Dialog? = null
    val CAM_PERM_REQUEST_CODE = 1000
    var fusedLocationClient: FusedLocationProviderClient? = null
    var homeSignUpFragment: HomeFragment? = null
    var chatLocationFavourite: ChatLocationFavourite? = null
    var mtPeripherals: MutableList<IBeaconFrame> = ArrayList()
    var iBeaconDataList: ArrayList<iBeacon> = ArrayList()
    var sound // = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            : Uri? = null
    var city = ""

    @RequiresApi(Build.VERSION_CODES.M)
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                   /* try {
                        StatusBarcolor.setStatusbarColor(this@MainActivity, "white")
                    } catch (_: Exception) {
                    }*/
                    toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    binding!!.mainActivity.appbar.premierLogo.visibility=View.VISIBLE
                    binding!!.mainActivity.appbar.ivHumburger.setColorFilter(
                        resources.getColor(R.color.black, theme) // or ContextCompat.getColor(context, R.color.white)
                    )
                    llLocationLayout!!.visibility = View.GONE
                    llSearch!!.visibility = View.GONE
                    supportFragmentManager.beginTransaction().show(homeSignUpFragment!!)
                        .commitAllowingStateLoss()
                    try {
                        if (chatLocationFavourite != null) {
                            supportFragmentManager.beginTransaction().hide(
                                chatLocationFavourite!!
                            ).commitAllowingStateLoss()
                        }
                    } catch (e: Exception) {
                        Log.e("Exception", "MainActivity$e")
                    }
                    binding!!.mainActivity.appbar.ivNoti.setImageDrawable(resources.getDrawable(R.drawable.bell))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tab_chat -> {

                  /*  try {
                       // StatusBarcolor.setStatusbarColor(this@MainActivity, "")
                        if (chatLocationFavourite == null) {
                            chatLocationFavourite = ChatLocationFavourite()

                            supportFragmentManager.beginTransaction()
                                .add(binding!!.mainActivity.frame.id, chatLocationFavourite!!)
                                .commitAllowingStateLoss()
                        }
                    } catch (_: Exception) {
                    }
                    supportFragmentManager.beginTransaction().hide(homeSignUpFragment!!)
                        .commitAllowingStateLoss()
                    supportFragmentManager.beginTransaction().show(chatLocationFavourite!!)
                        .commitAllowingStateLoss()
                    toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    tvLocation!!.setTextColor(resources.getColor(R.color.black))
                    binding!!.mainActivity.appbar.ivHumburger.setColorFilter(
                        resources.getColor(R.color.black, theme) // or ContextCompat.getColor(context, R.color.white)
                    )
                    llLocationLayout!!.visibility = View.VISIBLE
                    llSearch!!.visibility = View.VISIBLE
                    binding!!.mainActivity.appbar.tvNow.setTextColor(resources.getColor(R.color.black))
                    binding!!.mainActivity.appbar.premierLogo.visibility=View.GONE
                    toolbar!!.visibility = View.VISIBLE
                    toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    llSearch!!.visibility = View.VISIBLE
                    binding!!.mainActivity.appbar.appbar.visibility = View.VISIBLE
                    binding!!.mainActivity.appbar.tvNow.visibility = View.VISIBLE
                    binding!!.mainActivity.appbar.ivNoti.setImageDrawable(this.resources.getDrawable(R.drawable.bell))
                    val intent = Intent("Refresh_Chat_Fragment")
                    LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)
                    return@OnNavigationItemSelectedListener true*/
                    startActivity(Intent(this@MainActivity, MY_PromotionActivity::class.java))
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activitynavigationaldrawer)
        LocalBroadcastManager.getInstance(this@MainActivity)
            .registerReceiver(onNotice_refresh, IntentFilter("refresh_Page"))
        context = this
        permissionsManager = PermissionsManager.instance
        binding!!.mainActivity.navView.setItemIconTintList(null);
        // life cycler initilaise
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!![key]
                Log.d("MainActivityMMMMMM: ", "Key: $key Value: $value")
            }
            backgroundPushNotification(intent.extras!!)
        }


        myLog = MyLog()
        myLog!!.logE("fcm token", PreferenceHelper.getInstance(context)?.fcmToken)
        initToolBar()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission()
            }
        }
        if (mList != null) {
            mList!!.clear()
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!ensureBleExists()) finish()
            requiredPermissions
            initManager()
            initListener()
        }*/
        init()
        initJobTabs()


        if (getIntent().extras != null && getIntent().hasExtra("favLat")) {
            userLat = getIntent().getDoubleExtra("favLat", 0.0)
            userLang = getIntent().getDoubleExtra("favLng", 0.0)
            address = getIntent().getStringExtra("favCityName")
            tvLocation!!.text = getIntent().getStringExtra("favCityName")
        }
        val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Dismiss Notification
        notificationmanager.cancel(0)
        try {
            var `in` = getIntent()
            val data = `in`!!.data
            val protocol = data!!.scheme
            val server = data.authority
            Log.e("Serve", server!!)
            val path = data.path
            val str = path!!.split("/".toRegex()).toTypedArray()
            try {
                id = str[1]
            } catch (e: Exception) {

            }
            if (server.equals("home", ignoreCase = true)) {
                if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                    supportFragmentManager.beginTransaction().show(homeSignUpFragment!!)
                        .commitAllowingStateLoss()
                    supportFragmentManager.beginTransaction().hide(chatLocationFavourite!!)
                        .commitAllowingStateLoss()
                } else {
                    startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                    finish()
                }
            } else if (server.equals("dealdetails", ignoreCase = true)) {
                intent1 = Intent(this, LatestProductDetails::class.java)
                intent1!!.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, id)
                intent1!!.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, "")
                startActivity(intent1)
                `in` = null
                setIntent(null)
            } else if (server.equals("agentdetails", ignoreCase = true)) {
                intent1 = Intent(this, New_AgentDetails::class.java)
                intent1!!.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, id)
                intent1!!.putExtra("direct", "true")
                startActivity(intent1)
                `in` = null
                setIntent(null)
            }
        } catch (e: Exception) {
        }
        try {
            val i = getIntent()
            val title = i.getStringExtra("title")
            val text = i.getStringExtra("text")
            val imageUrl = i.getStringExtra("imageUrl")
            val type = i.getStringExtra("type")
            val id = i.getStringExtra("id")
            val agentId = i.getStringExtra("agentId")

            ErrorMessage.E("AgentId" + agentId)

            if (type.equals("NOTIF_TYPE_PRODUCT_PROMOTED", ignoreCase = true)) {
                val intent = Intent(context, LatestProductDetails::class.java)
                intent.putExtra("direct", "true")
                intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, id + "")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
                context?.startActivity(intent)
            } else if (type.equals("NOTIF_TYPE_MERCHANT_PROMOTED", ignoreCase = true)) {
                val intent = Intent(this, LatestProductDetails::class.java)
                intent.putExtra("direct", "true")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, id + "")
                startActivity(intent)
            }
        } catch (e: Exception) {
        }
        try {
            if (getIntent().extras != null && getIntent().hasExtra("isLocationSetFromSearch")) {
                userLat = getIntent().getDoubleExtra("latitude", 0.0)
                userLang = getIntent().getDoubleExtra("longitude", 0.0)
                distance = getIntent().getStringExtra("distance")
                address = getIntent().getStringExtra("locationName")
                tvLocation!!.text = getIntent().getStringExtra("locationName")
            }
            importAllMerchantsBranches()
        } catch (e: Exception) {
        }
        try {
            if (userLat == 0.0 && userLang == 0.0) {

                ErrorMessage.E("12345")
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)


                if (isLocationEnable && !gPSLocation) {
                    currentLocation
                    location
                }
            } else {
                getLocationAddress(userLat, userLang)
            }
        } catch (e: Exception) {
        }
        try {
            GPSTracker.requestSingleUpdate(this@MainActivity, object : GPSTracker.LocationCallback {
                override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                    if (location != null) {
                        userLat = location.latitude.toDouble()
                        userLang = location.longitude.toDouble()
                        getLocationAddress(location.latitude.toDouble(),
                            location.longitude.toDouble())
                    }
                }
            })
        } catch (e: Exception) {
        }


        /* if (ContextCompat.checkSelfPermission(this@MainActivity, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
             // Permission granted, perform your foreground task.
             ErrorMessage.E("if is working>>>>")
             val isMyServiceRunning: Boolean = isServiceRunning(this, LocationService::class.java)
             ErrorMessage.E("isMyServiceRunning>>>$isMyServiceRunning")
             if (!isMyServiceRunning) {
                 val serviceIntent = Intent(this, LocationService::class.java)
                 startService(serviceIntent)
             }
         }*/
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            val runningServices = manager.getRunningServices(Int.MAX_VALUE)
            if (runningServices != null) {
                for (serviceInfo in runningServices) {
                    if (serviceClass.name == serviceInfo.service.className) {
                        // The service is running
                        return true
                    }
                }
            }
        }

        // The service is not running
        return false
    }

    private val isLocationEnable: Boolean
        private get() {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            var statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!statusOfGPS) {
                statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
            return statusOfGPS
        }

    private fun permission() {
        if (permissionsManager!!.hasAllPermissions(this, mPermission)) {
            if (isLocationEnable) {
                location
            } else {
                alertPopupForGps()
            }
        } else {
            permissionsManager!!.requestPermissionsIfNecessaryForResult(
                this,
                mPermission,
                object : PermissionsResultAction() {
                    override fun onGranted() {
                        if (isLocationEnable) {
                            location
                        } else {
                            alertPopupForGps()
                        }
                    }

                    override fun onDenied(permission: String?) {}
                })
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        imageReturnedIntent: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        if (resultCode == RESULT_OK && requestCode != 85) {
            if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
                if (isLocationEnable) {
                    permission()
                } else {
                    alertPopupForGps()
                }
            }
        } else if (requestCode == 2) {
            try {
                val intent = Intent("Location")
                intent.putExtra("Lat", imageReturnedIntent!!.getStringExtra("latitude"))
                intent.putExtra("Long", imageReturnedIntent.getStringExtra("longitude"))
                intent.putExtra("location", imageReturnedIntent.getStringExtra("locationName"))
                LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)
                SavedData.saveLatitude(imageReturnedIntent.getStringExtra("latitude"))
                SavedData.saveLongitude(imageReturnedIntent.getStringExtra("longitude"))
                address = imageReturnedIntent.getStringExtra("locationName")
                tvLocation!!.text = address
                binding!!.mainActivity.navView.selectedItemId = R.id.tab_chat
            } catch (e: Exception) {
                Log.e("EXCEPTION", ">>$e")
            }
        } else if (isLocationEnable) {
            permission()
        }
    }

    private fun init() {
        llSearch = binding!!.mainActivity.llSearch
        binding!!.mainActivity.navView.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
        drawerLayout = binding!!.drawerLayout
        binding!!.mainActivity.appbar.ivNoti.setOnClickListener(this)
        rvMain = binding!!.rvMain
        tvName = binding!!.tvMainName
        tvNo = binding!!.tvMainnumber
        tvEmail = binding!!.tvMainEmail
        ivImage = binding!!.profileImage
        animSlideDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_down
        )
        animSlideUp = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_up
        )
        binding!!.mainActivity.appbar.ivHumburger.setOnClickListener {
//            if (AppUtil.isNetworkAvailable(context)) {

            if (!drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.openDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            } else if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.closeDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            }

//            }

        }
        binding!!.btnJoin.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
        }
    }

    private fun initToolBar() {
        toolbar = binding!!.mainActivity.appbar.toolbar
        setSupportActionBar(toolbar)
        llLocationLayout = binding!!.mainActivity.appbar.llLocationLayout
        llLocationLayout!!.visibility = View.GONE
        llLocationLayout!!.setOnClickListener(this)
        tvLocation = binding!!.mainActivity.appbar.tvToolbarLocation
        binding!!.mainActivity.ivDdCard.setOnClickListener(this)
        toolbar!!.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_location_layout -> {
                closeDrawer()
                if (SavedData.getLocationPermission() == "true") {
                    if (!isLocationEnable) {
                        isLocationClicked = true
                        alertPopupForGps()
                    } else {
                        isLocationClicked = false
                        val intent1 = Intent(this@MainActivity, SelectLocationActivity::class.java)
                        intent1.putExtra("clickedBottomTab", "12")
                        startActivityForResult(intent1, 2)
                    }
                } else {
                    isLocationClicked = false
                    val intent1 = Intent(this@MainActivity, SelectLocationActivity::class.java)
                    intent1.putExtra("clickedBottomTab", "12")
                    startActivityForResult(intent1, 2)
                }
            }

            R.id.ivDdCard -> startActivity(Intent(this@MainActivity, ActivitLinkCard::class.java))
            R.id.ivMenu -> if (!drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.openDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            } else if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.closeDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            }
            R.id.ivNoti -> try {
                if (AppUtil.isNetworkAvailable(context)) {
                    if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                        startActivity(Intent(this@MainActivity, NewNotification::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                    }
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.mainActivity.appbar.ivNoti,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
            }
        }
    }



    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            // Close the drawer if it is open
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            // Get the list of fragments currently added to the FragmentManager
            val fragmentManager = supportFragmentManager
            val fragmentList = fragmentManager.fragments
            for (fragment in fragmentList) {
                if (fragment.isVisible) {
                    when (fragment) {
                        // If the visible fragment is HomeFragment, show exit confirmation
                        is HomeFragment -> {
                            exitPopup("Are you sure you want to exit?")
                        }
                        // If the visible fragment is ChatLocationFavourite, switch back to HomeFragment
                        is ChatLocationFavourite -> {
                            try {
                                // Set status bar color and change UI elements as needed
                             //   StatusBarcolor.setStatusbarColor(this@MainActivity, "")
                                binding!!.mainActivity.navView.selectedItemId = R.id.tab_home
                            } catch (e: Exception) {
                                e.printStackTrace()  // Log any potential exception
                            }
                            // Update toolbar background and visibility of other UI elements
                            toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                            llLocationLayout!!.visibility = View.GONE
                            llSearch!!.visibility = View.GONE

                            // Hide the current chat fragment and show the home fragment
                            supportFragmentManager.beginTransaction().hide(fragment)
                                .commitAllowingStateLoss()
                            supportFragmentManager.beginTransaction().show(homeSignUpFragment!!)
                                .commitAllowingStateLoss()
                        }
                        else -> super.onBackPressed() // Default behavior for other fragments
                    }
                    return
                }
            }
        }
    }


    private fun exitPopup(msg: String) {
        val dialog1 = Dialog(this@MainActivity)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = msg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                try {
                    dialog1.dismiss()
                } catch (_: Exception) {
                }
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(a)
                finish()
            }
            btnNo.setOnClickListener {
                try {
                    dialog1.dismiss()
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
    }

    override fun onRefreshFragment(category: String, catId: String) {
        try {
            val fm = this.supportFragmentManager
            fm.popBackStack("categoryFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } catch (_: Exception) {
        }
        //tvCategoryName.setText(category);
        categoryId = catId.toInt()
    }

    override fun onResume() {
        super.onResume()
        closeDrawer()
        try {
            if (isLocationEnable) {
                location
            } else {
                ErrorMessage.E("getAllData<><>11111"+SavedData.getAllData())
                ErrorMessage.E("IntentConstant.countGiftVoucher"+IntentConstant.countGiftVoucher)
                if(IntentConstant.countGiftVoucher==0) {
                    myProfile
                }
            }
        } catch (_: Exception) {
        }
        try {
            StatusBarcolor.setStatusbarColor(this@MainActivity, "home");
            DealDioApplication.instance!!.setConnectivityListener(this@MainActivity)
        } catch (_: Exception) {
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(0)
        if (distance == null) {
            distance = "5"
        }

        // ATTENTION: This was auto-generated to handle app links.
        try {
            val appLinkIntent = getIntent()
            val appLinkAction = appLinkIntent.action
            val appLinkData = appLinkIntent.data
            if (appLinkData != null) {
                val id = appLinkData.lastPathSegment
            }
        } catch (_: Exception) {}
        //Todo deeplinking code
        if (tvLocation!!.text.toString()
                .trim { it <= ' ' }.length == 0 || tvLocation!!.text.toString() == ""
        ) {
            tvLocation!!.text = "Location"
            location
        }
        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
            binding!!.llHeaderProfile.visibility = View.VISIBLE
            binding!!.btnJoin.visibility = View.GONE
        } else {
            binding!!.llHeaderProfile.visibility = View.GONE
            binding!!.btnJoin.visibility = View.VISIBLE
        }
        val fragmentManager = supportFragmentManager
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment.isVisible) {
                if (fragment is HomeFragment) {
                    try {
                       // StatusBarcolor.setStatusbarColor(this@MainActivity, "white")
                        binding!!.mainActivity.navView.selectedItemId = R.id.tab_home
                    } catch (_: Exception) {
                    }
                    llLocationLayout!!.visibility = View.GONE
                    llSearch!!.visibility = View.GONE
                    visibleFragment = false
                } else if (fragment is ChatLocationFavourite) {
                    try {
                      //  StatusBarcolor.setStatusbarColor(this@MainActivity, "")
                        binding!!.mainActivity.navView.selectedItemId = R.id.tab_chat
                    } catch (_: Exception) {
                    }
                    toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    binding!!.mainActivity.appbar.ivHumburger.visibility = View.VISIBLE
                    tvLocation!!.setTextColor(resources.getColor(R.color.black))
                    llLocationLayout!!.visibility = View.VISIBLE
                    llSearch!!.visibility = View.VISIBLE
                    visibleFragment = true
                } else {
                    llLocationLayout!!.visibility = View.GONE
                    llSearch!!.visibility = View.GONE
                }
                break
            }
        }
    }//AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);

    //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
    private val myProfile: Unit
        private get() {
            val resp = arrayOfNulls<JSONObject>(1)
            if (AppUtil.isNetworkAvailable(this)) {
                if (userLat == 0.0) {
                    userLat = SavedData.getLatitude()?.toDouble()!!
                }
                if (userLang == 0.0) {
                    userLang = SavedData.getLongitude()?.toDouble()!!
                }

                ErrorMessage.E("awawaawdsdsdsdsdds>>>"+userLat.toString()+" >>>"+userLang.toString())
                val call = AppConfig.api_Interface()
                    .getMyProfileV1(userLat.toString(), userLang.toString())
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        val url = call.request().url().toString()
                        ErrorMessage.E("Full URL >> $url")
                        if (response.isSuccessful) {
                            ErrorMessage.E("ErrorCode >> " + response.code())
                            mList!!.clear()
                            try {
                                resp[0] = JSONObject(response.body()!!.string())
                                Log.e("getMyProfile", ">>" + resp[0].toString())
                                val errorType = resp[0]!!.optString(KeyConstant.KEY_ERROR_TYPE)
                                try {
                                    if (resp[0]!!
                                            .getString("error_code") != null && resp[0]!!.getString(
                                            "error_code"
                                        ) == "401"
                                    ) {
                                        ErrorMessage.E("Error Code1 401 >> RefreshToken")
                                        refreshToken()
                                    }
                                } catch (_: Exception) {
                                }
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                    val responseObj =
                                        resp[0]!!.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    val userDetailsArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_USER_DETAILS)
                                    val leftmenuArray = responseObj.optJSONArray("leftmenu")
                                    for (i in 0 until userDetailsArray.length()) {
                                        val `object` = userDetailsArray.optJSONObject(i)
                                        runOnUiThread {
                                            tvName!!.text =
                                                `object`.optString("userName") + " " + `object`.optString(
                                                    "userLastName"
                                                )
                                            tvEmail!!.text = `object`.optString("userEmail")
                                            tvNo!!.text =
                                                `object`.optString("userCountryCode") + `object`.optString(
                                                    "userMobile"
                                                )
                                            if (`object`.optString("device_token") != null && `object`.optString(
                                                    "device_token"
                                                ) != ""
                                            ) {
                                            }
                                            else {

                                                updateDeviceToken(
                                                    PreferenceHelper.getInstance(this@MainActivity)?.deviceId.toString(),
                                                    PreferenceHelper.getInstance(this@MainActivity)?.fcmToken.toString()
                                                )
                                            }
                                            try {
                                                notiCountMain = `object`.optInt("notificationCount")
                                                if (`object`.optInt("notificationCount") > 0) {
                                                    binding!!.mainActivity.appbar.llNoti.visibility =
                                                        View.VISIBLE
                                                    binding!!.mainActivity.appbar.tvNotiCounts.text =
                                                        `object`.optString("notificationCount")
                                                } else {
                                                    binding!!.mainActivity.appbar.llNoti.visibility =
                                                        View.GONE
                                                }
                                            } catch (e: Exception) {
                                            }
                                            val arrDD = responseObj.optJSONArray("DDcardList")
                                            if (arrDD.length() > 0) {
                                                for (l in 0..0) {
                                                    SharedPreferenceVariable.savePreferences(
                                                        this@MainActivity,
                                                        KeyConstant.KEY_CODE,
                                                        arrDD.optJSONObject(0)
                                                            .optString("userDDCardNo")
                                                    )
                                                }
                                            } else {
                                                SharedPreferenceVariable.deletePreferenceData(
                                                    this@MainActivity,
                                                    KeyConstant.KEY_CODE
                                                )
                                            }
                                        }
                                    }



                                    try {
                                        runOnUiThread {
                                            for (j in 0 until leftmenuArray.length()) {
                                                try {
                                                    var jsonObject_inner: JSONObject? = null
                                                    jsonObject_inner =
                                                        leftmenuArray.getJSONObject(j)
                                                    val menuList = MenuList(
                                                        jsonObject_inner?.getString("leftmenuId"),
                                                        jsonObject_inner?.getString("leftmenuName"),
                                                        jsonObject_inner?.getString("leftmenuStatusId"),
                                                        jsonObject_inner?.getString("leftmenuStatus"),
                                                        jsonObject_inner?.getString("leftmenuIcon"),
                                                        jsonObject_inner?.getString("webURLStatus"),
                                                        jsonObject_inner?.getString("webURL")
                                                    )
                                                    mList!!.add(menuList)
                                                } catch (e: JSONException) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            if (mList!!.size > 0) {
                                                Log.e("mList", "" + mList!!.size)
                                                adapLeftMenu =
                                                    AdapterLeftMenu(this@MainActivity, mList!!)
                                                rvMain!!.layoutManager = LinearLayoutManager(
                                                    this@MainActivity,
                                                    LinearLayoutManager.VERTICAL,
                                                    false
                                                )
                                                rvMain!!.adapter = adapLeftMenu
                                                rvMain!!.setHasFixedSize(true)
                                                rvMain!!.setItemViewCacheSize(
                                                    mList!!.size
                                                )
                                                adapLeftMenu!!.notifyDataSetChanged()
                                            }

                                            IntentConstant.countGiftVoucher=1
                                            SavedData.saveAllData(resp[0].toString())
                                            val intent = Intent("Profile")
                                            intent.putExtra("AllData", resp[0].toString())
                                            LocalBroadcastManager.getInstance(context!!)
                                                .sendBroadcast(intent)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("inner", ">>$e")
                                    }
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                            resp[0]!!.optString(
                                                KeyConstant.KEY_STATUS
                                            ), ignoreCase = true
                                        )
                                    ) {
                                        //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                    }
                                }
                            } catch (e2: Exception) {
                                e2.printStackTrace()
                                Log.e("Exception", ">main>$e2")
                                //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                            }
                        }
                        else {
                            if (response.code() == 401) {
                                ErrorMessage.E("ErrorCode2 >> " + response.code()+ "   "+ response.message() + "   "+ response.body()+ "   "+ response.errorBody())
                                refreshToken()
                            }
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        AppUtil.showMsgAlert(binding!!.tvMainEmail, t.message)
                    }
                })
            }
            else {
                AppUtil.showMsgAlert(
                    binding!!.tvMainEmail,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
                Log.e("check ", ">>" + SavedData.getAllData())
                if (SavedData.getAllData() != "") {
                    mList!!.clear()
                    val intent = Intent("Profile")
                    intent.putExtra("AllData", SavedData.getAllData())
                    LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
                    try {
                        resp[0] = JSONObject(SavedData.getAllData())
                        val responseObj = resp[0]!!.optJSONObject(KeyConstant.KEY_RESPONSE)
                        val leftmenuArray = responseObj.optJSONArray("leftmenu")
                        for (j in 0 until leftmenuArray.length()) {
                            try {
                                var jsonObject_inner: JSONObject? = null
                                jsonObject_inner = leftmenuArray.getJSONObject(j)
                                val menuList = MenuList(
                                    jsonObject_inner?.getString("leftmenuId"),
                                    jsonObject_inner?.getString("leftmenuName"),
                                    jsonObject_inner?.getString("leftmenuStatusId"),
                                    jsonObject_inner?.getString("leftmenuStatus"),
                                    jsonObject_inner?.getString("leftmenuIcon"),
                                    jsonObject_inner?.getString("webURLStatus"),
                                    jsonObject_inner?.getString("webURL")
                                )
                                mList!!.add(menuList)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        if (mList!!.size > 0) {
                            Log.e("mList", "" + mList!!.size)
                            adapLeftMenu = AdapterLeftMenu(this@MainActivity, mList!!)
                            rvMain!!.layoutManager = LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            rvMain!!.adapter = adapLeftMenu
                            rvMain!!.setHasFixedSize(true)
                            rvMain!!.setItemViewCacheSize(mList!!.size)
                            adapLeftMenu!!.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun refreshToken() {
        if (AppUtil.isNetworkAvailable(this)) {
            val call = AppConfig.api_Interface().refreshToken(
                KeyConstant.KEY_REFRESH_TOKEN,
                KeyConstant.KEY_CLIENT_ID_VALUE,
                KeyConstant.KEY_CLIENT_SEC_VALUE,
                PreferenceHelper.getInstance(context)?.refreshToken
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Log.e("RefreshToken", obj.toString())
                            ErrorMessage.E("ErrorCode RefreshToken>> " + response.code())
                            try {
                                if (obj.getString("error_type") != null && obj.getString("error_type") == "401") {
                                    Log.e("RefreshToken auth", "else if is working")
                                    SharedPreferenceVariable.ClearSharePref(context)
                                    PreferenceHelper.getInstance(context)?.logout
                                    val `in` = Intent(context, SignUpActivity::class.java)
                                    `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context!!.startActivity(`in`)
                                } else { //if (obj.getString("error_type") != null && obj.getString("error_type").equals("201")) {
                                    Log.e("RefreshToken", "if is working")
                                    val objResponse = obj.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    PreferenceHelper.getInstance(context)?.accessToken =
                                        objResponse.optString("access_token")


                                    ErrorMessage.E("accessToken>>> ${PreferenceHelper.getInstance(context)?.accessToken}")

                                    PreferenceHelper.getInstance(context)?.refreshToken =
                                        objResponse.optString("refresh_token")

                                    myProfile
                                } /*else {
                                    Log.e("RefreshToken auth", "else  is working");
                                    JSONObject objResponse = obj.optJSONObject(KeyConstant.KEY_RESPONSE);
                                    PreferenceHelper.getInstance(context).setAccessToken(objResponse.optString("access_token"));
                                    PreferenceHelper.getInstance(context).setRefreshToken(objResponse.optString("refresh_token"));

                                    getMyProfile();
                                }*/
                            } catch (e: Exception) {
                                Log.e("Exception", "" + e.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("Exception", "" + e.toString())
                        }
                    } else {
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    SharedPreferenceVariable.ClearSharePref(context)
                    PreferenceHelper.getInstance(context)?.logout
                    val `in` = Intent(context, SignUpActivity::class.java)
                    `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context!!.startActivity(`in`)
                }
            })
        } else {
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (gpsAlertDialog != null && gpsAlertDialog!!.isShowing) {
                gpsAlertDialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
    }

    private fun alertPopupForGps() {
        if (gpsAlertDialog != null) {
            gpsAlertDialog!!.dismiss()
        }
        gpsAlertDialog = Dialog(this)
        gpsAlertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        gpsAlertDialog!!.setContentView(R.layout.popup_common)
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
        gpsAlertDialog!!.setCancelable(true)
        gpsAlertDialog!!.show()
        try {
            btnOk.setOnClickListener {
                try {
                    gpsAlertDialog!!.dismiss()
                } catch (e: Exception) {
                }
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            btnNo.setOnClickListener {
                try {
                    gpsAlertDialog!!.dismiss()
                    SavedData.saveLocationPermission("false")
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
    private fun importAllMerchantsBranches() {
        if (AppUtil.isNetworkAvailable(this)) {
            val call = AppConfig.api_Interface()
                .importAllMerchantsBranches(userLat.toString(), userLang.toString(), distance)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        if (response != null) {
                            try {
                                val resp = JSONObject(response.body()!!.string())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                            resp.optString(
                                                KeyConstant.KEY_STATUS
                                            ), ignoreCase = true
                                        )
                                    ) {
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                        }
                    } else {
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("log*********", t.message!!)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvMainEmail, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun closeDrawer() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
    }

    override fun onRefreshHistoryList(list: List<PNHistoryItemResult>) {}
    override fun clearData() {}
    override fun onRefreshChatList(jsonObject: JsonObject) {
        runOnUiThread {
            DialogQr.ivTick?.visibility = View.VISIBLE
            DialogQr.tvSuccess?.visibility = View.VISIBLE
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame)
            if (currentFragment is HomeFragment) {
                myProfile
            } else {
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.READ_EXTERNAL_STORAGE,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result3 =
            ContextCompat.checkSelfPermission(applicationContext, permission.READ_EXTERNAL_STORAGE)
        val result4 =
            ContextCompat.checkSelfPermission(applicationContext, permission.WRITE_EXTERNAL_STORAGE)
        val result5 =
            ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_FINE_LOCATION)
        return result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsManager.instance?.notifyPermissionsChange(permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val AccessLocation = grantResults[2] == PackageManager.PERMISSION_GRANTED
                Log.e("AccessLocation>>", "<>$AccessLocation")
                if (!AccessLocation) {
                    SavedData.saveLocationPermission("false")
                    myProfile
                } else {
                    SavedData.saveLocationPermission("true")
                    currentLocation
                }
            }
            CAM_PERM_REQUEST_CODE -> try {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(Intent(context, ScanQr::class.java), 80)
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        context,
                        "Please allow permission in order to scan",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            REQUEST_FINE_LOCATION -> {
                var isGrant = true
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false
                        break
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (isGrant) {
                        if (!isBLEEnabled) {
                            showBLEDialog()
                        } else {
                            initData()
                        }
                    }
                }
            }
        }
    }

    private fun updateDeviceToken(deviceId: String, deviceToken: String) {
        try {
            if (AppUtil.isNetworkAvailable(this)) {
                var lat: String? = "0.0"
                var lng: String? = "0.0"
                if (PreferenceHelper.getInstance(context)?.lat != null) {
                    lat = PreferenceHelper.getInstance(context)?.lat
                    lng = PreferenceHelper.getInstance(context)?.lng
                }
                val call = AppConfig.api_Interface().updateDeviceToken(
                    deviceId,
                    "A",
                    deviceToken,
                    UrlConstant.DEVICE_DEBUG_MODE.toString() + "",
                    "1",
                    lat,
                    lng, deviceToken
                )
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        if (response.isSuccessful) {
                            try {
                                if (response != null) {
                                    Log.e("Update", response.body()!!.string())
                                } else {
                                }
                            } catch (e: Exception) {
                            }
                        } else {
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
                })
            } else {
            }
        } catch (e: Exception) {
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            Log.e("if is working", "")
            AppUtil.showMsgAlert(tvName, "Good! Connected to Internet")
        } else {
            Log.e("else is working", "")
            AppUtil.showMsgAlert(tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    val location: Unit
        get() {
            if (userLat == 0.0 && userLang == 0.0) {
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
                                getLocationAddress(location.latitude, location.longitude)
                            } catch (ew: Exception) {
                            }
                        }
                    }
                }
            }
        }
    private val onNotice_refresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                binding!!.mainActivity.appbar.llNoti.visibility = View.GONE
                binding!!.mainActivity.appbar.tvNotiCounts.text = ""
            } catch (r: Exception) {
            }
        }
    }

    private fun initJobTabs() {
        homeSignUpFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .add(binding!!.mainActivity.frame.id, homeSignUpFragment!!).commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().show(homeSignUpFragment!!)
            .commitAllowingStateLoss()
    }


    private val currentLocation: Unit
        private get() {

            ErrorMessage.E("opopopop")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            try {
                fusedLocationClient!!.lastLocation.addOnCompleteListener(this@MainActivity) { task ->
                    val lastLocation = task.result
                    if (lastLocation != null) {
                        getLocationAddress(lastLocation.latitude, lastLocation.longitude)
                    }
                }
            } catch (e: Exception) {
                Log.e("Exceptionnnnnn", e.toString())
            }
        }
    private val gPSLocation: Boolean
        private get() {
            GPSTracker.requestSingleUpdate(this@MainActivity, object : GPSTracker.LocationCallback {
                override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                    Log.e("Location on MainAct is ", "" + location.toString())
                    userLat = location!!.latitude.toDouble()
                    userLang = location.longitude.toDouble()
                    SavedData.saveLatitude(location.latitude.toString())
                    SavedData.saveLongitude(location.longitude.toString())
                    Log.e("Location", location.latitude.toString() + "")
                    PreferenceHelper.getInstance(context)?.lat = location.latitude.toString() + ""
                    PreferenceHelper.getInstance(context)?.lng = location.longitude.toString() + ""
                    getLocationAddress(location.latitude.toDouble(), location.longitude.toDouble())
                }
            })
            return userLat != 0.0
        }

    private fun getLocationAddress(lat: Double, longitude: Double) {
        var addresses: List<Address>? = null
        try {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            addresses = geocoder.getFromLocation(lat, longitude, 1)
            SavedData.saveLatitude(lat.toString())
            SavedData.saveLongitude(longitude.toString())
            userLat = lat
            userLang = longitude
            if (addresses != null && addresses.size > 0) {
                val cityName = addresses[0].getAddressLine(0)
                val fullAddress = addresses[0].getAddressLine(1)
                val city = addresses[0].locality
                val countryName = addresses[0].getAddressLine(2)
                Log.e("City", "$city,$cityName")
                if (tvLocation!!.text.toString().trim { it <= ' ' }
                        .equals("Location", ignoreCase = true)) {
                    if (city == null) {
                        tvLocation!!.text = cityName
                        address = cityName
                    } else {
                        tvLocation!!.text = city
                        address = city
                    }
                    if (mList != null && mList!!.size == 0) {
                        myProfile
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            stopService(Intent(this@MainActivity, GPSTracker::class.java))
            if (mMtCentralManager != null) {
                mMtCentralManager!!.stopService()
            }

            finish()
            val serviceIntent = Intent(this, MainActivity::class.java)
            stopService(serviceIntent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Runtime.getRuntime().gc();

        } catch (e: Exception) {
        }
    }

    fun alertPopup() {
        try {
            if (camPermDialog != null) {
                camPermDialog!!.dismiss()
            }
            camPermDialog = Dialog(context!!)
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
            val btnNo = camPermDialog!!.findViewById<TextView>(R.id.popup_no_btn)
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
                    } catch (e: Exception) {
                    }
                    if (countCamPermission < 2) {
                        requestCameraPermission()
                    } else {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
                btnNo.setOnClickListener {
                    try {
                        if (camPermDialog != null) {
                            camPermDialog!!.dismiss()
                        }
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
        }
    }

    private fun requestCameraPermission() {
        countCamPermission++
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.CAMERA, " "),
            CAM_PERM_REQUEST_CODE
        )
    }

    private fun initListener() {
        initData()
        mMtCentralManager!!.setMTCentralManagerListener { peripherals ->
            if (peripherals.size > 0) {
                for (i in peripherals.indices) {
                    // mAdapter.setData(peripherals);
                    mtPeripheral = peripherals[i]
                    ErrorMessage.E("beaconplus" + mtPeripheral?.mMTFrameHandler?.mac.toString());
                    mMtCentralManager!!.connect(mtPeripheral, connectionStatueListener)
                    val mtFrameHandler = mtPeripheral!!.mMTFrameHandler

                    val advFrames = mtFrameHandler.advFrames
                    for (minewFrame in advFrames) {
                        val frameType = minewFrame.frameType
                        when (frameType) {
                            FrameType.FrameiBeacon -> {
                                val iBeaconFrame = minewFrame as IBeaconFrame
                                Log.e(
                                    "beaconplus",
                                    "UUID" + iBeaconFrame.uuid + "<>" + iBeaconFrame.major + "<>" + iBeaconFrame.minor
                                )
                                if (mtPeripherals.size == 0) {
                                    mtPeripherals.add(iBeaconFrame)
                                    val ibeacon = iBeacon(
                                        iBeaconFrame.uuid,
                                        iBeaconFrame.major.toString(),
                                        iBeaconFrame.minor.toString(),
                                        mtFrameHandler.mac
                                    )
                                    ErrorMessage.E("DeviceName ${mtFrameHandler.mac.toString()}")
                                    iBeaconDataList.add(ibeacon)

                                } else {
                                    var check = true
                                    var j = 0
                                    while (j < mtPeripherals.size) {
                                        if (mtPeripherals[j].major == iBeaconFrame.major) {
                                            check = false
                                        }
                                        j++
                                    }
                                    if (check) {
                                        mtPeripherals.add(iBeaconFrame)
                                        val ibeacon = iBeacon(
                                            iBeaconFrame.uuid,
                                            iBeaconFrame.major.toString(),
                                            iBeaconFrame.minor.toString(),
                                            mtFrameHandler.mac
                                        )

                                        iBeaconDataList.add(ibeacon)
                                        showDeviceInfoPopup()
                                    }
                                    Log.e("demo11", " <><>" + mtPeripherals.size)
                                    /* if(showSingleTimePopup){
                                           showDeviceInfoPopup();}*/
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        /* mAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mtPeripheral = mAdapter.getData(position);
                mMtCentralManager.connect(mtPeripheral, connectionStatueListener);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });*/
    }

    private fun showDeviceInfoPopup() {
        if (iBeaconDataList.size > 0) {
            showSingleTimePopup = false
            for (i in iBeaconDataList.indices) {
                sendIBeaconInfo(iBeaconDataList[i].uuid,
                    iBeaconDataList[i].major.toString(),
                    iBeaconDataList[i].minor.toString(),
                    iBeaconDataList[i].mac_address.toString()
                )
                /*val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Beacon Device Info")
                builder.setMessage(
                    """
                        Device UUID: ${mtPeripherals[i].uuid}
                        Major :${mtPeripherals[i].major}
                        Minor :${mtPeripherals[i].minor}
                        """.trimIndent()
                )
                    .setPositiveButton("OK") { dialog, id ->
                        // START THE GAME!
                    }
                // Create the AlertDialog object and return it
                builder.show()*/
            }
        }
    }

    private fun sendIBeaconInfo(uuid: String, major: String, minor: String, mac_address: String) {
        try {
            if (AppUtil.isNetworkAvailable(this@MainActivity)) {
                val call = AppConfig.api_Interface().updateBeaconToken(
                    uuid,
                    major, minor, mac_address)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        if (response.isSuccessful) {
                            try {
                                val resp = JSONObject(response.body()!!.string())
                                ErrorMessage.E("home sendIBeaconInfo >>" + resp.toString())

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)

                    }
                })
            }
        } catch (e: Exception) {
            ErrorMessage.E("Exception>>123>$e")
        }
    }

    private fun initManager() {
        mMtCentralManager = MTCentralManager.getInstance(this)
        //startservice
        mMtCentralManager?.startService()
        val bluetoothState = mMtCentralManager?.getBluetoothState(this)
        when (bluetoothState) {
            BluetoothState.BluetoothStateNotSupported -> Log.e("tag", "BluetoothStateNotSupported")
            BluetoothState.BluetoothStatePowerOff -> Log.e("tag", "BluetoothStatePowerOff")
            BluetoothState.BluetoothStatePowerOn -> Log.e("tag", "BluetoothStatePowerOn")
            null -> TODO()
        }
        mMtCentralManager?.setBluetoothChangedListener(OnBluetoothStateChangedListener { state ->
            when (state) {
                BluetoothState.BluetoothStateNotSupported -> Log.e(
                    "tag",
                    "BluetoothStateNotSupported"
                )
                BluetoothState.BluetoothStatePowerOff -> Log.e("tag", "BluetoothStatePowerOff")
                BluetoothState.BluetoothStatePowerOn -> Log.e("tag", "BluetoothStatePowerOn")
            }
        })
    }

    private val connectionStatueListener: ConnectionStatueListener =
        object : ConnectionStatueListener {
            override fun onUpdateConnectionStatus(
                connectionStatus: ConnectionStatus,
                getPasswordListener: GetPasswordListener,
            ) {
                runOnUiThread {
                    when (connectionStatus) {
                        ConnectionStatus.CONNECTING -> {
                            Log.e("tag", "CONNECTING")
                            Toast.makeText(this@MainActivity, "CONNECTING", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.CONNECTED -> {
                            Log.e("tag", "CONNECTED")
                            Toast.makeText(this@MainActivity, "CONNECTED", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.READINGINFO -> {
                            Log.e("tag", "READINGINFO")
                            Toast.makeText(this@MainActivity, "READINGINFO", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.DEVICEVALIDATING -> {
                            Log.e("tag", "DEVICEVALIDATING")
                            Toast.makeText(
                                this@MainActivity,
                                "DEVICEVALIDATING",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        ConnectionStatus.PASSWORDVALIDATING -> {
                            Log.e("tag", "PASSWORDVALIDATING")
                            Toast.makeText(
                                this@MainActivity,
                                "PASSWORDVALIDATING",
                                Toast.LENGTH_SHORT
                            ).show()
                            val password = "minew123"
                            getPasswordListener.getPassword(password)
                        }
                        ConnectionStatus.SYNCHRONIZINGTIME -> {
                            Log.e("tag", "SYNCHRONIZINGTIME")
                            Toast.makeText(
                                this@MainActivity,
                                "SYNCHRONIZINGTIME",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        ConnectionStatus.READINGCONNECTABLE -> {
                            Log.e("tag", "READINGCONNECTABLE")
                            Toast.makeText(
                                this@MainActivity,
                                "READINGCONNECTABLE",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        ConnectionStatus.READINGFEATURE -> {
                            Log.e("tag", "READINGFEATURE")
                            Toast.makeText(this@MainActivity, "READINGFEATURE", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.READINGFRAMES -> {
                            Log.e("tag", "READINGFRAMES")
                            Toast.makeText(this@MainActivity, "READINGFRAMES", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.READINGTRIGGERS -> {
                            Log.e("tag", "READINGTRIGGERS")
                            Toast.makeText(this@MainActivity, "READINGTRIGGERS", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.COMPLETED -> {
                            Log.e("tag", "COMPLETED")
                            Toast.makeText(this@MainActivity, "COMPLETED", Toast.LENGTH_SHORT)
                                .show()
                        }
                        ConnectionStatus.CONNECTFAILED, ConnectionStatus.DISCONNECTED -> {
                            Log.e("tag", "DISCONNECTED")
                            Toast.makeText(this@MainActivity, "DISCONNECTED", Toast.LENGTH_SHORT)
                                .show()
                        }

                        ConnectionStatus.READINGSENSORS -> TODO()
                    }
                }
            }

            override fun onError(e: MTException) {
                Log.e("tag", e.message)
            }
        }

    private fun ensureBleExists(): Boolean {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Phone does not support BLE", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    //        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COARSE_LOCATION);
//        } else {
//            initData();
//        }

    private val requiredPermissions: Unit
        private get() {
            val requestPermissions: Array<String>
            requestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    permission.BLUETOOTH_SCAN,
                    permission.BLUETOOTH_CONNECT,
                    permission.ACCESS_COARSE_LOCATION,
                    permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayOf(
                    permission.ACCESS_COARSE_LOCATION,
                    permission.ACCESS_FINE_LOCATION
                )
            }
            ActivityCompat.requestPermissions(
                this,
                requestPermissions, REQUEST_FINE_LOCATION
            )

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COARSE_LOCATION);
//        } else {
//            initData();
//        }
        }
    protected val isBLEEnabled: Boolean
        protected get() {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = bluetoothManager.adapter
            return adapter != null && adapter.isEnabled
        }

    @SuppressLint("MissingPermission")
    private fun showBLEDialog() {
        try {

            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } catch (e: Exception) {
        }
    }

    private fun initData() {
        //三星手机系统可能会限制息屏下扫描，导致息屏后无法获取到广播数据
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED
                ) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(permission.BLUETOOTH_SCAN),
//                        REQUEST_FINE_LOCATION
//                    )
                } else {
                    mMtCentralManager!!.startScan()
                }
            }
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmField
        var notiCountMain = 0

        @JvmField
        var tvLocation: TextView? = null

        @JvmField
        var llLocationLayout: LinearLayout? = null

        @JvmField
        var llSearch: LinearLayout? = null

        @JvmField
        var toolbar: Toolbar? = null

        @JvmField
        var drawerLayout: DrawerLayout? = null

        @JvmField
        var categoryId = 0

        @JvmField
        var userLat = 0.0

        @JvmField
        var userLang = 0.0

        @JvmField
        var address: String? = ""

        @JvmField
        var distance: String? = "5"

        @JvmField
        var adapLeftMenu: AdapterLeftMenu? = null

        @JvmField
        var ivImage: ImageView? = null

        @JvmField
        var tvName: TextView? = null

        @JvmField
        var tvEmail: TextView? = null

        @JvmField
        var tvNo: TextView? = null

        @JvmField
        var rvMain: RecyclerView? = null

        @JvmField
        var visibleFragment = false
        var countCamPermission = 0
        var mtPeripheral: MTPeripheral? = null
        private const val REQUEST_FINE_LOCATION = 125
        private const val REQUEST_ENABLE_BT = 3
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save your state data to the 'outState' Bundle here.
    }


    private fun backgroundPushNotification(extras: Bundle) {

        val firebaseMessagingService = MyFirebaseMessagingService()

        val extras = intent.extras

        if (extras != null) {
            val message = extras.getString("message")
            val agentId = extras.getString("agentId")
            val dealId = extras.getString("dealId")
            val onlineStoreURL = extras.getString("onlineStoreURL")
            val notificationType = extras.getString("notificationType")
            val title = extras.getString("title")
            val body = extras.getString("body")
            val image = extras.getString("imageUrl")
            val type = extras.getString("type")
            val agentCompanyName = extras.getString("agentCompanyName")
            val id = extras.getString("id")

            val isAdmin = extras.getString("isAdmin")
            val subscribeKey = extras.getString("subscribeKey")
            val publishKey = extras.getString("publishKey")
            val followingStatus = extras.getString("followingStatus")
            val time = extras.getString("time")
            val dealName = extras.getString("dealName")



            try {

                if (notificationType.equals("NOTIF_TYPE_AGENT_CHAT",
                        ignoreCase = true)
                ) {

                    customChatNotification(
                        agentId!!.toInt(),
                        isAdmin!!.toInt(),
                        "" +title,
                        ""+subscribeKey,
                        ""+publishKey,
                        1,
                        "" +message,
                        ""+time,
                        ""+ agentCompanyName,
                        ""+ body,
                        ""+dealId,
                        ""+dealName,
                    )

                } else if (notificationType
                        .equals("NOTIF_TYPE_AGENT_DETAILS", ignoreCase = true)
                ) {
                    customAgentDetailsNotification("" + agentId,
                        "" +title,
                        "" +message)
                }
                else if (notificationType
                        .equals("NOTIF_TYPE_DEAL_DETAILS", ignoreCase = true)
                ) {
                    Log.e("format", "3   $agentId     $dealId     $title    $message")

                    customProductDetailsNotification("" + agentId,
                        "" +dealId,
                        "" +title,
                        "" +message)
                }
                else if (notificationType
                        .equals("NOTIF_TYPE_AGENT_ONLINE_STORE", ignoreCase = true)
                ) {
                    // Log.e("format", "4")
                    customWebsNotification("" + title,
                        "" + message,
                        "" + onlineStoreURL)
                }
                else if (notificationType
                        .equals("NOTIF_TYPE_DEFAULT_PROMOTED", ignoreCase = true)
                ) {
                    //  Log.e("format", "5")
                    customopensNotification("" + title,
                        "" + message)
                }

                else if (notificationType
                        .equals("NOTIF_TYPE_DD_GROCER", ignoreCase = true)
                ) {
                    //  Log.e("format", "6")
                    customDDGrocerNotification("" + title,
                        "" + message)
                }
                else {
                    try {

                        try {
                            if (AppUtil.isForeground(applicationContext)) {
                                //  Log.e("format", "7")
                                showTopBanner(title, body, image, type, id)
                                customNotification(title, body, image, type, id)
                                //backGroundNotification(title, body, image, type, id);
                            } else {
                                //  Log.e("1", "2")
                                backGroundNotification(title, body, image, type, id)
                            }
                        } catch (e: Exception) {
                            //  Log.e("format", "8")
                            showTopBanner(title, body, image, type, id)
                            customNotification(title, body, image, type, id)
                        }

                        //final String tone = remoteMessage.getData().get("sound");
                        //sound = Uri.parse("res/raw/" + tone);
                    } catch (e: Exception) {

                    }
                }
            }
            catch (e: NullPointerException) {
                ErrorMessage.E("First else is printed>>$e")
            }

        }

    }


    fun customProductDetailsNotification(id: String, pId: String?, name: String?, title: String?) {
        // Using RemoteViews to bind custom layouts into Notification

        sound = Uri.parse("android.resource://sambal.mydd.app/" + R.raw.noti_tone)

        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }


        if (!gps_enabled && !network_enabled) {

        }
//        else {

        try {
            GPSTracker.requestSingleUpdate(this,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        //  Log.e("Location on MainAct is ", location.toString())
                        userLat = location!!.latitude.toDouble()
                        userLang = location.longitude.toDouble()
                        /*  userLat = 51.5758719;
                        userLang = -0.421236;*/Log.e("Location",
                            userLat.toString() + "")
                    }
                })
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            // Open NotificationView Class on Notification Click
            val intent = Intent(this, LatestProductDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("product_id", pId)
            intent.putExtra("type", "direct")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        else {

            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            val intent = Intent(this, LatestProductDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("product_id", pId)
            intent.putExtra("type", "direct")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
//        }
    }

    fun customAgentDetailsNotification(id: String, name: String?, title: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        try {
            GPSTracker.requestSingleUpdate(this,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        // Log.e("Location on MainAct is ", location.toString())
                        userLat = location!!.latitude.toDouble()
                        userLang = location.longitude.toDouble()
                        /*  userLat = 51.5758719;
                            userLang = -0.421236;*/Log.e("Location",
                            userLat.toString() + "")
                    }
                })
        } catch (e: Exception) {
        }
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val remoteViews = RemoteViews(packageName,
            R.layout.chatnotification)
        remoteViews.setTextViewText(R.id.tv_noti_heading, name)
        remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
        remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, New_AgentDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("position", 0)
            intent.putExtra("direct", "true")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            startActivity(intent)
            finish()

        } else {
            val intent = Intent(this, New_AgentDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("position", 0)
            intent.putExtra("direct", "true")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            startActivity(intent)
            finish()
        }
    }

    fun customWebsNotification(name: String?, title: String?, url: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val remoteViews = RemoteViews(packageName,
            R.layout.chatnotification)
        remoteViews.setTextViewText(R.id.tv_noti_heading, name)
        remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
        remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)

        // Open NotificationView Class on Notification Click
        val intent = Intent(this, Webview::class.java)
        // Send data to NotificationView Class
        intent.putExtra("url", url)
        intent.putExtra("title", name)
        intent.putExtra("type", "direct")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    fun customopensNotification(title: String?, msg: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, msg)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            // Open NotificationView Class on Notification Click
            val intent = Intent(this, SplashActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("type", "")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            startActivity(intent)
            finish()
        }


        else {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, msg)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            // Open NotificationView Class on Notification Click
            val intent = Intent(this, SplashActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("type", "")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            // Open NotificationView.java Activity

        }
    }

    fun customDDGrocerNotification(title: String?, message: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        try {
            GPSTracker.requestSingleUpdate(this,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        // Log.e("Location on MainAct is ", location.toString())
                        userLat = location!!.latitude.toDouble()
                        userLang = location.longitude.toDouble()
                        /*  userLat = 51.5758719;
                            userLang = -0.421236;*/
                        val geocoder =
                            Geocoder(this@MainActivity, Locale.getDefault())
                        var addresses: List<Address>? = null
                        try {
                            addresses = geocoder.getFromLocation(location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                1)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        try {
                            if (addresses != null && addresses.size > 0) {
                                val cityName = addresses[0].getAddressLine(0)
                                val fullAddress = addresses[0].getAddressLine(1)
                                city = addresses[0].locality
                                val countryName = addresses[0].getAddressLine(2)
                                // Log.e("City", "$city,$cityName")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, message)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ActivityGroceryList::class.java)
            // Send data to NotificationView Class
            intent.putExtra("type", "direct")
            intent.putExtra("name", city)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            // Open NotificationView.java Activity

        }

        else {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, message)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ActivityGroceryList::class.java)
            // Send data to NotificationView Class
            intent.putExtra("type", "direct")
            intent.putExtra("name", city)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            // Open NotificationView.java Activity

        }
    }



    private fun showTopBanner(
        title: String?,
        body: String?,
        imageUrl: String?,
        type: String?,
        id: String?,
    ) {

        //Sound
        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Vibration
        val CHANNEL_ID = "my_channel_02"
        val vibration = longArrayOf(500, 1000)
        //new long[] { 1000, 1000, 1000, 1000, 1000 }
        //Build notification
        val noBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.appicon)
            .setTicker("DealDio")
            .setContentTitle(title)
            .setContentText(body)
            .setChannelId(CHANNEL_ID) //.setSound(sound)
            .setVibrate(vibration) //.setDefaults(Notification.DEFAULT_ALL)
            //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        Log.e("message background2", body!!)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.e("message background1", body)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title1",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, noBuilder.build())
    }

    fun customNotification(
        title: String?,
        body: String?,
        imageUrl: String?,
        type: String?,
        id: String?,
    ) {

        // Using RemoteViews to bind custom layouts into Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remoteViews = RemoteViews(packageName,
                R.layout.custom_notification_view)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, body)
            remoteViews.setTextViewText(R.id.tv_noti_time,
                DateFormat.getDateInstance().format(Date()))
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                remoteViews.setImageViewBitmap(R.id.iv_image, myBitmap)
            } catch (e: Exception) {
                // Log exception
                //  Log.e("Ex", e.toString())
                remoteViews.setImageViewResource(R.id.iv_image, R.drawable.app_icon_new)
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
        else {
            val remoteViews = RemoteViews(packageName,
                R.layout.custom_notification_view)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, body)
            remoteViews.setTextViewText(R.id.tv_noti_time,
                DateFormat.getDateInstance().format(Date()))
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                remoteViews.setImageViewBitmap(R.id.iv_image, myBitmap)
            } catch (e: IOException) {
                // Log exception
                //  Log.e("Ex", e.toString())
                remoteViews.setImageViewResource(R.id.iv_image, R.drawable.app_icon_new)
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
    }

    fun backGroundNotification(
        title: String?,
        body: String?,
        imgUrl: String?,
        type: String?,
        id: String?,
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("BAck", "1")
            val CHANNEL_ID = "my_channel_03"
            val intent = Intent(this, MainActivity::class.java)

            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imgUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        else {
            Log.e("BAck", "1")

            val CHANNEL_ID = "my_channel_03"
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imgUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }


    fun customChatNotification(
        id: Int,
        isAdmin: Int,
        name: String?,
        subskey: String?,
        pubskey: String?,
        followingstatus: Int,
        title: String?,
        time: String?,
        agentCompanyName: String?,
        body:String?,
        dealId:String?,
        dealName:String?,
    ) {


        // Using RemoteViews to bind custom layouts into Notification
        var time = time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            try {
                val inputPattern = "dd-MM-dd hh:mm a"
                val outputPattern = "dd-MM-yyyy hh:mm a"
                val inputFormat = SimpleDateFormat(inputPattern)
                val outputFormat = SimpleDateFormat(outputPattern)
                val date: Date? = null
                val str: String? = null
                try {
                    time = DateUtil.UTCToLocalSec(time)
                    /*    date = inputFormat.parse(time);
                str = outputFormat.format(date);*/remoteViews.setTextViewText(R.id.tv_noti_time,
                        time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ChatMain::class.java)
            // Send data to NotificationView Class
            intent.putExtra("id", id.toString() + "")
            intent.putExtra("isAdmin", isAdmin.toString() + "")
            intent.putExtra("name", agentCompanyName)
            intent.putExtra("subskey", subskey)
            intent.putExtra("pubskey", pubskey)
            intent.putExtra("followingstatus", followingstatus.toString() + "")
            intent.putExtra("type", "direct")
            intent.putExtra("body", body)
            intent.putExtra("dealId", dealId)
            intent.putExtra("dealName", dealName)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)
            finish()

            // Open NotificationView.java Activity

        }

        else {
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            try {
                val inputPattern = "dd-MM-dd hh:mm a"
                val outputPattern = "dd-MM-yyyy hh:mm a"
                val inputFormat = SimpleDateFormat(inputPattern)
                val outputFormat = SimpleDateFormat(outputPattern)
                val date: Date? = null
                val str: String? = null
                try {
                    time = DateUtil.UTCToLocalSec(time)
                    /*    date = inputFormat.parse(time);
                str = outputFormat.format(date);*/remoteViews.setTextViewText(R.id.tv_noti_time,
                        time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ChatMain::class.java)
            // Send data to NotificationView Class
            intent.putExtra("id", id.toString() + "")
            intent.putExtra("isAdmin", isAdmin.toString() + "")
            intent.putExtra("name", agentCompanyName)
            intent.putExtra("subskey", subskey)
            intent.putExtra("pubskey", pubskey)
            intent.putExtra("followingstatus", followingstatus.toString() + "")
            intent.putExtra("type", "direct")
            intent.putExtra("body", body)
            intent.putExtra("dealId", dealId)
            intent.putExtra("dealName", dealName)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

            // Open NotificationView.java Activity


        }
    }

/// For background services location fetching

//@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//open fun onAppBackgrounded() {
//
//    ErrorMessage.E("background services")
//
//
//    Dexter.withActivity(this@MainActivity)
//        .withPermissions(
//            permission.ACCESS_FINE_LOCATION,
//            permission.ACCESS_COARSE_LOCATION)
//        .withListener(object : MultiplePermissionsListener {
//            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                if (report.areAllPermissionsGranted()) {
//                    startService(Intent(this@MainActivity, LocationServices::class.java))
//                }
//                if (report.isAnyPermissionPermanentlyDenied()) {
////                    showSettingsDialog()
//                }
//            }
//
//
//            override fun onPermissionRationaleShouldBeShown(
//                permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
//                token: PermissionToken?
//            ) {
//                token?.continuePermissionRequest()
//            }
//        })
//        .onSameThread()
//        .check()
//
//
////        Toast.makeText(this, "background", Toast.LENGTH_SHORT).show();
//}
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        ErrorMessage.E("foreground services")
//        stopService(Intent(this@MainActivity, LocationServices::class.java))
////                Toast.makeText(this, "foreground", Toast.LENGTH_SHORT).show();
//
//    }

    /// For background services location fetching



}