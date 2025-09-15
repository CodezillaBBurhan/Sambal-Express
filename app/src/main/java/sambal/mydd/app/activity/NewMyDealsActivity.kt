package sambal.mydd.app.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.SavedData
import android.content.Intent
import sambal.mydd.app.utils.MyLog
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.location.Address
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import com.google.gson.Gson
import sambal.mydd.app.adapter.SignUp_Deal_Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.adapter.Daily_Deal_Adapter
import sambal.mydd.app.adapter.Favourite_deal_Adapter
import sambal.mydd.app.adapter.My_Promotion_Adapter
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.utils.GPSTracker
import android.location.Geocoder
import android.util.Log
import android.view.View
import com.google.android.gms.location.*
import sambal.mydd.app.R
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.ActivityNewMyDealsBinding
import sambal.mydd.app.models.MyDeal_Models.Example
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class NewMyDealsActivity : BaseActivity() {
    private var lat: String? = ""
    private var lng: String? = ""
    var example: Example? = null
    var binding: ActivityNewMyDealsBinding? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    override val contentResId: Int
        get() = R.layout.activity_new_my_deals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_my_deals)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocalBroadcastManager.getInstance(this@NewMyDealsActivity)
            .registerReceiver(onNotice_refresh, IntentFilter("refresh_Page"))
        setToolbarWithBackButton_colorprimary("My Deals")

        lat = MainActivity.userLat.toString() + ""
        lng = MainActivity.userLang.toString() + ""
        try {
            // tvLocation.setText(MainActivity.address.equals("")?"Location":MainActivity.address);
            binding!!.tvLocation.text =
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
        binding!!.shimmerViewContainer.startShimmerAnimation()
        try {
            if (MainActivity.notiCountMain > 0) {
                binding!!.llNoti.visibility = View.VISIBLE
                binding!!.tvNotiCounts.text = MainActivity.notiCountMain.toString() + ""
            } else {
                binding!!.llNoti.visibility = View.GONE
                binding!!.tvNotiCounts.text = ""
            }
        } catch (e: Exception) {
        }

        /*Bundle bundles = getIntent().getExtras();
        if (bundles != null) {
            try {
                if (bundles.getString("notiCount") != null && !bundles.getString("notiCount").equals("")) {

                    binding.llNoti.setVisibility(View.VISIBLE);
                    binding.tvNotiCounts.setText(bundles.getString("notiCount"));
                }
            } catch (Exception e) {
                Log.e("Exception", "" + e.toString());
            }
        }*/GetTenData()
        binding!!.signUpDealTv.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, ExclusiveDeals::class.java))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.dailyDealTv.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, DailyDeals::class.java))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.latestDealTv.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, LatestDeals::class.java))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.favouriteDealTv.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, MyFavorites::class.java))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.allCategoryImg.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, Categories::class.java))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.notificationImg.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, NewNotification::class.java)
                .putExtra("agentId", "")
                .putExtra("title", "Notifications"))
            MyLog.onAnim(this@NewMyDealsActivity)
        }
        binding!!.myPramotionViewAllTv.setOnClickListener {
            startActivity(Intent(this@NewMyDealsActivity, MY_PromotionActivity::class.java))
            MyLog.onAnim(this@NewMyDealsActivity as Activity)
        }
        binding!!.tvLocation.setOnClickListener {
            binding!!.llLocation.isEnabled = false
//            val intent1 = Intent(this@NewMyDealsActivity, SelectLocationActivityStore::class.java)
            val intent1 = Intent(this@NewMyDealsActivity, SelectLocationActivity::class.java)

            startActivityForResult(intent1, 40)
        }
        binding!!.downArrowImg.setOnClickListener {
            binding!!.llLocation.isEnabled = false
//            val intent1 = Intent(this@NewMyDealsActivity, SelectLocationActivityStore::class.java)
            val intent1 = Intent(this@NewMyDealsActivity, SelectLocationActivity::class.java)

            startActivityForResult(intent1, 40)
        }
        binding!!.searchEtv.setOnClickListener {
            val i = Intent(this@NewMyDealsActivity, SearchAllDealActivity::class.java)
            i.putExtra("lat", lat)
            i.putExtra("lng", lng)
            startActivityForResult(i, 101)
        }

        binding!!.myDealPageErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this)) {
                binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                GetTenData()
            } else {
                AppUtil.showMsgAlert(
                    binding!!.tvNotiCounts,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
    }

    private fun GetTenData() {
        if (AppUtil.isNetworkAvailable(this@NewMyDealsActivity)) {
            binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
            ErrorMessage.E("CHECK 1")
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().getAllTenDealsV1(lat, lng)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                  //  Log.e("sendToken", response.code().toString())
                    if (response.isSuccessful) {
                        ErrorMessage.E("CHECK 3")
                        try {
                            binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                            binding!!.shimmerViewContainer.stopShimmerAnimation()
                            binding!!.shimmerViewContainer.visibility = View.GONE
                            binding!!.mainNestedscrollview.visibility = View.VISIBLE
                            val obj = JSONObject(response.body()!!.string())
                            ErrorMessage.E("CHECK 4$obj")
                            dialogManager.stopProcessDialog()
                            ErrorMessage.E("CHECK 5" + obj.optString("error_type"))
                            if (obj.optString("error_type") == "200") {
                                ErrorMessage.E("CHECK 4")
                                val gson = Gson()
                                example = gson.fromJson(obj.toString(), Example::class.java)
                                ErrorMessage.E("LATTITUDE>>" + lat + "LONGITUDE>>>" + lng)
                                if (example!!.getResponse().signupDealsList.size > 0) {
                                    binding!!.signUpDealLayout.visibility = View.VISIBLE
                                    binding!!.signUpDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        SignUp_Deal_Adapter(this@NewMyDealsActivity,
                                            example!!.getResponse().signupDealsList,
                                            "")
                                    binding!!.signUpDealRcv.layoutManager =
                                        LinearLayoutManager(this@NewMyDealsActivity,
                                            RecyclerView.HORIZONTAL,
                                            false)
                                    binding!!.signUpDealRcv.isNestedScrollingEnabled = false
                                    binding!!.signUpDealRcv.setItemViewCacheSize(example!!.getResponse().signupDealsList.size)
                                    binding!!.signUpDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                    if (example!!.getResponse().signupDealsList.size >= 10) {
                                        binding!!.signUpDealTv.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.signUpDealLayout.visibility = View.GONE
                                    binding!!.signUpDealRcv.visibility = View.GONE
                                }
                                if (example!!.getResponse().dailyDealsList.size > 0) {
                                    binding!!.dailyDealLayout.visibility = View.VISIBLE
                                    binding!!.dailyDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Daily_Deal_Adapter(this@NewMyDealsActivity,
                                            example!!.getResponse().dailyDealsList,
                                            "DailyDeal",
                                            "")
                                    binding!!.dailyDealRcv.layoutManager =
                                        LinearLayoutManager(this@NewMyDealsActivity,
                                            RecyclerView.HORIZONTAL,
                                            false)
                                    binding!!.dailyDealRcv.isNestedScrollingEnabled = false
                                    binding!!.dailyDealRcv.setItemViewCacheSize(example!!.getResponse().dailyDealsList.size)
                                    binding!!.dailyDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                    if (example!!.getResponse().dailyDealsList.size >= 10) {
                                        binding!!.dailyDealTv.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.dailyDealLayout.visibility = View.GONE
                                    binding!!.dailyDealRcv.visibility = View.GONE
                                }
                                if (example!!.getResponse().latestDealsList.size > 0) {
                                    binding!!.latestDealLayout.visibility = View.VISIBLE
                                    binding!!.latestDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Daily_Deal_Adapter(this@NewMyDealsActivity,
                                            example!!.getResponse().latestDealsList,
                                            "",
                                            "")
                                    binding!!.latestDealRcv.layoutManager =
                                        LinearLayoutManager(this@NewMyDealsActivity,
                                            RecyclerView.HORIZONTAL,
                                            false)
                                    binding!!.latestDealRcv.isNestedScrollingEnabled = false
                                    binding!!.latestDealRcv.setItemViewCacheSize(example!!.getResponse().latestDealsList.size)
                                    binding!!.latestDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                    if (example!!.getResponse().latestDealsList.size >= 10) {
                                        binding!!.latestDealTv.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.latestDealLayout.visibility = View.GONE
                                    binding!!.latestDealRcv.visibility = View.GONE
                                }
                                if (example!!.getResponse().favouriteDealsList.size > 0) {
                                    binding!!.favouriteDealRcv.visibility = View.VISIBLE
                                    binding!!.favouriteDealLayout.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Favourite_deal_Adapter(this@NewMyDealsActivity,
                                            example!!.getResponse().favouriteDealsList,
                                            "")
                                    binding!!.favouriteDealRcv.layoutManager = LinearLayoutManager(
                                        this@NewMyDealsActivity,
                                        RecyclerView.HORIZONTAL,
                                        false)
                                    binding!!.favouriteDealRcv.isNestedScrollingEnabled = false
                                    binding!!.favouriteDealRcv.setItemViewCacheSize(example!!.getResponse().favouriteDealsList.size)
                                    binding!!.favouriteDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                    if (example!!.getResponse().favouriteDealsList.size >= 10) {
                                        binding!!.favouriteDealTv.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.favouriteDealRcv.visibility = View.GONE
                                    binding!!.favouriteDealLayout.visibility = View.GONE
                                }
                                if (example!!.getResponse().promotionDealsList.size > 0) {
                                    binding!!.myPramotionRcv.visibility = View.VISIBLE
                                    binding!!.myPramotionLayout.visibility = View.VISIBLE
                                    binding!!.myPramotionViewAllTv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        My_Promotion_Adapter(this@NewMyDealsActivity,
                                            example!!.getResponse().promotionDealsList,
                                            example!!.getResponse().promotionDealsList.size)
                                    binding!!.myPramotionRcv.layoutManager = LinearLayoutManager(
                                        this@NewMyDealsActivity,
                                        RecyclerView.HORIZONTAL,
                                        false)
                                    binding!!.myPramotionRcv.isNestedScrollingEnabled = false
                                    binding!!.myPramotionRcv.setItemViewCacheSize(example!!.getResponse().promotionDealsList.size)
                                    binding!!.myPramotionRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.myPramotionRcv.visibility = View.GONE
                                    binding!!.myPramotionLayout.visibility = View.GONE
                                }
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvLocation, obj.optString("message"))
                            }
                        } catch (e: Exception) {
                           // Log.e("Ex1", e.toString())
                            dialogManager.stopProcessDialog()
                            binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                       // Log.e("sendToken", "else is working" + response.code().toString())
                        binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                }
            })
        } else {
            binding!!.myDealPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
            AppUtil.showMsgAlert(
                binding!!.tvNotiCounts,
                MessageConstant.MESSAGE_INTERNET_CONNECTION
            )
        }
    }

    fun moveToDetails(productId: String?, agentId: String?, pos: Int) {
        val intent = Intent(this@NewMyDealsActivity, LatestProductDetails::class.java)
        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        intent.putExtra("type", "non_direct")
        intent.putExtra("pos", pos)
        startActivityForResult(intent, 80)

    }

    override fun onResume() {
        super.onResume()
        //GetTenData();
        try {
            StatusBarcolor.setStatusbarColor(this@NewMyDealsActivity, "")
        } catch (e: Exception) {
        }
        try {
            if (MainActivity.address == "") {
                location
                GPSTracker.requestSingleUpdate(this@NewMyDealsActivity,
                    object : GPSTracker.LocationCallback {
                        override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                            if (location != null) {
                                getLocationAddress(location.latitude.toDouble(),
                                    location.longitude.toDouble())
                            }
                        }
                    })
            }
            if (binding!!.tvLocation.text.toString().contains("Location")) {
                location
                GPSTracker.requestSingleUpdate(this@NewMyDealsActivity,
                    object : GPSTracker.LocationCallback {
                        override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                            if (location != null) {
                                getLocationAddress(location.latitude.toDouble(),
                                    location.longitude.toDouble())
                            }
                        }
                    })
            }
        } catch (e: Exception) {
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
      //  Log.e("Ress", requestCode.toString() + "")
        if (requestCode == 40 && resultCode == RESULT_OK) {
            try {
                val name = data!!.getStringExtra("name")
                lat = data.getStringExtra("lat")
                lng = data.getStringExtra("lng")
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                binding!!.mainNestedscrollview.visibility = View.GONE
                GetTenData()
              //  Log.e("Lat", "$name,$lat,$lng")
                binding!!.tvLocation.text = name
                try {
                    MainActivity.address = name
                    MainActivity.userLat = data.getStringExtra("lat")!!.toDouble()
                    MainActivity.userLang = data.getStringExtra("lng")!!.toDouble()
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
              //  Log.e("Ex", e.toString())
            }
        } else if (resultCode == RESULT_OK && requestCode == 80) {
            GetTenData()
        }
    }

    private val onNotice_refresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                binding!!.llNoti.visibility = View.GONE
                binding!!.tvNotiCounts.text = ""
                MainActivity.notiCountMain = 0
            } catch (r: Exception) {
            }
        }
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
                            lat = location.latitude.toString()
                            lng = location.longitude.toString()
                            getLocationAddress(location.latitude, location.longitude)
                        } catch (ew: Exception) {
                        }
                    }
                }
            }
        }

    private fun getLocationAddress(latitude: Double, longi: Double) {
      //  Log.e("LcoationsAa", "LocationaAssa")
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latitude, longi, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            if (addresses != null && addresses.size > 0) {
                val cityName = addresses[0].getAddressLine(0)
                val fullAddress = addresses[0].getAddressLine(1)
                val city = addresses[0].locality
                val countryName = addresses[0].getAddressLine(2)
              //  Log.e("City", "$city,$cityName")
                if (binding!!.tvLocation.text.toString().trim { it <= ' ' }
                        .equals("Location", ignoreCase = true)) {
                    if (city == null) {
                        binding!!.tvLocation.text = cityName
                        MainActivity.address = cityName
                    } else {
                        binding!!.tvLocation.text = city
                        MainActivity.address = city
                    }
                    lat = latitude.toString()
                    lng = longi.toString()
                    GetTenData()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
          //  Log.e("This is Working 5", "" + e.toString())
        }
    }
}