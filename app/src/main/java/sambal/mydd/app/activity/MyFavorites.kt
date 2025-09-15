package sambal.mydd.app.activity

import sambal.mydd.app.callback.ItemClickedPositionCallback
import sambal.mydd.app.beans.ExpiredSoonModel
import org.json.JSONArray
import sambal.mydd.app.adapter.AdapterFavourite
import sambal.mydd.app.database.DatabaseHandler
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.WrapContentLinearLayoutManager
import org.json.JSONObject
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.utils.GPSTracker
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.AppConfig
import com.google.android.material.snackbar.Snackbar
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.constant.IntentConstant
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
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

class MyFavorites : BaseActivity(), ItemClickedPositionCallback, View.OnClickListener {
    var modelList: MutableList<ExpiredSoonModel?> = ArrayList()
    private var context: Context? = null
    private var favouriteArrayList: JSONArray? = null
    private var adapter: AdapterFavourite? = null
    private var db: DatabaseHandler? = null
    private var clickedPosition = 0
    var offset = 0
    var count = "10"
    private var binding: DailydealsBinding? = null
    private var userLat = 0.0
    private var userLang = 0.0
    private var Count_item = 0

    override val contentResId: Int
        get() = R.layout.dailydeals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.dailydeals)
        context = this@MyFavorites
        setToolbarWithBackButton_colorprimary("Favourites")
        userLat = MainActivity.userLat
        userLang = MainActivity.userLang
        init()
    }

    private fun init() {
        binding!!.llLocation.setOnClickListener(this)
        db = DatabaseHandler(this@MyFavorites)
        binding!!.llSearch.visibility = View.VISIBLE
        binding!!.rvDeals.setHasFixedSize(false)
        binding!!.rvDeals.layoutManager = WrapContentLinearLayoutManager(context)
        adapter = AdapterFavourite(
            this@MyFavorites,
            modelList,
            binding!!.rvDeals
        ) { jsonObject, eventHasMultipleParts ->
            // getMyFavourite();
        }
        binding!!.llSearch.setOnClickListener {
            val intent = Intent(context, SearchAllNewActivity::class.java)
            startActivityForResult(intent, 85)
        }
        binding!!.rvDeals.adapter = adapter
        adapter!!.notifyDataSetChanged()
        modelList.clear()
        getMyFavourite(true, true)
        binding!!.rvDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        getMyFavourite(true, false)
                    }
                } catch (e: Exception) {
                }
            }
        })
        binding!!.tvLocation.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@MyFavorites,
                    SelectLocationActivity::class.java
                ), 90
            )
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@MyFavorites, "")
        } catch (e: Exception) {
        }
        if (binding != null && binding!!.tvLocation != null) {
            if (binding!!.tvLocation.text.toString().trim { it <= ' ' }
                    .equals("Location", ignoreCase = true)) {
                binding!!.tvLocation.text = "Location"
                if (MainActivity.userLat != 0.0) {
                    userLat = MainActivity.userLat
                    userLang = MainActivity.userLang
                    binding!!.tvLocation.text =
                        if (MainActivity.address == "") "Location" else MainActivity.address
                } else {
                    location
                }
            }
        }
    }

    private val location: Unit
        private get() {
            GPSTracker.requestSingleUpdate(this@MyFavorites,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        if (location != null) {
                            Log.e("Location on MainAct is ", location.toString())
                            userLat = location.latitude.toDouble()
                            userLang = location.longitude.toDouble()
                            PreferenceHelper.getInstance(context)?.lat = userLat.toString() + ""
                            PreferenceHelper.getInstance(context)?.lng = userLang.toString() + ""
                            locationAddress
                        }
                    }
                })
        }

    private fun initView(isFirstTime: Boolean) {
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
            adapter!!.notifyItemInserted(modelList.size)
            Count_item = if (!isFirstTime) {
                binding!!.rvDeals.scrollToPosition(Count_item + 1)
                modelList.size
            } else {
                modelList.size
            }
            if (modelList.size > 9) {
                adapter!!.setLoaded()
            }
            binding!!.rvDeals.visibility = View.VISIBLE
        }
        if (isFirstTime) {
            if (modelList.size > 0 && isFirstTime) {
                binding!!.llNoData.visibility = View.GONE
                binding!!.rvDeals.visibility = View.VISIBLE
            } else {
                binding!!.llNoData.visibility = View.VISIBLE
                binding!!.rvDeals.visibility = View.GONE
            }
        }
    }

    fun getMyFavourite(showLoader: Boolean, isFirstTime: Boolean) {
        favouriteArrayList = null
        val agentIds = ""
        if (AppUtil.isNetworkAvailable(context)) {
            if (isFirstTime) {
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
            }
            val dialogManager = DialogManager()
            if (showLoader) {
                dialogManager.showProcessDialog(this@MyFavorites, "", false, null)
            }
            ErrorMessage.E("ACTIVITY LAT>> $userLat LONG>> $userLang")
            val call = AppConfig.api_Interface().getMyFavouriteDeals(
                userLat.toString(),
                userLang.toString(),
                offset.toString(),
                count
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ResFa", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(
                                    KeyConstant.KEY_RESPONSE_CODE_200,
                                    ignoreCase = true
                                )
                            ) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                if (showLoader) {
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                }
                                favouriteArrayList =
                                    responseObj.optJSONArray(KeyConstant.KEY_FAVOURITE_PRODUCT_LIST)
                                ErrorMessage.E("LIST_SIZE >>> " + favouriteArrayList?.length())
                                if (isFirstTime) {
                                    if (favouriteArrayList != null && favouriteArrayList?.length()!! > 0) {
                                        binding!!.llNoData.visibility = View.GONE
                                        binding!!.rvDeals.visibility = View.VISIBLE
                                    } else {
                                        binding!!.llNoData.visibility = View.VISIBLE
                                        binding!!.rvDeals.visibility = View.GONE
                                    }
                                }

                                runOnUiThread {
                                    if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                                        if (showLoader) {
                                            dialogManager.stopProcessDialog()
                                        }
                                    }
                                    binding!!.shimmerViewContainer.visibility = View.GONE
                                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                                    binding!!.mainLayout.visibility = View.VISIBLE
                                    initView(isFirstTime)
                                }
                            } else if (errorType.equals(
                                    KeyConstant.KEY_RESPONSE_CODE_202,
                                    ignoreCase = true
                                )
                            ) {
                                favouriteArrayList = null
                                runOnUiThread {
                                    if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                                        if (showLoader) {
                                            dialogManager.stopProcessDialog()
                                        }
                                    }
                                    binding!!.shimmerViewContainer.visibility = View.GONE
                                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                                    binding!!.mainLayout.visibility = View.VISIBLE
                                    if (isFirstTime) {
                                        if (modelList.size > 0) {
                                            binding!!.llNoData.visibility = View.GONE
                                            binding!!.rvDeals.visibility = View.VISIBLE
                                        } else {
                                            binding!!.llNoData.visibility = View.VISIBLE
                                            binding!!.rvDeals.visibility = View.GONE
                                        }
                                    }
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                    Snackbar.make(
                                        binding!!.llNoData,
                                        "Data not found!",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
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
                    val position = data.getIntExtra("pos", 0)
                    modelList[position]!!.productFavourite = "0"
                    modelList.removeAt(data.getIntExtra("pos", 0))
                    adapter!!.notifyItemRemoved(data.getIntExtra("pos", 0))
                    adapter!!.notifyItemRangeChanged(data.getIntExtra("pos", 0), modelList.size)
                    if (modelList.size == 0) {
                        binding!!.llNoData.visibility = View.GONE
                        binding!!.rvDeals.visibility = View.VISIBLE
                    } else {
                        binding!!.llNoData.visibility = View.GONE
                        binding!!.rvDeals.visibility = View.VISIBLE
                    }
                } else if (fav.equals("1", ignoreCase = true)) {
                    try {
                        val position = data.getIntExtra("pos", 0)
                        modelList[position]!!.productFavourite = "1"
                        adapter!!.notifyItemRangeChanged(data.getIntExtra("pos", 0), modelList.size)
                        if (modelList.size == 0) {
                            binding!!.llNoData.visibility = View.GONE
                            binding!!.rvDeals.visibility = View.VISIBLE
                        } else {
                            binding!!.llNoData.visibility = View.GONE
                            binding!!.rvDeals.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        } else if (requestCode == 90 && resultCode == RESULT_OK) {
//            userLat = data!!.getStringExtra("latitude")!!.toDouble()
//            userLang = data.getStringExtra("longitude")!!.toDouble()
//            binding!!.tvLocation.text = data.getStringExtra("locationName")

            userLat = data!!.getStringExtra("lat")!!.toDouble()
            userLang = data.getStringExtra("lng")!!.toDouble()
            binding!!.tvLocation.text = data.getStringExtra("name")

            MainActivity.userLat = userLat
            MainActivity.userLang = userLang
            MainActivity.address = data.getStringExtra("name")
            modelList.clear()
            favouriteArrayList = null
            offset = 0
            getMyFavourite(true, true)
            adapter!!.notifyDataSetChanged()
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
                    val fullAddress = addresses[0].getAddressLine(1)
                    val city = addresses[0].locality
                    val countryName = addresses[0].getAddressLine(2)
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llLocation -> startActivityForResult(
                Intent(
                    this@MyFavorites,
                    SelectLocationActivity::class.java
                ), 90
            )
        }
    }

    companion object {
        val newInstance: MyFavorites
            get() = MyFavorites()
    }
}