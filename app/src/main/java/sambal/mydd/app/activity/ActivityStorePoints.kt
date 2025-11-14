package sambal.mydd.app.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.adapter.AdapterStorePoints
import sambal.mydd.app.adapter.BannerImage_Adapter
import sambal.mydd.app.apiResponse.ApiResponse
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.beans.*
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.StorepointsBinding
import sambal.mydd.app.models.RefreshCard
import sambal.mydd.app.utils.*
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class ActivityStorePoints : BaseActivity(), ChatHistoryCallback {
    private var binding: StorepointsBinding? = null
    private var offset = 0
    private var catId = "0"
    private val mAgentList = ArrayList<AgentMainBean>()
    private var adap: AdapterStorePoints? = null
    private var isFirst = true
    private var ivTick: ImageView? = null
    private var dialog1: Dialog? = null
    var viewpagerBannerImage: ViewPager? = null
    private var tvSuccess: TextView? = null
    var tabDots: TabLayout? = null
    var dialogManager: DialogManager? = null
    var context: Context? = null
    var count = 10
    var userChoosenTask: String? = null
    var bannerList: JSONArray? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var handler: Handler? = null
    var tvNoData: TextView? = null
    var packageManager1: PackageManager? = null
    var ivVoucher: ImageView? = null
    var pointsFAQ: String? = null
    var timer: Timer? = null
    var type: String? = "1"
    var lat: String? = ""
    var lng: String? = ""
    private var pubNubChat: PubNubChat? = null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private var selectedImagePath: String? = null
    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                handlePickedMedia(uri)
            } else {
                Log.d("ActivityStorePoints", "No media selected from picker")
            }
        }

    override val contentResId: Int
        get() = R.layout.storepoints

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.storepoints)
        setToolbarWithBackButton_colorprimary("My Rewards")
//        setToolbarWithBackButton_colorprimary("My Wallet")
        //LocalBroadcastManager.getInstance(ActivityStorePoints.this).registerReceiver(onNotice_refresh, new IntentFilter("refresh"));
        context = this@ActivityStorePoints
        val bundle = intent.extras
        if (bundle != null) {
            type = bundle.getString("type")
        }
        Log.e("tokan>>>", "" + PreferenceHelper.getInstance(context)?.refreshToken)
        lat = MainActivity.userLat.toString() + ""
        lng = MainActivity.userLang.toString() + ""
        mAgentList.clear()
        packageManager1 = getPackageManager()
        handler = Handler()
        catId = "0"
        offset = 0
        init()
        linearLayoutManager = WrapContentLinearLayoutManager(this@ActivityStorePoints)
        binding!!.rvDeals.setHasFixedSize(false)
        binding!!.rvDeals.layoutManager = linearLayoutManager
        adap = AdapterStorePoints(
            this@ActivityStorePoints,
            mAgentList,
            binding!!.rvDeals,
            this@ActivityStorePoints
        )
        binding!!.rvDeals.setItemViewCacheSize(mAgentList.size)
        binding!!.rvDeals.adapter = adap
        adap!!.notifyDataSetChanged()
        binding!!.rvDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        isFirst = false
                        offset++
                        getAllDetails(true)
                    }
                } catch (e: Exception) {
                }
            }
        })
        isFirst = true
        if (adap != null) {
            adap!!.notifyDataSetChanged()
            adap!!.setLoaded()
        }
        binding!!.storePointPageErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this)) {
                binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                getAllDetails(true)
            } else {
                AppUtil.showMsgAlert(
                    binding!!.tvToolbarLocation,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
    }

    private fun getAllDetails(isShowingLoader: Boolean) {
        bannerList = null
        if (dialogManager == null) {
            dialogManager = DialogManager()
        }
        ErrorMessage.E("getAllDetails lat>.$lat  lng>>$lng  offset>>$offset  catId>>$catId  type>>$type   count>>$count")
        if (AppUtil.isNetworkAvailable(this)) {
            if (isShowingLoader) {
                dialogManager!!.showProcessDialog(this@ActivityStorePoints, "", false, null)
                if (binding!!.shimmerViewContainer.visibility == View.VISIBLE) {
                    dialogManager!!.stopProcessDialog()
                }
            }
            catId = "0"
            val call = AppConfig.api_Interface().getMyPointListV3(
                lat,
                lng,
                offset.toString() + "",
                count.toString() + "",
                catId,
                type
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        if (response != null && response.code() == 200) {
                            try {
                                binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility =
                                    View.GONE
                                if (binding!!.shimmerViewContainer.visibility == View.VISIBLE) {
                                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                                    binding!!.shimmerViewContainer.visibility = View.GONE
                                }
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("ActivityStore", ">$resp")
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                    val responseobj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    if (dialogManager != null) {
                                        dialogManager!!.stopProcessDialog()
                                    }
                                    /*  productCatList = responseobj.optJSONArray("categoryList");*/bannerList =
                                        responseobj.optJSONArray("bannerList")
                                    val arr = responseobj.optJSONArray("agentList")
                                    if (isFirst && arr.length() > 0) {
                                        mAgentList.clear()
                                    }
                                    for (i in 0 until arr.length()) {
                                        val obj = arr.getJSONObject(i)
                                        val agentId = obj.optString("agentId")
                                        val agentCompanyName = obj.optString("agentCompanyName")
                                        val agentAddress = obj.optString("agentAddress")
                                        val distance = obj.optString("distance")
                                        val agentStandardPointStatus =
                                            obj.optString("agentStandardPointStatus")
                                        val agentDoublePointStatus =
                                            obj.optString("agentDoublePointStatus")
                                        val agentBonusPointStatus =
                                            obj.optString("agentBonusPointStatus")
                                        val agentBonusPoint = obj.optString("agentBonusPoint")
                                        val donateStatus = obj.optString("donateStatus")
                                        val charityName = obj.optString("charityName")
                                        val charityMemberCount = obj.optString("charityMemberCount")
                                        val charityDonatedText = obj.optString("charityDonatedText")
                                        val agentRecommendText = obj.optString("agentRecommendText")
                                        val agentPointStartDate =
                                            obj.optString("agentPointStartDate")
                                        val agentPointEndDate = obj.optString("agentPointEndDate")
                                        val userEarnedPoints = obj.optString("userEarnedPoints")
                                            .replace("null".toRegex(), "")
                                        val targetPoints = obj.optString("targetPoints")
                                        val charityEnabled = obj.optString("charityEnabled")
                                        val redeemRemarks = obj.optString("redeemRemarks")
                                        val pointRemarks = obj.optString("pointRemarks")
                                        val minimumSpend = obj.optString("minimumSpend")
                                        val agentPointsFAQ = obj.optString("agentPointsFAQ")
                                        val agentRecommendEnabled =
                                            obj.optString("agentRecommendEnabled")
                                        val agentNotificationEnabled =
                                            obj.optString("agentNotificationEnabled")
                                        val agentNotificationCount =
                                            obj.optString("agentNotificationCount")
                                        val agentWalletType = obj.optString("agentWalletType")
                                        val agentUserTargetVisitCount =
                                            obj.optString("agentUserTargetVisitCount")
                                        val agentUserVisitCount =
                                            obj.optString("agentUserVisitCount")
                                        val agentUserTodayVisit =
                                            obj.optString("agentUserTodayVisit")
                                        val agentUserFreeDealID =
                                            obj.optString("agentUserFreeDealID")
                                        val agentUserFreeDealName =
                                            obj.optString("agentUserFreeDealName")
                                        val agentUserFreeDealImage =
                                            obj.optString("agentUserFreeDealImage")
                                        val agentUserDealStatus =
                                            obj.optString("agentUserDealStatus")
                                        val agentUserFreeDealStatus =
                                            obj.optString("agentUserFreeDealStatus")
                                        val agentUserFreeDealText =
                                            obj.optString("agentUserFreeDealText")
                                        val membershipStatus = obj.optString("membershipStatus")
                                        val membershipImage = obj.optString("membershipImage")
                                        val mcolorList = ArrayList<VisitcolorBean>()
                                        mcolorList.clear()
                                        if (obj.has("visitList")) {
                                            val arrColor = obj.optJSONArray("visitList")
                                            for (l in 0 until arrColor.length()) {
                                                val ob = arrColor.optJSONObject(l)
                                                val visitColor = ob.optString("visitColor")
                                                val vb = VisitcolorBean(visitColor)
                                                mcolorList.add(vb)
                                            }
                                        }
                                        val arrV = obj.optJSONArray("voucherList")
                                        val mVoucherList = ArrayList<AgentVoucherListBean>()
                                        mVoucherList.clear()
                                        for (j in 0 until arrV.length()) {
                                            val ob = arrV.optJSONObject(j)
                                            val voucherId = ob.optString("voucherId")
                                            val voucherSerialNumber =
                                                ob.optString("voucherSerialNumber")
                                            val voucherNumber = ob.optString("voucherNumber")
                                            val voucherUUID = ob.optString("voucherUUID")
                                            val voucherPrice = ob.optString("voucherPrice")
                                            val currency = ob.optString("currency")
                                            val voucherRedeemEnabled =
                                                ob.optString("voucherRedeemEnabled")
                                            val voucherText = ob.optString("voucherText")
                                            val voucherRedeemedText =
                                                ob.optString("voucherRedeemedText")
                                            val voucherRedeemedPrice =
                                                ob.optString("voucherRedeemedPrice")
                                            val voucherColorCode =
                                                ob.optString("voucherColorCode")
                                            val avm = AgentVoucherListBean(
                                                voucherId,
                                                voucherSerialNumber,
                                                voucherNumber,
                                                voucherUUID,
                                                voucherPrice,
                                                currency,
                                                charityEnabled,
                                                voucherRedeemEnabled,
                                                voucherText,
                                                voucherRedeemedText,
                                                voucherRedeemedPrice,
                                                voucherColorCode

                                            )
                                            mVoucherList.add(avm)
                                        }
                                        val giftcard = obj.optJSONArray("giftCardList")
                                        val giftCardListList = ArrayList<GiftCard>()
                                        giftCardListList.clear()
                                        for (j in 0 until giftcard.length()) {
                                            val ob = giftcard.optJSONObject(j)
                                            val giftId = ob.optInt("giftId")
                                            val giftVoucherId = ob.optInt("giftVoucherId")
                                            val giftBackgroundImage =
                                                ob.optString("giftBackgroundImage")
                                            val agentImage = ob.optString("agentImage")
                                            val giftPrice = ob.optString("giftPrice")
                                            val currency = ob.optString("currency")
                                            val giftText = ob.optString("giftText")
                                            val giftExpireDate = ob.optString("giftExpireDate")
                                            val giftSellingPrice = ob.optString("giftSellingPrice")
                                            val discountValue = ob.optString("discountValue")
                                            val giftTextRemark = ob.optString("giftTextRemark")
                                            val borderColor = ob.optString("borderColor")
                                            val giftStatus = ob.optInt("giftStatus")
                                            val avm = GiftCard(
                                                giftId,
                                                giftVoucherId,
                                                giftStatus,
                                                giftBackgroundImage,
                                                agentImage,
                                                giftPrice,
                                                giftSellingPrice,
                                                currency,
                                                discountValue,
                                                giftExpireDate,
                                                giftText,
                                                giftTextRemark,
                                                borderColor
                                            )
                                            giftCardListList.add(avm)
                                        }
                                        val mDealList = ArrayList<StorePointsDealsList>()
                                        mDealList.clear()
                                        val arDealList = obj.optJSONArray("dealsList")
                                        run {
                                            if (arDealList.length() > 0) {
                                                for (l in 0 until arDealList.length()) {
                                                    val objs = arDealList.optJSONObject(l)
                                                    val dealId = objs.optString("dealId")
                                                    val productId = objs.optString("productId")
                                                    val dealName = objs.optString("dealName")
                                                    val agentIds = objs.optString("agentId")
                                                    val agentName = objs.optString("agentName")
                                                    val viewCount = objs.optString("viewCount")
                                                    val type = objs.optString("type")
                                                    val dealFavourite =
                                                        objs.optString("dealFavourite")
                                                    val productCurrency =
                                                        objs.optString("productCurrency")
                                                    val productDiscountPercentage =
                                                        objs.optString("productDiscountPercentage")
                                                    val productPrice =
                                                        objs.optString("productPrice")
                                                    val productFinalPrice =
                                                        objs.optString("productFinalPrice")
                                                    val dealExpiredDate =
                                                        objs.optString("dealExpiredDate")
                                                    val offerType = objs.optString("offerType")
                                                    val offerTypeId = objs.optString("offerTypeId")
                                                    val dealExclusiveStatus =
                                                        objs.optString("dealExclusiveStatus")
                                                    val priceEnabledId =
                                                        objs.optString("priceEnabledId")
                                                    val discountPriceEnabledId =
                                                        objs.optString("discountPriceEnabledId")
                                                    val productDiscountPercentageEnabled =
                                                        objs.optString("productDiscountPercentageEnabled")
                                                    val priceEnabledStatus =
                                                        objs.optString("priceEnabledStatus")
                                                    val dealImage = objs.optString("dealImage")
                                                    val sp = StorePointsDealsList(
                                                        dealId,
                                                        productId,
                                                        dealName,
                                                        agentIds,
                                                        dealFavourite,
                                                        productCurrency,
                                                        productDiscountPercentage,
                                                        productPrice,
                                                        productFinalPrice,
                                                        dealExpiredDate,
                                                        offerType,
                                                        offerTypeId,
                                                        priceEnabledId,
                                                        discountPriceEnabledId,
                                                        productDiscountPercentageEnabled,
                                                        priceEnabledStatus,
                                                        dealImage,
                                                        agentName,
                                                        viewCount,
                                                        type,
                                                        dealExclusiveStatus
                                                    )
                                                    mDealList.add(sp)
                                                }
                                            }
                                        }
                                        val amb = AgentMainBean(
                                            agentId,
                                            agentCompanyName,
                                            agentAddress,
                                            distance,
                                            agentStandardPointStatus,
                                            agentDoublePointStatus,
                                            agentBonusPointStatus,
                                            agentBonusPoint,
                                            donateStatus,
                                            charityDonatedText,
                                            agentPointStartDate,
                                            agentPointEndDate,
                                            userEarnedPoints,
                                            targetPoints,
                                            redeemRemarks,
                                            pointRemarks,
                                            minimumSpend,
                                            agentPointsFAQ,
                                            obj.optString("termsAndConditions"),
                                            charityEnabled,
                                            agentRecommendEnabled,
                                            agentNotificationEnabled,
                                            agentNotificationCount,
                                            agentWalletType,
                                            agentUserTargetVisitCount,
                                            agentUserVisitCount,
                                            agentUserTodayVisit,
                                            agentUserFreeDealID,
                                            agentUserFreeDealName,
                                            agentUserFreeDealImage,
                                            agentUserDealStatus,
                                            agentUserFreeDealStatus,
                                            agentUserFreeDealText,
                                            mVoucherList,
                                            mDealList,
                                            mcolorList,
                                            charityName,
                                            charityMemberCount,
                                            membershipStatus,
                                            membershipImage,
                                            agentRecommendText,
                                            giftCardListList
                                        )
                                        mAgentList.add(amb)
                                        adap!!.notifyItemInserted(mAgentList.size)
                                        if (mAgentList.size > 9) {
                                            adap!!.setLoaded()
                                            binding!!.rvDeals.setItemViewCacheSize(mAgentList.size)
                                            binding!!.rvDeals.setHasFixedSize(true)
                                        }
                                    }
                                    if (isFirst) {
                                        if (mAgentList.size > 0) {
                                            binding!!.llNoData.visibility = View.GONE
                                            binding!!.rvDeals.visibility = View.VISIBLE
                                        } else {
                                            binding!!.llNoData.visibility = View.VISIBLE
                                            binding!!.rvDeals.visibility = View.GONE
                                        }
                                        updateUI(responseobj)
                                    }
                                }
                                if (errorType == "401") {
                                    refreshToken()
                                }
                                else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                            resp.optString(
                                                KeyConstant.KEY_STATUS
                                            ), ignoreCase = true
                                        )
                                    ) {
                                        if (dialogManager != null) {
                                            dialogManager!!.stopProcessDialog()
                                        }
                                        AppUtil.showMsgAlert(
                                            binding!!.tvToolbarLocation,
                                            resp.optString(KeyConstant.KEY_MESSAGE)
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                if (dialogManager != null) {
                                    dialogManager!!.stopProcessDialog()
                                }
                                AppUtil.showMsgAlert(
                                    binding!!.tvToolbarLocation,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG
                                )
                                binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility =
                                    View.VISIBLE
                            }
                        }
                        else if (response.code() != 401) {
                            refreshToken()
                        } else if (response.code() != 200) {
                            binding!!.llNoData.visibility = View.VISIBLE
                            binding!!.rvDeals.visibility = View.GONE
                        } else {
                            //swipeRefreshLayout.setRefreshing(false);
                            if (dialogManager != null) {
                                dialogManager!!.stopProcessDialog()
                            }
                            AppUtil.showMsgAlert(
                                binding!!.tvToolbarLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                            binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility =
                                View.VISIBLE
                        }
                    }
                    else {
                        if (dialogManager != null) {
                            dialogManager!!.stopProcessDialog()
                        }
                        binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility =
                            View.VISIBLE
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    if (dialogManager != null) {
                        dialogManager!!.stopProcessDialog()
                    }
                    binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility =
                        View.VISIBLE
                    AppUtil.showMsgAlert(binding!!.tvToolbarLocation, MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            })
        } else {
            binding!!.storePointPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
            AppUtil.showMsgAlert(
                binding!!.tvToolbarLocation,
                MessageConstant.MESSAGE_INTERNET_CONNECTION
            )
        }
    }

    private fun updateUI(responseObj: JSONObject) {
        try {
            viewpagerBannerImage!!.adapter =
                BannerImage_Adapter(this@ActivityStorePoints, bannerList!!)
            tabDots!!.setupWithViewPager(viewpagerBannerImage, true)
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    viewpagerBannerImage!!.post {
                        try {
                            viewpagerBannerImage!!.currentItem =
                                (viewpagerBannerImage!!.currentItem + 1) % bannerList!!.length()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            timer = Timer()
            timer!!.schedule(timerTask, 3000, 3000)
        } catch (e: Exception) {
        }
        pointsFAQ = responseObj.optString("agentPointsFAQ")
    }

    private fun init() {
        ivVoucher = findViewById(R.id.ivVoucher)
        viewpagerBannerImage = findViewById(R.id.viewpagerBannerImage)
        tabDots = findViewById(R.id.tabDots)
        tvNoData = findViewById(R.id.tvNoData)
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
        binding!!.ivHisto.setOnClickListener {
            startActivity(Intent(this@ActivityStorePoints, NewhistoryDashboardDetials::class.java))
            MyLog.onAnim(this@ActivityStorePoints)
        }
        binding!!.tvHistory.setOnClickListener {
            startActivity(Intent(this@ActivityStorePoints, NewhistoryDashboardDetials::class.java))
            MyLog.onAnim(this@ActivityStorePoints)
        }
        binding!!.llLocationLayout.setOnClickListener {
            binding!!.llLocationLayout.isEnabled = false
//            val intent1 = Intent(this@ActivityStorePoints, SelectLocationActivityStore::class.java)
            val intent1 = Intent(this@ActivityStorePoints, SelectLocationActivity::class.java)

            startActivityForResult(intent1, 40)
        }
        binding!!.shimmerViewContainer.visibility = View.VISIBLE
        binding!!.shimmerViewContainer.startShimmerAnimation()
        getAllDetails(true)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    public override fun onPause() {
        super.onPause()
        if (dialogManager != null) {
            dialogManager!!.stopProcessDialog()
        }
    }

    fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@ActivityStorePoints)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            when (items[item]) {
                "Take Photo" -> {
                    userChoosenTask = "Take Photo"
                    cameraIntent()
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
            Log.e("ActivityStorePoints", "Unable to launch photo picker", e)
            Toast.makeText(this, "Unable to open gallery", Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePickedMedia(uri: Uri) {
        try {
            val localPath = copyUriToCache(uri)
            selectedImagePath = localPath
            Log.d("ActivityStorePoints", "Image selected at $localPath")
            // TODO: Hook into upload/display flow if needed
        } catch (e: Exception) {
            Log.e("ActivityStorePoints", "Failed to handle picked media", e)
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

    fun showQRCODE(jsonObject: JSONObject, refreshCard: RefreshCard) {
        if (dialog1 == null) {
            dialog1 = Dialog(context!!, R.style.NewDialog)
            dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1!!.setContentView(R.layout.ddqrcode)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog1!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.FILL_PARENT
            lp.height = WindowManager.LayoutParams.FILL_PARENT
            dialog1!!.window!!.attributes = lp
            dialog1!!.setCanceledOnTouchOutside(false)
            dialog1!!.setCancelable(false)
            tvSuccess = dialog1!!.findViewById(R.id.tvSuccess)
            ivTick = dialog1!!.findViewById(R.id.ivTick)
            val ivClose = dialog1!!.findViewById<ImageView>(R.id.ivClose)
            val tvPrice = dialog1!!.findViewById<TextView>(R.id.tvPrice)
            ivClose.setOnClickListener {
                refreshCard.onSuccess("dwdg wygq")
                dialog1!!.dismiss()
                dialog1 = null
            }
            val tvTitle = dialog1!!.findViewById<TextView>(R.id.ticket_title)
            tvTitle.text = jsonObject.optString("")
            val ivQrCode = dialog1!!.findViewById<ImageView>(R.id.iv_qr_code)
            val ivBarCOde = dialog1!!.findViewById<ImageView>(R.id.ivBarcode)
            tvTitle.text = jsonObject.optString("agentName")

            /*      try {
                      // Convert the string to a double
                      val amount: Double = jsonObject.optString("redeemAmount").toDouble()

                      // Format the value to one decimal place
                      val decimalFormat = DecimalFormat("#.#")
                      val formattedAmount: String = decimalFormat.format(amount)

                      tvPrice.text = jsonObject.optString("currency") + formattedAmount

                  } catch (e: NumberFormatException) {
      */
            tvPrice.text = jsonObject.optString("currency") + jsonObject.optString("redeemAmount")

//            }

//            tvPrice.text = jsonObject.optString("currency") + jsonObject.optString("redeemAmount")
            val myBitmap = QRCode.from(jsonObject.optString("redeemUUID"))
                .bitmap()
            ivQrCode.setImageBitmap(myBitmap)
            pubNubChat = PubNubChat(context!!, this)
            pubNubChat!!.initPubNub()
            pubNubChat!!.subscribePubNubChannel(jsonObject.optString("redeemUUID") + "")
            pubNubChat!!.subscribePubNubListener()
            if (!dialog1!!.isShowing) {
                dialog1!!.show()
                refreshCard.onSuccess("dwdg wygq")
            }
        }
    }

    override fun onRefreshHistoryList(list: List<PNHistoryItemResult>) {}
    override fun clearData() {

    }

    fun refreshDeals(catIds: String) {
        Log.e("CatId", catIds)
        offset = 0
        catId = catIds
        mAgentList.clear()
        adap!!.notifyDataSetChanged()
        isFirst = false
        linearLayoutManager = LinearLayoutManager(this@ActivityStorePoints)
        binding!!.rvDeals.setHasFixedSize(false)
        binding!!.rvDeals.layoutManager = linearLayoutManager
        adap = AdapterStorePoints(
            this@ActivityStorePoints,
            mAgentList,
            binding!!.rvDeals,
            this@ActivityStorePoints
        )
        binding!!.rvDeals.setItemViewCacheSize(mAgentList.size)
        binding!!.rvDeals.adapter = adap
        adap!!.setLoaded()
        count = 10
        binding!!.shimmerViewContainer.visibility = View.VISIBLE
        binding!!.shimmerViewContainer.startShimmerAnimation()
        getAllDetails(true)
    }

    override fun onRefreshChatList(jsonObject: JsonObject) {
        isFirst = true
        runOnUiThread {
            try {
                ivTick!!.visibility = View.VISIBLE
                tvSuccess!!.visibility = View.VISIBLE
                try {
                    mAgentList.clear()
                    adap!!.notifyDataSetChanged()
                } catch (e: Exception) {
                }
                offset = 0
                linearLayoutManager = WrapContentLinearLayoutManager(this@ActivityStorePoints)
                binding!!.rvDeals.setHasFixedSize(false)
                binding!!.rvDeals.layoutManager = linearLayoutManager
                adap = AdapterStorePoints(
                    this@ActivityStorePoints,
                    mAgentList,
                    binding!!.rvDeals,
                    this@ActivityStorePoints
                )
                binding!!.rvDeals.setItemViewCacheSize(mAgentList.size)
                binding!!.rvDeals.adapter = adap
                isFirst = true
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                getAllDetails(true)
            } catch (e: Exception) {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("Req Code", "" + requestCode)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(
                    this@ActivityStorePoints,
                    "Please allow permissions to update userprofile pic",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Ress", requestCode.toString() + "")
        if (resultCode == RESULT_OK && requestCode == SELECT_FILE) {
            onSelectFromGalleryResult(data)
            return
        }
        if (requestCode == 40 && resultCode == RESULT_OK) {
            try {
                val name = data!!.getStringExtra("name")
                lat = data.getStringExtra("lat")
                lng = data.getStringExtra("lng")
                Log.e("Lat", "$name,$lat,$lng")
                binding!!.tvToolbarLocation.text = name
                try {
                    MainActivity.address = name
                    MainActivity.userLat = data.getStringExtra("lat")!!.toDouble()
                    MainActivity.userLang = data.getStringExtra("lng")!!.toDouble()
                } catch (e: Exception) {
                }
                try {
                    mAgentList.clear()
                    adap!!.notifyDataSetChanged()
                } catch (e: Exception) {
                }
                offset = 0
                linearLayoutManager = WrapContentLinearLayoutManager(this@ActivityStorePoints)
                binding!!.rvDeals.setHasFixedSize(false)
                binding!!.rvDeals.layoutManager = linearLayoutManager
                adap = AdapterStorePoints(
                    this@ActivityStorePoints,
                    mAgentList,
                    binding!!.rvDeals,
                    this@ActivityStorePoints
                )
                binding!!.rvDeals.setItemViewCacheSize(mAgentList.size)
                binding!!.rvDeals.adapter = adap
                isFirst = true
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                getAllDetails(true)
            } catch (e: Exception) {
                Log.e("Ex", e.toString())
            }
        } else if (resultCode == RESULT_OK && requestCode == 80) {
            if (data != null) {
                val fav = data.getStringExtra("fav")
                val pos = data.getIntExtra("pos", 0)
                val mainpos = data.getIntExtra("mainPos", 0)
                for (i in mAgentList.indices) {
                    for (j in mAgentList[i].getmDealList().indices) {
                        if (mAgentList[i].getmDealList()[j].productId.equals(
                                data.getStringExtra("id"),
                                ignoreCase = true
                            )
                        ) {
                            Log.e("ss", j.toString() + "")
                            mAgentList[i].getmDealList()[pos].dealFavourite = fav
                            //                                mAgentList.get(i).getmDealList().get(pos).setDealFavourite(fav);
                            adap!!.notifyItemChanged(i)
                            //                                AdapterStorePoints.adap.notifyItemRangeChanged(pos,mAgentList.get(j).getmDealList().size());
                            //adap.notifyItemRangeChanged(i,mAgentList.size());
                            break
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == 100) {
            try {
                StatusBarcolor.setStatusbarColor(this@ActivityStorePoints, "")
                mAgentList.clear()
                adap!!.notifyItemInserted(mAgentList.size)
                adap!!.notifyDataSetChanged()
                offset = 0
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                getAllDetails(true)
            } catch (e: Exception) {
            }
        } else if (requestCode == 140 && resultCode == RESULT_OK) {
            try {
                StatusBarcolor.setStatusbarColor(this@ActivityStorePoints, "")
                mAgentList.clear()
                adap!!.notifyItemInserted(mAgentList.size)
                adap!!.notifyDataSetChanged()
                offset = 0
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                getAllDetails(true)
            } catch (e: Exception) {
            }
        }
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        val uri = data?.data ?: return
        handlePickedMedia(uri)
    }

    override fun onResume() {
        super.onResume()
        try {
            if (StoreGiftCardActivity.somethingDone) {
                StoreGiftCardActivity.somethingDone = false
                offset = 0
                isFirst = true
                mAgentList.clear()
                binding!!.shimmerViewContainer.visibility = View.VISIBLE
                binding!!.shimmerViewContainer.startShimmerAnimation()
                getAllDetails(true)
                adap!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e("Exception", "" + e.toString())
        }
        binding!!.llLocationLayout.isEnabled = true
        try {
            StatusBarcolor.setStatusbarColor(this@ActivityStorePoints, "")
        } catch (e: Exception) {
        }
        try {
            if (binding!!.tvToolbarLocation.text.toString().contains("Location")) {
                GPSTracker.requestSingleUpdate(this@ActivityStorePoints,
                    object : GPSTracker.LocationCallback {
                        override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                            if (location != null) {
                                getLocationAddress(
                                    location.latitude.toDouble(),
                                    location.longitude.toDouble()
                                )
                            }
                        }
                    })
            }
        } catch (e: Exception) {
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (dialogManager != null) {
            dialogManager!!.stopProcessDialog()
        }
        try {
            mAgentList.clear()
            adap!!.notifyItemInserted(mAgentList.size)
            adap!!.notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }

    fun showFAQDialog(agentPoint: String?) {
        val dialog1 = Dialog(context!!, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.dialogstandard)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        val tvTitle = dialog1.findViewById<TextView>(R.id.ticket_title)
        ivClose.setOnClickListener { dialog1.dismiss() }
        tvTitle.text = agentPoint
        dialog1.show()
    }

    fun getCharityList(id: String?) {}
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    fun doCharity(agentId: String?, charityId: String) {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.doCharity(this,
            agentId,
            charityId,
            binding!!.tvHistory,
            object : ResponseListener {

                override fun onSuccess(response: ResponseBody?) {
                    try {
                        val resp = JSONObject(response!!.string())
                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                        if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                            val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                            Log.e("DoCharity", resp.toString() + "")
                            runOnUiThread {
                                try {
                                    Toast.makeText(
                                        this@ActivityStorePoints,
                                        resp.optString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    try {
                                        mAgentList.clear()
                                        adap!!.notifyDataSetChanged()
                                    } catch (e: Exception) {
                                    }
                                    offset = 0
                                    isFirst = true
                                    binding!!.shimmerViewContainer.visibility = View.VISIBLE
                                    binding!!.shimmerViewContainer.startShimmerAnimation()
                                    getAllDetails(true)
                                } catch (e: Exception) {
                                }
                            }
                        } else {
                            AdapterStorePoints.isChange = false
                            (context as Activity?)!!.runOnUiThread {
                                Toast.makeText(
                                    this@ActivityStorePoints,
                                    resp.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("Ex", e.toString())
                        try {
                            AdapterStorePoints.isChange = false
                        } catch (es: Exception) {
                        }
                        Toast.makeText(
                            this@ActivityStorePoints,
                            MessageConstant.MESSAGE_SOMETHING_WRONG,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e("Ex", e.toString())
                        try {
                            AdapterStorePoints.isChange = false
                        } catch (es: Exception) {
                        }
                        Toast.makeText(
                            this@ActivityStorePoints,
                            MessageConstant.MESSAGE_SOMETHING_WRONG,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(text: String?) {
                    ErrorMessage.E("ON FAILURE > " + text)
                    AppUtil.showMsgAlert(binding!!.tvHistory, text)
                }
            })
    }

    fun moveToDetails(productId: String?, agentId: String?, pos: Int, mainPos: Int) {
        val intent = Intent(context, LatestProductDetails::class.java)
        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        intent.putExtra("pos", pos)
        intent.putExtra("type", "non_direct")
        intent.putExtra("mainPos", mainPos)
        startActivityForResult(intent, 80)
    }

    fun goToAgentDetailPage(agentId: String?, position: Int) {
        val intent = Intent(this@ActivityStorePoints, New_AgentDetails::class.java)
        intent.putExtra("agentId", agentId)
        intent.putExtra("direct", "ActivityStorePoint")
        intent.putExtra("position", position)
        startActivityForResult(intent, 140)
    }

    private fun getLocationAddress(latitude: Double, longi: Double) {
        Log.e("LcoationsAa", "LocationaAssa")
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
                Log.e("City", "$city,$cityName")
                if (binding!!.tvToolbarLocation.text.toString().trim { it <= ' ' }
                        .equals("Location", ignoreCase = true)) {
                    if (city == null) {
                        binding!!.tvToolbarLocation.text = cityName
                        MainActivity.address = cityName
                    } else {
                        binding!!.tvToolbarLocation.text = city
                        MainActivity.address = city
                    }
                    lat = latitude.toString()
                    lng = longi.toString()
                    getAllDetails(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*private BroadcastReceiver onNotice_refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                offset = 0;
                isFirst = true;
                mAgentList.clear();
                binding.shimmerViewContainer.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.startShimmerAnimation();
                getAllDetails(true);
                adap.notifyDataSetChanged();
            } catch (Exception r) {
            }
        }
    };*/
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
                                    PreferenceHelper.getInstance(context)?.refreshToken =
                                        objResponse.optString("refresh_token")
                                    getAllDetails(true)
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

    companion object {
        private const val REQUEST_CODE_PERMISSION = 2
    }
}