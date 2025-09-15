package sambal.mydd.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.adapter.*
import sambal.mydd.app.apiResponse.ApiResponse
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.beans.AgentDetailsOfferBean
import sambal.mydd.app.beans.CharityListBean
import sambal.mydd.app.beans.GiftCard
import sambal.mydd.app.beans.VideoDataModel
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.SecondnewagentdetailsBinding
import sambal.mydd.app.fragment.NearMeHomeFragment
import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.models.RefreshCard
import sambal.mydd.app.models.new_agent_details.Example
import sambal.mydd.app.utils.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar.OnRatingChangeListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class New_AgentDetails : AppCompatActivity(), View.OnClickListener {
    private var commentId = ""
    var context: Context? = null
    var dialog1: Dialog? = null
    var agentId: String? = null
    var bannerArrayList: JSONArray? = null
    var agentObj: JSONObject? = null
    var productId: String? = null
    var menuURLStatus = ""
    var menuURL = ""
    var dealList: JSONArray? = null
    var agentWhatsappNumber = ""
    var agentTextNumber = ""
    var agentContactEmailId = ""
    var agentViberNumber = ""
    var adapHorizontal: AdapterRecommendedHorizontal? = null
    var dialogManager: DialogManager? = null
    var adapCat: AdapterAgentDD? = null
    var adapter_awardList: Adapter_awardList? = null
    var direct: String? = ""
    private var isChange = false
    private var storeTimeJsonArray: JSONArray? = null
    private var userReviews: JSONArray? = null
    private var imageSliderList: ArrayList<VideoDataModel>? = null
    private var dotsCount = 0
    private var dots: Array<ImageView?> = arrayOfNulls(5)
    private var agentContact = ""
    private var agentFbUrl: String? = ""
    private var agentInstaUrl: String? = ""
    private var agentYoutubeUrl: String? = ""
    private var agentURL: String? = ""
    var isRating = false
    var position = 0
    private var binding: SecondnewagentdetailsBinding? = null
    var adapLeafLet: AdapterNewAgentLeaflet? = null
    var publishKey: String? = null
    var subscribeKey: String? = null
    var type = "non_direct"
    var sum = 0
    var isFromAPi = true
    var userComments = ""
    private var dialog: Dialog? = null
    var arrleafLetList: JSONArray? = null
    private var charityEnabled = ""
    private var donateStatus = ""
    private var bannerVideoPosition = -1
    var dialog_vote_popup: Dialog? = null
    var example: Example? = null
    var viewpager_view_gallery: ViewPager? = null
    var tv_num_pages: TextView? = null
    var tv_share_note: TextView? = null
    var rcv_vote_now: RecyclerView? = null
    var check_userCommented = false
    var et_vote_email_id: EditText? = null
    var starRating = "0"
    var refreshCard: RefreshCard? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@New_AgentDetails, R.layout.secondnewagentdetails)
        isRating = false
        context = this@New_AgentDetails
        try {
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
            }
            if (Build.VERSION.SDK_INT >= 19) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
                window.statusBarColor = Color.TRANSPARENT
            }
        } catch (e: Exception) {
        }
        init()
        binding!!.tvRating.text = "0.0"
        val bundle = intent.extras
        if (bundle != null) {
            agentId = intent.getStringExtra(IntentConstant.INTENT_KEY_AGENT_ID)
            Log.e("agentId", agentId + "")
            try {
                position = intent.getIntExtra("position", 0)
              //  Log.e("positii", position.toString() + "")
            } catch (e: Exception) {
            }
            try {
                direct = intent.getStringExtra("direct")
            } catch (e: Exception) {
            }
        }

        if (AppUtil.isNetworkAvailable(context)) {
            agentDetails
        } else {
            AppUtil.showMsgAlert(binding!!.tvReview, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
        binding!!.llReview.setOnClickListener {
            if (check_userCommented == false) {
                startActivityForResult(Intent(this@New_AgentDetails,
                    Rating_ReviewActivity::class.java)
                    .putExtra("agentId", agentId)
                    .putExtra("Edit", "false")
                    .putExtra("id", "")
                    .putExtra("rating", binding!!.tvRating.text.toString())
                    .putExtra("review", ""), 80)
            } else {
                startActivityForResult(Intent(this@New_AgentDetails,
                    Rating_ReviewActivity::class.java)
                    .putExtra("agentId", agentId)
                    .putExtra("Edit", "true")
                    .putExtra("id", commentId)
                    .putExtra("rating", binding!!.tvRating.text.toString())
                    .putExtra("review", userComments), 80)
            }
        }
        binding!!.ivReviewArrow.setOnClickListener {
            startActivityForResult(Intent(context, AgentAllComments::class.java)
                .putExtra("agentId", agentId)
                .putExtra("productId", productId), 80)
        }
        binding!!.appbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                binding!!.llView.visibility = View.GONE
            } else {
                binding!!.llView.visibility = View.VISIBLE
            }
        })
        binding!!.ivChat.setOnClickListener {
            startActivity(Intent(this@New_AgentDetails, ChatMain::class.java)
                .putExtra("id", agentObj!!.optString("agentId"))
                .putExtra("name", agentObj!!.optString("agentCompanyName"))
                .putExtra("pubskey", agentObj!!.optString("publishKey"))
                .putExtra("subskey", agentObj!!.optString("subscribeKey"))
                .putExtra("followingstatus", agentObj!!.optString("followingStatus"))
                .putExtra("isAdmin", agentObj!!.optString("isAdmin"))
                .putExtra("type", "non_direct")
                .putExtra("position", "0"))
        }
        binding!!.llBottom.setOnClickListener {
            if (!TextUtils.isEmpty(agentObj!!.optString("onlineOrderURL"))) {
              //  Log.e("ageee", agentObj!!.optString("onlineOrderURL"))
                startActivity(Intent(this@New_AgentDetails, Webview::class.java)
                    .putExtra("url", agentObj!!.optString("onlineOrderURL"))
                    .putExtra("title", agentObj!!.optString("agentCompanyName"))
                    .putExtra("type", "non_direct"))
            }
        }
        binding!!.ivOwn.setOnClickListener {
            val dialog1 = Dialog(context!!, R.style.NewDialog)
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1.setContentView(R.layout.editcomment)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog1.window!!.attributes)
            lp.width = WindowManager.LayoutParams.FILL_PARENT
            lp.height = WindowManager.LayoutParams.FILL_PARENT
            dialog1.window!!.attributes = lp
            val llEdit = dialog1.findViewById<LinearLayout>(R.id.llEdit)
            val llDelete = dialog1.findViewById<LinearLayout>(R.id.llDelete)
            val llCancel = dialog1.findViewById<LinearLayout>(R.id.Cancel)
            llEdit.setOnClickListener {
                dialog1.dismiss()
                try {
                    editComment(userReviews!!.getJSONObject(0).optString("commentsId"),
                        userReviews!!.getJSONObject(0).optString("userRating"),
                        userReviews!!.getJSONObject(0).optString("userComments"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            llDelete.setOnClickListener {
                dialog1.dismiss()
                try {
                    deleteComment(userReviews!!.getJSONObject(0).optString("commentsId"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            llCancel.setOnClickListener { dialog1.dismiss() }
            dialog1.show()
        }
        binding!!.btnViewGallery.setOnClickListener { viewGallery() }
    }
    private val agentDetails: Unit
        private get() {

            ErrorMessage.E("agentDetails   {$agentId}   {$MainActivity.userLang.toString()}  {$MainActivity.userLat.toString()}   " )

            try {
                try {
                    binding!!.progress1.progress = 0
                    binding!!.progress2.progress = 0
                    binding!!.progress3.progress = 0
                    binding!!.progress4.progress = 0
                    binding!!.progress5.progress = 0
                } catch (e: Exception) {
                }
                //User user = PreferenceHelper.getInstance(context).getUserDetail();
                if (AppUtil.isNetworkAvailable(context)) {
                    val materialDialog = ErrorMessage.initProgressDialog(this@New_AgentDetails)



                    val call = AppConfig.api_Interface().getAgentDetailsV3(agentId,
                        MainActivity.userLat.toString(),
                        MainActivity.userLang.toString())
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                try {
                                    try {
                                        materialDialog?.dismiss()
                                    } catch (e: Exception) {
                                    }
                                    val resp = JSONObject(response.body()!!.string())
                                  //  Log.e("getAgentDetails", ">>$resp")
                                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                        val responseObj =
                                            resp.getJSONObject(KeyConstant.KEY_RESPONSE)
                                        if (responseObj != null) {
                                            val agentDetailsArray =
                                                responseObj.optJSONArray(KeyConstant.KEY_AGENT_DETAILS)
                                            for (i in 0 until agentDetailsArray.length()) {
                                                agentObj = agentDetailsArray.optJSONObject(i)
                                                if(agentObj!=null){
                                                bannerArrayList =
                                                    agentObj!!.optJSONArray(KeyConstant.KEY_BANNER_LIST)
                                                storeTimeJsonArray =
                                                    agentObj!!.optJSONArray(KeyConstant.KEY_TIMING_LIST)
                                                userReviews = agentObj!!.optJSONArray("userReviews")
                                                agentWhatsappNumber =
                                                    agentObj!!.optString("agentWhatsappNumber")
                                                agentContactEmailId =
                                                    agentObj!!.optString("agentContactEmailId")
                                                agentTextNumber =
                                                    agentObj!!.optString("agentTextNumber")
                                                agentURL = agentObj!!.optString("agentURL")
                                            }
                                            }
                                            dealList = agentObj!!.optJSONArray("dealsList")
                                            //Log.e("dealList>>>", "" + dealList!!.length())
                                            val mOfferlist = ArrayList<AgentDetailsOfferBean>()
                                            mOfferlist.clear()
                                            if (dealList!=null && dealList!!.length() > 0) {
                                                for (j in 0 until dealList!!.length()) {
                                                    val ob = dealList!!.optJSONObject(j)
                                                    val dealId = ob.optString("dealId")
                                                    val dealName = ob.optString("dealName")
                                                    val dealImage = ob.optString("dealImage")
                                                    val dealDescription =
                                                        ob.optString("dealDescription")
                                                    val agentId = ob.optString("agentId")
                                                    val agentName = ob.optString("agentName")
                                                    val agentAddress = ob.optString("agentAddress")
                                                    val agentDistance =
                                                        ob.optString("agentDistance")
                                                    val agentImage = ob.optString("agentImage")
                                                    val productCategoryName =
                                                        ob.optString("productCategoryName")
                                                    val dealFavourite =
                                                        ob.optString("dealFavourite")
                                                    val corporateDeal =
                                                        ob.optString("corporateDeal")
                                                    val moreProductLink =
                                                        ob.optString("moreProductLink")
                                                    val moreProductText =
                                                        ob.optString("moreProductText")
                                                    val agentExternalURLEnable =
                                                        ob.optString("agentExternalURLEnable")
                                                    val agentExternalURL =
                                                        ob.optString("agentExternalURL")
                                                    val productCurrency =
                                                        ob.optString("productCurrency")
                                                    val productDiscountPercentage =
                                                        ob.optString("productDiscountPercentage")
                                                    val productPrice = ob.optString("productPrice")
                                                    val productFinalPrice =
                                                        ob.optString("productFinalPrice")
                                                    val offerLimitedEnabled =
                                                        ob.optString("offerLimitedEnabled")
                                                    val productTotalReedom =
                                                        ob.optString("productTotalReedom")
                                                    val dealStatus = ob.optString("dealStatus")
                                                    val dealStatusId = ob.optString("dealStatusId")
                                                    val dealUUID = ob.optString("dealUUID")
                                                    val dealBarCode = ob.optString("dealBarCode")
                                                    val dealExpiredDate =
                                                        ob.optString("dealExpiredDate")
                                                    val dealScanStatus =
                                                        ob.optString("dealScanStatus")
                                                    val dealScanStatusId =
                                                        ob.optString("dealScanStatusId")
                                                    val offerType = ob.optString("offerType")
                                                    val offerTypeId = ob.optString("offerTypeId")
                                                    val dealExclusiveStatus =
                                                        ob.optString("dealExclusiveStatus")
                                                    val priceEnabledId =
                                                        ob.optString("priceEnabledId")
                                                    val discountPriceEnabledId =
                                                        ob.optString("discountPriceEnabledId")
                                                    val productDiscountPercentageEnabled =
                                                        ob.optString("productDiscountPercentageEnabled")
                                                    val priceEnabledStatus =
                                                        ob.optString("priceEnabledStatus")
                                                    val loyaltyEnabled =
                                                        ob.optString("loyaltyEnabled")
                                                    val offerGiftEnabled =
                                                        ob.optString("offerGiftEnabled")
                                                    val offerGiftDescription =
                                                        ob.optString("offerGiftDescription")
                                                    val dealType = ob.optString("dealType")
                                                    val dealBannerEnabled =
                                                        ob.optString("dealBannerEnabled")
                                                    val type = ob.optString("type")
                                                    val ag = AgentDetailsOfferBean(dealId,
                                                        dealName,
                                                        dealImage,
                                                        dealDescription,
                                                        agentId,
                                                        agentName,
                                                        agentAddress,
                                                        agentDistance,
                                                        agentImage,
                                                        productCategoryName,
                                                        dealFavourite,
                                                        corporateDeal,
                                                        moreProductLink,
                                                        moreProductText,
                                                        agentExternalURLEnable,
                                                        agentExternalURL,
                                                        productCurrency,
                                                        productDiscountPercentage,
                                                        productPrice,
                                                        productFinalPrice,
                                                        offerLimitedEnabled,
                                                        productTotalReedom,
                                                        dealStatus,
                                                        dealUUID,
                                                        dealStatusId,
                                                        dealBarCode,
                                                        dealExpiredDate,
                                                        dealScanStatus,
                                                        dealScanStatusId,
                                                        offerType,
                                                        offerTypeId,
                                                        dealExclusiveStatus,
                                                        priceEnabledId,
                                                        discountPriceEnabledId,
                                                        productDiscountPercentageEnabled,
                                                        priceEnabledStatus,
                                                        loyaltyEnabled,
                                                        offerGiftEnabled,
                                                        offerGiftDescription,
                                                        dealType,
                                                        dealBannerEnabled,
                                                        type)
                                                    mOfferlist.add(ag)
                                                }
                                            }
                                            val arrCommenst = agentObj!!.optJSONArray("userReviews")
                                            runOnUiThread {
                                                isFromAPi = true
                                                commentId = agentObj!!.optString("userCommentsId")
                                                binding!!.tvRating.text =
                                                    agentObj!!.optString("userRating")
                                                starRating = agentObj!!.optString("userRating")
                                                userComments = agentObj!!.optString("userComments")
                                                try {
                                                    charityEnabled =
                                                        agentObj!!.optString("charityEnabled")
                                                    donateStatus =
                                                        agentObj!!.optString("donateStatus")
                                                    if (charityEnabled == "1") {
                                                        binding!!.donateBackgroungLayout.visibility =
                                                            View.VISIBLE
                                                        if (donateStatus == "0") {
                                                            binding!!.tvDonate.text = "Donate"
                                                            binding!!.donateBackgroungLayout.background =
                                                                context!!.resources.getDrawable(
                                                                    R.drawable.background_donate_green)
                                                        } else if (donateStatus == "1") {
                                                            binding!!.tvDonate.text = "Donating"
                                                            binding!!.donateBackgroungLayout.background =
                                                                resources.getDrawable(
                                                                    R.drawable.background_donate_pink)
                                                            binding!!.donateBackgroungLayout.setBackgroundColor(
                                                                ContextCompat.getColor(this@New_AgentDetails,
                                                                    R.color.pink))
                                                        }
                                                    } else if (charityEnabled == "0") {
                                                        binding!!.donateBackgroungLayout.visibility =
                                                            View.GONE
                                                    }
                                                } catch (e: Exception) {
                                                    ErrorMessage.E("Exception>1>$e")
                                                }
                                                binding!!.ratingBa.rating =
                                                    agentObj!!.optString("userRating").toFloat()
                                                //dialogManager.stopProcessDialog();
                                                binding!!.defaultView.visibility = View.GONE
                                                if (bannerArrayList!!.length() > 0) {
                                                    ErrorMessage.E("BANNERLIST_SIZE >>> " + bannerArrayList!!.length())
                                                    binding!!.viewpager.background = null
                                                    setViewPager()
                                                    binding!!.btnViewGallery.visibility =
                                                        View.VISIBLE
                                                    for (i in 0 until bannerArrayList!!.length()) {
                                                        try {
                                                            if (bannerArrayList!!.getJSONObject(i)
                                                                    .optString("bannerVideoType") == "youtube"
                                                            ) {
                                                                bannerVideoPosition = i
                                                                break
                                                            }
                                                        } catch (e: JSONException) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                    if (bannerVideoPosition != -1) {
                                                        binding!!.llVideoBtn.visibility =
                                                            View.VISIBLE
                                                    } else {
                                                        binding!!.llVideoBtn.visibility = View.GONE
                                                    }
                                                } else {
                                                    binding!!.llVideoBtn.visibility = View.GONE
                                                    binding!!.btnViewGallery.visibility = View.GONE
                                                }
                                                publishKey = agentObj!!.optString("publishKey")
                                                subscribeKey = agentObj!!.optString("subscribeKey")
                                                binding!!.tvRatingText.text =
                                                    agentObj!!.optString("agentRating")
                                                binding!!.tvAgentName.text =
                                                    agentObj!!.optString("agentCompanyName")
                                                if (agentObj!!.optString("agentDistance") != null) {
                                                    val html =
                                                        agentObj!!.optString("agentAddress") + "  [img src=ic_walk/] " + agentObj!!.optString(
                                                            "agentDistance")
                                                    binding!!.tvAgentLocation.text = html
                                                } else {
                                                    val html =
                                                        agentObj!!.optString("agentAddress") + "  [img src=ic_walk/] "
                                                    binding!!.tvAgentLocation.text = html
                                                }
                                                binding!!.tvViews.text =
                                                    agentObj!!.optString("visitCount") + " View"
                                                binding!!.tvFollowingCount.text =
                                                    agentObj!!.optString("followingCount") + " Following"
                                                binding!!.tvReview.text =
                                                    agentObj!!.optString("totalComments") + " Reviews"
                                                if (TextUtils.isEmpty(agentObj!!.optString("agentAddress"))) {
                                                    binding!!.llAddress.visibility = View.GONE
                                                } else {
                                                    binding!!.llAddress.visibility = View.VISIBLE
                                                    binding!!.tvAddress.text =
                                                        agentObj!!.optString("agentAddress")
                                                }
                                                binding!!.descOverviewTv.text =
                                                    agentObj!!.optString("agentDescription")
                                                binding!!.totalRatingsTv.text =
                                                    agentObj!!.optString("totalComments")
                                                binding!!.tvReviewHeading.text =
                                                    "Reviews (" + agentObj!!.optString("totalComments") + ")"
                                                try {
                                                    if (agentObj!!.optString("onlineOrderStatus")
                                                            .equals("1", ignoreCase = true)
                                                    ) {
                                                        binding!!.llBottom.visibility = View.VISIBLE
                                                    } else if (agentObj!!.optString("onlineOrderStatus")
                                                            .equals("0", ignoreCase = true)
                                                    ) {
                                                        binding!!.llBottom.visibility = View.GONE
                                                    }
                                                } catch (e: Exception) {
                                                    ErrorMessage.E("Exception>2>$e")
                                                }
                                                if (TextUtils.isEmpty(agentObj!!.optString("agentContactNumber"))) {
                                                    binding!!.llPhone.visibility = View.GONE
                                                } else {
                                                    binding!!.llPhone.visibility = View.VISIBLE
                                                }
                                                if (TextUtils.isEmpty(agentObj!!.optString("agentURL"))) {
                                                    binding!!.llWeb1.visibility = View.GONE
                                                } else {
                                                    binding!!.llWeb1.visibility = View.VISIBLE
                                                }
                                                if (TextUtils.isEmpty(agentObj!!.optString("agentAddress"))) {
                                                    binding!!.llPhone.visibility = View.GONE
                                                } else {
                                                    binding!!.llPhone.visibility = View.VISIBLE
                                                    binding!!.tvMobileNo.text =
                                                        agentObj!!.optString("agentContactNumber")
                                                }
                                                agentId = agentObj!!.optString("agentId")
                                                binding!!.tvToday.text = "Open : "
                                                productId = agentObj!!.optString("productId")
                                                try {
                                                    if (storeTimeJsonArray!!.length() > 0) {
                                                        binding!!.llTIme.visibility = View.VISIBLE
                                                        binding!!.llMainTimess.visibility =
                                                            View.VISIBLE

                                                        //---------------------Time---------------------------//
                                                        binding!!.tvTodaytime.text =
                                                            storeTimeJsonArray!!.optJSONObject(0)
                                                                .optString("openTime") + " (Today)"
                                                        binding!!.tvDay1.text =
                                                            storeTimeJsonArray!!.optJSONObject(0)
                                                                .optString("openTime")
                                                        binding!!.tvDay2.text =
                                                            storeTimeJsonArray!!.optJSONObject(1)
                                                                .optString("openTime")
                                                        binding!!.tvDay3.text =
                                                            storeTimeJsonArray!!.optJSONObject(2)
                                                                .optString("openTime")
                                                        binding!!.tvDay4.text =
                                                            storeTimeJsonArray!!.optJSONObject(3)
                                                                .optString("openTime")
                                                        binding!!.tvDay5.text =
                                                            storeTimeJsonArray!!.optJSONObject(4)
                                                                .optString("openTime")
                                                        binding!!.tvDay6.text =
                                                            storeTimeJsonArray!!.optJSONObject(5)
                                                                .optString("openTime")
                                                        binding!!.tvDay7.text =
                                                            storeTimeJsonArray!!.optJSONObject(6)
                                                                .optString("openTime")

                                                        //////////// --------days ------------------------------------//
                                                        binding!!.tvday1.text =
                                                            storeTimeJsonArray!!.optJSONObject(0)
                                                                .optString("openDay")
                                                        binding!!.tvday2.text =
                                                            storeTimeJsonArray!!.optJSONObject(1)
                                                                .optString("openDay")
                                                        binding!!.tvday3.text =
                                                            storeTimeJsonArray!!.optJSONObject(2)
                                                                .optString("openDay")
                                                        binding!!.tvday4.text =
                                                            storeTimeJsonArray!!.optJSONObject(3)
                                                                .optString("openDay")
                                                        binding!!.tvday5.text =
                                                            storeTimeJsonArray!!.optJSONObject(4)
                                                                .optString("openDay")
                                                        binding!!.tvday6.text =
                                                            storeTimeJsonArray!!.optJSONObject(5)
                                                                .optString("openDay")
                                                        binding!!.tvday7.text =
                                                            storeTimeJsonArray!!.optJSONObject(6)
                                                                .optString("openDay")
                                                    } else {
                                                        binding!!.llTIme.visibility = View.GONE
                                                        binding!!.llMainTimess.visibility =
                                                            View.GONE
                                                    }

                                                    //if (agentObj.optString("turtaAwardStatus").equalsIgnoreCase("1"))
                                                    val arrAwardList =
                                                        agentObj!!.optJSONArray("awardList")
                                                    if (arrAwardList != null) {
                                                        if (arrAwardList.length() > 0) {
                                                            binding!!.rcvAwardList.visibility =
                                                                View.VISIBLE
                                                            try {
                                                                adapter_awardList =
                                                                    Adapter_awardList(this@New_AgentDetails,
                                                                        arrAwardList)
                                                                binding!!.rcvAwardList.layoutManager =
                                                                    LinearLayoutManager(this@New_AgentDetails,
                                                                        LinearLayoutManager.VERTICAL,
                                                                        false)
                                                                binding!!.rcvAwardList.adapter =
                                                                    adapter_awardList
                                                                binding!!.rcvAwardList.isNestedScrollingEnabled =
                                                                    false
                                                                adapter_awardList!!.notifyDataSetChanged()
                                                            } catch (e: Exception) {
                                                                ErrorMessage.E("Exception>3>$e")
                                                            }
                                                        } else {
                                                            binding!!.rcvAwardList.visibility =
                                                                View.GONE
                                                        }
                                                    }
                                                    val arrCat =
                                                        agentObj!!.optJSONArray("categoryNameList")
                                                    if (arrCat.length() > 0) {
                                                        binding!!.rvCat.isNestedScrollingEnabled =
                                                            false
                                                        adapCat =
                                                            AdapterAgentDD(this@New_AgentDetails,
                                                                arrCat)
                                                        binding!!.rvCat.layoutManager =
                                                            LinearLayoutManager(this@New_AgentDetails,
                                                                LinearLayoutManager.HORIZONTAL,
                                                                false)
                                                        binding!!.rvCat.adapter = adapCat
                                                        binding!!.rvCat.isNestedScrollingEnabled =
                                                            false
                                                        adapCat!!.notifyDataSetChanged()
                                                    }
                                                    arrleafLetList =
                                                        agentObj!!.optJSONArray("leafLetList")
                                                    //Log.e("dealList1>>>", "" + dealList.length())
                                                    if (dealList!=null && dealList!!.length() > 0) {
                                                        binding!!.llRecommended.visibility =
                                                            View.VISIBLE
                                                    }
                                                    //ErrorMessage.E("+ Lentgth >>" + arrleafLetList.length())
                                                    if (arrleafLetList!=null && arrleafLetList!!.length() > 0) {
                                                        binding!!.llLeaflet.visibility =
                                                            View.VISIBLE
                                                        binding!!.tvLeaflet.visibility =
                                                            View.VISIBLE
                                                        binding!!.rvLeaflet.visibility =
                                                            View.VISIBLE
                                                    }

                                                    //Log.e("dealList2>>>", "" + dealList.length())
                                                    if (arrleafLetList!=null && arrleafLetList!!.length() > 0) {
                                                        binding!!.tvLeaflet.visibility =
                                                            View.VISIBLE
                                                        binding!!.rvLeaflet.visibility =
                                                            View.VISIBLE
                                                        adapLeafLet =
                                                            AdapterNewAgentLeaflet(this@New_AgentDetails,
                                                                arrleafLetList!!)
                                                        binding!!.rvLeaflet.layoutManager =
                                                            LinearLayoutManager(this@New_AgentDetails,
                                                                LinearLayoutManager.HORIZONTAL,
                                                                false)
                                                        binding!!.rvLeaflet.adapter = adapLeafLet
                                                        binding!!.rvLeaflet.isNestedScrollingEnabled =
                                                            false
                                                        adapLeafLet!!.notifyDataSetChanged()
                                                    } else {
                                                        binding!!.tvLeaflet.visibility = View.GONE
                                                        binding!!.rvLeaflet.visibility = View.GONE
                                                    }
                                                    binding!!.tvRb.text =
                                                        agentObj!!.optString("agentRating")
                                                    binding!!.rb1.rating =
                                                        agentObj!!.optString("agentRating")
                                                            .toFloat()
                                                    try {
                                                        val arrRating =
                                                            agentObj!!.optJSONArray("RatingList")
                                                     //   Log.e("Arrra", arrRating.toString() + "")
                                                        sum = 0
                                                        var total = 0
                                                        for (j in 0 until arrRating.length()) {
                                                            total += arrRating.optJSONObject(j)
                                                                .optString("ratingCount").toInt()
                                                        }
                                                        ErrorMessage.E("Total Count$total")
                                                        for (i in 0 until arrRating.length()) {
                                                            //total += Integer.parseInt(arrRating.optJSONObject(i).optString("ratingCount"));; //Same as total = total + increase;
                                                            val obj = arrRating.optJSONObject(i)
                                                            if (obj.optString("rating")
                                                                    .equals("5", ignoreCase = true)
                                                            ) {
                                                                binding!!.progress1.progress =
                                                                    obj.optString("ratingCount")
                                                                        .toInt() * 100 / total
                                                            }
                                                            if (obj.optString("rating")
                                                                    .equals("4", ignoreCase = true)
                                                            ) {
                                                                binding!!.progress2.progress =
                                                                    obj.optString("ratingCount")
                                                                        .toInt() * 100 / total
                                                            }
                                                            if (obj.optString("rating")
                                                                    .equals("3", ignoreCase = true)
                                                            ) {
                                                                binding!!.progress3.progress =
                                                                    obj.optString("ratingCount")
                                                                        .toInt() * 100 / total
                                                            }
                                                            if (obj.optString("rating")
                                                                    .equals("2", ignoreCase = true)
                                                            ) {
                                                                binding!!.progress4.progress =
                                                                    obj.optString("ratingCount")
                                                                        .toInt() * 100 / total
                                                            }
                                                            if (obj.optString("rating")
                                                                    .equals("1", ignoreCase = true)
                                                            ) {
                                                                binding!!.progress5.progress =
                                                                    obj.optString("ratingCount")
                                                                        .toInt() * 100 / total
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        ErrorMessage.E("Exception>5>$e")
                                                    }
                                                } catch (e: Exception) {
                                                    ErrorMessage.E("Exception>6>$e")
                                                }
                                                agentFbUrl =
                                                    agentObj!!.optString(KeyConstant.KEY_AGENT_FB_URL)
                                                agentInstaUrl =
                                                    agentObj!!.optString(KeyConstant.KEY_AGENT_INSTA_URL)
                                                agentYoutubeUrl =
                                                    agentObj!!.optString(KeyConstant.KEY_AGENT_YOUTUBE_URL)
                                                agentViberNumber =
                                                    agentObj!!.optString(KeyConstant.KEY_AGENT_VIBER_NUMBER)
                                                agentContact =
                                                    agentObj!!.optString("agentContactNumber")
                                                if (agentWhatsappNumber.equals("",
                                                        ignoreCase = true)
                                                ) {
                                                    binding!!.llWhatsapp.visibility = View.GONE
                                                } else {
                                                    binding!!.llWhatsapp.visibility = View.VISIBLE
                                                }
                                                if (agentContactEmailId.equals("",
                                                        ignoreCase = true)
                                                ) {
                                                    binding!!.llemail.visibility = View.GONE
                                                } else {
                                                    binding!!.llemail.visibility = View.VISIBLE
                                                }
                                                if (agentContact.equals("", ignoreCase = true)) {
                                                    binding!!.llCall1.visibility = View.GONE
                                                } else {
                                                    binding!!.llCall1.visibility = View.VISIBLE
                                                }
                                                if (agentTextNumber.equals("", ignoreCase = true)) {
                                                    binding!!.llText1.visibility = View.GONE
                                                } else {
                                                    binding!!.llText1.visibility = View.VISIBLE
                                                }
                                                if (agentWhatsappNumber.equals("",
                                                        ignoreCase = true) && agentTextNumber.equals(
                                                        "",
                                                        ignoreCase = true) && agentContact.equals("",
                                                        ignoreCase = true) && agentContactEmailId.equals(
                                                        "",
                                                        ignoreCase = true) && agentViberNumber.equals(
                                                        "",
                                                        ignoreCase = true)
                                                ) {
                                                    binding!!.tvContacts.visibility = View.GONE
                                                    binding!!.llContacts.visibility = View.GONE
                                                } else {
                                                    binding!!.tvContacts.visibility = View.VISIBLE
                                                    binding!!.llContacts.visibility = View.VISIBLE
                                                }
                                                if (agentObj!!.optString("followingStatus")
                                                        .equals("1", ignoreCase = true)
                                                ) {
                                                    binding!!.llFollowBtn.isEnabled = false
                                                    binding!!.tvfollow.setTextColor(context!!.resources.getColor(
                                                        R.color.pink))
                                                    binding!!.tvfollow.text = "Following "
                                                } else {
                                                    binding!!.ivFollowing.setImageDrawable(
                                                        context!!.resources.getDrawable(R.drawable.plusblack))
                                                    binding!!.llFollowBtn.isEnabled = true
                                                    binding!!.tvfollow.setTextColor(Color.parseColor(
                                                        "#676767"))
                                                    binding!!.tvfollow.text = "Follow "
                                                    binding!!.llFollowBtn.background =
                                                        resources.getDrawable(
                                                            R.drawable.background_border)
                                                }
                                                menuURLStatus =
                                                    agentObj!!.optString("menuURLStatus")
                                                menuURL = agentObj!!.optString("menuURL")
                                                if (menuURLStatus.equals("1", ignoreCase = true)) {
                                                    binding!!.llMenu.visibility = View.VISIBLE
                                                } else {
                                                    binding!!.llMenu.visibility = View.GONE
                                                }
                                                if (agentObj!!.optString("totalComments")
                                                        .toInt() > 0
                                                ) {
                                                    binding!!.llFirstComment.visibility =
                                                        View.VISIBLE
                                                    try {
                                                        Picasso.with(context).load(
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userImage"))
                                                            .placeholder(context!!.resources.getDrawable(
                                                                R.drawable.place_holder))
                                                            .error(context!!.resources.getDrawable(
                                                                R.drawable.place_holder)).into(
                                                                binding!!.ivCommentPhoto)
                                                    } catch (e: Exception) {
                                                        binding!!.ivCommentPhoto.setImageResource(
                                                            R.drawable.sponplaceholder)
                                                    }
                                                    try {
                                                        binding!!.tvcommentRating.text =
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userRating")
                                                        binding!!.commentRatingBars.rating =
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userRating").toFloat()
                                                        binding!!.tvName.text =
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userName")
                                                        binding!!.tvCommentNew.text =
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userComments")
                                                        binding!!.tvTime.text =
                                                            userReviews!!.getJSONObject(0)
                                                                .optString("userCommentsDate")
                                                        if (userReviews!!.getJSONObject(0)
                                                                .optString("userOwnComments")
                                                                .equals("1", ignoreCase = true)
                                                        ) {
                                                            binding!!.ivOwn.visibility =
                                                                View.VISIBLE
                                                            check_userCommented = true
                                                        } else {
                                                            binding!!.ivOwn.visibility = View.GONE
                                                            check_userCommented = false
                                                        }
                                                    } catch (e: Exception) {
                                                    }
                                                } else {
                                                    binding!!.llFirstComment.visibility = View.GONE
                                                }
                                                try {
                                                    Log.e("Commme",
                                                        arrCommenst.length().toString() + "")
                                                    if (agentObj!!.optString("totalComments")
                                                            .toInt() > 1
                                                    ) {
                                                        binding!!.btnAll.visibility = View.VISIBLE
                                                    } else {
                                                        binding!!.btnAll.visibility = View.GONE
                                                    }
                                                } catch (e: Exception) {
                                                    ErrorMessage.E("Exception>7>$e")
                                                }
                                                if (agentObj!!.optString("agentFacebookEnabled")
                                                        .equals("0", ignoreCase = true)
                                                ) {
                                                    binding!!.llFb.visibility = View.GONE
                                                } else {
                                                    binding!!.llFb.visibility = View.VISIBLE
                                                }
                                                if (agentObj!!.optString("agentInstagramEnabled")
                                                        .equals("0", ignoreCase = true)
                                                ) {
                                                    binding!!.llInsta1.visibility = View.GONE
                                                } else {
                                                    binding!!.llInsta1.visibility = View.VISIBLE
                                                }
                                                if (agentObj!!.optString("agentYoutubeEnabled")
                                                        .equals("0", ignoreCase = true)
                                                ) {
                                                    binding!!.llYoutube.visibility = View.GONE
                                                    binding!!.llYoutube.isEnabled = false
                                                } else {
                                                    binding!!.llYoutube.visibility = View.VISIBLE
                                                    binding!!.llYoutube.isEnabled = true
                                                }
                                                if (agentObj!!.optString("agentInstagramEnabled")
                                                        .equals("0",
                                                            ignoreCase = true) && agentObj!!.optString(
                                                        "agentYoutubeEnabled").equals("0",
                                                        ignoreCase = true) && agentObj!!.optString("agentFacebookEnabled")
                                                        .equals("0",
                                                            ignoreCase = true) && agentURL.equals("",
                                                        ignoreCase = true)
                                                ) {
                                                    binding!!.llSocial.visibility = View.GONE
                                                } else {
                                                    binding!!.llSocial.visibility = View.VISIBLE
                                                }
                                                try {
                                                    adapHorizontal = AdapterRecommendedHorizontal(
                                                        this@New_AgentDetails,
                                                        dealList!!,
                                                        productId!!)
                                                    binding!!.rvRecommmendedealsHori.layoutManager =
                                                        LinearLayoutManager(this@New_AgentDetails,
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false)
                                                    //rvRecommmendedealsHori.addItemDecoration(new ItemOffsetDecoration(7));
                                                    binding!!.rvRecommmendedealsHori.adapter =
                                                        adapHorizontal
                                                    binding!!.rvRecommmendedealsHori.visibility =
                                                        View.VISIBLE
                                                    binding!!.rvRecommmendedealsHori.isNestedScrollingEnabled =
                                                        false
                                                    adapHorizontal!!.notifyDataSetChanged()
                                                    binding!!.rvRecommmendedealsHori.visibility =
                                                        View.VISIBLE
                                                } catch (e: Exception) {
                                                    ErrorMessage.E("Exception>8>$e")
                                                }
                                            }
                                            val giftcard = agentObj!!.optJSONArray("giftCardList")
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
                                                val giftSellingPrice =
                                                    ob.optString("giftSellingPrice")
                                                val discountValue = ob.optString("discountValue")
                                                val giftTextRemark = ob.optString("giftTextRemark")
                                                val borderColor = ob.optString("borderColor")
                                                val giftStatus = ob.optInt("giftStatus")
                                                val avm = GiftCard(giftId,
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
                                                    borderColor)
                                                giftCardListList.add(avm)
                                                runOnUiThread {
                                                    try {
                                                        if (giftcard.length() > 0) {
                                                            binding!!.giftCardRcv.visibility =
                                                                View.VISIBLE
                                                            binding!!.giftcardLayout.visibility =
                                                                View.VISIBLE
                                                            val walletGiftCardAdapter: WalletGiftCardAdapter
                                                            walletGiftCardAdapter =
                                                                WalletGiftCardAdapter(this@New_AgentDetails,
                                                                    giftCardListList)
                                                            binding!!.giftCardRcv.layoutManager =
                                                                LinearLayoutManager(this@New_AgentDetails,
                                                                    LinearLayoutManager.HORIZONTAL,
                                                                    false)
                                                            binding!!.giftCardRcv.adapter =
                                                                walletGiftCardAdapter
                                                            binding!!.giftCardRcv.isNestedScrollingEnabled =
                                                                false
                                                            walletGiftCardAdapter.notifyDataSetChanged()
                                                        } else {
                                                            binding!!.giftCardRcv.visibility =
                                                                View.GONE
                                                            binding!!.giftcardLayout.visibility =
                                                                View.GONE
                                                        }
                                                    } catch (e: Exception) {
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                                KeyConstant.KEY_STATUS), ignoreCase = true)
                                        ) {
                                            //dialogManager.stopProcessDialog();
                                            materialDialog?.dismiss()
                                            AppUtil.showMsgAlert(binding!!.tvReview,
                                                resp.optString(KeyConstant.KEY_MESSAGE))
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Log.e("Ex", e.toString())

                                    //dialogManager.stopProcessDialog();
                                    materialDialog?.dismiss()
                                    AppUtil.showMsgAlert(binding!!.tvReview,
                                        MessageConstant.MESSAGE_SOMETHING_WRONG)
                                }
                            } else {
                                materialDialog?.dismiss()
                                AppUtil.showMsgAlert(binding!!.tvReview,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            materialDialog?.dismiss()
                            AppUtil.showMsgAlert(binding!!.tvAddress, t.message)
                        }
                    })
                } else {
                    AppUtil.showMsgAlert(binding!!.tvReview,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION)
                }
            } catch (e: Exception) {
            }
        }

    private fun setViewPager() {
        runOnUiThread {
            try {
                imageSliderList = ArrayList()
                imageSliderList!!.clear()
                for (i in 0 until bannerArrayList!!.length()) {
                    Log.e("bannee", i.toString() + "")
                    val dataModel = VideoDataModel()
                    val `object` = bannerArrayList!!.optJSONObject(i)
                    val bannerImageUrl = `object`.optString(KeyConstant.KEY_BANNER_IMAGE_URL)
                    val bannerVideoUrl = `object`.optString(KeyConstant.KEY_BANNER_VIDEO_URL)
                    val bannerVideoType = `object`.optString(KeyConstant.KEY_BANNER_VIDEO_TYPE)
                    val youtubeVideoId = `object`.optString(KeyConstant.KEY_YOUTUBE_VIDEO_ID)
                    val bannerType = `object`.optInt(KeyConstant.KEY_BANNER_TYPE)
                    dataModel.bannerImageUrl = bannerImageUrl
                    dataModel.bannerVideoUrl = bannerVideoUrl
                    dataModel.bannerType = bannerType.toString() + ""
                    dataModel.bannerVideoType = bannerVideoType
                    dataModel.youtubeVideoId = youtubeVideoId
                    imageSliderList!!.add(dataModel)
                }
                val viewPagerAdapter: ViewPagerAdapters<*> =
                    ViewPagerAdapters<Any?>(this@New_AgentDetails, imageSliderList!!, false)
                binding!!.viewpager.adapter = viewPagerAdapter
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        binding!!.viewpager.post {
                            if (imageSliderList != null && imageSliderList!!.size > 0) {
                                binding!!.viewpager.currentItem =
                                    (binding!!.viewpager.currentItem + 1) % imageSliderList!!.size
                            }
                        }
                    }
                }
                dotsCount = viewPagerAdapter.count
                dots = arrayOfNulls(dotsCount)
                binding!!.sliderDots.removeAllViews()
                for (i in 0 until dotsCount) {
                    dots[i] = ImageView(this@New_AgentDetails)
                    dots[i]!!.setImageDrawable(ContextCompat.getDrawable(this@New_AgentDetails,
                        R.drawable.non_active_dots))
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5, 0, 5, 0)
                    binding!!.sliderDots.addView(dots[i], params)
                }
                dots[0]!!.setImageDrawable(ContextCompat.getDrawable(this@New_AgentDetails,
                    R.drawable.active_dots))
            } catch (e: Exception) {
            }
            binding!!.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0 until dotsCount) {
                        dots[i]!!.setImageDrawable(ContextCompat.getDrawable(this@New_AgentDetails,
                            R.drawable.non_active_dots))
                    }
                    dots[position]!!.setImageDrawable(ContextCompat.getDrawable(this@New_AgentDetails,
                        R.drawable.active_dots))
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun init() {
        binding!!.ibWeb.setOnClickListener(this)
        binding!!.btnAll.setOnClickListener(this)
        binding!!.ivTextIcon.setOnClickListener(this)
        binding!!.llVideoBtn.setOnClickListener(this)
        binding!!.llFollowBtn.setOnClickListener(this)
        binding!!.llMenu.setOnClickListener(this)
        binding!!.ivshare.setOnClickListener(this)
        binding!!.ivCall.setOnClickListener(this)
        binding!!.ibFb.setOnClickListener(this)
        binding!!.ibInsta.setOnClickListener(this)
        binding!!.ibYoutube.setOnClickListener(this)
        binding!!.llMap.setOnClickListener(this)
        binding!!.ivWhatsapp.setOnClickListener(this)
        binding!!.ivViber.setOnClickListener(this)
        binding!!.ivEmail.setOnClickListener(this)
        binding!!.ivDownTime.setOnClickListener {
            expandOrCollapse(binding!!.llDays,
                binding!!.llDays.visibility == View.GONE,
                40)
        }
        binding!!.donateBackgroungLayout.setOnClickListener {
            binding!!.donateBackgroungLayout.isEnabled = false
            getCharityList(agentId)
        }
        binding!!.tvTodaytime.setOnClickListener { binding!!.ivDownTime.performClick() }
        binding!!.tvToday.setOnClickListener { binding!!.ivDownTime.performClick() }
        binding!!.ratingBa.onRatingChangeListener = OnRatingChangeListener { ratingBar, rating ->
            try {
                binding!!.tvRating.text = rating.toString()
                if (!isFromAPi) {
                    //f (binding.tvRating.getText().toString().equalsIgnoreCase("0") || binding.tvRating.getText().toString().equalsIgnoreCase("0.0") || TextUtils.isEmpty(binding.tvRating.getText().toString()))
                    if (check_userCommented == false) {
                        startActivityForResult(Intent(this@New_AgentDetails,
                            Rating_ReviewActivity::class.java)
                            .putExtra("agentId", agentId)
                            .putExtra("Edit", "false")
                            .putExtra("id", "")
                            .putExtra("rating", binding!!.tvRating.text.toString())
                            .putExtra("review", ""), 80)
                    } else {
                        startActivityForResult(Intent(this@New_AgentDetails,
                            Rating_ReviewActivity::class.java)
                            .putExtra("agentId", agentId)
                            .putExtra("Edit", "true")
                            .putExtra("id", commentId)
                            .putExtra("rating", binding!!.tvRating.text.toString())
                            .putExtra("review", userComments), 80)
                    }
                }
            } catch (e: Exception) {
            }
        }
        binding!!.ratingBa.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                isFromAPi = false
            }
            false
        }
        binding!!.ivBack.setOnClickListener {
            try {
                if (isRating) {
                    Log.e("isRati", "15")
                    val returnIntent = intent
                    returnIntent.putExtra("pos", position)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        returnIntent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        returnIntent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else if (type == "direct") {
                    try {
                        startActivity(Intent(this@New_AgentDetails, SplashActivity::class.java))
                    } catch (e: Exception) {
                    }
                } else if (direct == "true") {
                    try {
                        startActivity(Intent(this@New_AgentDetails, MainActivity::class.java))
                    } catch (e: Exception) {
                    }
                } else if (direct == "exclusive_popup") {
                    try {
                        finish()
                        val bundle = Bundle()
                        bundle.putString("return_back", "home")
                        ErrorMessage.I(this@New_AgentDetails, NearMeHomeFragment::class.java,bundle)
                    } catch (e: Exception) {
                    }
                } else if (direct == "ActivityStorePoint") {
                    if (donateStatus == "Changing") {
                        try {
                            val returnIntent = intent
                            returnIntent.putExtra("pos", position)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                returnIntent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                returnIntent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            }
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        } catch (e: Exception) {
                        }
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            } catch (e: Exception) {
            }
        }
        binding!!.exComments.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (PreferenceHelper.getInstance(context)?.isLogin != true) {
                    startActivity(Intent(context, SignUpActivity::class.java)
                        .putExtra("isComment", "true"))
                }
            }
        })
    }

    private fun expandOrCollapse(v: View, is_expand: Boolean, animheight: Int) {
        var anim: TranslateAnimation? = null
        if (is_expand) {
            anim = TranslateAnimation(0.0f, 0.0f, (-animheight).toFloat(), 0.0f)
            v.visibility = View.VISIBLE
            val expandedlistener: Animation.AnimationListener =
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {}
                }
            binding!!.ivDownTime.setImageDrawable(context!!.resources.getDrawable(R.drawable.ic_baseline_arrow_drop_up_24))
            anim.duration = 400
            anim.setAnimationListener(expandedlistener)
        } else {
            anim = TranslateAnimation(0.0f, 0.0f, 0.0f, (-animheight).toFloat())
            val collapselistener: Animation.AnimationListener =
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        v.visibility = View.GONE
                    }
                }
            binding!!.ivDownTime.setImageDrawable(context!!.resources.getDrawable(R.drawable.ic_baseline_arrow_drop_down_24))
            anim.duration = 400
            anim.setAnimationListener(collapselistener)
        }
        anim.interpolator = AccelerateInterpolator(0.5f)
        v.startAnimation(anim)
    }

    override fun onResume() {
        super.onResume()
        try {
            if (StoreGiftCardActivity.somethingDone) {
                StoreGiftCardActivity.somethingDone = false
                agentDetails
            }
        } catch (e: Exception) {
            Log.e("Exception", "" + e.toString())
        }
        isFromAPi = true
        binding!!.ratingBa.rating = starRating.toFloat()
        try {
            StatusBarcolor.setStatusbarColor(this@New_AgentDetails, "colorPrimary")
        } catch (e: Exception) {
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAll -> startActivityForResult(Intent(context, AgentAllComments::class.java)
                .putExtra("agentId", agentId)
                .putExtra("productId", productId), 80)
            R.id.ib_fb -> if (agentFbUrl != null && agentFbUrl!!.length > 0) {
                val browserIntent1 = Intent(Intent.ACTION_VIEW, Uri.parse(agentFbUrl))
                startActivity(browserIntent1)
            }
            R.id.ibWeb -> if (agentURL != null) {
                Log.e("URL", agentURL!!)
                val browserIntent1 = Intent(Intent.ACTION_VIEW, Uri.parse(agentURL))
                startActivity(browserIntent1)
            }
            R.id.ib_insta -> if (agentInstaUrl != null && agentInstaUrl!!.length > 0) {
                val browserIntent1 = Intent(Intent.ACTION_VIEW, Uri.parse(agentInstaUrl))
                startActivity(browserIntent1)
            }
            R.id.llMenu -> if (menuURLStatus.equals("1", ignoreCase = true)) {
                startActivity(Intent(this@New_AgentDetails, Webview::class.java)
                    .putExtra("url", agentObj!!.optString("menuURL"))
                    .putExtra("type", "non_direct")
                    .putExtra("title", ""))
            }
            R.id.ib_youtube -> if (agentYoutubeUrl != null && agentYoutubeUrl!!.length > 0) {
                val browserIntent1 = Intent(Intent.ACTION_VIEW, Uri.parse(agentYoutubeUrl))
                startActivity(browserIntent1)
            }
            R.id.llMap -> {
                val location =
                    "http://maps.google.com/maps?q=" + binding!!.tvAddress.text.toString()
                        .trim { it <= ' ' }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(location))
                startActivity(browserIntent)
            }
            R.id.ivCall -> {
                val contact = agentContact.split(",".toRegex()).toTypedArray()
                val contactList: MutableList<String> = ArrayList()
                var i = 0
                while (i < contact.size) {
                    contactList.add(contact[i])
                    i++
                }
                if (contactList.size > 1) {
                    contactListPopup(contactList)
                } else {
                    val intent1 = Intent(Intent.ACTION_CALL)
                    intent1.data = Uri.parse("tel:$agentContact")

                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            MY_PERMISSIONS_REQUEST_CALL_PHONE)
                    } else {
                        try {
                            startActivity(intent1)
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            R.id.iv_text_icon -> showPopupSuccessMsg("Text", agentTextNumber)
            R.id.ivWhatsapp -> showPopupSuccessMsg("Whatsapp", agentWhatsappNumber)
            R.id.ivshare -> AppUtil.shareIntent(context,
                agentObj!!.optString(KeyConstant.KEY_SHARE_URL))
            R.id.ivEmail -> showPopupSuccessMsg("Email", agentContactEmailId)
            R.id.ivViber -> showPopupSuccessMsg("Viber", agentViberNumber)
            R.id.ll_video_btn -> try {
                if (bannerArrayList!!.getJSONObject(bannerVideoPosition)
                        .optString("bannerVideoType") == "youtube"
                ) {
                    val intent = Intent(context, PlayYouTubeVideoActivity::class.java)
                    intent.putExtra("videoUrl",
                        bannerArrayList!!.getJSONObject(bannerVideoPosition)
                            .optString("bannerVideoURL"))
                    intent.putExtra("videoId",
                        bannerArrayList!!.getJSONObject(bannerVideoPosition)
                            .optString("youtubeVideoId"))
                    context!!.startActivity(intent)
                } else {
                    val intent = Intent(context, PlayVideoActivity::class.java)
                    intent.putExtra("videoUrl",
                        bannerArrayList!!.getJSONObject(bannerVideoPosition)
                            .optString("bannerVideoURL"))
                    context!!.startActivity(intent)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            R.id.ll_follow_btn -> if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                followAgent()
            } else if (PreferenceHelper.getInstance(context)?.isLogin == false) {
                val intent = Intent(context, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun contactListPopup(list: List<String>) {
        val dialog1 = Dialog(context!!, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_multiple_contact_list)
        //dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bg_qrcode_popup);
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val recyclerView = dialog1.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = AgentContactListAdapter(context!!, this, list)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        dialog1.setCanceledOnTouchOutside(true)
        dialog1.setCancelable(true)
        dialog1.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CALL_PHONE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the phone call
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun showPopupSuccessMsg(title: String, msg: String) {
        val dialog1 = Dialog(context!!)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contextInBold = dialog1.findViewById<TextView>(R.id.popup_content_inbold)
        contextInBold.visibility = View.VISIBLE
        contextInBold.text = title
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = msg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "Cancel"
        btnNo.visibility = View.VISIBLE
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Open"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                dialog1.dismiss()
                if (title.equals("Text", ignoreCase = true)) {
                    openTextMsg(msg)
                } else if (title.equals("Whatsapp", ignoreCase = true)) {
                    openWhatsApp(msg)
                } else if (title.equals("Viber", ignoreCase = true)) {
                    openViber(msg)
                } else if (title.equals("Email", ignoreCase = true)) {
                    openEmail(msg)
                }
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun openTextMsg(number: String) {
        try {
            Log.e("Number", number)
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.data = Uri.parse("sms:$number")
            sendIntent.putExtra("sms_body", "")
            startActivity(sendIntent)
        } catch (e: Exception) {
        }
    }

    private fun openEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf(email)
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(agentURL))
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            intent.putExtra(Intent.EXTRA_CC, email)
            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        } catch (e: Exception) {
        }
    }

    private fun openViber(number: String) {
        val url = "viber://add?number=$number"
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
        }
    }

    private fun openWhatsApp(number: String) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
        }
    }


    override fun onBackPressed() {
        if (!direct.equals("", ignoreCase = true) && direct.equals("true", ignoreCase = true)) {
            startActivity(Intent(this@New_AgentDetails, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        } else if (direct == "ActivityStorePoint") {
            if (donateStatus == "Changing") {
                try {
                    val returnIntent = intent
                    returnIntent.putExtra("pos", position)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        returnIntent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        returnIntent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } catch (e: Exception) {
                }
            } else {
                finish()
            }
        } else if (direct == "exclusive_popup") {

                try {
                    finish()
                    val bundle = Bundle()
                    bundle.putString("return_back", "home")
                    ErrorMessage.I/*_clear*/(this@New_AgentDetails, NearMeHomeFragment::class.java,bundle)
                } catch (e: Exception) {
                }

        }else if (type == "direct") {
            try {
            } catch (e: Exception) {
            }
            startActivity(Intent(this@New_AgentDetails, SplashActivity::class.java))
        } else {
            finish()
        }
        super.onBackPressed()
    }

    override fun finish() {
        if (isRating) {
            Log.e("isRati", "11")
            val returnIntent = intent
            returnIntent.putExtra("", position)
            Log.e("Positin", position.toString() + "")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                returnIntent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                returnIntent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            setResult(RESULT_OK, returnIntent)
        }
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun moveToDetailsScreen(agentId: String?, productId: String?) {
        val `in` = Intent(context, LatestProductDetails::class.java)
        `in`.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        `in`.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        `in`.putExtra("type", "non_direct")
        startActivityForResult(`in`, 40)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Codee", requestCode.toString() + "")
        if (resultCode == RESULT_OK && requestCode == 40) {
            runOnUiThread {
                val fav = data!!.getStringExtra("fav")
                val id = data.getStringExtra("id")
                Log.e("id", "$fav,$id")
                try {
                    val arr = JSONArray(dealList.toString())
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i)
                        val dealFavourite = obj.optString("dealFavourite")
                        if (obj.optString("dealId").equals(id, ignoreCase = true)) {
                            Log.e("j", i.toString() + "")
                            obj.put("dealFavourite", fav!!.toInt())
                            arr.put(i, obj)
                            try {
                                adapHorizontal!!.updateData(i, fav)
                            } catch (e: Exception) {
                            }
                            break
                        }
                    }
                } catch (e: Exception) {
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == 80) {
            if (AppUtil.isNetworkAvailable(context)) {
                agentDetails
            } else {
                AppUtil.showMsgAlert(binding!!.tvReview,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }
    }

    fun fullscreenPager(image: JSONArray?, position: Int) {
        dialog = Dialog(this@New_AgentDetails, R.style.full_screen_dialog)
        // Include dialog.xml file
        dialog!!.setContentView(R.layout.fullscreenpager)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        dialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        val viewPager = dialog!!.findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = dialog!!.findViewById<TabLayout>(R.id.tablayout)
        val ivClose = dialog!!.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { dialog!!.dismiss() }
        viewPager.adapter = LeafLet_Adapter(this@New_AgentDetails, image!!)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = position
        dialog!!.show()
    }

    private fun editComment(id: String, rating: String, review: String) {
        startActivityForResult(Intent(this@New_AgentDetails, Rating_ReviewActivity::class.java)
            .putExtra("agentId", agentId)
            .putExtra("Edit", "true")
            .putExtra("id", id)
            .putExtra("rating", rating)
            .putExtra("review", review), 80)
    }

    private fun voteNowPopup(awardID: Int, refreshCard_: RefreshCard) {
        if (dialog_vote_popup == null) {
            dialog_vote_popup = Dialog(this@New_AgentDetails, R.style.NewDialog)
            dialog_vote_popup!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog_vote_popup!!.setContentView(R.layout.vote_now_popup)
            dialog_vote_popup!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog_vote_popup!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.FILL_PARENT
            lp.height = WindowManager.LayoutParams.FILL_PARENT
            dialog_vote_popup!!.window!!.attributes = lp
            dialog_vote_popup!!.setCanceledOnTouchOutside(false)
            dialog_vote_popup!!.setCancelable(false)
            val ivClose = dialog_vote_popup!!.findViewById<ImageView>(R.id.ivClose)
            val iv_banner_img =
                dialog_vote_popup!!.findViewById<RoundedImageView>(R.id.iv_banner_img)
            et_vote_email_id = dialog_vote_popup!!.findViewById(R.id.et_vote_email_id)
            val tv_agent_company_name =
                dialog_vote_popup!!.findViewById<TextView>(R.id.tv_agent_company_name)
            tv_share_note = dialog_vote_popup!!.findViewById(R.id.tv_share_note)
            rcv_vote_now = dialog_vote_popup!!.findViewById(R.id.rcv_vote_now)
            val ll_sharing_apps =
                dialog_vote_popup!!.findViewById<LinearLayout>(R.id.ll_sharing_apps)
            val iv_fb = dialog_vote_popup!!.findViewById<ImageView>(R.id.iv_fb)
            val iv_twitter = dialog_vote_popup!!.findViewById<ImageView>(R.id.iv_twitter)
            val iv_insta = dialog_vote_popup!!.findViewById<ImageView>(R.id.iv_insta)
            val iv_whatsapp = dialog_vote_popup!!.findViewById<ImageView>(R.id.iv_whatsapp)
            Glide.with(context!!)
                .load(example!!.response.agentDetails[0].awardBanner)
                .placeholder(R.drawable.mainimageplaceholder)
                .error(R.drawable.mainimageplaceholder)
                .into(iv_banner_img)
            tv_agent_company_name.text = example!!.response.agentDetails[0].agentCompanyName
            tv_share_note!!.setText(example!!.response.agentDetails[0].shareNotes)
            if (example!!.response.agentDetails[0].categoryNameList.size > 0) {
                val adap = AdapterVoteNow(this@New_AgentDetails,
                    example!!.response.agentDetails[0].categoryNameList,
                    awardID)
                rcv_vote_now!!.setLayoutManager(LinearLayoutManager(this@New_AgentDetails,
                    LinearLayoutManager.VERTICAL,
                    false))
                rcv_vote_now!!.setAdapter(adap)
                adap.notifyDataSetChanged()
            }
            iv_fb.setOnClickListener { fbshare(example!!.response.agentDetails[0].shareMessage) }
            iv_twitter.setOnClickListener { twitterShare(example!!.response.agentDetails[0].shareMessage) }
            iv_insta.setOnClickListener { instaShare(example!!.response.agentDetails[0].shareMessage) }
            iv_whatsapp.setOnClickListener { whatsappShare(example!!.response.agentDetails[0].shareMessage) }
            ivClose.setOnClickListener { view ->
                AppUtil.hideKeyboard(view,context)
                dialog_vote_popup!!.dismiss()
                dialog_vote_popup = null
            }
            if (!dialog_vote_popup!!.isShowing) {
                dialog_vote_popup!!.show()
                refreshCard_.onSuccess(";cdkv;k")
            }
        }
    }

    fun agentAwardDetailsToServer(awardID: Int, check: Boolean, Position: Int, refreshCard: RefreshCard, ) {
        if (dialog_vote_popup == null) {
            if (AppUtil.isNetworkAvailable(this@New_AgentDetails)) {
                val dialogManager = DialogManager()
                dialogManager.showProcessDialog(this, "", false, null)
                val call = AppConfig.api_Interface().getAgentAwardDetails(
                    agentId!!.toInt(), awardID)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        if (response.isSuccessful) {
                            Log.e("sendToken", response.code().toString())
                            try {
                                dialogManager.stopProcessDialog()
                                try {
                                    val obj = JSONObject(response.body()!!.string())
                                    ErrorMessage.E("getAgentAwardDetails$obj")
                                    val gson = Gson()
                                    example = gson.fromJson(obj.toString(), Example::class.java)
                                    if (check) {
                                        voteNowPopup(awardID, refreshCard)
                                    } else {
                                        if (example!!.getResponse().agentDetails[0].categoryNameList.size > 0) {
                                            val adap = AdapterVoteNow(this@New_AgentDetails,
                                                example!!.getResponse().agentDetails[0].categoryNameList,
                                                awardID)
                                            rcv_vote_now!!.layoutManager =
                                                LinearLayoutManager(this@New_AgentDetails,
                                                    LinearLayoutManager.VERTICAL,
                                                    false)
                                            rcv_vote_now!!.adapter = adap
                                            rcv_vote_now!!.isNestedScrollingEnabled = false
                                            adap.notifyDataSetChanged()
                                        }
                                        tv_share_note!!.setTextColor(resources.getColor(R.color.black))
                                    }
                                } catch (e: Exception) {
                                    ErrorMessage.E("Exception$e")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                dialogManager.stopProcessDialog()
                                Log.e("Exception", e.toString())
                            }
                        } else {
                            dialogManager.stopProcessDialog()
                            Log.e("sendToken >>", response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        // ErrorMessage.T(getActivity(), "Response Fail");
                        println("============update profile fail  :$t")
                        dialogManager.stopProcessDialog()
                    }
                })
            } else {
                ErrorMessage.T(this@New_AgentDetails, "No Internet Found!")
            }
        } else {
            refreshCard.onSuccess(";cdkv;k")
        }
    }

    fun emailValidation(
        voteStatus: Int,
        categoryId: Int,
        awardID: Int,
        position: Int,
        refreshCard__: RefreshCard?,
    ) {
        refreshCard = refreshCard__
        if (et_vote_email_id!!.text.toString().isEmpty()) {
            voteToServer(voteStatus, categoryId, awardID, position)
        } else {
            if (UserAccount.isEmailValid(et_vote_email_id!!)) {
                voteToServer(voteStatus, categoryId, awardID, position)
            } else {
                et_vote_email_id!!.requestFocus()
                et_vote_email_id!!.error = "Invalid Email"
            }
        }
    }

    private fun voteToServer(voteStatus: Int, categoryId: Int, awardID: Int, Position: Int) {
        if (AppUtil.isNetworkAvailable(this@New_AgentDetails)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().vote(
                agentId!!.toInt(),
                voteStatus,
                categoryId,
                et_vote_email_id!!.text.toString(),
                awardID)
            call!!.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()
                            try {
                                val obj = JSONObject(response.body()!!.string())
                                ErrorMessage.E("vote : $obj")
                                refreshCard!!.onSuccess(";cdkv;k")
                                //agentAwardDetailsToServer(awardID, false, Position);
                                infoPopup(obj.optString("message"), awardID)
                            } catch (e: Exception) {
                                ErrorMessage.E("Exception$e")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken else is working", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    // ErrorMessage.T(getActivity(), "Response Fail");
                    println("============update profile fail  :$t")
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@New_AgentDetails, "No Internet Found!")
        }
    }

    private fun fbshare(shareMsg: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent
            .putExtra(Intent.EXTRA_TEXT, shareMsg)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.facebook.orca")
        try {
            startActivity(sendIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_SHORT).show()
        }
    }

    private fun instaShare(shareMsg: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent
            .putExtra(Intent.EXTRA_TEXT, shareMsg)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.instagram.android")
        try {
            startActivity(sendIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "Please Install Instagram", Toast.LENGTH_SHORT).show()
        }
    }

    private fun twitterShare(shareMsg: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent
            .putExtra(Intent.EXTRA_TEXT, shareMsg)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.twitter.android")
        try {
            startActivity(sendIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "Please Install Twitter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun whatsappShare(shareMsg: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent
            .putExtra(Intent.EXTRA_TEXT, shareMsg)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        try {
            startActivity(sendIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "Please Install Whatsapp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun infoPopup(responseMsg: String, awardID: Int) {
        val dialog1 = Dialog(this)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = responseMsg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.visibility = View.GONE
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "OK"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun viewGallery() {
        val dialog = Dialog(this@New_AgentDetails, R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.view_gallery_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog.window!!.attributes = lp
        val iv_galleryClose = dialog.findViewById<ImageView>(R.id.iv_galleryClose)
        viewpager_view_gallery = dialog.findViewById(R.id.viewpager_gallery)
        tv_num_pages = dialog.findViewById(R.id.tv_num_pages)
        if (bannerArrayList!!.length() > 0) {
            ErrorMessage.E("BANNERLIST_SIZE >>> " + bannerArrayList!!.length())
            viewpager_view_gallery!!.setBackground(null)
            setViewPager_viewgallery()
        }
        iv_galleryClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setViewPager_viewgallery() {
        runOnUiThread {
            try {
                imageSliderList = ArrayList()
                imageSliderList!!.clear()
                for (i in 0 until bannerArrayList!!.length()) {
                    Log.e("bannee", i.toString() + "")
                    val dataModel = VideoDataModel()
                    val `object` = bannerArrayList!!.optJSONObject(i)
                    val bannerImageUrl = `object`.optString(KeyConstant.KEY_BANNER_IMAGE_URL)
                    val bannerVideoUrl = `object`.optString(KeyConstant.KEY_BANNER_VIDEO_URL)
                    val bannerVideoType = `object`.optString(KeyConstant.KEY_BANNER_VIDEO_TYPE)
                    val youtubeVideoId = `object`.optString(KeyConstant.KEY_YOUTUBE_VIDEO_ID)
                    val bannerType = `object`.optInt(KeyConstant.KEY_BANNER_TYPE)
                    dataModel.bannerImageUrl = bannerImageUrl
                    dataModel.bannerVideoUrl = bannerVideoUrl
                    dataModel.bannerType = bannerType.toString() + ""
                    dataModel.bannerVideoType = bannerVideoType
                    dataModel.youtubeVideoId = youtubeVideoId
                    imageSliderList!!.add(dataModel)
                }
                val viewPagerAdapter: ViewPagerAdapters<*> =
                    ViewPagerAdapters<Any?>(this@New_AgentDetails, imageSliderList!!, true)
                viewpager_view_gallery!!.adapter = viewPagerAdapter
                tv_num_pages!!.text = "1/" + bannerArrayList!!.length()
            } catch (e: Exception) {
            }
            viewpager_view_gallery!!.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                }

                override fun onPageSelected(position: Int) {
                    //tv_num_pages.setText(position + 1 + "/" + bannerArrayList!!.length())
                    tv_num_pages!!.setText((position + 1).toString() + "/" + bannerArrayList!!.length().toString())
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 222
        @JvmField
        var charity_id2 = ""
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }

    private fun followAgent() {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.followAgent(this, agentId,binding!!.tvfollow,object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    ErrorMessage.E("FollowAgent>>"+resp.toString())
                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                        runOnUiThread {
                            binding!!.llFollowBtn.isEnabled = false
                            binding!!.ivFollowing.setImageDrawable(context!!.resources.getDrawable(R.drawable.heartfulled))
                            binding!!.tvfollow.text = " Following "
                            binding!!.tvfollow.setTextColor(resources.getColor(R.color.pink))
                            binding!!.llFollowBtn.background = resources.getDrawable(R.drawable.background_border_pink)
                            agentDetails
                        }
                    } else {
                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                ignoreCase = true)
                        ) {
                            AppUtil.showMsgAlert(binding!!.tvReview,
                                resp.optString(KeyConstant.KEY_MESSAGE))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(binding!!.tvReview,
                        MessageConstant.MESSAGE_SOMETHING_WRONG)
                }catch (e: IOException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(binding!!.tvReview,
                        MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > " + text)
                AppUtil.showMsgAlert(binding!!.tvAddress, text)
            }
        })
    }

    private fun deleteComment(commentId: String) {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.deleteComment(this, commentId,binding!!.tvCommentNew,object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    Log.e("DeleetComet", resp.toString())
                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                        (context as Activity).runOnUiThread {
                            agentDetails
                            try {
                                for (i in NearMeHomeFragment.modelList.indices) {
                                    if (NearMeHomeFragment.modelList[i].agentId.equals(
                                            agentId,
                                            ignoreCase = true)
                                    ) {
                                        Log.e("Pro", agentId!!)
                                        NearMeHomeFragment.modelList[i].agentRating =
                                            resp.optString("agentRating")
                                        NearMeHomeFragment.adapter!!.notifyItemChanged(i)
                                        break
                                    }
                                }
                            } catch (e: Exception) {
                            }

                            AppUtil.showMsgAlert(binding!!.tvCommentNew,
                                resp.optString(KeyConstant.KEY_MESSAGE))
                        }
                    } else {
                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                ignoreCase = true)
                        ) {
                            AppUtil.showMsgAlert(binding!!.tvCommentNew,
                                resp.optString(KeyConstant.KEY_MESSAGE))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(binding!!.tvCommentNew, MessageConstant.MESSAGE_SOMETHING_WRONG)
                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUtil.showMsgAlert(binding!!.tvCommentNew, MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > $text")
                AppUtil.showMsgAlert(binding!!.tvAddress, text)
            }
        })
    }

    private fun getCharityList(agentId: String?) {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.getCharityList(this, agentId,binding!!.tvfollow,object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                        val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                        Log.e("cHARITYrESPOJSE", responseObj.toString())
                        val mList = ArrayList<CharityListBean>()
                        mList.clear()
                        val ar = responseObj.optJSONArray("charityList")
                        for (i in 0 until ar.length()) {
                            val obj = ar.optJSONObject(i)
                            val charityId = obj.optString("charityId")
                            val charityName = obj.optString("charityName")
                            val charitySubName = obj.optString("charitySubName")
                            val charityStatus = obj.optString("charityStatus")
                            val charityJoinedText = obj.optString("charityJoinedText")
                            val charityWebURL = obj.optString("charityWebURL")
                            val charityImage = obj.optString("charityImage")
                            val charityDescription = obj.optString("charityDescription")
                            val cn = CharityListBean(charityId,
                                charityName,
                                charitySubName,
                                charityStatus,
                                charityJoinedText,
                                charityWebURL,
                                charityImage,
                                charityDescription,
                                false)
                            mList.add(cn)
                        }
                        runOnUiThread {
                            isChange = false
                            if (dialog1 == null) {
                                dialog1 = Dialog(context!!, R.style.NewDialog)
                                dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog1!!.setContentView(R.layout.dialogcharity)
                                val lp = WindowManager.LayoutParams()
                                lp.copyFrom(dialog1!!.window!!.attributes)
                                lp.width = WindowManager.LayoutParams.FILL_PARENT
                                lp.height = WindowManager.LayoutParams.FILL_PARENT
                                dialog1!!.window!!.attributes = lp
                                val ivClose =
                                    dialog1!!.findViewById<ImageView>(R.id.ivClose)
                                ivClose.setOnClickListener {
                                    Log.e("isCha", isChange.toString() + "")
                                    charity_id2 = ""
                                    if (dialog1 != null) {
                                        dialog1!!.dismiss()
                                        binding!!.donateBackgroungLayout.isEnabled = true
                                        dialog1 = null
                                    }
                                }
                                val rv =
                                    dialog1!!.findViewById<RecyclerView>(R.id.rvCharitylist)
                                val tvPoints =
                                    dialog1!!.findViewById<TextView>(R.id.tvPoints)
                                val tvP = dialog1!!.findViewById<TextView>(R.id.tvP)
                                val tvAgree = dialog1!!.findViewById<TextView>(R.id.tvAgree)
                                val tvCharity =
                                    dialog1!!.findViewById<TextView>(R.id.tvCharity)
                                val cbAgr = dialog1!!.findViewById<CheckBox>(R.id.cbAgr)
                                val btnDonate =
                                    dialog1!!.findViewById<Button>(R.id.btnDonate)
                                tvAgree.setOnClickListener {
                                    context!!.startActivity(Intent(context,
                                        Webview::class.java)
                                        .putExtra("url",
                                            responseObj.optString("termsAndConditionsURL"))
                                        .putExtra("type", "non_direct")
                                        .putExtra("title", "Terms & Conditions"))
                                }
                                tvPoints.text =
                                    "(" + responseObj.optString("redeemRemarks") + ")"
                                val cbcollectmy =
                                    dialog1!!.findViewById<RadioButton>(R.id.cbcollectmy)
                                if (responseObj.optString("charityStatus")
                                        .equals("1", ignoreCase = true)
                                ) {
                                    cbcollectmy.isChecked = true
                                    charity_id2 = responseObj.optString("charityId")
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            cbcollectmy.buttonTintList =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        context!!, R.color.colorPrimary))
                                        }
                                    } catch (e: Exception) {
                                    }
                                } else {
                                    cbcollectmy.isChecked = false
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            cbcollectmy.buttonTintList =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        context!!,
                                                        R.color.defult_background))
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                                tvP.text = responseObj.optString("myswlfText")
                                tvCharity.text = responseObj.optString("charityTitleText")
                                btnDonate.setOnClickListener(View.OnClickListener {
                                    if (charity_id2.equals("", ignoreCase = true)) {
                                        AppUtil.showMsgAlert(tvP,
                                            "Select One of the charity")
                                        return@OnClickListener
                                    } else if (!cbAgr.isChecked) {
                                        AppUtil.showMsgAlert(tvP,
                                            "Agree terms and conditions")
                                        return@OnClickListener
                                    } else if (!AppUtil.isNetworkAvailable(context)) {
                                        AppUtil.showMsgAlert(tvP,
                                            MessageConstant.MESSAGE_INTERNET_CONNECTION)
                                    } else {
                                        ErrorMessage.E("charity_id2" + charity_id2)
                                        doCharity(agentId, charity_id2)
                                        binding!!.donateBackgroungLayout.isEnabled = true
                                        if (dialog1 != null) {
                                            dialog1!!.dismiss()
                                            dialog1 = null
                                        }
                                    }
                                })
                                val adap = AdapterCharityListStorePoints_New(context!!,
                                    mList,
                                    this@New_AgentDetails,
                                    cbcollectmy)
                                rv.layoutManager = LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false)
                                rv.adapter = adap
                                cbcollectmy.setOnCheckedChangeListener { compoundButton, b ->
                                    if (b) {
                                        for (i in mList.indices) {
                                            mList[i].isChecked = false
                                            mList[i].charityStatus = "0"
                                            charity_id2 = responseObj.optString("charityId")
                                            val adap = AdapterCharityListStorePoints_New(
                                                context!!,
                                                mList,
                                                this@New_AgentDetails,
                                                cbcollectmy)
                                            rv.layoutManager = LinearLayoutManager(context,
                                                LinearLayoutManager.VERTICAL,
                                                false)
                                            rv.adapter = adap
                                        }
                                    } else {
                                    }
                                }
                                dialog1!!.show()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    // swipeRefreshLayout.setRefreshing(false);
                    AppUtil.showMsgAlert(binding!!.tvAddress, MessageConstant.MESSAGE_SOMETHING_WRONG)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    AppUtil.showMsgAlert(binding!!.tvAddress, MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > $text")
                AppUtil.showMsgAlert(binding!!.tvAddress, text)
            }
        })
    }

    private fun doCharity(agentId: String?, charityId: String) {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.doCharity(this, agentId,charityId,binding!!.tvfollow,object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    Log.e("DoCharity", resp.toString())
                    if (resp.getString("error_type") == "200") {
                        runOnUiThread {
                            try {
                                Toast.makeText(this@New_AgentDetails,
                                    resp.optString("message"),
                                    Toast.LENGTH_SHORT).show()
                                try {
                                    if (dialog1 != null) {
                                        dialog1!!.dismiss()
                                    }
                                    donateStatus = "Changing"
                                    ErrorMessage.E("MESSAGE > " + resp.getString("message"))
                                    if (resp.getString("message")
                                            .contains("Hereafter you are started collecting points for yourself.")
                                    ) {
                                        binding!!.tvDonate.text = "Donate"
                                        binding!!.donateBackgroungLayout.background =
                                            context!!.resources.getDrawable(R.drawable.background_donate_green)
                                    } else if (resp.getString("message")
                                            .contains("Thank you for donating your points to the charity")
                                    ) {
                                        binding!!.tvDonate.text = "Donating"
                                        binding!!.donateBackgroungLayout.background =
                                            resources.getDrawable(R.drawable.background_donate_pink)
                                        binding!!.donateBackgroungLayout.setBackgroundColor(
                                            ContextCompat.getColor(this@New_AgentDetails,
                                                R.color.pink))
                                    }
                                } catch (e: Exception) {
                                    Log.e("DoCharity error", e.toString() + "")
                                }
                            } catch (e: Exception) {
                            }
                        }
                    } else {
                        AdapterStorePoints.isChange = false
                        (context as Activity?)!!.runOnUiThread {
                            Toast.makeText(this@New_AgentDetails, resp.optString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    try {
                        AdapterStorePoints.isChange = false
                    } catch (es: Exception) {
                    }
                    Toast.makeText(this@New_AgentDetails,
                        MessageConstant.MESSAGE_SOMETHING_WRONG,
                        Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    try {
                        AdapterStorePoints.isChange = false
                    } catch (es: Exception) {
                    }
                    Toast.makeText(this@New_AgentDetails,
                        MessageConstant.MESSAGE_SOMETHING_WRONG,
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > $text")
                AppUtil.showMsgAlert(binding!!.tvAddress, text)
            }
        })
    }
}