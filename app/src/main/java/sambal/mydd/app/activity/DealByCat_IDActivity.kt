package sambal.mydd.app.activity

import sambal.mydd.app.callback.ItemClickedPositionCallback
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.beans.ExpiredSoonModel
import android.widget.TextView
import org.json.JSONArray
import sambal.mydd.app.adapter.DealByCat_ID_Adapter
import sambal.mydd.app.database.DatabaseHandler
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import sambal.mydd.app.utils.WrapContentLinearLayoutManager
import org.json.JSONObject
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.utils.GPSTracker
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.constant.IntentConstant
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.R
import sambal.mydd.app.databinding.DailydealsBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*

class DealByCat_IDActivity : BaseActivity(), ItemClickedPositionCallback {
    var recyclerView: RecyclerView? = null
    var isFirstTime = false
    var modelList: MutableList<ExpiredSoonModel> = ArrayList()
    private var context: Context? = null
    private var favouriteArrayList: JSONArray? = null
    var adapter: DealByCat_ID_Adapter? = null
    private var db: DatabaseHandler? = null
    private var clickedPosition = 0
    var handler: Handler? = null
    var offset = 0
    var count = "10"
    private var binding: DailydealsBinding? = null
    private var userLat = 0.0
    private var userLang = 0.0
    var Cat_id: String? = ""
    var Cat_Name: String? = " "
    private var listLoadMore = 0

    override val contentResId: Int
        get() = R.layout.dailydeals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.dailydeals)
        setToolbarWithBackButton_colorprimary("")
        context = this@DealByCat_IDActivity
        val bundle = intent.extras
        if (bundle != null) {
            Cat_id = bundle.getString("Cat_id")
            if ( !bundle.getString("Cat_Name").equals("")) {
                Cat_Name = bundle.getString("Cat_Name")
            }
            setToolbarWithBackButton_colorprimary(Cat_Name.toString())
        }
        listLoadMore = 0
        modelList.clear()
        init()
    }

    private fun init() {
        binding!!.llLocation.setOnClickListener {
            startActivityForResult(Intent(this@DealByCat_IDActivity,
                SelectLocationActivity::class.java), 90)
        }
        handler = Handler()
        db = DatabaseHandler(this@DealByCat_IDActivity)
        recyclerView = findViewById(R.id.rvDeals)
        recyclerView!!.setHasFixedSize(false)
        recyclerView!!.setLayoutManager(WrapContentLinearLayoutManager(context))
        adapter = DealByCat_ID_Adapter(this@DealByCat_IDActivity,
            modelList,
            recyclerView) { jsonObject, eventHasMultipleParts ->
            // getMyFavourite();
        }
        recyclerView!!.setAdapter(adapter)
        recyclerView!!.setItemViewCacheSize(modelList.size)
        adapter!!.notifyDataSetChanged()
        modelList.clear()
        isFirstTime = true
        getMyFavourite(true)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirstTime = false
                        getMyFavourite(false)
                    }
                } catch (e: Exception) {
                }
            }
        })
        binding!!.tvLocation.setOnClickListener { }
        userLat = MainActivity.userLat
        userLang = MainActivity.userLang
    }

    public override fun onResume() {
        super.onResume()
        StatusBarcolor.setStatusbarColor(this@DealByCat_IDActivity, "")
        binding!!.tvLocation.text = "Location"
        if (binding!!.tvLocation.text.toString().trim { it <= ' ' }
                .equals("Location", ignoreCase = true)) {
            binding!!.tvLocation.text = "Location"
            if (MainActivity.userLat != 0.0) {
                userLat = MainActivity.userLat
                userLang = MainActivity.userLang
                binding!!.tvLocation.text = MainActivity.address
            } else {
                location
            }
        }
    }

    private val location: Boolean
        private get() {
            GPSTracker.requestSingleUpdate(this@DealByCat_IDActivity, object : GPSTracker.LocationCallback {
                override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                    Log.e("Location on MainAct is ", location.toString())
                    userLat = location!!.latitude.toDouble()
                    userLang = location.longitude.toDouble()
                    PreferenceHelper.getInstance(context)?.lat = userLat.toString() + ""
                    PreferenceHelper.getInstance(context)?.lng = userLang.toString() + ""
                    locationAddress
                }
            })
            return userLat != 0.0
        }

    private fun initView() {
        if (favouriteArrayList!!.length() > 0) {
            for (i in 0 until favouriteArrayList!!.length()) {
                val `object` = favouriteArrayList!!.optJSONObject(i)
                val model = ExpiredSoonModel()
                val productCategoryId =
                    `object`.optInt(KeyConstant.KEY_PRODUCT_CATEGORY_ID).toString() + ""
                model.productCategoryId = productCategoryId
                val productId = `object`.optInt(KeyConstant.KEY_PRODUCT_ID).toString() + ""
                val productName = `object`.optString(KeyConstant.KEY_PRODUCT_NAME)
                val productAgentName = `object`.optString(KeyConstant.KEY_PRODUCT_AGENT_NAME)
                val productCategoryName = `object`.optString(KeyConstant.KEY_PRODUCT_CATEGORY_NAME)
                val productDesc = `object`.optString(KeyConstant.KEY_PRODUCT_DESC)
                val produImage = `object`.optString("productAgentImage")
                model.productAgentImage = produImage
                val productLocation = `object`.optString(KeyConstant.KEY_PRODUCT_LOCATION)
                val productFavorite = `object`.optString(KeyConstant.KEY_PRODUCT_FAVORITE)
                val productImage = `object`.optString(KeyConstant.KEY_PRODUCT_IMAGE)
                val productCurrency = `object`.optString(KeyConstant.KEY_PRODUCT_CURRENCY)
                val df = DecimalFormat("#.##")
                val productDiscountPercentage =
                    `object`.optString(KeyConstant.KEY_PRODUCT_DISCOUNT_PERCENTAGE)
                val productPrice = `object`.optString(KeyConstant.KEY_PRODUCT_PRICE) + ""
                val productFinalPrice = `object`.optString(KeyConstant.KEY_PRODUCT_FINAL_PRICE) + ""
                val productTotalReedom = `object`.optString(KeyConstant.KEY_PRODUCT_TOTAL_REDEEOM)
                val productType = `object`.optString(KeyConstant.KEY_PRODUCT_TYPE)
                val productTypeId = `object`.optInt(KeyConstant.KEY_PRODUCT_TYPE_ID).toString() + ""
                val dealExpiredDate = `object`.optString(KeyConstant.KEY_DEAL_EXPIRED_DATE)
                val offerTypeId = `object`.optInt(KeyConstant.KEY_OFFER_TYPE_ID).toString() + ""
                val offerGiftEnabled =
                    `object`.optInt(KeyConstant.KEY_OFFER_GIFT_ENABLED).toString() + ""
                val offerGiftDescription = `object`.optString(KeyConstant.KEY_GIFT_DESCRIPTION)
                val offerLimitedEnabled =
                    `object`.optInt(KeyConstant.KEY_OFFER_LIMITED_ENABLED).toString() + ""
                val priceEnabledId =
                    `object`.optInt(KeyConstant.KEY_PRICE_ENABLED_ID).toString() + ""
                val discountPriceEnabledId =
                    `object`.optInt(KeyConstant.KEY_DISCOUNT_PRICE_ENABLED_ID).toString() + ""
                val productDiscountPercentageEnabled =
                    `object`.optInt(KeyConstant.KEY_PRODUCT_DISCOUNT_PERCENTAGE_ENABLED)
                        .toString() + ""
                val productDistance = `object`.optString(KeyConstant.KEY_PRODUCT_DISTANCE)
                val productAgentId =
                    `object`.optInt(KeyConstant.KEY_PRODUCT_AGENT_ID).toString() + ""
                val corporateDeal = `object`.optInt(KeyConstant.KEY_CORPORATE_DEAL).toString() + ""
                val moreProductText = `object`.optString(KeyConstant.KEY_MORE_PRODUCT_TEXT)
                val agentExternalURLEnable = `object`.optString("agentExternalURLEnable")
                val agentExternalURL = `object`.optString("agentExternalURL")
                val arr = `object`.optJSONArray("dealBannerList")
                model.arrayGallery = arr
                model.agentExternalURLEnable = agentExternalURLEnable
                model.agentExternalURL = agentExternalURL
                model.corporateDeal = corporateDeal
                model.moreProductText = moreProductText
                model.agentId = productAgentId
                model.productDistance = productDistance
                model.priceEnabledId = priceEnabledId
                model.discountPriceEnabledId = discountPriceEnabledId
                model.productDiscountPercentageEnabled = productDiscountPercentageEnabled
                model.productId = productId
                model.productName = productName
                model.productAgentName = productAgentName
                model.productCategoryName = productCategoryName
                model.productDesc = productDesc
                model.productLocation = productLocation
                model.productFavourite = productFavorite
                model.productImage = productImage
                model.productCurrency = productCurrency
                model.productDiscountPercentage = productDiscountPercentage
                model.productPrice = productPrice
                model.productFinalPrice = productFinalPrice
                model.productTotalReedom = productTotalReedom
                model.productType = productType
                model.productTypeId = productTypeId
                model.dealExpiredDate = dealExpiredDate
                model.offerTypeId = offerTypeId
                model.offerGiftEnabled = offerGiftEnabled
                model.offerGiftDescription = offerGiftDescription
                model.offerLimitedEnabled = offerLimitedEnabled
                model.dealStatus = `object`.optString("dealStatus")
                model.dealStatusId = `object`.optString("dealStatusId")
                model.dealId = `object`.optString("dealId")
                modelList.add(model)
            }
            Log.e("getMyFavourite", " >>" + modelList.size)
            adapter!!.notifyItemInserted(modelList.size)
            if (modelList.size > 9) {
                adapter!!.setLoaded()
            }
            recyclerView!!.visibility = View.VISIBLE
        }
        if (isFirstTime) {
            if (modelList.size > 0 && isFirstTime) {
                binding!!.llNoData.visibility = View.GONE
                recyclerView!!.visibility = View.VISIBLE
            } else {
                binding!!.llNoData.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
            }
        }
    }

    private fun getMyFavourite(showLoader: Boolean) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().getCategoryDealsList(Cat_id,
                userLat.toString(),
                userLang.toString(),
                offset.toString(),
                count)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            dialogManager.stopProcessDialog()
                        } catch (e: Exception) {
                        }
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("getMyFavourite", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                favouriteArrayList =
                                    responseObj.optJSONArray(KeyConstant.KEY_DEALS_LIST)
                                runOnUiThread {
                                    if (showLoader) {
                                        dialogManager.stopProcessDialog()
                                    }
                                    binding!!.shimmerViewContainer.visibility = View.GONE
                                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                                    binding!!.mainLayout.visibility = View.VISIBLE
                                    if (isFirstTime) {
                                        if (favouriteArrayList!=null && favouriteArrayList!!.length() > 0) {
                                            binding!!.llNoData.visibility = View.GONE
                                            recyclerView!!.visibility = View.VISIBLE
                                        } else {
                                            binding!!.llNoData.visibility = View.VISIBLE
                                            recyclerView!!.visibility = View.GONE
                                        }
                                    }
                                    initView()
                                }
                            } else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202,
                                    ignoreCase = true)
                            ) {
                                favouriteArrayList = null
                                runOnUiThread {
                                    if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                                        dialogManager.stopProcessDialog()
                                    }
                                    binding!!.shimmerViewContainer.visibility = View.GONE
                                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                                    binding!!.mainLayout.visibility = View.VISIBLE
                                    if (isFirstTime) {
                                        if (modelList.size > 0) {
                                            binding!!.llNoData.visibility = View.GONE
                                            recyclerView!!.visibility = View.VISIBLE
                                        } else {
                                            binding!!.llNoData.visibility = View.VISIBLE
                                            recyclerView!!.visibility = View.GONE
                                        }
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", "" + e.toString())
                            //AppUtil.showMsgAlert(msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvLocation, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.llNoData, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun itemClickedPosition(position: Int, isFavourite: Int) {
        clickedPosition = position
    }

    fun moveToDetails(productId: String?, agentId: String?, pos: Int) {
        val intent = Intent(context, LatestProductDetails::class.java)
        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        intent.putExtra("type", "non_direct")
        intent.putExtra("pos", pos)
        startActivityForResult(intent, 80)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 80) {
            if (data != null) {
                Log.e("Faa", data.getStringExtra("fav")!!)
                val fav = data.getStringExtra("fav")
                if (fav.equals("0", ignoreCase = true)) {
                    try {
                        val position = data.getIntExtra("pos", 0)
                        modelList[position].productFavourite = "0"
                        adapter!!.notifyItemRangeChanged(data.getIntExtra("pos", 0), modelList.size)
                        if (modelList.size == 0) {
                            binding!!.llNoData.visibility = View.GONE
                            recyclerView!!.visibility = View.VISIBLE
                        } else {
                            binding!!.llNoData.visibility = View.GONE
                            recyclerView!!.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                    }
                } else if (fav.equals("1", ignoreCase = true)) {
                    try {
                        val position = data.getIntExtra("pos", 0)
                        modelList[position].productFavourite = "1"
                        adapter!!.notifyItemRangeChanged(data.getIntExtra("pos", 0), modelList.size)
                        if (modelList.size == 0) {
                            binding!!.llNoData.visibility = View.GONE
                            recyclerView!!.visibility = View.VISIBLE
                        } else {
                            binding!!.llNoData.visibility = View.GONE
                            recyclerView!!.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        } else if (requestCode == 90 && resultCode == RESULT_OK) {
            try {
                if (data!!.getStringExtra("latitude") != null && data.getStringExtra("longitude") != null) {
                    userLat = data.getStringExtra("latitude")!!.toDouble()
                    userLang = data.getStringExtra("longitude")!!.toDouble()
                    MainActivity.userLat = data.getStringExtra("latitude")!!.toDouble()
                    MainActivity.userLang = data.getStringExtra("longitude")!!.toDouble()
                    MainActivity.address = data.getStringExtra("locationName")
                    binding!!.tvLocation.text = data.getStringExtra("locationName")
                    modelList.clear()
                    favouriteArrayList = null
                    offset = 0
                    adapter!!.notifyDataSetChanged()
                    isFirstTime = true
                    getMyFavourite(true)
                }
            } catch (e: Exception) {
            }
        }
    }

    private val locationAddress: Unit
        private get() {
            Log.e("LcoationsAa", "LocationaAssa")
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
                    val city = addresses[0].locality
                    Log.e("City", "$city,$cityName")
                    if (binding!!.tvLocation.text.toString().trim { it <= ' ' }
                            .equals("Location", ignoreCase = true)) {
                        if (city == null) {
                            binding!!.tvLocation.text = cityName
                        } else {
                            binding!!.tvLocation.text = city
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun gotoAgentDetails(agentId: String, productId: String, adapterPosition: Int) {
        val intent = Intent(context, New_AgentDetails::class.java)
        intent.putExtra("direct", "")
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
        intent.putExtra("product_id", productId + "")
        intent.putExtra("position",
            (recyclerView!!.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition())
        startActivity(intent)
        // startActivityForResult(intent, 105);
    }

    companion object {
        val newInstance: DealByCat_IDActivity
            get() = DealByCat_IDActivity()
    }
}