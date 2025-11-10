package sambal.mydd.app.fragment.MyRewards

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.activity.StoreGiftCardActivity
import sambal.mydd.app.adapter.AdapterStoreVoucher
import sambal.mydd.app.beans.*
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.FragmentMyVouchersBinding
import sambal.mydd.app.models.RefreshCard
import sambal.mydd.app.utils.*
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVouchersFragment : Fragment(), ChatHistoryCallback {
    private var reward_binding: FragmentMyVouchersBinding? = null
    private var Latt: String? = "0.0"
    private var Longii: String? = "0.0"
    private val rewardsAgentList = ArrayList<AgentMainBean>()
    private var rBannerList: JSONArray? = null
    private var CatId = "0"
    private var Offset = 0
    private val Count = 10
    private var isFirst = true
    private val rDealList = ArrayList<StorePointsDealsList>()
    private var Adap: AdapterStoreVoucher? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var ItemCount = 0
    private var dialog1: Dialog? = null
    private var tvSuccess: TextView? = null
    private var ivTick: ImageView? = null

    private var context1: Context? = null
    //private var rHandler: Handler? = null
    private var tvPrice: TextView? = null
    private var available_data = false

    //private BroadcastReceiver onNotice_refresh;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        reward_binding = FragmentMyVouchersBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_my_vouchers, container, false);
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(onNotice_refresh, IntentFilter("refresh_rewards"))
        context1 = activity
        Latt = MainActivity.userLat.toString() + ""
        Longii = MainActivity.userLang.toString() + ""
        rewardsAgentList.clear()
        CatId = "0"
        rHandler = Handler()
        reward_binding!!.rvDeals.setHasFixedSize(true)
        linearLayoutManager = WrapContentLinearLayoutManager(context1)
        reward_binding!!.rvDeals.layoutManager = linearLayoutManager
        ErrorMessage.E("list>>>" + rewardsAgentList.size)
        Adap = AdapterStoreVoucher(context1, rewardsAgentList, reward_binding!!.rvDeals)
        reward_binding!!.rvDeals.setItemViewCacheSize(rewardsAgentList.size)
        reward_binding!!.rvDeals.adapter = Adap
        isFirst = true
        if (Adap != null) {
            Adap!!.notifyDataSetChanged()
            Adap!!.setLoaded()
        }
        reward_binding!!.mainTitleTv.text = """You have not earned any 
 vouchers yet"""
        val appName = getString(R.string.app_name)
        reward_binding!!.subTitleTv.text = """Keep visiting $appName Partners and claim 
 points to earn vouchers"""
        getAllDetails(true)
        reward_binding!!.rvDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        ErrorMessage.E("Last data++++1$Offset")
                        if (available_data) {
                            Offset++
                            isFirst = false
                            getAllDetails(true)
                        }
                    }
                } catch (e: Exception) {
                }
            }
        })
        reward_binding!!.myVoucherErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(context1)) {
                reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.GONE
            getAllDetails(true)}
            else {

                AppUtil.showMsgAlert(
                    reward_binding!!.mainTitleTv,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
        return reward_binding!!.root
    }

    private fun getAllDetails(isShowingLoader: Boolean) {
        try {
            Latt = MainActivity.userLat.toString() + ""
            Longii = MainActivity.userLang.toString() + ""
            rBannerList = null
            var materialDialog: Dialog? = null
            if (AppUtil.isNetworkAvailable(context1)) {
                reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.GONE
                if (isShowingLoader) {
                    materialDialog = ErrorMessage.initProgressDialog(context1)
                }
                val finalMaterialDialog = materialDialog
                ErrorMessage.E("Latt : $Latt")
                ErrorMessage.E("Longii : $Longii")
                ErrorMessage.E("CatId : $CatId")
                ErrorMessage.E("Offset : $Offset")
                ErrorMessage.E("Count : $Count")
                val call = AppConfig.api_Interface()
                    .getMyPointListV3(Latt, Longii, Offset.toString(), Count.toString(), CatId, "2")
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        if (response.isSuccessful) {
                            try {
                                finalMaterialDialog?.dismiss()
                            } catch (e: Exception) {
                                reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                            }
                            if (response != null && response.code() == 200) {
                                try {
                                    reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.GONE

                                    Log.e("RESPONSE", ">>" + response.code())
                                    Log.e("RESPONSE", ">>$response")
                                    val resp = JSONObject(response.body()!!.string())
                                    Log.e("ResVocuher", resp.toString() + "")
                                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                        val responseobj =
                                            resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                        //productCatList = responseobj.optJSONArray("categoryList");
                                        rBannerList = responseobj.optJSONArray("rBannerList")
                                        val arr = responseobj.optJSONArray("agentList")
                                        Log.e("ArrStorepoint", arr.toString())
                                        if (isFirst && arr.length() > 0) {
                                            rewardsAgentList.clear()
                                        }
                                        if (arr.length() > 0) {
                                            available_data = true
                                            val mColorList = ArrayList<VisitcolorBean>()
                                            mColorList.clear()
                                            for (i in 0 until arr.length()) {
                                                val obj = arr.getJSONObject(i)
                                                val agentId = obj.optString("agentId")
                                                val agentCompanyName =
                                                    obj.optString("agentCompanyName")
                                                val agentAddress = obj.optString("agentAddress")
                                                val distance = obj.optString("distance")
                                                val agentStandardPointStatus =
                                                    obj.optString("agentStandardPointStatus")
                                                val agentDoublePointStatus =
                                                    obj.optString("agentDoublePointStatus")
                                                val agentBonusPointStatus =
                                                    obj.optString("agentBonusPointStatus")
                                                val agentBonusPoint =
                                                    obj.optString("agentBonusPoint")
                                                val donateStatus = obj.optString("donateStatus")
                                                val charityDonatedText =
                                                    obj.optString("charityDonatedText")
                                                val agentPointStartDate =
                                                    obj.optString("agentPointStartDate")
                                                val agentPointEndDate =
                                                    obj.optString("agentPointEndDate")
                                                val userEarnedPoints =
                                                    obj.optString("userEarnedPoints")
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
                                                val agentWalletType =
                                                    obj.optString("agentWalletType")
                                                val agentUserVisitCount =
                                                    obj.optString("agentUserVisitCount")
                                                val agentUserTodayVisit =
                                                    obj.optString("agentUserTodayVisit")
                                                val agentUserFreeDealID =
                                                    obj.optString("agentUserFreeDealID")
                                                val agentUserFreeDealName =
                                                    obj.optString("agentUserFreeDealName")
                                                val agentUserDealStatus =
                                                    obj.optString("agentUserDealStatus")
                                                val membershipStatus =
                                                    obj.optString("membershipStatus")
                                                val membershipImage =
                                                    obj.optString("membershipImage")
                                                val arrV = obj.optJSONArray("voucherList")
                                                val mVoucherList = ArrayList<AgentVoucherListBean>()
                                                mVoucherList.clear()
                                                for (j in 0 until arrV.length()) {
                                                    val ob = arrV.optJSONObject(j)
                                                    val voucherId = ob.optString("voucherId")
                                                    val voucherSerialNumber =
                                                        ob.optString("voucherSerialNumber")
                                                    val voucherNumber =
                                                        ob.optString("voucherNumber")
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
                                                    val giftExpireDate =
                                                        ob.optString("giftExpireDate")
                                                    val giftSellingPrice =
                                                        ob.optString("giftSellingPrice")
                                                    val discountValue =
                                                        ob.optString("discountValue")
                                                    val giftTextRemark =
                                                        ob.optString("giftTextRemark")
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
                                                val arDealList = obj.optJSONArray("dealsList")
                                                run {
                                                    Log.e("dealLi", arDealList.toString() + "")
                                                    if (arDealList.length() > 0) {
                                                        rDealList.clear()
                                                        for (l in 0 until arDealList.length()) {
                                                            val objs = arDealList.optJSONObject(l)
                                                            val dealId = objs.optString("dealId")
                                                            val productId =
                                                                objs.optString("productId")
                                                            val dealName =
                                                                objs.optString("dealName")
                                                            val agentIds = objs.optString("agentId")
                                                            val agentName =
                                                                objs.optString("agentName")
                                                            val viewCount =
                                                                objs.optString("viewCount")
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
                                                            val offerType =
                                                                objs.optString("offerType")
                                                            val offerTypeId =
                                                                objs.optString("offerTypeId")
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
                                                            val dealImage =
                                                                objs.optString("dealImage")
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
                                                            rDealList.add(sp)
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
                                                    "",
                                                    agentWalletType,
                                                    "",
                                                    agentUserVisitCount,
                                                    agentUserTodayVisit,
                                                    agentUserFreeDealID,
                                                    agentUserFreeDealName,
                                                    "",
                                                    agentUserDealStatus,
                                                    "",
                                                    "",
                                                    mVoucherList,
                                                    rDealList,
                                                    mColorList,
                                                    "",
                                                    "",
                                                    membershipStatus,
                                                    membershipImage,
                                                    "",
                                                    giftCardListList
                                                )
                                                rewardsAgentList.add(amb)
                                            }
                                            ErrorMessage.E("list>234>>" + rewardsAgentList.size)
                                            if (Offset == 0) {
                                                ItemCount = rewardsAgentList.size
                                                Adap!!.notifyDataSetChanged()
                                            }
                                            Log.e("rewardsAgentList>>", "" + ItemCount)
                                            Log.e("rewardsAgentList>>", "" + rewardsAgentList.size)
                                            Adap!!.notifyItemInserted(rewardsAgentList.size)
                                            if (reward_binding!!.rvDeals.visibility == View.VISIBLE && Offset > 0) {
                                                ErrorMessage.E("ITEM COUNT 1 >>$ItemCount")
                                                reward_binding!!.rvDeals.scrollToPosition(ItemCount)
                                            }
                                            //  Adap.setLoaded();
                                            if (Offset > 0) {
                                                ItemCount = rewardsAgentList.size
                                            }
                                            if (isFirst) {
                                                if (rewardsAgentList.size > 0) {
                                                    reward_binding!!.llNoData.visibility = View.GONE
                                                    reward_binding!!.rvDeals.visibility =
                                                        View.VISIBLE
                                                } else {
                                                    reward_binding!!.mainTitleTv.text =
                                                        """You have not earned any 
 vouchers yet"""
                                                    val appName = getString(R.string.app_name)
                                                    reward_binding!!.subTitleTv.text =
                                                        """Keep visiting $appName Partners and claim 
 points to earn vouchers"""
                                                    reward_binding!!.llNoData.visibility =
                                                        View.VISIBLE
                                                    reward_binding!!.rvDeals.visibility = View.GONE
                                                }

                                                //updateUI(responseobj);
                                            }
                                        } else {
                                            available_data = false
                                            if (rewardsAgentList.size > 0) {
                                                Snackbar.make(
                                                    reward_binding!!.llNoData,
                                                    "No more data available",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            } else {
                                                reward_binding!!.llNoData.visibility = View.VISIBLE
                                            }
                                        }
                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                                resp.optString(
                                                    KeyConstant.KEY_STATUS
                                                ), ignoreCase = true
                                            )
                                        ) {
                                            finalMaterialDialog?.dismiss()
                                            //AppUtil.showMsgAlert(mainTitleTv, resp.optString(KeyConstant.KEY_MESSAGE));
                                            ErrorMessage.T(
                                                context1,
                                                resp.optString(KeyConstant.KEY_MESSAGE)
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    // swipeRefreshLayout.setRefreshing(false);
                                    finalMaterialDialog?.dismiss()
                                    //AppUtil.showMsgAlert(mainTitleTv, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                    ErrorMessage.T(context1,
                                        MessageConstant.MESSAGE_SOMETHING_WRONG)

                                    reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE

                                }
                            } else if (response.code() != 200) {
                                reward_binding!!.llNoData.visibility = View.VISIBLE
                            } else {
                                //AppUtil.showMsgAlert(mainTitleTv, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                ErrorMessage.T(context1, MessageConstant.MESSAGE_SOMETHING_WRONG)
                                reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE

                            }
                        } else {
                            finalMaterialDialog?.dismiss()
                            Log.e("sendToken", "else is working" + response.code().toString())
                            reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        finalMaterialDialog?.dismiss()
                        AppUtil.showMsgAlert(reward_binding!!.subTitleTv, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                    }
                })
            } else {
                reward_binding!!.myVoucherErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                AppUtil.showMsgAlert(
                    reward_binding!!.mainTitleTv,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        } catch (e: Exception) {
            ErrorMessage.E("Exception>>>>>$e")
        }
    }

    private val onNotice_refresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Log.e("CHECK ", "2")
                if (intent != null) {
                    Latt = intent.getStringExtra("lat")
                    Longii = intent.getStringExtra("lng")
                }
                rewardsAgentList.clear()
                Offset = 0
                isFirst = true
                getAllDetails(true)
                Adap!!.notifyDataSetChanged()
            } catch (r: Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Offset = 0
            rewardsAgentList.clear()
            Adap!!.notifyItemInserted(rewardsAgentList.size)
            Adap!!.notifyDataSetChanged()

            dialog1!!.dismiss()
        } catch (e: Exception) {
        }
    }


    fun showQRCODE(jsonObject: JSONObject, refreshCard: RefreshCard, context: Context?) {
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
            tvPrice = dialog1!!.findViewById(R.id.tvPrice)
            ivClose.setOnClickListener {
                dialog1!!.dismiss()
                dialog1 = null
            }
            val tvTitle = dialog1!!.findViewById<TextView>(R.id.ticket_title)
            tvTitle.text = jsonObject.optString("")
            val ivQrCode = dialog1!!.findViewById<ImageView>(R.id.iv_qr_code)
            val ivBarCOde = dialog1!!.findViewById<ImageView>(R.id.ivBarcode)
            tvTitle.text = jsonObject.optString("agentName")
            tvPrice!!.setText(jsonObject.optString("currency") + jsonObject.optString("redeemAmount"))
            val myBitmap = QRCode.from(jsonObject.optString("redeemUUID"))
                .bitmap()
            ivQrCode.setImageBitmap(myBitmap)
            var pubNubChat: PubNubChat? = null
            pubNubChat = PubNubChat(context!!, this)
            pubNubChat!!.initPubNub()
            pubNubChat!!.subscribePubNubChannel(jsonObject.optString("redeemUUID") + "")
            pubNubChat!!.subscribePubNubListener()
            dialog1!!.show()
            refreshCard.onSuccess("dvcw,udvg")
        }

    }

    override fun onRefreshHistoryList(list: List<PNHistoryItemResult>) {}
    override fun clearData() {}
    override fun onRefreshChatList(jsonObject: JsonObject) {

        rHandler!!.postDelayed({
            rewardsAgentList.clear()
            try {
                ivTick!!.visibility = View.VISIBLE
                tvSuccess!!.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("ivTick", ">>>$e")
            }
            if (Adap != null) {
                Adap!!.notifyDataSetChanged()
            }
            Offset = 0
            isFirst = true
            //Check = "voucher";
            try {
                getAllDetails(true)
            } catch (e: Exception) {
                Log.e("ivTick", ">>>$e")
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()

        try {

            ErrorMessage.E("StoreGiftCardActivity.somethingDone = " + StoreGiftCardActivity.somethingDone)
            if (StoreGiftCardActivity.somethingDone) {
                StoreGiftCardActivity.somethingDone = false
                rewardsAgentList.clear()
                Offset = 0
                isFirst = true
                getAllDetails(true)
                Adap!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e("Exception", "" + e.toString())
        }
    }

    companion object {
        @JvmStatic
        var rHandler: Handler? = null
    }
}