package sambal.mydd.app.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.abs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.activity.*
import sambal.mydd.app.activity.reward_club.RewardClubActivity
import sambal.mydd.app.adapter.*
import sambal.mydd.app.beans.HomeBannerLIst
import sambal.mydd.app.beans.MenuList
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.NewhomesignupBinding
import sambal.mydd.app.local_image_cache.ImageLoader
import sambal.mydd.app.models.HomePageDeal.LatestDeals
import sambal.mydd.app.models.MyDeal_Models.Example
import sambal.mydd.app.runtimePermission.PermissionsManager
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), View.OnClickListener {
    private val dialog1: Dialog? = null
    var context1: Context? = null
    var bitmap: Bitmap? = null
    var `object`: JSONObject? = null
    var binding: NewhomesignupBinding? = null
    var mList = ArrayList<HomeBannerLIst>()
    var latestDeal = ArrayList<LatestDeals>()
    var menuList = ArrayList<MenuList>()
    var dialogManager: DialogManager? = null
    var o: JSONObject? = null
    var referBusinessWebURL = ""
    var cakeWebURL = ""
    var koodaiWebURL = ""
    var dineInWebURL = ""
    var OrderOnlineWebURL = ""
    var permissionsManager: PermissionsManager? = null
    var packageManager: PackageManager? = null
    private var ddKitchenWebURL = ""
    private var ddGroceryWebURL = "-1"
    var my_pramotion_layout: LinearLayout? = null
    var my_deal_layout: LinearLayout? = null
    var more_layout: LinearLayout? = null
    var my_pramotion_rcv: RecyclerView? = null
    var my_deals_rcv: RecyclerView? = null
    var title_tv: TextView? = null
    var top_title_tv: TextView? = null
    var top_image: RoundedImageView? = null
    var second_title_tv: TextView? = null
    var my_deal_cardview: CardView? = null
    private var imgLoader: ImageLoader? = null
    var notificationCount: String? = null
    private var isDeviceLocked = false
    private var Check_popup_visible = ""
    var exclusive_dialog: Dialog? = null
    var reedeemreward_dialog: Dialog? = null
    private var promo_code_popup: Dialog? = null
    val myFragment = HomeFragment
    private var offer_popup_counter = 0
    var loadingbarforfetchingdata: Dialog? = null
    private var gift_card_popup: Dialog? = null
    var ourProfileAgentId = "";
    private var lastScrollY = 0
    private var isTopCardVisible = true
    private var lastScrollPosition = -1
    private var hasTriggeredBottom = false
    private var hasTriggeredTop = true
    private var isTopCardAnimationRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.newhomesignup,
            container,
            false
        ) /*newhomesignup*/
        val view = binding?.getRoot()
        packageManager = activity?.packageManager
        permissionsManager = PermissionsManager.instance
        context1 = activity
        view?.let { initView(it) }
        dialogManager = DialogManager()
        imgLoader = ImageLoader(activity)


        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(onNotice, IntentFilter("Profile"))
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(onNotice1, IntentFilter("update_noti"))

        binding!!.nestedScolling.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (isTopCardAnimationRunning) {
                return@setOnScrollChangeListener
            }

            val delta = scrollY - oldScrollY
            if (abs(delta) < 12) {
                return@setOnScrollChangeListener
            }

            val nestedScrollView = v as? NestedScrollView
            val isScrollingDown = delta > 0
            val isScrollingUp = delta < 0
            val atBottom = nestedScrollView?.canScrollVertically(1)?.not() ?: false
            val atTop = nestedScrollView?.canScrollVertically(-1)?.not() ?: false

            val topCard = binding!!.topCardLayout
            val menuLayout = binding!!.menuLayout

            if (isScrollingDown && isTopCardVisible && !atBottom) {
                // Scroll down → hide topCard, show menu
                isTopCardAnimationRunning = true
                menuLayout.animate().cancel()
                topCard.animate().cancel()

                menuLayout.visibility = View.VISIBLE
                menuLayout.alpha = 0f
                menuLayout.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()

                topCard.animate()
                    .alpha(0f)
                    .translationY(-topCard.height.toFloat())
                    .setDuration(250)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction {
                        topCard.visibility = View.GONE
                        topCard.alpha = 1f
                        isTopCardAnimationRunning = false
                    }
                    .start()

                isTopCardVisible = false
                ErrorMessage.E("Hiding top card - scrolling down")

            } else if (isScrollingUp && !isTopCardVisible && !atTop) {
                // Scroll up → show topCard, hide menu
                isTopCardAnimationRunning = true
                menuLayout.animate().cancel()
                topCard.animate().cancel()

                topCard.visibility = View.VISIBLE
                topCard.alpha = 0f
                topCard.translationY = -topCard.height.toFloat()

                menuLayout.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction {
                        menuLayout.visibility = View.GONE
                        menuLayout.alpha = 1f
                    }
                    .start()

                topCard.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(250)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction {
                        isTopCardAnimationRunning = false
                    }
                    .start()

                isTopCardVisible = true
                ErrorMessage.E("Showing top card - scrolling up")
            }

            lastScrollPosition = scrollY
        }





        try {
            if (AppUtil.isNetworkAvailable(activity)) {

            } else {
                // Log.e("check ", ">>" + SavedData.getAllData())
                if (SavedData.getAllData() != "") {
                    try {
                        // Log.e("All data", "broadcast" + SavedData.getAllData())

                        val resp = JSONObject(SavedData.getAllData())

                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)

                        if (errorType.equals(
                                KeyConstant.KEY_RESPONSE_CODE_200,
                                ignoreCase = true
                            )
                        ) {
                            `object` = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                            val arr = `object`?.optJSONArray("userDetails")
                            if (arr != null) {
                                for (i in 0 until arr.length()) {
                                    o = arr?.optJSONObject(i)

                                    try {
                                        SharedPreferenceVariable.savePreferences(
                                            activity,
                                            KeyConstant.KEY_USER_ID,
                                            o?.optString("userId")
                                        )
                                        SharedPreferenceVariable.savePreferences(
                                            activity,
                                            KeyConstant.KEY_GOOGLE_API_KEY,
                                            o?.optString("googleAPIKey")
                                        )
                                        referBusinessWebURL =
                                            o?.optString("referBusinessWebURL").toString()

                                        cakeWebURL =
                                            o?.optString("cakeWebURL").toString()
                                        koodaiWebURL =
                                            o?.optString("koodaiWebURL").toString()
                                        OrderOnlineWebURL =
                                            o?.optString("OrderOnlineWebURL").toString()
                                        dineInWebURL =
                                            o?.optString("dineInWebURL").toString()

                                        binding?.ticketPointsValueTv?.text =
                                            o?.optString("userAvailablePoints")

                                        binding?.ticketVoucherValueTv?.text =
                                            o?.optString("storeVouchersValue")

                                    } catch (e: Exception) {
                                    }
                                    // Log.e("Promo", o?.optString("promocodeStatus").toString())
                                    if (!o?.optString("promocodeStatus")
                                            .equals("0", ignoreCase = true)
                                    ) {
                                        if (SharedPreferenceVariable.loadSavedPreferences(
                                                activity,
                                                KeyConstant.KEY_OPEN_PROMOCODE
                                            )?.isEmpty()!!
                                        ) {
                                            showPromoCode(
                                                o?.optString("promocodeStatus").toString(),
                                                o?.optString("promocodeRemarks").toString()
                                            )
                                        }
                                    }
                                    if (o?.getInt("promotionPopupStatus") == 1 && Check_popup_visible == "") {

                                        if (reedeemreward_dialog == null || !reedeemreward_dialog!!.isShowing) {
                                            GetPromotionData()
                                        }
                                    }
                                    if (o?.getInt("promotionPopupStatusV1") == 1 /*&& Check_popup_visible == ""*/) {
                                        GetExclusiveDealData()
                                    }

                                    if (o?.getInt("giftPopupStatus") == 1) {
                                        GetGiftCardRequest()
                                    }
                                    /*if (o?.getInt("ourProfileStatus") == 1) {
                                        binding?.tvBnousVoucher!!.text =
                                            o?.getString("ourProfileText")
                                        ourProfileAgentId =
                                            o?.getString("ourProfileAgentId")!!.toString()
                                    } else {
                                        binding?.tvBnousVoucher!!.text = "My Local"
                                    }*/

//                                    ErrorMessage.E("promotionPopupStatusV2   ${o?.getInt("promotionPopupStatusV2")}")

//                                    if ( o?.getInt("promotionPopupStatusV2")!=null && o?.getInt("promotionPopupStatusV2") == 1  && loadingbarforfetchingdata==null &&
//                                        !loadingbarforfetchingdata!!.isShowing) {
//                                        o?.getString("message")?.let { GetPromoCodePopUp(it,
//                                            o?.getInt("externalURLStatus")!!,
//                                            o?.getString("externalURL")!!,
//                                            o?.getString("buttonText")!!) }
//                                    }


                                    try {
                                        imgLoader!!.DisplayImage(
                                            o?.optString("userPhoto"),
                                            binding?.iv
                                        )
                                        imgLoader!!.DisplayImage(
                                            o?.optString("userPhoto"),
                                            MainActivity.ivImage
                                        )
                                    } catch (e: Exception) {
                                    }

                                    ddKitchenWebURL = o?.optString("ddKitchenWebURL").toString()
                                    ddGroceryWebURL = o?.optString("ddGroceryWebURL").toString()
                                    binding!!.availableFreeDealCountTv.text =
                                        o?.optString("availableFreeDealCount").toString()
                                    binding!!.productTargetVisitPointTv.text =
                                        o?.optString("productVisitCount").toString()
                                    binding!!.productTargetVisitCountTv.text =
                                        "/" + o?.optString("productTargetVisitCount").toString()
                                    Glide.with(requireContext())
                                        .load(o?.optString("availableFreeDealIcon"))
                                        .placeholder(R.drawable.sponplaceholder)
                                        .error(R.drawable.sponplaceholder)
                                        .into(binding!!.availableFreeDealIconImg)
                                    ErrorMessage.E("ddKitchenWebURL   ${o?.optString("ddKitchenWebURL")}")

                                    try {
                                        MainActivity.tvName!!.text = o?.optString("userName")
                                        MainActivity.tvEmail!!.text = o?.optString("userEmail")
                                        MainActivity.tvNo!!.text =
                                            "+" + o?.optString("userCountryCode") + o?.optString("userMobile")
                                        binding?.shimmerViewContainer?.stopShimmerAnimation()
                                    } catch (e: Exception) {
                                    }
                                    try {
                                        val arrDD = `object`?.optJSONArray("DDcardList")
                                        if (arrDD != null && arrDD?.length()!! > 0) {
                                            for (l in 0..0) {
                                                SharedPreferenceVariable.savePreferences(
                                                    activity,
                                                    KeyConstant.KEY_CODE,
                                                    arrDD?.optJSONObject(0)
                                                        ?.optString("userDDCardNo")
                                                )
                                                MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                            }
                                        } else {
                                            SharedPreferenceVariable.deletePreferenceData(
                                                activity,
                                                KeyConstant.KEY_CODE
                                            )
                                            MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                        }
                                    } catch (e: Exception) {
                                    }
                                    var anim: Animation
                                    if (o?.optString("voucherAvailable")
                                            .equals("1", ignoreCase = true)
                                    ) {
                                        binding?.llAvailable?.visibility = View.VISIBLE
                                        anim = AlphaAnimation(0.0f, 1.0f)
                                        anim.setDuration(400) //You can manage the blinking time with this parameter
                                        anim.setStartOffset(20)
                                        anim.setRepeatMode(Animation.REVERSE)
                                        anim.setRepeatCount(Animation.INFINITE)
                                        binding?.llAvailable?.startAnimation(anim)
                                    } else {
                                        binding?.llAvailable?.clearAnimation()
                                        binding?.llAvailable?.visibility = View.GONE
                                    }
                                    binding?.voucherPointsTv?.visibility = View.GONE
                                    binding?.voucherPointsTv?.text =
                                        o?.optString("storeVouchersValue")
                                    /*  binding?.tvVoucher?.text =
                                          "My Rewards (" + o?.optString("storeVouchers") + ")"*/
                                    binding?.tvUserName?.text = o?.optString("userName")
                                    if (o?.optString("notificationCount")
                                            .equals("0", ignoreCase = true)
                                    ) {
                                        //MainActivity.llNoti.setVisibility(View.GONE);
                                    } else {
                                        /*MainActivity.llNoti.setVisibility(View.VISIBLE);
                                                            MainActivity.tvNotis.setText(o.optString("notificationCount"));*/
                                        notificationCount = o?.optString("notificationCount")
                                    }
                                    SharedPreferenceVariable.savePreferences(
                                        activity,
                                        KeyConstant.KEY_USER_UUID,
                                        o?.optString("userQRuuid")
                                    )
                                    SharedPreferenceVariable.savePreferences(
                                        activity,
                                        KeyConstant.Shar_Name,
                                        o?.optString("userName")
                                    )
                                    if (TextUtils.isEmpty(o?.optString("storeAvailablePoints")) || o?.optString(
                                            "storeAvailablePoints"
                                        ).equals("0", ignoreCase = true)
                                    ) {
                                    } else {
                                        binding?.tvStorePoints?.visibility = View.GONE
                                        binding?.tvStorePoints?.text =
                                            o?.optString("storeAvailablePoints")
                                    }
                                    /* val arrbannerList = `object`?.optJSONArray("bannerList")
                                     mList.clear()
                                     for (j in 0 until arrbannerList?.length()!!) {
                                         val objBanner = arrbannerList.optJSONObject(j)
                                         val adsImage = objBanner.optString("adsImage")
                                         val hb = HomeBannerLIst(adsImage)
                                         mList.add(hb)
                                     }
                                     val viewPagerAdapter =
                                         HomeBanner_Adapter(requireActivity(), mList)
                                     binding?.viewPager?.adapter = viewPagerAdapter*/


//                                    GetPromoCodePopUp("hii",
//                                        1,
//                                        "dsfddffggf",
//                                        "fdfdfd")


                                    try {
                                        if (o?.getInt("promotionPopupStatusV2") != null && o?.getInt(
                                                "promotionPopupStatusV2"
                                            ) == 1
                                        ) {
                                            o?.getString("message")?.let {
                                                GetPromoCodePopUp(
                                                    it,
                                                    o?.getInt("externalURLStatus")!!,
                                                    o?.getString("externalURL")!!,
                                                    o?.getString("buttonText")!!
                                                )
                                            }
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            val latestDealList = `object`?.optJSONArray("latestDealsList")
                            latestDeal.clear()
                            if (latestDealList != null) {
                                for (j in 0 until latestDealList.length()) {
                                    val objBanner = latestDealList.optJSONObject(j)
                                    if (objBanner != null) {
                                        val exampleItem: LatestDeals =
                                            Gson().fromJson(
                                                objBanner.toString(),
                                                LatestDeals::class.java
                                            )
                                        latestDeal.add(exampleItem)
                                    }
                                }
                            }
                            if (latestDeal.size > 0) {
                                showDealAdapter(latestDeal);
                            }

                        } else {
                            if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                    resp.optString(KeyConstant.KEY_STATUS),
                                    ignoreCase = true
                                )
                            ) {
                                dialogManager!!.stopProcessDialog()
                                //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dialogManager!!.stopProcessDialog()
                        //  Log.e("Exception", ">>$e")
                        //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                    }
                } else {
                }
            }
        } catch (e: Exception) {
        }

        ErrorMessage.E("fddfdrfe   ${SavedData.getLatitude()}   ${SavedData.getLongitude()}")
        /*        binding!!.viewPager.setPageTransformer(false) { page, position ->
                    val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
                    page.scaleY = scale
                    page.scaleX = scale
                    page.alpha = 0.5f + (1 - kotlin.math.abs(position)) * 0.5f
              ign  }*/
        /*  binding!!.viewPager.clipToPadding = false
          binding!!.viewPager.clipChildren = false
          binding!!.viewPager.offscreenPageLimit = 3
          binding!!.viewPager.setPadding(20, 0, 60, 0) // adjust this to your need
          binding!!.viewPager.setPageMargin(20) // gap between cards*/
        binding!!.rewardClubLayout.setOnClickListener(View.OnClickListener {
            activity?.let {
                ErrorMessage.I(it, RewardClubActivity::class.java, null)
            }
        })
        return view
    }


    private val onNotice1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            try {
                myProfile
            } catch (e: Exception) {
            }
        }
    }

    private fun showDealAdapter(exampleList: List<LatestDeals>) {
        ErrorMessage.E("DealDebug exampleList size: ${exampleList.size}") // Only proceed if not empty
        if (exampleList.isEmpty()) return

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding!!.dealListRcv.layoutManager = layoutManager

        val adapter = HomePageDealAdapter(activity, exampleList)
        binding!!.dealListRcv.itemAnimator = DefaultItemAnimator()
        binding!!.dealListRcv.setHasFixedSize(true)
        binding!!.dealListRcv.visibility = View.VISIBLE
        binding!!.dealListRcv.setItemViewCacheSize(exampleList.size)
        binding!!.dealListRcv.adapter = adapter
    }


    private val onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            //  Log.e("onNotice", "Running BroadcastReceiver")
            try {
                //  Log.e("All data", "broadcast" + intent.getStringExtra("AllData"))
                val resp = JSONObject(intent.getStringExtra("AllData"))
                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200, ignoreCase = true)) {
                    `object` = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                    val arr = `object`?.optJSONArray("userDetails")
                    if (arr != null && arr?.length()!! > 0) {
                        for (i in 0 until arr.length()) {
                            o = arr.optJSONObject(i)
                            try {
                                SharedPreferenceVariable.savePreferences(
                                    activity,
                                    KeyConstant.KEY_USER_ID,
                                    o?.optString("userId")
                                )
                                SharedPreferenceVariable.savePreferences(
                                    activity,
                                    KeyConstant.KEY_GOOGLE_API_KEY,
                                    o?.optString("googleAPIKey")
                                )
                                referBusinessWebURL = o?.optString("referBusinessWebURL").toString()

                                cakeWebURL =
                                    o?.optString("cakeWebURL").toString()
                                koodaiWebURL =
                                    o?.optString("koodaiWebURL").toString()
                                OrderOnlineWebURL =
                                    o?.optString("OrderOnlineWebURL").toString()
                                dineInWebURL =
                                    o?.optString("dineInWebURL").toString()
                                binding?.ticketPointsValueTv?.text =
                                    o?.optString("userAvailablePoints")

                                binding?.ticketVoucherValueTv?.text =
                                    o?.optString("storeVouchersValue")
                            } catch (e: Exception) {
                            }
                            Log.e("Promo", o?.optString("promocodeStatus").toString())
                            if (!o?.optString("promocodeStatus").equals("0", ignoreCase = true)) {
                                if (SharedPreferenceVariable.loadSavedPreferences(
                                        activity,
                                        KeyConstant.KEY_OPEN_PROMOCODE
                                    )?.isEmpty()!!
                                ) {
                                    showPromoCode(
                                        o?.optString("promocodeStatus").toString(),
                                        o?.optString("promocodeRemarks").toString()
                                    )
                                }
                            }
                            if (o?.getInt("promotionPopupStatus") == 1 && Check_popup_visible == "") {
                                if (reedeemreward_dialog == null || !reedeemreward_dialog!!.isShowing) {
                                    GetPromotionData()
                                }
                            }
                            if (o?.getInt("promotionPopupStatusV1") == 1 /*&& Check_popup_visible == ""*/) {
                                GetExclusiveDealData()
                            }

//                            ErrorMessage.E("promotionPopupStatusV2   ${o?.getInt("promotionPopupStatusV2")}")

//                            if ( o?.getInt("promotionPopupStatusV2")!=null && o?.getInt("promotionPopupStatusV2") == 1 ) {
//                            if ( o?.getInt("promotionPopupStatusV2")!=null && o?.getInt("promotionPopupStatusV2") == 1  && loadingbarforfetchingdata==null &&
//                                !loadingbarforfetchingdata!!.isShowing) {
//                                o?.getString("message")?.let { GetPromoCodePopUp(it,
//                                    o?.getInt("externalURLStatus")!!,
//                                    o?.getString("externalURL")!!,
//                                    o?.getString("buttonText")!!) }
//                            }


                            if (o?.getInt("giftPopupStatus") == 1) {
                                GetGiftCardRequest()
                            }
                            /* if (o?.getInt("ourProfileStatus") == 1) {
                                 binding?.tvBnousVoucher!!.text =
                                     o?.getString("ourProfileText")
                                 ourProfileAgentId = o?.getString("ourProfileAgentId")!!.toString()
                             } else {
                                 binding?.tvBnousVoucher!!.text = "My Local"
                             }*/

                            /*   if (true) {
                                   GetGiftCardRequest()
                               }*/

                            try {
                                imgLoader!!.DisplayImage(o?.optString("userPhoto"), binding!!.iv)
                                imgLoader!!.DisplayImage(
                                    o?.optString("userPhoto"),
                                    MainActivity.ivImage
                                )
                            } catch (e: Exception) {
                            }

                            Log.e("ddKitchenWebURL", o?.optString("ddKitchenWebURL").toString())

                            ErrorMessage.E("ddKitchenWebURL2   ${o?.optString("ddKitchenWebURL")}")
                            ddKitchenWebURL = o?.optString("ddKitchenWebURL").toString()
                            ddGroceryWebURL = o?.optString("ddGroceryWebURL").toString()
                            binding!!.availableFreeDealCountTv.text =
                                o?.optString("availableFreeDealCount").toString()
                            binding!!.productTargetVisitPointTv.text =
                                o?.optString("productVisitCount").toString()
                            binding!!.productTargetVisitCountTv.text =
                                "/" + o?.optString("productTargetVisitCount").toString()
                            Glide.with(requireContext())
                                .load(o?.optString("availableFreeDealIcon"))
                                .placeholder(R.drawable.sponplaceholder)
                                .error(R.drawable.sponplaceholder)
                                .into(binding!!.availableFreeDealIconImg)
                            try {
                                MainActivity.tvName!!.text = o?.optString("userName")
                                MainActivity.tvEmail!!.text = o?.optString("userEmail")
                                MainActivity.tvNo!!.text =
                                    "+" + o?.optString("userCountryCode") + o?.optString("userMobile")
                                binding!!.shimmerViewContainer.stopShimmerAnimation()
                            } catch (e: Exception) {
                            }
                            try {
                                val arrDD = `object`?.optJSONArray("DDcardList")
                                if (arrDD != null && arrDD?.length()!! > 0) {
                                    for (l in 0..0) {
                                        SharedPreferenceVariable.savePreferences(
                                            activity,
                                            KeyConstant.KEY_CODE,
                                            arrDD.optJSONObject(0).optString("userDDCardNo")
                                        )
                                        MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                    }
                                } else {
                                    SharedPreferenceVariable.deletePreferenceData(
                                        activity,
                                        KeyConstant.KEY_CODE
                                    )
                                    MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                }
                            } catch (e: Exception) {
                            }
                            var anim: Animation
                            if (o?.optString("voucherAvailable").equals("1", ignoreCase = true)) {
                                binding!!.llAvailable.visibility = View.VISIBLE
                                anim = AlphaAnimation(0.0f, 1.0f)
                                anim.setDuration(400) //You can manage the blinking time with this parameter
                                anim.setStartOffset(20)
                                anim.setRepeatMode(Animation.REVERSE)
                                anim.setRepeatCount(Animation.INFINITE)
                                binding!!.llAvailable.startAnimation(anim)
                            } else {
                                binding!!.llAvailable.clearAnimation()
                                binding!!.llAvailable.visibility = View.GONE
                            }
                            binding!!.voucherPointsTv.visibility = View.GONE
                            binding!!.voucherPointsTv.text = o?.optString("storeVouchersValue")
                            /* binding!!.tvVoucher.text =
                                 "My Rewards (" + o?.optString("storeVouchers") + ")"*/
                            binding!!.tvUserName.text = o?.optString("userName")
                            if (o?.optString("notificationCount").equals("0", ignoreCase = true)) {
                                //MainActivity.llNoti.setVisibility(View.GONE);
                            } else {
                                /*MainActivity.llNoti.setVisibility(View.VISIBLE);
                                MainActivity.tvNotis.setText(o.optString("notificationCount"));*/
                                notificationCount = o?.optString("notificationCount")
                            }
                            SharedPreferenceVariable.savePreferences(
                                activity,
                                KeyConstant.KEY_USER_UUID,
                                o?.optString("userQRuuid")
                            )
                            SharedPreferenceVariable.savePreferences(
                                activity,
                                KeyConstant.Shar_Name,
                                o?.optString("userName")
                            )
                            if (TextUtils.isEmpty(o?.optString("storeAvailablePoints")) || o?.optString(
                                    "storeAvailablePoints"
                                ).equals("0", ignoreCase = true)
                            ) {
                            } else {
                                binding!!.tvStorePoints.visibility = View.GONE
                                binding!!.tvStorePoints.text = o?.optString("storeAvailablePoints")
                            }
                            val arrbannerList = `object`?.optJSONArray("bannerList")
                            mList.clear()
                            for (j in 0 until arrbannerList?.length()!!) {
                                val objBanner = arrbannerList?.optJSONObject(j)
                                val adsImage = objBanner?.optString("adsImage")
                                val hb = adsImage?.let { HomeBannerLIst(it) }
                                hb?.let { mList.add(it) }
                            }

                            /*if (activity != null) {
                                val viewPagerAdapter = HomeBanner_Adapter(activity!!, mList)
                                binding!!.viewPager.adapter = viewPagerAdapter
                                if (mList.size == 0) {
                                    binding!!.llBanner.visibility = View.GONE
                                }
                            }*/





                            try {
                                if (o?.getInt("promotionPopupStatusV2") != null && o?.getInt("promotionPopupStatusV2") == 1
                                ) {
                                    o?.getString("message")?.let {
                                        GetPromoCodePopUp(
                                            it,
                                            o?.getInt("externalURLStatus")!!,
                                            o?.getString("externalURL")!!,
                                            o?.getString("buttonText")!!
                                        )
                                    }
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }

                    val latestDealList = `object`?.optJSONArray("latestDealsList")
                    latestDeal.clear()
                    if (latestDealList != null) {
                        for (j in 0 until latestDealList.length()) {
                            val objBanner = latestDealList.optJSONObject(j)
                            if (objBanner != null) {
                                val exampleItem: LatestDeals =
                                    Gson().fromJson(objBanner.toString(), LatestDeals::class.java)
                                latestDeal.add(exampleItem)
                            }
                        }
                    }
                    if (latestDeal.size > 0) {
                        showDealAdapter(latestDeal);
                    }

                } else {
                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                            resp.optString(KeyConstant.KEY_STATUS),
                            ignoreCase = true
                        )
                    ) {
                        dialogManager!!.stopProcessDialog()
                        //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialogManager!!.stopProcessDialog()
                Log.e("Exception", "603>>$e")
                //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
            }
        }
    }

    private fun initView(view: View) {
        iv = view.findViewById(R.id.iv)
        binding!!.llStorePoints.setOnClickListener(this)
        binding!!.ourStoreMainLayout.setOnClickListener(this)
        binding!!.userAvailablePointsLayout.setOnClickListener(this)
        binding!!.myWalletLayout.setOnClickListener(this)
        binding!!.availablePointsLayout.setOnClickListener(this)
        // binding!!.llStoreVoucher.setOnClickListener(this)
        binding!!.orderOnlineLayout.setOnClickListener(this)
        binding!!.llDeals.setOnClickListener(this)
        binding!!.btnMoreDeals.setOnClickListener(this)
        binding!!.myDealLayout.setOnClickListener(this)
        binding!!.llNearMe.setOnClickListener(this)
        binding!!.ourStoreLayout.setOnClickListener(this)
        binding!!.ivNoti.setOnClickListener(this)
        binding!!.llRefer.setOnClickListener(this)
        binding!!.llLink.setOnClickListener(this)
        // binding!!.llDDGrocer.setOnClickListener(this)
        binding!!.ivMenu.setOnClickListener(this)
        binding!!.llDDKitchen.setOnClickListener(this)
        binding!!.preOrderLayout.setOnClickListener(this)
        binding!!.llKoodai.setOnClickListener(this)
        binding!!.llCake.setOnClickListener(this)
        binding!!.cakeLayout.setOnClickListener(this)
        binding!!.koodaiLayout.setOnClickListener(this)
        Log.e(
            "key>>",
            "" + SharedPreferenceVariable.loadSavedPreferences(
                activity,
                KeyConstant.KEY_GOOGLE_API_KEY
            )
        )
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.llDDKitchen ->


                if (ddKitchenWebURL != "") {
                    ErrorMessage.E("ddKitchenWebURL11   ${ddKitchenWebURL}   ddGroceryWebURL  ${ddGroceryWebURL}")
                    try {
                        if (AppUtil.isNetworkAvailable(context1)) {

                            if (exclusive_dialog != null) {
                                exclusive_dialog!!.dismiss()
                            }

                            context1!!.startActivity(
                                Intent(activity, Webview::class.java)
                                    .putExtra("url", ddKitchenWebURL)
                                    .putExtra("title", "DD Kitchen")
                                    .putExtra("type", "non_direct")
                            )
                        } else {
                            AppUtil.showMsgAlert(
                                binding!!.tvNotiCount,
                                MessageConstant.MESSAGE_INTERNET_CONNECTION
                            )
                        }
                    } catch (e: Exception) {
                    }
                } else {
                    ErrorMessage.E("ddKitchenWebURL   ${ddKitchenWebURL}   ddGroceryWebURL  ${ddGroceryWebURL}")
                }


//            R.id.llDDGrocer -> if (ddGroceryWebURL == "") {
//
//                if (exclusive_dialog != null) {
//                    exclusive_dialog!!.dismiss()
//                }
////                exclusive_dialog!!.dismiss()
//
//                startActivity(
//                    Intent(activity, ActivityGroceryList::class.java)
//                        .putExtra("type", "non_direct")
//                )
//            }
//            else {
//                try {
//                    if (ddGroceryWebURL != "-1") {
//                        if (AppUtil.isNetworkAvailable(context1)) {
//
//                            if (exclusive_dialog != null) {
//                                exclusive_dialog!!.dismiss()
//                            }
//
//                            context1!!.startActivity(
//                                Intent(activity, Webview::class.java)
//                                    .putExtra("url", ddGroceryWebURL)
//                                    .putExtra("title", "DD Grocer")
//                                    .putExtra("type", "non_direct")
//                            )
//                        } else {
//                            AppUtil.showMsgAlert(
//                                binding!!.tvNotiCount,
//                                MessageConstant.MESSAGE_INTERNET_CONNECTION
//                            )
//                        }
//                    }
//                } catch (e: Exception) {
//                }
//            }

            R.id.llRefer ->

                if (AppUtil.isNetworkAvailable(context1)) {
                    startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", referBusinessWebURL)
                            .putExtra("type", "non_direct")
                            .putExtra("title", "Refer your Local Business")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }

            R.id.ll_koodai ->

                if (AppUtil.isNetworkAvailable(context1)) {
                    startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", koodaiWebURL)
                            .putExtra("type", "non_direct")
                            .putExtra("title", "Koodai")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }

            R.id.ll_cake ->

                if (AppUtil.isNetworkAvailable(context1)) {
                    startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", cakeWebURL)
                            .putExtra("type", "non_direct")
                            .putExtra("title", resources.getString(R.string.cakes))
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }

            R.id.pre_order_layout ->

                if (AppUtil.isNetworkAvailable(context1)) {
                    startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", dineInWebURL)
                            .putExtra("type", "non_direct")
                            .putExtra("title", resources.getString(R.string.sambal_kitchen))
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }

            R.id.llLink -> showLinkDialog()
            R.id.ivMenu -> if (!MainActivity.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                MainActivity.drawerLayout!!.openDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            } else if (MainActivity.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                MainActivity.drawerLayout!!.closeDrawer(Gravity.LEFT) //Edit Gravity.START need API 14
            }

            R.id.ivQr -> DialogQr.showDialog(activity, bitmap, `object`!!.optString("userBarCode"))
            R.id.llStorePoints ->
                try {

                    /* if (AppUtil.isNetworkAvailable(context1)) {*/
                    binding!!.ivMenu.visibility = View.GONE

//                    exclusive_dialog!!.dismiss()

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }
                    startActivity(
                        Intent(activity, ActivityStorePoints::class.java)
                            .putExtra("type", "1")
                    )
                    /*} else {
                        AppUtil.showMsgAlert(
                            binding!!.tvNotiCount,
                            MessageConstant.MESSAGE_INTERNET_CONNECTION
                        )

                    }*/
                } catch (e: Exception) {
                }

            R.id.user_available_points_layout ->
                try {

                    /* if (AppUtil.isNetworkAvailable(context1)) {*/
                    binding!!.ivMenu.visibility = View.GONE

//                    exclusive_dialog!!.dismiss()

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }
                    startActivity(
                        Intent(activity, ActivityStorePoints::class.java)
                            .putExtra("type", "1")
                    )
                    /*} else {
                        AppUtil.showMsgAlert(
                            binding!!.tvNotiCount,
                            MessageConstant.MESSAGE_INTERNET_CONNECTION
                        )

                    }*/
                } catch (e: Exception) {
                }

            R.id.my_wallet_layout ->
                try {

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }
                    startActivity(
                        Intent(activity, ActivityMyRewards::class.java)
                            .putExtra("name", o!!.optString("storeVouchers"))
                    )
                } catch (e: Exception) {
                }

            R.id.available_points_layout ->
                try {

                    /* if (AppUtil.isNetworkAvailable(context1)) {*/
                    binding!!.ivMenu.visibility = View.GONE

//                    exclusive_dialog!!.dismiss()

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }
                    startActivity(
                        Intent(activity, ActivityStorePoints::class.java)
                            .putExtra("type", "1")
                    )
                    /*} else {
                        AppUtil.showMsgAlert(
                            binding!!.tvNotiCount,
                            MessageConstant.MESSAGE_INTERNET_CONNECTION
                        )

                    }*/
                } catch (e: Exception) {
                }

            R.id.llDeals -> try {
                /* if (AppUtil.isNetworkAvailable(context1)) {*/

                try {
                    //                        exclusive_dialog!!.dismiss()

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }
                    startActivity(
                        Intent(activity, ActivityMyRewards::class.java)
                            .putExtra("name", o!!.optString("storeVouchers"))
                    )
                } catch (e: Exception) {
                }
                /* } else {
                     AppUtil.showMsgAlert(
                         binding!!.tvNotiCount,
                         MessageConstant.MESSAGE_INTERNET_CONNECTION
                     )
                 }*/
            } catch (e: Exception) {
            }

            R.id.btnMoreDeals -> try {
                /* if (AppUtil.isNetworkAvailable(context1)) {*/

                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
//                    exclusive_dialog!!.dismiss()

                startActivity(
                    Intent(activity, NewMyDealsActivity::class.java)
                        .putExtra("notiCount", notificationCount)
                )
                /* } else {
                     AppUtil.showMsgAlert(
                         binding!!.tvNotiCount,
                         MessageConstant.MESSAGE_INTERNET_CONNECTION
                     )
                 }*/
            } catch (e: Exception) {
            }

            R.id.my_deal_layout -> try {
                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
                if (ourProfileAgentId != null && ourProfileAgentId != "") {
                    val intent =
                        Intent(activity, New_AgentDetails::class.java)
                    intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, ourProfileAgentId)
                    startActivity(intent)
                } else {
                    startActivity(Intent(activity, NearMeHomeFragment::class.java))
                }

            } catch (e: Exception) {
            }

            R.id.llNearMe -> try {
                ErrorMessage.E("llNearMe>>>>" + OrderOnlineWebURL.toString())
                context1!!.startActivity(
                    Intent(activity, Webview::class.java)
                        .putExtra("url", OrderOnlineWebURL)
                        .putExtra(
                            "title",
                            resources.getString(R.string.sambal_express_online_order)
                        )
                        .putExtra("type", "non_direct")
                )
            } catch (e: Exception) {
                ErrorMessage.E("Exception>>>>" + e.toString())
            }

            R.id.our_store_layout -> try {
                ErrorMessage.E("llNearMe>>>>" + OrderOnlineWebURL.toString())
                context1!!.startActivity(
                    Intent(activity, Webview::class.java)
                        .putExtra("url", OrderOnlineWebURL)
                        .putExtra(
                            "title",
                            resources.getString(R.string.sambal_express_online_order)
                        )
                        .putExtra("type", "non_direct")
                )
            } catch (e: Exception) {
                ErrorMessage.E("Exception>>>>" + e.toString())
            }

            /*R.id.llStoreVoucher -> try {
                *//*  binding!!.ivMenu.visibility = View.GONE
                  try {
  //                        exclusive_dialog!!.dismiss()

                      if (exclusive_dialog != null) {
                          exclusive_dialog!!.dismiss()
                      }
                      startActivity(
                          Intent(activity, ActivityMyRewards::class.java)
                              .putExtra("name", o!!.optString("storeVouchers"))
                      )
                  } catch (e: Exception) {
                  }*//*
                *//*} else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }*//*
                if (AppUtil.isNetworkAvailable(context1)) {

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }

                    context1!!.startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", ddKitchenWebURL)
                            .putExtra("title", "DD Kitchen")
                            .putExtra("type", "non_direct")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
            }*/

            R.id.order_online_layout -> try {
                if (AppUtil.isNetworkAvailable(context1)) {

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }

                    context1!!.startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", dineInWebURL)
                            .putExtra("title", resources.getString(R.string.sambal_kitchen))
                            .putExtra("type", "non_direct")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
            }

            R.id.cake_layout -> try {
                if (AppUtil.isNetworkAvailable(context1)) {

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }

                    context1!!.startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", cakeWebURL)
                            .putExtra("title", resources.getString(R.string.cakes))
                            .putExtra("type", "non_direct")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
            }

            R.id.koodai_layout -> try {
                if (AppUtil.isNetworkAvailable(context1)) {

                    if (exclusive_dialog != null) {
                        exclusive_dialog!!.dismiss()
                    }

                    context1!!.startActivity(
                        Intent(activity, Webview::class.java)
                            .putExtra("url", koodaiWebURL)
                            .putExtra("title", resources.getString(R.string.online_groceries))
                            .putExtra("type", "non_direct")
                    )
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
            }

            R.id.our_store_main_layout -> try {
                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
                if (ourProfileAgentId != null && ourProfileAgentId != "") {
                    val intent =
                        Intent(activity, New_AgentDetails::class.java)
                    intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, ourProfileAgentId)
                    startActivity(intent)
                } else {
                    startActivity(Intent(activity, NearMeHomeFragment::class.java))
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun showLinkDialog() {
        val dialog1 = Dialog(requireActivity(), R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.dialoglink)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val btnSkip = dialog1.findViewById<Button>(R.id.btnSkip)
        val llLink = dialog1.findViewById<LinearLayout>(R.id.llLink)
        btnSkip.setOnClickListener {
            SharedPreferenceVariable.savePreferences(activity, KeyConstant.KEY_SKIP, "1")
            dialog1.dismiss()
        }
        llLink.setOnClickListener {
            dialog1.dismiss()
            startActivity(Intent(activity, ScanQr::class.java))
        }
        dialog1.show()
    }

    override fun onPause() {
        if (binding!!.shimmerViewContainer != null) {
            binding!!.shimmerViewContainer.stopShimmerAnimation()
        }
        try {
            val powerManager =
                requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn: Boolean
            isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                powerManager.isInteractive
            } else {
                powerManager.isScreenOn
            }
            if (!isScreenOn) {
                isDeviceLocked = true
                // The screen has been locked
                // do stuff...
            }
        } catch (e: Exception) {
            ErrorMessage.E("EXCEPTION > >$e")
        }
        super.onPause()
    }

    override fun onResume() {

        //getMyProfile();
        if (isDeviceLocked) {
            isDeviceLocked = false
            myProfile
        }

        if (!MainActivity.visibleFragment) {
            MainActivity.toolbar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            MainActivity.llLocationLayout!!.visibility = View.GONE
            MainActivity.llSearch!!.visibility = View.GONE
        }
        binding!!.ll.visibility = View.GONE
        binding!!.ivMenu.visibility = View.VISIBLE
        /*try {
            StatusBarcolor.setStatusbarColor(requireActivity(), "white")
        } catch (e: Exception) {
        }*/
        super.onResume()
    }


    private val myProfile: Unit
        private get() {


            try {
                if (AppUtil.isNetworkAvailable(context1)) {
                    val call = AppConfig.api_Interface().getMyProfileV1(
                        MainActivity.userLat.toString(),
                        MainActivity.userLang.toString()
                    )
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                try {
                                    val resp = JSONObject(response.body()!!.string())
                                    Log.e("home fragment res>>", resp.toString())
                                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                        `object` = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                        val arr = `object`?.optJSONArray("userDetails")
                                        if (arr != null) {
                                            for (i in 0 until arr?.length()!!) {
                                                o = arr.optJSONObject(i)
                                                activity!!.runOnUiThread {
                                                    try {
                                                        SharedPreferenceVariable.savePreferences(
                                                            activity,
                                                            KeyConstant.KEY_USER_ID,
                                                            o?.optString("userId")
                                                        )
                                                        SharedPreferenceVariable.savePreferences(
                                                            activity,
                                                            KeyConstant.KEY_GOOGLE_API_KEY,
                                                            o?.optString("googleAPIKey")
                                                        )
                                                        referBusinessWebURL =
                                                            o?.optString("referBusinessWebURL")
                                                                .toString()
                                                    } catch (e: Exception) {
                                                    }
                                                    Log.e(
                                                        "Promo",
                                                        o?.optString("promocodeStatus").toString()
                                                    )
                                                    if (!o?.optString("promocodeStatus")
                                                            .equals("0", ignoreCase = true)
                                                    ) {
                                                        if (SharedPreferenceVariable.loadSavedPreferences(
                                                                activity,
                                                                KeyConstant.KEY_OPEN_PROMOCODE
                                                            )?.isEmpty()!!
                                                        ) {
                                                            showPromoCode(
                                                                o?.optString("promocodeStatus")
                                                                    .toString(),
                                                                o?.optString("promocodeRemarks")
                                                                    .toString()
                                                            )
                                                        }
                                                    }

                                                    try {
                                                        imgLoader!!.DisplayImage(
                                                            o?.optString("userPhoto"),
                                                            binding!!.iv
                                                        )
                                                        imgLoader!!.DisplayImage(
                                                            o?.optString("userPhoto"),
                                                            MainActivity.ivImage
                                                        )
                                                    } catch (e: Exception) {
                                                    }
                                                    ddKitchenWebURL =
                                                        o?.optString("ddKitchenWebURL").toString()
                                                    ddGroceryWebURL =
                                                        o?.optString("ddGroceryWebURL").toString()
                                                    binding!!.availableFreeDealCountTv.text =
                                                        o?.optString("availableFreeDealCount")
                                                            .toString()
                                                    binding!!.productTargetVisitPointTv.text =
                                                        o?.optString("productVisitCount").toString()
                                                    binding!!.productTargetVisitCountTv.text =
                                                        "/" + o?.optString("productTargetVisitCount")
                                                            .toString()
                                                    Glide.with(requireContext())
                                                        .load(o?.optString("availableFreeDealIcon"))
                                                        .placeholder(R.drawable.sponplaceholder)
                                                        .error(R.drawable.sponplaceholder)
                                                        .into(binding!!.availableFreeDealIconImg)
                                                    try {
                                                        MainActivity.tvName!!.text =
                                                            o?.optString("userName")
                                                        MainActivity.tvEmail!!.text =
                                                            o?.optString("userEmail")
                                                        MainActivity.tvNo!!.text =
                                                            "+" + o?.optString("userCountryCode") + o?.optString(
                                                                "userMobile"
                                                            )
                                                        binding!!.shimmerViewContainer.stopShimmerAnimation()
                                                    } catch (e: Exception) {
                                                    }
                                                    try {
                                                        val arrDD =
                                                            `object`?.optJSONArray("DDcardList")
                                                        if (arrDD != null && arrDD?.length()!! > 0) {
                                                            for (l in 0..0) {
                                                                SharedPreferenceVariable.savePreferences(
                                                                    activity,
                                                                    KeyConstant.KEY_CODE,
                                                                    arrDD.optJSONObject(0)
                                                                        .optString("userDDCardNo")
                                                                )
                                                                MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                                            }
                                                        } else {
                                                            SharedPreferenceVariable.deletePreferenceData(
                                                                activity, KeyConstant.KEY_CODE
                                                            )
                                                            MainActivity.adapLeftMenu!!.notifyDataSetChanged()
                                                        }
                                                    } catch (e: Exception) {
                                                    }
                                                    val anim: Animation
                                                    if (o?.optString("voucherAvailable")
                                                            .equals("1", ignoreCase = true)
                                                    ) {
                                                        binding!!.llAvailable.visibility =
                                                            View.VISIBLE
                                                        anim = AlphaAnimation(0.0f, 1.0f)
                                                        anim.setDuration(400) //You can manage the blinking time with this parameter
                                                        anim.setStartOffset(20)
                                                        anim.setRepeatMode(Animation.REVERSE)
                                                        anim.setRepeatCount(Animation.INFINITE)
                                                        binding!!.llAvailable.startAnimation(anim)
                                                    } else {
                                                        binding!!.llAvailable.clearAnimation()
                                                        binding!!.llAvailable.visibility = View.GONE
                                                    }
                                                    binding!!.voucherPointsTv.visibility = View.GONE
                                                    binding!!.voucherPointsTv.text =
                                                        o?.optString("storeVouchersValue")
                                                    /* binding!!.tvVoucher.text =
                                                         "My Rewards (" + o?.optString("storeVouchers") + ")"*/
                                                    binding!!.tvUserName.text =
                                                        o?.optString("userName")
                                                    if (o?.optString("notificationCount")
                                                            .equals("0", ignoreCase = true)
                                                    ) {
                                                        //MainActivity.llNoti.setVisibility(View.GONE);
                                                    } else {
                                                        /*MainActivity.llNoti.setVisibility(View.VISIBLE);
                                                                                                          MainActivity.tvNotis.setText(o.optString("notificationCount"));*/
                                                        notificationCount =
                                                            o?.optString("notificationCount")
                                                    }
                                                    SharedPreferenceVariable.savePreferences(
                                                        activity,
                                                        KeyConstant.KEY_USER_UUID,
                                                        o?.optString("userQRuuid")
                                                    )
                                                    SharedPreferenceVariable.savePreferences(
                                                        activity,
                                                        KeyConstant.Shar_Name,
                                                        o?.optString("userName")
                                                    )
                                                    if (TextUtils.isEmpty(o?.optString("storeAvailablePoints")) || o?.optString(
                                                            "storeAvailablePoints"
                                                        ).equals("0", ignoreCase = true)
                                                    ) {
                                                    } else {
                                                        binding!!.tvStorePoints.visibility =
                                                            View.GONE
                                                        binding!!.tvStorePoints.text =
                                                            o?.optString("storeAvailablePoints")
                                                    }

//
                                                    val arrbannerList =
                                                        `object`?.optJSONArray("bannerList")
                                                    mList.clear()
                                                    for (j in 0 until arrbannerList?.length()!!) {
                                                        val objBanner =
                                                            arrbannerList.optJSONObject(j)
                                                        val adsImage =
                                                            objBanner.optString("adsImage")
                                                        val hb = HomeBannerLIst(adsImage)
                                                        mList.add(hb)
                                                    }
                                                    try {
                                                        val leftmenuArray =
                                                            `object`?.optJSONArray("leftmenu")
                                                        menuList.clear()
                                                        for (j in 0 until leftmenuArray?.length()!!) {
                                                            try {
                                                                var jsonObject_inner: JSONObject? =
                                                                    null
                                                                jsonObject_inner =
                                                                    leftmenuArray.getJSONObject(j)
                                                                val menuList_model = MenuList(
                                                                    jsonObject_inner.getString("leftmenuId"),
                                                                    jsonObject_inner.getString("leftmenuName"),
                                                                    jsonObject_inner.getString("leftmenuStatusId"),
                                                                    jsonObject_inner.getString("leftmenuStatus"),
                                                                    jsonObject_inner.getString("leftmenuIcon"),
                                                                    jsonObject_inner.getString("webURLStatus"),
                                                                    jsonObject_inner.getString("webURL")
                                                                )
                                                                menuList.add(menuList_model)
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                        if (menuList.size > 0) {
                                                            Log.e("mList", "" + menuList.size)
                                                            val adapLeftMenu = AdapterLeftMenu(
                                                                (activity as MainActivity?)!!,
                                                                menuList
                                                            )
                                                            MainActivity.rvMain!!.layoutManager =
                                                                LinearLayoutManager(
                                                                    activity,
                                                                    LinearLayoutManager.VERTICAL,
                                                                    false
                                                                )
                                                            MainActivity.rvMain!!.adapter =
                                                                adapLeftMenu
                                                            MainActivity.rvMain!!.setHasFixedSize(
                                                                true
                                                            )
                                                            MainActivity.rvMain!!.setItemViewCacheSize(
                                                                menuList.size
                                                            )
                                                            adapLeftMenu.notifyDataSetChanged()
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e("inner", ">>$e")
                                                    }
                                                    /*  val viewPagerAdapter = HomeBanner_Adapter(
                                                          activity!!, mList
                                                      )
                                                      binding!!.viewPager.adapter = viewPagerAdapter
                                                      if (mList.size == 0) {
                                                          binding!!.llBanner.visibility = View.GONE
                                                      }*/
                                                }
                                            }
                                        }
                                        val latestDealList =
                                            `object`?.optJSONArray("latestDealsList")
                                        latestDeal.clear()
                                        if (latestDealList != null) {
                                            for (j in 0 until latestDealList.length()) {
                                                val objBanner = latestDealList.optJSONObject(j)
                                                if (objBanner != null) {
                                                    val exampleItem: LatestDeals =
                                                        Gson().fromJson(
                                                            objBanner.toString(),
                                                            LatestDeals::class.java
                                                        )
                                                    latestDeal.add(exampleItem)
                                                }
                                            }
                                        }
                                        if (latestDeal.size > 0) {
                                            showDealAdapter(latestDeal);
                                        }
                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                                resp.optString(
                                                    KeyConstant.KEY_STATUS
                                                ), ignoreCase = true
                                            )
                                        ) {
                                            //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dialogManager!!.stopProcessDialog()
                                    //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                }
                            } else {
                                dialogManager!!.stopProcessDialog()
                                Log.e("sendToken", "else is working" + response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            dialogManager!!.stopProcessDialog()
                            //AppUtil.showMsgAlert(binding.tvNotiCount, t.getMessage());
                        }
                    })
                } else {
                    AppUtil.showMsgAlert(
                        binding!!.tvNotiCount,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION
                    )
                }
            } catch (e: Exception) {
                ErrorMessage.E("Exception>>123>$e")
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            dialog1?.dismiss()
            if (dialogManager != null) {
                dialogManager!!.stopProcessDialog()
            }
        } catch (e: Exception) {
        }
    }

    override fun onDetach() {
        super.onDetach()
        context1 = null
    }

    private fun showPromoCode(code: String, remark: String) {
        try {
            val dialog1 = Dialog(context1!!, R.style.NewDialog)
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1.setContentView(R.layout.dialopromocode)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog1.window!!.attributes)
            lp.width = WindowManager.LayoutParams.FILL_PARENT
            lp.height = WindowManager.LayoutParams.FILL_PARENT
            dialog1.window!!.attributes = lp
            val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
            val tvRemark = dialog1.findViewById<TextView>(R.id.tvRemark)
            if (code.equals("1", ignoreCase = true)) {
                tvRemark.setTextColor(resources.getColor(R.color.greenpromo))
            } else if (code.equals("2", ignoreCase = true)) {
                tvRemark.setTextColor(resources.getColor(R.color.redpromo))
            }
            tvRemark.text = remark
            ivClose.setOnClickListener {
                SharedPreferenceVariable.savePreferences(
                    activity,
                    KeyConstant.KEY_OPEN_PROMOCODE,
                    "1"
                )
                dialog1.dismiss()
            }
        } catch (e: Exception) {
        }
        try {
            dialog1!!.show()
        } catch (e: Exception) {
        }
    }

    private fun Promotion_PopUP() {
//        ErrorMessage.E("aaa"+ exclusive_dialog.isShowing)

//        val dialog = Dialog(activity!!)

        if (reedeemreward_dialog != null && reedeemreward_dialog!!.isShowing) {
            reedeemreward_dialog!!.dismiss()
        }
        reedeemreward_dialog = Dialog(requireActivity())

        reedeemreward_dialog!!.setContentView(R.layout.promotion_popup)
        reedeemreward_dialog!!.setCanceledOnTouchOutside(false)
        reedeemreward_dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        val window = reedeemreward_dialog!!.window
        lp.copyFrom(window!!.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        Check_popup_visible = "visible"
        title_tv = reedeemreward_dialog!!.findViewById(R.id.title_tv)
        top_title_tv = reedeemreward_dialog!!.findViewById(R.id.top_title_tv)
        second_title_tv = reedeemreward_dialog!!.findViewById(R.id.second_title_tv)
        my_pramotion_layout = reedeemreward_dialog!!.findViewById(R.id.my_pramotion_layout)
        my_deal_layout = reedeemreward_dialog!!.findViewById(R.id.my_deal_layout)
        more_layout = reedeemreward_dialog!!.findViewById(R.id.more_layout)
        my_pramotion_rcv = reedeemreward_dialog!!.findViewById(R.id.my_pramotion_rcv)
        my_deals_rcv = reedeemreward_dialog!!.findViewById(R.id.my_deals_rcv)
        val ivClose = reedeemreward_dialog!!.findViewById<ImageView>(R.id.ivClose)
        val my_pramotion_view_all_tv =
            reedeemreward_dialog!!.findViewById<TextView>(R.id.my_pramotion_view_all_tv)
        val my_deals_view_all_tv =
            reedeemreward_dialog!!.findViewById<TextView>(R.id.my_deals_view_all_tv)
        my_deal_cardview = reedeemreward_dialog!!.findViewById(R.id.my_deal_cardview)
        val more_deals_btn = reedeemreward_dialog!!.findViewById<Button>(R.id.more_deals_btn)
        my_pramotion_view_all_tv.setOnClickListener {
            try {
//                exclusive_dialog!!.dismiss()

                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }

                reedeemreward_dialog!!.dismiss()
                startActivity(Intent(activity, MY_PromotionActivity::class.java))
                MyLog.onAnim(requireActivity())
            } catch (e: Exception) {
            }
        }
        my_deals_view_all_tv.setOnClickListener {
            try {
                reedeemreward_dialog!!.dismiss()
//                exclusive_dialog!!.dismiss()
                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
                startActivity(Intent(activity, NewMyDealsActivity::class.java))
                MyLog.onAnim(requireActivity())
            } catch (e: Exception) {
            }
        }
        more_layout?.setOnClickListener(View.OnClickListener {
            try {
                reedeemreward_dialog!!.dismiss()
//                exclusive_dialog!!.dismiss()

                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
                startActivity(Intent(activity, NewMyDealsActivity::class.java))
                MyLog.onAnim(requireActivity())
            } catch (e: Exception) {
            }
        })
        more_deals_btn.setOnClickListener {
            try {
                reedeemreward_dialog!!.dismiss()
//                exclusive_dialog!!.dismiss()

                if (exclusive_dialog != null) {
                    exclusive_dialog!!.dismiss()
                }
                startActivity(Intent(activity, NewMyDealsActivity::class.java))
                MyLog.onAnim(requireActivity())
            } catch (e: Exception) {
            }
        }
        ivClose.setOnClickListener { reedeemreward_dialog!!.dismiss() }
        reedeemreward_dialog!!.show()
    }

    private fun ExclusiveDealsP_PopUP() {

//        ErrorMessage.E("fff"+ exclusive_dialog.isShowing)

        if (exclusive_dialog != null && exclusive_dialog!!.isShowing) {
            exclusive_dialog!!.dismiss()
        }


        exclusive_dialog = Dialog(requireActivity())
        exclusive_dialog!!.setContentView(R.layout.exclusive_promotion_popup)
        exclusive_dialog!!.setCanceledOnTouchOutside(false)
        exclusive_dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        val window = exclusive_dialog!!.window
        lp.copyFrom(window!!.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        Check_popup_visible = "visible"

        top_title_tv = exclusive_dialog!!.findViewById(R.id.top_title_tv)
        top_image = exclusive_dialog!!.findViewById(R.id.top_image)
        my_deals_rcv = exclusive_dialog!!.findViewById(R.id.my_deals_rcv)
        val ivClose = exclusive_dialog!!.findViewById<ImageView>(R.id.ivClose)


        ivClose.setOnClickListener { exclusive_dialog!!.dismiss() }
        exclusive_dialog!!.show()
    }

    private fun GetPromotionData() {
        if (AppUtil.isNetworkAvailable(activity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(activity, "", false, null)
            val call = AppConfig.api_Interface().getPopupPromotionDealsList(
                MainActivity.userLat.toString(),
                MainActivity.userLang.toString()
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        Log.e("ResssPor", response.toString())
                        try {
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                        } catch (e: Exception) {
                        }
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Log.e(">GetPromotionData>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                val example = gson.fromJson(obj.toString(), Example::class.java)
                                if (example.response.latestDealsList.size > 0 || example.response.promotionDealsList.size > 0) {

//                                        exclusive_dialog.dismiss();

                                    Promotion_PopUP()


                                    title_tv!!.text = "" + example.response.title
                                    top_title_tv!!.text = "" + example.response.topTitle
                                    second_title_tv!!.text = "" + example.response.totalVoucher
                                    title_tv!!.text = "" + example.response.title
                                    if (example.response.latestDealsList.size > 0) {
                                        more_layout!!.visibility = View.VISIBLE
                                        my_deal_cardview!!.visibility = View.VISIBLE
                                        my_deal_layout!!.visibility = View.VISIBLE
                                        val side_rv_adapter = Daily_Deal_Adapter(
                                            activity!!,
                                            example.response.latestDealsList,
                                            "",
                                            "popup"
                                        )
                                        my_deals_rcv!!.layoutManager = LinearLayoutManager(
                                            activity,
                                            RecyclerView.VERTICAL,
                                            false
                                        )
                                        my_deals_rcv!!.isNestedScrollingEnabled = false
                                        my_deals_rcv!!.setItemViewCacheSize(example.response.latestDealsList.size)
                                        my_deals_rcv!!.adapter = side_rv_adapter
                                        side_rv_adapter.notifyDataSetChanged()
                                    } else {
                                        more_layout!!.visibility = View.GONE
                                        my_deal_cardview!!.visibility = View.GONE
                                        my_deal_layout!!.visibility = View.GONE
                                    }
                                    if (example.response.promotionDealsList.size > 0) {
                                        my_pramotion_rcv!!.visibility = View.VISIBLE
                                        my_pramotion_layout!!.visibility = View.VISIBLE
                                        val side_rv_adapter = My_Promotion_Adapter(
                                            activity!!,
                                            example.response.promotionDealsList,
                                            -2
                                        )
                                        my_pramotion_rcv!!.layoutManager = GridLayoutManager(
                                            activity, 2
                                        )
                                        my_pramotion_rcv!!.isNestedScrollingEnabled = false
                                        my_pramotion_rcv!!.setItemViewCacheSize(example.response.promotionDealsList.size)
                                        my_pramotion_rcv!!.adapter = side_rv_adapter
                                        side_rv_adapter.notifyDataSetChanged()
                                    } else {
                                        my_pramotion_rcv!!.visibility = View.GONE
                                        my_pramotion_layout!!.visibility = View.GONE
                                    }
                                }
                            } else {
                                AppUtil.showMsgAlert(
                                    MainActivity.tvLocation,
                                    obj.optString("message")
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                        }
                    } else {
                        if (dialogManager != null) {
                            dialogManager.stopProcessDialog()
                        }
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog()
                    }
                    AppUtil.showMsgAlert(MainActivity.tvLocation, t.message)
                }
            })
        } else {
            ErrorMessage.T(activity, "No Internet Found!")
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun GetExclusiveDealData() {


        if (activity != null) {

            if (AppUtil.isNetworkAvailable(activity!!)) {

                if (isVisible && offer_popup_counter == 0) {
                    offer_popup_counter++
                    val dialogManager = DialogManager()
                    dialogManager.showProcessDialog(activity, "", false, null)
                    val call = AppConfig.api_Interface().getPopupPromotionDealsListV1(
                        /* MainActivity.userLat.toString(),
                         MainActivity.userLang.toString()*/
                    )
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                ErrorMessage.E("nDealsListV1>>" + response.toString())
                                try {
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                } catch (e: Exception) {
                                }
                                try {
                                    val obj = JSONObject(response.body()!!.string())
                                    Log.e(">GetPromotionData>>", obj.toString())
                                    if (obj.optString("error_type") == "200") {
                                        val gson = Gson()
                                        val example =
                                            gson.fromJson(obj.toString(), Example::class.java)
                                        if (example.response.latestDealsList.size > 0 || example.response.promotionDealsList.size > 0) {

//                                    ErrorMessage.E("qwert ${exclusive_dialog}")

                                            if (exclusive_dialog == null || !exclusive_dialog!!.isShowing) {
                                                ExclusiveDealsP_PopUP()
                                            }

                                            top_title_tv!!.text = "" + example.response.topTitle
                                            Glide.with(activity!!)
                                                .load(example.response.topTitleImage)
                                                .into(top_image!!)
                                            if (example.response.latestDealsList.size > 0) {
                                                val side_rv_adapter = exclusive_dialog?.let {
                                                    ExclusiveDealAdapter(
                                                        activity!!,
                                                        example.response.latestDealsList,
                                                        "",
                                                        "popup", it

                                                    )
                                                }
                                                my_deals_rcv!!.layoutManager = LinearLayoutManager(
                                                    activity,
                                                    RecyclerView.VERTICAL,
                                                    false
                                                )
                                                my_deals_rcv!!.isNestedScrollingEnabled = false
                                                my_deals_rcv!!.setItemViewCacheSize(example.response.latestDealsList.size)
                                                my_deals_rcv!!.adapter = side_rv_adapter
                                                side_rv_adapter?.notifyDataSetChanged()
                                            } else {
                                                more_layout!!.visibility = View.GONE


                                            }
                                            if (example != null && example.response != null && example.response.promotionDealsList != null && example.response.promotionDealsList.size > 0) {
                                                my_pramotion_rcv!!.visibility = View.VISIBLE
                                                my_pramotion_layout!!.visibility = View.VISIBLE
                                                val side_rv_adapter = My_Promotion_Adapter(
                                                    activity!!,
                                                    example.response.promotionDealsList,
                                                    -2
                                                )
                                                my_pramotion_rcv!!.layoutManager =
                                                    GridLayoutManager(
                                                        activity, 2
                                                    )
                                                my_pramotion_rcv!!.isNestedScrollingEnabled = false
                                                my_pramotion_rcv!!.setItemViewCacheSize(example.response.promotionDealsList.size)
                                                my_pramotion_rcv!!.adapter = side_rv_adapter
                                                side_rv_adapter.notifyDataSetChanged()
                                            } else {

                                                if (my_pramotion_rcv != null && my_deal_layout != null) {
                                                    my_pramotion_rcv!!.visibility = View.GONE
                                                    my_pramotion_layout!!.visibility = View.GONE
                                                }


                                            }
                                        }
                                    } else {
                                        AppUtil.showMsgAlert(
                                            MainActivity.tvLocation,
                                            obj.optString("message")
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("Ex1", e.toString())
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                }
                            } else {
                                if (dialogManager != null) {
                                    dialogManager.stopProcessDialog()
                                }
                                Log.e("sendToken", "else is working" + response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                            AppUtil.showMsgAlert(MainActivity.tvLocation, t.message)
                        }
                    })
                } else {
                    ErrorMessage.E("offer_popup_counter" + offer_popup_counter)
                }

            } else {
                ErrorMessage.T(activity, "No Internet Found!")
            }
        }
    }


    private fun GetPromoCodePopUp(
        message: String,
        externalURLStatus: Int,
        externalURL: String,
        buttonText: String,
    ) {


        if (promo_code_popup == null) {

//    ErrorMessage.E("dsdsddssdsd>>${loadingbarforfetchingdata}" )
//    if (loadingbarforfetchingdata == null) {
            loadingbarforfetchingdata = ErrorMessage.initProgressDialog(context1)
            loadingbarforfetchingdata!!.show()
//    }

            promo_code_popup = context1?.let { Dialog(it) }
            promo_code_popup!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            promo_code_popup!!.setContentView(R.layout.custum_promo_code_popup)

            promo_code_popup!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(promo_code_popup!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            promo_code_popup!!.window!!.attributes = lp
            val tvTitle =
                promo_code_popup!!.findViewById<TextView>(R.id.popup_heading)
            val cancel_button =
                promo_code_popup!!.findViewById<ImageButton>(R.id.cancel_button)
            val popup_yes_btn =
                promo_code_popup!!.findViewById<Button>(R.id.popup_yes_btn)


            if (buttonText != null && !buttonText.equals("")) {
                popup_yes_btn.visibility = View.VISIBLE
                popup_yes_btn.setText("${buttonText}")
            } else {
                popup_yes_btn.visibility = View.GONE
            }

            if (message != null && !message.equals("")
            ) {
                tvTitle.visibility = View.VISIBLE
                tvTitle.setText("${message}")
            } else {
                tvTitle.visibility = View.GONE
            }


            popup_yes_btn.setOnClickListener(View.OnClickListener {
                if (externalURLStatus != null && externalURLStatus == 1) {

                    promo_code_popup!!.dismiss()

                    val intent = Intent(context1, WebViewActivity::class.java)
                    intent.putExtra("url", externalURL)
                    intent.putExtra("title", message)
                    startActivity(intent)

                } else {
                    promo_code_popup!!.dismiss()
                }
            })

            cancel_button.setOnClickListener(View.OnClickListener {
                promo_code_popup!!.dismiss()
            })
            if (loadingbarforfetchingdata != null) {
                loadingbarforfetchingdata!!.dismiss()
            }

            if (popup_yes_btn.visibility == View.GONE && tvTitle.visibility == View.GONE) {

            } else {
                promo_code_popup!!.show()
            }

        }
    }


    companion object {
        @JvmField
        var iv: ImageView? = null
    }

    private fun GetGiftCardRequest() {

        ErrorMessage.E("activity<><><>" + activity)
        if (activity != null) {

            if (AppUtil.isNetworkAvailable(requireActivity())) {

                if (isVisible) {
                    val dialogManager = DialogManager()
                    dialogManager.showProcessDialog(activity, "", true, null)
                    val call = AppConfig.api_Interface().getGiftCardRequest()
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                ErrorMessage.E("GetGiftCardRequest>>" + response.toString())
                                try {
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                } catch (e: Exception) {
                                }
                                try {
                                    val obj = JSONObject(response.body()!!.string())
                                    Log.e(">GetGiftCardRequest>>", obj.toString())
                                    if (obj.optString("error_type") == "200") {
                                        val gson = Gson()
                                        val example =
                                            gson.fromJson(
                                                obj.toString(),
                                                sambal.mydd.app.models.GiftCard_Model.Example::class.java
                                            )



                                        if (example != null) {
                                            ErrorMessage.E("example<><>" + example)
                                            GiftCardPopUp(example)
                                        }


                                    } else {
                                        AppUtil.showMsgAlert(
                                            MainActivity.tvLocation,
                                            obj.optString("message")
                                        )

                                        if (loadingbarforfetchingdata != null) {
                                            loadingbarforfetchingdata!!.dismiss()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Ex1", e.toString())
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }

                                    if (loadingbarforfetchingdata != null) {
                                        loadingbarforfetchingdata!!.dismiss()
                                    }
                                }
                            } else {
                                if (dialogManager != null) {
                                    dialogManager.stopProcessDialog()
                                }
                                Log.e("sendToken", "else is working" + response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                            AppUtil.showMsgAlert(MainActivity.tvLocation, t.message)
                        }
                    })
                } else {
                    ErrorMessage.E("offer_popup_counter" + offer_popup_counter)
                }

            } else {
                ErrorMessage.T(activity, "No Internet Found!")
            }
        }
    }

    private fun GiftCardPopUp(
        example: sambal.mydd.app.models.GiftCard_Model.Example,
    ) {


        if (gift_card_popup == null) {


            gift_card_popup = context1?.let { Dialog(it) }
            gift_card_popup!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            gift_card_popup!!.setContentView(R.layout.gift_card_pop_up)

            gift_card_popup!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(gift_card_popup!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            gift_card_popup!!.window!!.attributes = lp
            gift_card_popup!!.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent)

            val gift_card_msg_tv =
                gift_card_popup!!.findViewById<TextView>(R.id.gift_card_msg)
            val giftMessage_tv =
                gift_card_popup!!.findViewById<TextView>(R.id.giftMessage_tv)
            val cancel_button =
                gift_card_popup!!.findViewById<Button>(R.id.popup_no_btn)
            val popup_yes_btn =
                gift_card_popup!!.findViewById<Button>(R.id.popup_yes_btn)

            val cancle_btn =
                gift_card_popup!!.findViewById<ImageButton>(R.id.cancel_button)

            var gift_card_msg_text = ""

            if (example != null && example.giftFromUserName != null && example.giftCurrency != null && example.giftAmount != null && example.giftAgentName != null) {
                gift_card_msg_text =
                    "${example.giftFromUserName} has sent you a ${example.giftCurrency}${example.giftAmount} worth of a gift voucher from  \"${example.giftAgentName}\""
                gift_card_msg_tv.visibility = View.VISIBLE;
                gift_card_msg_tv.setText(gift_card_msg_text)
            } else {
                gift_card_msg_tv.visibility = View.GONE;
            }

            if (example != null && example.giftMessage != null && !example!!.giftMessage.isEmpty()) {
                giftMessage_tv.visibility = View.VISIBLE;
                giftMessage_tv.setText("\"${example.giftMessage}\"")
            } else {
//                giftMessage_tv.visibility = View.GONE;
            }



            popup_yes_btn.setOnClickListener(View.OnClickListener {
                gift_card_popup!!.dismiss()

                if (example != null && example.giftAgentName != null && example.giftId != null) {
//                    GiftCardConfirmOrAcceptPopUp(example.giftFromUserName, 1)
                    acceptGiftCardRequest("" + example.giftId, example.giftAgentName)
                }

            })

            cancel_button.setOnClickListener(View.OnClickListener {
                gift_card_popup!!.dismiss()
                if (example != null && example.giftFromUserName != null && example.giftId != null) {
                    GiftCardConfirmOrAcceptPopUp(example.giftFromUserName, 0, "" + example.giftId)
                }
            })

            cancle_btn.setOnClickListener(View.OnClickListener {
                gift_card_popup!!.dismiss()
            })

            if (loadingbarforfetchingdata != null) {
                loadingbarforfetchingdata!!.dismiss()
            }


        }
        gift_card_popup!!.show()
    }


    private fun GiftCardConfirmOrAcceptPopUp(
        message: String,
        accept_or_reject_check: Int,
        giftId: String,
    ) {

        var gift_card_accept_or_reject_popup: Dialog? = null




        gift_card_accept_or_reject_popup = context1?.let { Dialog(it) }
        gift_card_accept_or_reject_popup!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        gift_card_accept_or_reject_popup!!.setContentView(R.layout.custum_gift_pop_up_rejected_and_accept_layout)

        gift_card_accept_or_reject_popup!!.setCanceledOnTouchOutside(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(gift_card_accept_or_reject_popup!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        gift_card_accept_or_reject_popup!!.window!!.attributes = lp
        gift_card_accept_or_reject_popup!!.getWindow()!!
            .setBackgroundDrawableResource(android.R.color.transparent)


        val rejected_heading_tv =
            gift_card_accept_or_reject_popup!!.findViewById<TextView>(R.id.rejected_heading_tv)
        val cancel_button =
            gift_card_accept_or_reject_popup!!.findViewById<Button>(R.id.popup_no_btn)
        val popup_yes_btn =
            gift_card_accept_or_reject_popup!!.findViewById<Button>(R.id.popup_yes_btn)
        val accepted_layout =
            gift_card_accept_or_reject_popup!!.findViewById<LinearLayout>(R.id.accepted_layout)
        val rejected_layout =
            gift_card_accept_or_reject_popup!!.findViewById<LinearLayout>(R.id.rejected_layout)
        val accepted_heading_tv =
            gift_card_accept_or_reject_popup!!.findViewById<TextView>(R.id.accepted_heading_tv)

        val cancel_rejection_button =
            gift_card_accept_or_reject_popup!!.findViewById<ImageButton>(R.id.cancel_rejection_button)


        if (accept_or_reject_check == 0) {
            accepted_layout.visibility = View.GONE
            rejected_layout.visibility = View.VISIBLE

            if (message != null) {
                rejected_heading_tv.setText("Are you sure, you want to reject  the Gift card frm ${message}?")
            }
        } else {
            accepted_layout.visibility = View.VISIBLE
            rejected_layout.visibility = View.GONE


            if (message != null) {
                accepted_heading_tv.setText("Your gift card is added to your ${message} wallet.")
            }


            // Use a Handler to cancel the toast after 5 seconds
            /*     Handler(Looper.getMainLooper()).postDelayed({
                     gift_card_accept_or_reject_popup.dismiss()
                 }, 5000)*/

        }



        popup_yes_btn.setOnClickListener(View.OnClickListener {
            gift_card_accept_or_reject_popup!!.dismiss()
            rejectGiftCardRequest(giftId, "");
        })

        cancel_button.setOnClickListener(View.OnClickListener {
            gift_card_accept_or_reject_popup!!.dismiss()

        })

        cancel_rejection_button.setOnClickListener(View.OnClickListener {
            gift_card_accept_or_reject_popup!!.dismiss()

        })

        if (loadingbarforfetchingdata != null) {
            loadingbarforfetchingdata!!.dismiss()
        }

        gift_card_accept_or_reject_popup!!.show()
    }


    private fun acceptGiftCardRequest(giftId: String, message: String) {

        ErrorMessage.E("acceptGiftCardRequest<><><>" + activity)
        if (activity != null) {

            if (AppUtil.isNetworkAvailable(requireActivity())) {

                if (isVisible) {
                    val dialogManager = DialogManager()
                    dialogManager.showProcessDialog(activity, "", true, null)
                    val call = AppConfig.api_Interface().acceptGiftCardRequest(giftId)
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                ErrorMessage.E("acceptGiftCardRequest>>" + response.toString())
                                try {
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                } catch (e: Exception) {
                                }
                                try {
                                    val obj = JSONObject(response.body()!!.string())
                                    Log.e(">GetGiftCardRequest>>", obj.toString())
                                    if (obj.optString("error_type") == "200") {

                                        ErrorMessage.T(context1, obj.optString("message"))
                                        GiftCardConfirmOrAcceptPopUp(message, 1, giftId)
                                    } else {
                                        AppUtil.showMsgAlert(
                                            MainActivity.tvLocation,
                                            obj.optString("message")
                                        )

                                        if (loadingbarforfetchingdata != null) {
                                            loadingbarforfetchingdata!!.dismiss()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Ex1", e.toString())
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }

                                    if (loadingbarforfetchingdata != null) {
                                        loadingbarforfetchingdata!!.dismiss()
                                    }
                                }
                            } else {
                                if (dialogManager != null) {
                                    dialogManager.stopProcessDialog()
                                }
                                Log.e("sendToken", "else is working" + response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                            AppUtil.showMsgAlert(MainActivity.tvLocation, t.message)
                        }
                    })
                } else {
                    ErrorMessage.E("offer_popup_counter" + offer_popup_counter)
                }

            } else {
                ErrorMessage.T(activity, "No Internet Found!")
            }
        }
    }


    private fun rejectGiftCardRequest(giftId: String, message: String) {

        ErrorMessage.E("rejectGiftCardRequest<><><>" + activity)
        if (activity != null) {

            if (AppUtil.isNetworkAvailable(requireActivity())) {

                if (isVisible) {
                    val dialogManager = DialogManager()
                    dialogManager.showProcessDialog(activity, "", true, null)
                    val call = AppConfig.api_Interface().rejectGiftCardRequest(giftId)
                    call!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>,
                        ) {
                            if (response.isSuccessful) {
                                ErrorMessage.E("rejectGiftCardRequest>>" + response.toString())
                                try {
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }
                                } catch (e: Exception) {
                                }
                                try {
                                    val obj = JSONObject(response.body()!!.string())
                                    Log.e(">GetGiftCardRequest>>", obj.toString())
                                    if (obj.optString("error_type") == "200") {

                                        ErrorMessage.T(context1, obj.optString("message"))
                                    } else {
                                        AppUtil.showMsgAlert(
                                            MainActivity.tvLocation,
                                            obj.optString("message")
                                        )

                                        if (loadingbarforfetchingdata != null) {
                                            loadingbarforfetchingdata!!.dismiss()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Ex1", e.toString())
                                    if (dialogManager != null) {
                                        dialogManager.stopProcessDialog()
                                    }

                                    if (loadingbarforfetchingdata != null) {
                                        loadingbarforfetchingdata!!.dismiss()
                                    }
                                }
                            } else {
                                if (dialogManager != null) {
                                    dialogManager.stopProcessDialog()
                                }
                                Log.e("sendToken", "else is working" + response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            ErrorMessage.E("ON FAILURE > " + t.message)
                            if (dialogManager != null) {
                                dialogManager.stopProcessDialog()
                            }
                            AppUtil.showMsgAlert(MainActivity.tvLocation, t.message)
                        }
                    })
                } else {
                    ErrorMessage.E("offer_popup_counter" + offer_popup_counter)
                }

            } else {
                ErrorMessage.T(activity, "No Internet Found!")
            }
        }
    }

}