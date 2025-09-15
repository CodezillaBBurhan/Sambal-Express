package sambal.mydd.app.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.adapter.AdapterDeals
import sambal.mydd.app.beans.DealListModel
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.DailydealsBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class DailyDeals : BaseActivity(), View.OnClickListener {

    private lateinit var binding: DailydealsBinding

    internal lateinit var dialogManager: DialogManager
    private var offset = 0
    private var mList = ArrayList<DealListModel?>()
    private var catId = "0"
    private var isFirstTime = false
    private lateinit var adap: AdapterDeals
    var userLat = 0.0
    var userLang = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@DailyDeals, R.layout.dailydeals)
        setToolbarWithBackButton_colorprimary("Daily Deals")
        init()
        binding.rvDeals.setHasFixedSize(false)
        binding.rvDeals.layoutManager = WrapContentLinearLayoutManager(this@DailyDeals)
        adap = AdapterDeals(this@DailyDeals, mList, binding.rvDeals, "DailyDeal")
        binding.rvDeals.adapter = adap

        binding.rvDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirstTime = false
                        getAllDetails(false)
                    }
                } catch (e: java.lang.Exception) {
                }
            }
        })

        isFirstTime = true
        getAllDetails(true)

        binding.llSearch.visibility = View.VISIBLE
        binding.llSearch.setOnClickListener {
            val intent = Intent(this@DailyDeals, SearchAllNewActivity::class.java)
            startActivityForResult(intent, 85)
        }
    }

    private fun init() {
        binding.tvLocation.text = MainActivity.address
        userLat = MainActivity.userLat
        userLang = MainActivity.userLang
        binding.llLocation.setOnClickListener(this)
    }

    private fun getAllDetails(isShowingLoader: Boolean) {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.startShimmerAnimation()

        dialogManager = DialogManager()

        val agentIds = ""

        var lat = userLat.toString() + ""
        var lang = userLang.toString() + ""

        if (AppUtil.isNetworkAvailable(this@DailyDeals)) {

            if (isShowingLoader) {
                if (binding.shimmerViewContainer.visibility === View.GONE) {
                    dialogManager.showProcessDialog(this@DailyDeals, "", false, null)
                }
            } else {
                binding.shimmerViewContainer.visibility = View.GONE;
                if (binding.shimmerViewContainer.visibility === View.GONE) {
                    dialogManager.showProcessDialog(this@DailyDeals, "", false, null)
                }
            }
            val call = AppConfig.api_Interface().getAllDealsV1(agentIds, lat, lang, offset.toString(), "10", catId,  "1",)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            try {
                                dialogManager.stopProcessDialog()
                            } catch (e: Exception) {
                                dialogManager.stopProcessDialog()
                            }
                            val resp = JSONObject(response.body()!!.string())
                          //  Log.e("GetAllDeals", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            binding.shimmerViewContainer.visibility = View.GONE
                            binding.shimmerViewContainer.stopShimmerAnimation()
                            binding.mainLayout.visibility = View.VISIBLE

                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)) {

                                dialogManager.stopProcessDialog()
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                var arrDealsList = responseObj.optJSONArray("dealsList")

                                try {
                                    if (arrDealsList.length() > 0) {

                                        for (i in 0 until arrDealsList.length()) {

                                            var obj = arrDealsList.optJSONObject(i)

                                            var productId = obj.optString("productId")
                                            var productName = obj.optString("productName")
                                            var productDistance =
                                                obj.optString("productDistance")
                                            var productAgentId = obj.optString("productAgentId")
                                            var productAgentName =
                                                obj.optString("productAgentName")
                                            var productAgentImage =
                                                obj.optString("productAgentImage")
                                            var productLocation =
                                                obj.optString("productLocation")
                                            var productFavourite =
                                                obj.optString("productFavourite")
                                            var productImage = obj.optString("productImage")
                                            var productCurrency =
                                                obj.optString("productCurrency")
                                            var productDiscountPercentageEnabled =
                                                obj.optString("productDiscountPercentageEnabled")
                                            var productDiscountPercentage =
                                                obj.optString("productDiscountPercentage")
                                            var productPrice = obj.optString("productPrice")
                                            var productFinalPrice =
                                                obj.optString("productFinalPrice")
                                            var priceEnabledId = obj.optString("priceEnabledId")
                                            var priceEnabledStatus =
                                                obj.optString("priceEnabledStatus")
                                            var discountPriceEnabledId =
                                                obj.optString("discountPriceEnabledId")
                                            var discountPriceEnabledStatus =
                                                obj.optString("discountPriceEnabledStatus")
                                            var offerType = obj.optString("offerType")
                                            var offerTypeId = obj.optString("offerTypeId")
                                            var dealExpiredDate =
                                                obj.optString("dealExpiredDate")
                                            var dealExclusiveStatus =
                                                obj.optString("dealExclusiveStatus")
                                            var productDDLoyaltyPrice =
                                                obj.optString("productDDLoyaltyPrice")

                                            var dl = DealListModel(
                                                productId,
                                                productName,
                                                productDistance,
                                                productAgentId,
                                                productAgentName,
                                                productAgentImage,
                                                productLocation,
                                                productFavourite,
                                                productImage,
                                                productCurrency,
                                                productDiscountPercentageEnabled,
                                                productDiscountPercentage,
                                                productPrice,
                                                productFinalPrice,
                                                priceEnabledId,
                                                priceEnabledStatus,
                                                discountPriceEnabledId,
                                                discountPriceEnabledStatus,
                                                offerType,
                                                offerTypeId,
                                                dealExpiredDate,
                                                dealExclusiveStatus,
                                                productDDLoyaltyPrice
                                            )
                                            mList.add(dl)

                                        }
                                       // Log.e("mList", ">>" + mList.size)
                                        try {
                                            if (isFirstTime) {
                                                dialogManager.stopProcessDialog()
                                                if (mList.size == 0) {
                                                    binding.llNoData.visibility = View.VISIBLE
                                                    binding.rvDeals.visibility = View.GONE
                                                } else {
                                                    binding.llNoData.visibility = View.GONE
                                                    binding.rvDeals.visibility = View.VISIBLE
                                                }
                                            }
                                            adap.notifyItemInserted(mList.size)
                                            binding.rvDeals.scrollToPosition(mList.size + 1);
                                            //adap.setLoaded()

                                        } catch (e: Exception) {
                                        }
                                    } else {
                                        try {
                                            if (isFirstTime) {
                                                dialogManager.stopProcessDialog()
                                                if (mList.size == 0) {

                                                    binding.llNoData.visibility = View.VISIBLE
                                                    binding.rvDeals.visibility = View.GONE
                                                } else {
                                                    binding.llNoData.visibility = View.GONE
                                                    binding.rvDeals.visibility = View.VISIBLE
                                                }
                                            }
                                            adap.notifyItemInserted(mList.size)
                                            //  binding.rvDeals.scrollToPosition(mList.size +1);
                                            adap.setLoaded()

                                        } catch (e: Exception) {
                                        }
                                    }
                                } catch (e: Exception) {
                                  //  Log.e("ExTopList", e.toString())
                                    dialogManager.stopProcessDialog()
                                }

                                dialogManager.stopProcessDialog()

                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS), ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding.mainLayout,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: Exception) {
                          //  Log.e("Excep", e.toString())
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding.mainLayout,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.mainLayout, MessageConstant.MESSAGE_SOMETHING_WRONG)
                       // Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tvLocation, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.mainLayout, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }




    override fun onResume() {
        super.onResume()
        StatusBarcolor.setStatusbarColor(this@DailyDeals, "")

        if (binding.tvLocation.text.toString().trim().length == 0) {
            binding.tvLocation.text = "Location"
            if (MainActivity.userLat != 0.0) {
                userLat = MainActivity.userLat
                userLang = MainActivity.userLang
                binding.tvLocation.text = MainActivity.address
            } else {
                getLocation()
            }
        }
    }

    private fun getLocation(): Boolean {

        GPSTracker.requestSingleUpdate(this@DailyDeals, object : GPSTracker.LocationCallback {
            override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
         //   Log.e("Location on MainAct is ", location.toString())
            userLat = location!!.latitude.toDouble()
            userLang = location.longitude.toDouble()
          //  Log.e("Location", userLang.toString() + "")
            PreferenceHelper.getInstance(this@DailyDeals)?.lat = userLat.toString() + ""
            PreferenceHelper.getInstance(this@DailyDeals)?.lng = userLang.toString() + ""
            getLocationAddress()
            }
        })
        return userLat !== 0.0
    }

    private fun getLocationAddress() {
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
                val fullAddress = addresses[0].getAddressLine(1)
                val city = addresses[0].locality
                val countryName = addresses[0].getAddressLine(2)
                if (binding.tvLocation.text.toString().trim().equals("Location")) {
                    if (city == null) {
                        binding.tvLocation.text = cityName
                    } else {
                        binding.tvLocation.text = city
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.llLocation -> {
                startActivityForResult(
                    Intent(this@DailyDeals, SelectLocationActivity::class.java),
                    90
                )
            }
        }
    }

    override val contentResId: Int
        get() = R.layout.dailydeals


    fun moveToDetails(productId: String?, agentId: String?, pos: Int) {
        val intent = Intent(this@DailyDeals, LatestProductDetails::class.java)
        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        intent.putExtra("type", "non_direct")
        intent.putExtra("pos", pos)
        startActivityForResult(intent, 80)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 80) {
            if (data != null) {
              //  Log.e("Faa", data.getStringExtra("fav")!!)
                val fav = data.getStringExtra("fav")
                if (fav.equals("0", ignoreCase = true)) {
                    try {
                        val position = data.getIntExtra("pos", 0)
                        mList.get(position)!!.productFavourite = "0"
                        adap.notifyItemRangeChanged(data.getIntExtra("pos", 0), mList.size)
                        if (mList.size == 0) {
                            binding.llNoData.visibility = View.GONE
                            binding.rvDeals.visibility = View.VISIBLE
                        } else {
                            binding.llNoData.visibility = View.GONE
                            binding.rvDeals.visibility = View.VISIBLE
                        }
                    } catch (e: java.lang.Exception) {
                    }
                } else if (fav.equals("1", ignoreCase = true)) {
                    try {
                        val position = data.getIntExtra("pos", 0)
                        mList.get(position)!!.productFavourite = "1"
                        adap.notifyItemRangeChanged(data.getIntExtra("pos", 0), mList.size)
                        if (mList.size == 0) {
                            binding.llNoData.visibility = View.GONE
                            binding.rvDeals.visibility = View.VISIBLE
                        } else {
                            binding.llNoData.visibility = View.GONE
                            binding.rvDeals.visibility = View.VISIBLE
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
        } else if (requestCode == 90 && resultCode == Activity.RESULT_OK) {
            try {
                userLat = data!!.getDoubleExtra("latitude", 0.0)
                userLang = data.getDoubleExtra("longitude", 0.0)
                binding.tvLocation.text = data.getStringExtra("locationName")
                MainActivity.userLat = userLat
                MainActivity.userLang = userLang
                MainActivity.address = data.getStringExtra("locationName")
                if (mList.size > 0) {
                    mList.clear()
                }

                offset = 0
                isFirstTime = true
                binding.shimmerViewContainer.setVisibility(View.VISIBLE)
                binding.shimmerViewContainer.startShimmerAnimation()
                binding.mainLayout.setVisibility(View.GONE)
                getAllDetails(true)
            } catch (e: Exception) {
            }
        }
    }
}