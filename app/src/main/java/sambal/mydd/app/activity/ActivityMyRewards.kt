package sambal.mydd.app.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import sambal.mydd.app.fragment.MyRewards.MyVouchersFragment
import sambal.mydd.app.fragment.MyRewards.VisitDealsFragment
import com.google.android.material.tabs.TabLayout
import sambal.mydd.app.adapter.TabsAdapter
import sambal.mydd.app.utils.AppUtil
import android.content.Intent
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.utils.GPSTracker
import sambal.mydd.app.MainActivity
import android.location.Geocoder
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.os.Build
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityMyRewardsBinding
import sambal.mydd.app.utils.SavedData
import java.io.IOException
import java.lang.Exception
import java.util.*

class ActivityMyRewards : BaseActivity() {
    var binding: ActivityMyRewardsBinding? = null
    var lat: String? = "0.0"
    var lng: String? = "0.0"

    override val contentResId: Int
        get() = R.layout.activity_my_rewards




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_rewards)
        setToolbarWithBackButton_colorprimary("My Rewards")
        LocalBroadcastManager.getInstance(this@ActivityMyRewards)
            .registerReceiver(onNotice_refresh, IntentFilter("refresh"))
        init()
        val fragmentList: MutableList<Fragment> = ArrayList()
        fragmentList.add(MyVouchersFragment())
        fragmentList.add(VisitDealsFragment())
        val titleList: MutableList<String> = ArrayList()
        titleList.add("My Vouchers")
        titleList.add("Visit Deals")
        binding!!.tabLayout.tabMode = TabLayout.MODE_FIXED
        val adapter = TabsAdapter(supportFragmentManager, titleList, fragmentList)
        binding!!.pager.offscreenPageLimit = titleList.size
        binding!!.pager.adapter = adapter
        binding!!.tabLayout.setupWithViewPager(binding!!.pager)
        binding!!.llLocationLayout.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivityMyRewards)) {
                val intent1 =
//                    Intent(this@ActivityMyRewards, SelectLocationActivityStore::class.java)
                    Intent(this@ActivityMyRewards, SelectLocationActivity::class.java)

                startActivityForResult(intent1, 40)
            } else {
                Log.e("else is working", "")
                AppUtil.showMsgAlert(binding!!.tvHistory,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }



        binding!!.ivHisto.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivityMyRewards)) {
                startActivity(Intent(this@ActivityMyRewards,
                    NewhistoryDashboardDetials::class.java))
            } else {
                Log.e("else is working", "")
                AppUtil.showMsgAlert(binding!!.tvHistory,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }
        binding!!.tvHistory.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@ActivityMyRewards)) {
                startActivity(Intent(this@ActivityMyRewards,
                    NewhistoryDashboardDetials::class.java))
            } else {
                Log.e("else is working", "")
                AppUtil.showMsgAlert(binding!!.tvHistory,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@ActivityMyRewards, "yellow")
        } catch (e: Exception) {
        }
        try {
            if (binding!!.tvToolbarLocation.text.toString().contains("Location")) {
                GPSTracker.requestSingleUpdate(this@ActivityMyRewards, object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                    if (location != null) {
                        MainActivity.userLat = location.latitude.toDouble()
                        MainActivity.userLang = location.longitude.toDouble()
                        Log.e("FROM Here", " 2")
                        getLocationAddress(location.latitude.toDouble(),
                            location.longitude.toDouble())
                    }
                    }
                })
            }
        } catch (e: Exception) {
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
//            mGoogleApiClient!!.disconnect()
            finish()
        } catch (e: Exception) {
        }

        val serviceIntent = Intent(this, ActivityMyRewards::class.java)
        stopService(serviceIntent)

    }



    private fun getLocationAddress(latatitude: Double, longi: Double) {
        Log.e("CHECK ", "1")
        Log.e("LcoationsAa", "LocationaAssa")
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latatitude, longi, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            if (addresses != null && addresses.size > 0) {
                Log.e("CHECK ", "2")
                val cityName = addresses[0].getAddressLine(0)
                val fullAddress = addresses[0].getAddressLine(1)
                val city = addresses[0].locality
                val countryName = addresses[0].getAddressLine(2)
                Log.e("City", "$city,$cityName")
                if (binding!!.tvToolbarLocation.text.toString().trim { it <= ' ' }
                        .equals("Location", ignoreCase = true)) {
                    Log.e("CHECK ", "3")
                    if (city == null) {
                        binding!!.tvToolbarLocation.text = cityName
                        MainActivity.address = cityName
                    } else {
                        binding!!.tvToolbarLocation.text = city
                        MainActivity.address = city
                    }
                    lat = latatitude.toString()
                    lng = longi.toString()
                    Log.e("CHECK ", "4")
                    val intent = Intent("refresh_rewards")
                    intent.putExtra("lat", lat)
                    intent.putExtra("lng", lng)
                    LocalBroadcastManager.getInstance(this@ActivityMyRewards).sendBroadcast(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 40) {
            try {
                val name = data!!.getStringExtra("name")
                lat = data.getStringExtra("lat")
                lng = data.getStringExtra("lng")
                Log.e("Lat", "$name,$lat,$lng")
                binding!!.tvToolbarLocation.text = name
                SavedData.saveAddress(name)
                MainActivity.address = name
                MainActivity.userLat = lat!!.toDouble()
                MainActivity.userLang = lng!!.toDouble()
                val intent = Intent("refresh_rewards")
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
                LocalBroadcastManager.getInstance(this@ActivityMyRewards).sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e("Ex", e.toString())
            }
        }
    }

    private fun init() {
        try {
            binding!!.tvToolbarLocation.text =
                if (MainActivity.address == "") if (SavedData.getAddress() == "") "Location" else SavedData.getAddress() else MainActivity.address
            if (MainActivity.userLat != 0.0) {
                lat = MainActivity.userLat.toString() + ""
            } else if (SavedData.getLatitude() != "0") {
                lat = SavedData.getLatitude() + ""
            }
            if (MainActivity.userLang != 0.0) {
                lng = MainActivity.userLang.toString() + ""
            } else if (SavedData.getLongitude() != "0") {
                lng = SavedData.getLongitude() + ""
            }
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val onNotice_refresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val intent1 = Intent("refresh_rewards")
                intent1.putExtra("lat", lat)
                intent1.putExtra("lng", lng)
                LocalBroadcastManager.getInstance(this@ActivityMyRewards).sendBroadcast(intent1)
            } catch (r: Exception) {
            }
        }
    }
}