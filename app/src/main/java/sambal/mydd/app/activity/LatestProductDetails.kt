package sambal.mydd.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.gson.JsonObject
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.squareup.picasso.Picasso
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.adapter.DealList_Adapter
import sambal.mydd.app.beans.StorePointsDealsList
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.constant.MessageConstant.Companion.MESSAGE_INTERNET_CONNECTION
import sambal.mydd.app.databinding.NewporductdetailsBinding
import sambal.mydd.app.utils.*
/*import kotlinx.android.synthetic.main.my_wallet_shimmerview_layout.*
import kotlinx.android.synthetic.main.newporductdetails.**/
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LatestProductDetails : AppCompatActivity(), View.OnClickListener, ChatHistoryCallback {

    private lateinit var binding: NewporductdetailsBinding
    private lateinit var dealId: String
    private lateinit var agentImage: String
    private lateinit var productId: String
    private var orderOnlineLink: String = ""
    private lateinit var agentId: String
    private var dealUUID = ""
    private lateinit var pubNubChat: PubNubChat
    private lateinit var ivTick: ImageView
    private lateinit var tvSuccess: TextView
    private lateinit var lat: String
    private lateinit var lng: String
      var agentUrl=""
    private var pos = 0
    private var mainPos = 0
    private var favProId = ""
    private lateinit var isFav: String
    private var isChange = false
    var type = "non_direct"

    var dealRedeemAlertDate = ""
    var dealRedeemAlert = ""
    var dealRedeemAlertTime = ""
    var dealAvailability = ""
    var dealRedeemLockedAlert = ""
    var scanQRButtonEnable = ""
    var generateQRButtonEnable = ""
    private var mDealList = ArrayList<StorePointsDealsList?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding =
            DataBindingUtil.setContentView(this@LatestProductDetails, R.layout.newporductdetails)
        pubNubChat = PubNubChat(this@LatestProductDetails, this)
        pubNubChat.initPubNub()

        val bundle = intent.extras

        if (bundle != null) {
            agentId = bundle.getString("agentId").toString()
            try {
                type = bundle.getString("type").toString()
                if (type.equals("direct")) {
                    isChange = false
                }
            } catch (e: java.lang.Exception) {
            }
            try {
                if (productId.equals("null")) {
                    productId = ""
                }
            } catch (e: Exception) {
            }
            productId = bundle.getString("product_id").toString()
            favProId = bundle.getString("product_id").toString()
            Log.e("productId", ">>" + productId)

            try {
                pos = bundle.getInt("pos")
                mainPos = bundle.getInt("mainPos")
            } catch (e: Exception) {
            }
        }
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
        } catch (e: java.lang.Exception) {
        }

        if (!AppUtil.isNetworkAvailable(this@LatestProductDetails)) {
            AppUtil.showMsgAlert(binding.ivAgentimage, MESSAGE_INTERNET_CONNECTION)

        } else {

            getProductDetails()
        }

        binding.ivBack.setOnClickListener(this)
        binding.ivHeart.setOnClickListener(this)
        binding.tvVisit.setOnClickListener(this)
        binding.btnRedeem.setOnClickListener(this)
        binding.llRoute.setOnClickListener(this)
        binding.btnScanQR.setOnClickListener(this)
        binding.ivshared.setOnClickListener(this)
        binding.ivLike.setOnClickListener(this)
        binding.tvAgentName.setOnClickListener(this)

    }

    override fun onRefreshHistoryList(list: MutableList<PNHistoryItemResult>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefreshChatList(jsonObject: JsonObject?) {
        Log.e("OnRefresg", "OnRefreh")
        try {
            runOnUiThread {
                try {
                    ivTick.visibility = View.VISIBLE
                    tvSuccess.visibility = View.VISIBLE
                    getProductDetails()

                } catch (e: Exception) {
                }

            }

        } catch (e: Exception) {
            Log.e("QR", e.toString())
        }
    }

    private fun initPubNub(token: String) {

        pubNubChat.subscribePubNubChannel(token)
        pubNubChat.subscribePubNubListener()
    }

    private fun getProductDetails() {
        if (AppUtil.isNetworkAvailable(this@LatestProductDetails)) {
            mDealList.clear();
            var dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", true, null)
            if (productId.equals(null)) {
                productId = ""
                Log.e("proi", productId + ">>" + agentId)
            }


            ErrorMessage.E("getProductDetailsV1   {$productId}   {$agentId}   {$MainActivity.userLat.toString()}   {$MainActivity.userLang.toString()}  " )

            val call = AppConfig.api_Interface().getProductDetailsV1(productId, agentId, MainActivity.userLat.toString(), MainActivity.userLang.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            Log.e("proi", productId + ">>>>>>" + agentId)
                            Log.e("Lat", MainActivity.userLat.toString() + ">>>>>>>>>>" + MainActivity.userLang.toString())
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("LatestProd", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)) {
                                var responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                if (responseObj != null) {

                                    try {

                                        runOnUiThread {

                                            var ob = responseObj.optJSONArray("productDetails")
                                            var deallist_ob = responseObj.optJSONArray("dealsList")

                                            for (i in 0 until ob.length()) {

                                                var objProductDetails = ob.optJSONObject(i)
                                                Log.e("Ob", objProductDetails.toString())

                                                try {
                                                    Picasso.with(this@LatestProductDetails)
                                                        .load(objProductDetails.optString("dealImage"))
                                                        .placeholder(R.drawable.mainimageplaceholder)
                                                        .error(R.drawable.mainimageplaceholder)
                                                        .into(binding.ivAgentimage)

                                                } catch (e: Exception) {
                                                }
                                                try {
                                                    val transformation =
                                                        RoundedTransformationBuilder()
                                                            .oval(false)
                                                            .build()
                                                    Picasso.with(this@LatestProductDetails)
                                                        .load(objProductDetails.optString("agentImage"))
                                                        .transform(transformation)
                                                        .placeholder(R.drawable.place_holder)
                                                        .error(R.drawable.place_holder)
                                                        .into(binding.ivMainImage)
                                                } catch (e: Exception) {

                                                }

                                                /*mapURL =
                                                    objProductDetails.optString("agentMapURL")*/
                                                dealId = objProductDetails.optString("dealId")

                                                dealUUID =
                                                    objProductDetails.optString("dealQRuuid")
                                                binding.tvBusinessName.text =
                                                    objProductDetails.optString("agentName")

                                                initPubNub(dealUUID)

                                                agentUrl =
                                                    objProductDetails.optString("agentURL")
                                                scanQRButtonEnable =
                                                    objProductDetails.optString("scanQRButtonEnable")
                                                generateQRButtonEnable =
                                                    objProductDetails.optString("generateQRButtonEnable")



                                                if (objProductDetails.optString("dealRedeemEnable") == "0") {

                                                    binding.btnRedeem.visibility = View.VISIBLE
                                                    binding.btnRedeem.setBackgroundResource(R.drawable.llredeemstoreoff)
                                                    binding.btnRedeem.text =
                                                        "Check Availability"
                                                    binding.btnRedeem.setTextColor(
                                                        resources.getColor(
                                                            R.color.white
                                                        )
                                                    )

                                                } else if (objProductDetails.optString("dealRedeemEnable") == "1") {
                                                    binding.layoutButtons.visibility = View.VISIBLE
                                                    if (scanQRButtonEnable == "1" && generateQRButtonEnable == "1") {
                                                        binding.btnScanQR.visibility =
                                                            View.VISIBLE
                                                        binding.btnScanQR.setBackgroundResource(
                                                            R.drawable.llredeemstore
                                                        )
                                                        binding.btnScanQR.text = "Scan QR"
                                                        binding.btnScanQR.setTextColor(
                                                            Color.parseColor(
                                                                "#FFFFFF"
                                                            )
                                                        )

                                                        binding.btnRedeem.visibility =
                                                            View.VISIBLE
                                                        binding.btnRedeem.setBackgroundResource(
                                                            R.drawable.llredeemstore
                                                        )
                                                        binding.btnRedeem.text = "Generate QR"
                                                        binding.btnRedeem.setTextColor(
                                                            Color.parseColor(
                                                                "#FFFFFF"
                                                            )
                                                        )

                                                    }
                                                    else if (generateQRButtonEnable == "1" && scanQRButtonEnable == "0") {
                                                        binding.layoutButtons.visibility = View.VISIBLE
                                                        binding.btnScanQR.visibility = View.GONE
                                                        binding.btnRedeem.visibility =
                                                            View.VISIBLE
                                                        binding.btnRedeem.setBackgroundResource(
                                                            R.drawable.llredeemstore
                                                        )
                                                        binding.btnRedeem.visibility = View.VISIBLE
                                                        binding.btnRedeem.text = "Generate QR"
                                                        binding.btnRedeem.setTextColor(
                                                            Color.parseColor(
                                                                "#FFFFFF"
                                                            )
                                                        )
                                                    } else if (scanQRButtonEnable == "1" && generateQRButtonEnable == "0") {
                                                        binding.layoutButtons.visibility = View.VISIBLE
                                                        binding.btnScanQR.visibility =
                                                            View.VISIBLE
                                                        binding.btnRedeem.visibility = View.GONE
                                                        binding.btnScanQR.setBackgroundResource(
                                                            R.drawable.llredeemstore
                                                        )
                                                        binding.btnScanQR.text = "Scan QR"
                                                        binding.btnScanQR.setTextColor(
                                                            Color.parseColor(
                                                                "#FFFFFF"
                                                            )
                                                        )
                                                    }

                                                } else if (objProductDetails.optString("dealRedeemEnable") == "2") {
                                                    binding.layoutButtons.visibility = View.VISIBLE
                                                    binding.btnRedeem.visibility = View.VISIBLE
                                                    binding.btnRedeem.setBackgroundResource(R.drawable.lldealdetailsred)
                                                    binding.btnRedeem.text = "Redeemed"
                                                    binding.btnRedeem.setTextColor(
                                                        Color.parseColor(
                                                            "#FFFFFF"
                                                        )
                                                    )
                                                } else if (objProductDetails.optString("dealRedeemEnable") == "3") {
                                                    binding.layoutButtons.visibility = View.VISIBLE
                                                    binding.btnRedeem.visibility = View.VISIBLE
                                                    binding.btnRedeem.setBackgroundResource(R.drawable.llredeemlock)
                                                    binding.btnRedeem.text = "LOCKED"
                                                    binding.btnRedeem.setTextColor(
                                                        Color.parseColor(
                                                            "#FFFFFF"
                                                        )
                                                    )
                                                } else if (objProductDetails.optString("dealRedeemEnable")
                                                        .equals("4")
                                                ) {
                                                    binding.layoutButtons.visibility = View.VISIBLE
                                                    binding.btnRedeem.visibility = View.VISIBLE
                                                    binding.btnRedeem.setBackgroundResource(R.drawable.order_now_background)
                                                    binding.btnRedeem.text = "ORDER NOW"
                                                    binding.btnRedeem.setTextColor(
                                                        Color.parseColor(
                                                            "#FFFFFF"
                                                        )
                                                    )
                                                } else {
                                                    binding.btnRedeem.visibility = View.GONE
                                                    binding.btnScanQR.visibility = View.GONE
                                                    binding.layoutButtons.visibility = View.GONE

                                                }

                                                orderOnlineLink =
                                                    objProductDetails.optString("orderOnlineLink")
                                                binding.tvViews.text =
                                                    objProductDetails.optString("dealViews") + " VIEW"
                                                binding.tvFollowing.text =
                                                    objProductDetails.optString("followingCount") + " FOLLOWING"
                                                binding.tvCat.text =
                                                    objProductDetails.optString("productCategoryName")
                                                binding.tvReview.text =
                                                    objProductDetails.optString("dealRating") + " REVIEW"
                                                binding.tvDec.text =
                                                    objProductDetails.optString("dealDescription")
                                                binding.tvDealName.text =
                                                    objProductDetails.optString("dealName")
                                                binding.tvAgentName.text =
                                                    objProductDetails.optString("agentAddress") + "(" + objProductDetails.optString(
                                                        "agentDistance"
                                                    ) + ")"
                                                //binding.tvExpiriyDate.setText("Expired on -" + objProductDetails.optString("dealExpiredDate"))
                                                binding.tvVisit.paintFlags =
                                                    Paint.UNDERLINE_TEXT_FLAG

                                                dealRedeemAlert =
                                                    objProductDetails.optString("dealAvailability")
                                                dealRedeemAlertDate =
                                                    objProductDetails.optString("dealRedeemAlertDate")
                                                dealRedeemAlertTime =
                                                    objProductDetails.optString("dealRedeemAlertTime")
                                                dealRedeemLockedAlert =
                                                    objProductDetails.optString("dealRedeemLockedAlert")

                                                binding.tvAvailibilyAlert.text =
                                                    objProductDetails.optString("dealAvailability")
                                                binding.tvAvailablityDate.text =
                                                    objProductDetails.optString("dealAvailabilityDay")
                                                binding.tvAvailablityTime.text =
                                                    objProductDetails.optString("dealAvailabilityTime")

                                                val duration = DateUtil.getTimerMiliSec(
                                                    objProductDetails.optString("dealExpiredDate")
                                                )

                                                if (duration.contains("day left")) {
                                                    binding.tvExpiriyDate.text = duration
                                                } else if (duration.contains("days left")) {
                                                    binding.tvExpiriyDate.text = duration
                                                } else if (duration.contains("00:00:00")) {
                                                    binding.tvExpiriyDate.text = duration
                                                }

                                                lat =
                                                    objProductDetails.optString("agentLatitude")
                                                lng =
                                                    objProductDetails.optString("agentLongitude")

                                                dialogManager.stopProcessDialog()
                                                binding.layoutBottom.visibility = View.VISIBLE
                                                binding.defaultLayoutBottom.visibility = View.GONE
                                                Log.e("Ob", "11")
                                                if (objProductDetails.optString("dealType")
                                                        .equals("1")
                                                ) {
                                                    binding.newDailyDealLayout.visibility =
                                                        View.VISIBLE
                                                    binding.llPrice.visibility = View.GONE
                                                    binding.regularPriceTv.text =
                                                        ("" + objProductDetails.optString("productCurrency") + objProductDetails.optString(
                                                            "productPrice"
                                                        ))
                                                    binding.ddLoyaltyPriceTv.text =
                                                        objProductDetails.optString("productCurrency") + objProductDetails.optString(
                                                            "productFinalPrice"
                                                        )
                                                    binding.offPerchentageTv.text =
                                                        objProductDetails.optString("productDiscountPercentage") + " OFF"

                                                } else {
                                                    binding.newDailyDealLayout.visibility =
                                                        View.GONE
                                                    binding.llPrice.visibility = View.VISIBLE

                                                    if (objProductDetails.optString("productDiscountPercentageEnabled")
                                                            .equals(
                                                                "0",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("priceEnabledId")
                                                            .equals(
                                                                "0",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("discountPriceEnabledId")
                                                            .equals("0", ignoreCase = true)
                                                    ) {
                                                        binding.llPrice.visibility = View.GONE
                                                    } else if (objProductDetails.optString("productDiscountPercentageEnabled")
                                                            .equals(
                                                                "0",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("priceEnabledId")
                                                            .equals(
                                                                "1",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("discountPriceEnabledId")
                                                            .equals("0", ignoreCase = true)
                                                    ) {
                                                        binding.llPrice.visibility = View.VISIBLE
                                                        binding.tvFinalPrice.visibility =
                                                            View.VISIBLE
                                                        binding.tvFinalPrice.text =
                                                            objProductDetails.optString("productCurrency") + objProductDetails.optString(
                                                                "productPrice"
                                                            )

                                                    } else if (objProductDetails.optString("productDiscountPercentageEnabled")
                                                            .equals(
                                                                "1",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("priceEnabledId")
                                                            .equals(
                                                                "1",
                                                                ignoreCase = true
                                                            ) && objProductDetails.optString("discountPriceEnabledId")
                                                            .equals("1", ignoreCase = true)
                                                    ) {
                                                        binding.llPrice.visibility = View.VISIBLE
                                                        binding.tvFinalPrice.text =
                                                            objProductDetails.optString("productCurrency") + objProductDetails.optString(
                                                                "productFinalPrice"
                                                            )
                                                        binding.tvDscPrice.paintFlags =
                                                            binding.tvDscPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                                        binding.tvDscPrice.text =
                                                            objProductDetails.optString("productCurrency") + objProductDetails.optString(
                                                                "productPrice"
                                                            )
                                                        binding.tvDiscount.text =
                                                            objProductDetails.optString("productDiscountPercentage") + " OFF"
                                                    }
                                                }
                                                Log.e("Ob", "12")

                                                if (objProductDetails.optString("dealFavourite")
                                                        .equals("1", ignoreCase = true)
                                                ) {

                                                    binding.ivHeart.visibility = View.GONE
                                                    binding.ivLike.visibility = View.VISIBLE

                                                    // binding.ivHeart.setImageDrawable(ContextCompat.getDrawable(this@LatestProductDetails, R.drawable.heartfulled))

                                                } else {
                                                    binding.ivLike.visibility = View.GONE
                                                    binding.ivHeart.visibility = View.VISIBLE
                                                }
                                                Log.e("Ob", "113")

                                            }
                                            ErrorMessage.E("mDealList>>" + deallist_ob.length());
                                            if (deallist_ob.length()>0){
                                                binding.dealLayout.visibility==View.VISIBLE
                                            }else {
                                                binding.dealLayout.visibility==View.GONE
                                            }
                                            if (deallist_ob.length() > 0) {
                                                for (l in 0 until deallist_ob.length()) {
                                                    ErrorMessage.E("mDealList>12>" + l);
                                                    val objs: JSONObject =
                                                        deallist_ob.optJSONObject(l)
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
                                                    mDealList.add(sp)
                                                }
                                            }


                                            ErrorMessage.E("mDealList>>" + mDealList.size);


                                            val side_rv_adapter = DealList_Adapter(
                                                this@LatestProductDetails,
                                                mDealList,
                                                0
                                            )



                                            binding.rvOffer.setLayoutManager(
                                                LinearLayoutManager(
                                                    this@LatestProductDetails,
                                                    RecyclerView.HORIZONTAL,
                                                    false
                                                )
                                            )


                                            binding.rvOffer.setNestedScrollingEnabled(false)
                                            binding.rvOffer.setItemViewCacheSize(
                                                deallist_ob.length()
                                            )
                                            binding.rvOffer.setAdapter(side_rv_adapter)
                                            side_rv_adapter.notifyDataSetChanged()
                                        }
                                    } catch (e: java.lang.Exception) {
                                        Log.e("Eqwqx", e.toString())
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(
                                            KeyConstant.KEY_STATUS
                                        ), ignoreCase = true
                                    )
                                ) {
                                    if(dialogManager!=null){
                                        dialogManager.stopProcessDialog()}
                                    /*AppUtil.showMsgAlert(
                                        binding.tvDealName,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )*/
                                    runOnUiThread(
                                        object : Runnable {
                                            override fun run() {
                                                binding.invalidDetailsView.visibility = View.VISIBLE
                                                infoPopup(resp.optString(KeyConstant.KEY_MESSAGE))
                                            }
                                        }
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tvDealName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.tvDealName, MESSAGE_INTERNET_CONNECTION)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {

        when (v.id) {

            R.id.tvAgentName -> {
                binding.tvVisit.performClick()
            }

            R.id.ivshared -> {

                ErrorMessage.E("toating"+agentUrl)
                if(agentUrl!=null && agentUrl!="") {
                    AppUtil.shareIntent(this@LatestProductDetails, agentUrl)
                }

                else{
//                    AppUtil.showMsgAlert(
//                        ,
//                        MessageConstant.MESSAGE_INTERNET_CONNECTION
//                    )
                    ErrorMessage.T(this@LatestProductDetails,""+ MESSAGE_INTERNET_CONNECTION)

                }

            }

            R.id.llRoute -> {
                /*Log.e("Map", mapURL)*/

                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + MainActivity.userLat + "," + MainActivity.userLang + "&daddr=" + lat + "," + lng + "")
                )
                startActivity(intent)

            }

            R.id.btnRedeem -> {

                if (binding.btnRedeem.text.toString().trim()
                        .equals("Check Availability", ignoreCase = true)
                ) {
                    showComingSoonPopUp()
                } else if (binding.btnRedeem.text.toString().trim()
                        .equals("Redeemed", ignoreCase = true)
                ) {
                    showRedeemedPOPUp()
                } else if (binding.btnRedeem.text.toString().trim()
                        .equals("ORDER NOW", ignoreCase = true)
                ) {
                    startActivity(
                        Intent(this@LatestProductDetails, Webview::class.java)
                            .putExtra("url", orderOnlineLink)
                            .putExtra("title", "ORDER NOW")
                            .putExtra("type", "non_direct")
                    )
                } else if (binding.btnRedeem.text.toString().trim()
                        .equals("LOCKED", ignoreCase = true)
                ) {
                    showRedeemedLOCKPOPUp()
                } else {

//                    ErrorMessage.E("showQRCodeDialog<><> ${dealUUID}")

                    if (!dealUUID.equals("", ignoreCase = true)) {
                        val myBitmap = QRCode.from(dealUUID).bitmap()

                        try {
                            showQRCodeDialog(
                                binding.tvDealName.text.toString().trim({ it <= ' ' }),
                                myBitmap
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            R.id.btnScanQR -> {

                val dialog1 = Dialog(this@LatestProductDetails, R.style.NewDialog)
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog1.setContentView(R.layout.dialogredeemqr)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog1.window!!.attributes)
                lp.width = WindowManager.LayoutParams.FILL_PARENT
                lp.height = WindowManager.LayoutParams.FILL_PARENT
                dialog1.window!!.attributes = lp

                val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
                val llYes = dialog1.findViewById<LinearLayout>(R.id.llYes)
                val llNo = dialog1.findViewById<LinearLayout>(R.id.llNo)


                llNo.setOnClickListener {
                    dialog1.dismiss()
                }

                llYes.setOnClickListener {
                    dialog1.dismiss()

                    startActivity(
                        Intent(this@LatestProductDetails, ScanQrProductDetails::class.java)
                            .putExtra("dealUUID", dealUUID)
                            .putExtra("agentId", agentId)
                            .putExtra("type", "non_direct")
                            .putExtra("product_id", productId)
                            .putExtra("pos", pos)
                    )
                }

                ivClose.setOnClickListener { dialog1.dismiss() }
                dialog1.show()
            }


            R.id.ivBack -> {
                if (isChange && type.equals("non_direct")) {
                    var intent = intent

                    intent.putExtra("pos", pos)  //here u can pass data to previous activity
                    intent.putExtra("fav", isFav)
                    intent.putExtra("id", productId)
                    intent.putExtra("mainPos", mainPos)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else if (type.equals("direct")) {
                    startActivity(Intent(this@LatestProductDetails, SplashActivity::class.java))
                } else if (type.equals("exclusive_popup")) {
                   finish()
                    val bundle = Bundle()
                    bundle.putString("return_back", "home")
                    ErrorMessage.I/*_clear*/(this@LatestProductDetails, ExclusiveDeals::class.java,bundle)
                } else {
                    finish()
                }
            }
            R.id.ivHeart -> {

                //likeProduct(dealId)
                ErrorMessage.E("########### PRODUCT ID >>"+productId);
                likeProduct(productId)
            }

            R.id.ivLike -> {
                commonPopup(dealId)
            }

            R.id.tvVisit -> {

                val intent = Intent(this@LatestProductDetails, New_AgentDetails::class.java)
                intent.putExtra("direct", "false")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
                startActivity(intent)
            }
        }
    }

    private fun commonPopup(productId: String) {
        /**
         * Show Dialog....
         */
        val dialog1 = Dialog(this@LatestProductDetails)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp

        val contentText = dialog1.findViewById(R.id.popup_content) as TextView
        contentText.text = "Are you sure you want to remove from favourites?"
        val btnNo = dialog1.findViewById(R.id.popup_no_btn) as TextView
        btnNo.text = "No"
        val btnOk = dialog1.findViewById(R.id.popup_yes_btn) as TextView
        btnOk.text = "Yes"

        //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
        dialog1.setCancelable(false)
        dialog1.show()

        try {
            btnOk.setOnClickListener {
                updateFavoriteProduct(favProId)
                dialog1.dismiss()
            }
            //
            btnNo.setOnClickListener { dialog1.dismiss() }

        } catch (e: Exception) {
        }
    }

    private fun updateFavoriteProduct(favProId: String) {
        if (AppUtil.isNetworkAvailable(this@LatestProductDetails)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this@LatestProductDetails, "", false, null)
            val call = AppConfig.api_Interface().updateFavouriteDeal(favProId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            Log.e("proi", favProId)
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ResposneUpdateLike", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)
                            ) {

                                (this@LatestProductDetails as Activity).runOnUiThread {

                                    binding.ivHeart.visibility = View.VISIBLE
                                    binding.ivLike.visibility = View.GONE

                                    isChange = true
                                    isFav = "0"
                                    dialogManager.stopProcessDialog()

                                }

                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(
                                            KeyConstant.KEY_STATUS
                                        ), ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding.tvDealName,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding.tvDealName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tvDealName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }

    private fun likeProduct(productId: String) {
        if (AppUtil.isNetworkAvailable(this@LatestProductDetails)) {
            Log.e("DealId", productId)
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this@LatestProductDetails, "", false, null)
            val call = AppConfig.api_Interface().updateFavouriteDeal(productId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ResposneLike", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)
                            ) {

                                runOnUiThread {
                                    isChange = true
                                    isFav = "1"
                                    binding.ivHeart.visibility = View.GONE
                                    binding.ivLike.visibility = View.VISIBLE
                                    dialogManager.stopProcessDialog()

                                }

                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(
                                            KeyConstant.KEY_STATUS
                                        ), ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding.tvDealName,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding.tvDealName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tvDealName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.tvDealName, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showQRCodeDialog(title: String, bitmap: Bitmap) {


        val dialog1 = Dialog(this@LatestProductDetails, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_show_qr_code)
        dialog1.window!!.setBackgroundDrawableResource(R.drawable.bg_qrcode_popup)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val tvTitle = dialog1.findViewById(R.id.ticket_title) as TextView
        tvTitle.text = title

        val ivQrCode = dialog1.findViewById(R.id.iv_qr_code) as ImageView
        ivTick = dialog1.findViewById(R.id.ivTick)
        tvSuccess = dialog1.findViewById(R.id.tvSuccess)

        ivQrCode.setImageBitmap(bitmap)


        val tvClosePopup = dialog1.findViewById(R.id.tv_close) as TextView
        tvClosePopup.setOnClickListener { dialog1.dismiss() }

        dialog1.show()
    }

    override fun onBackPressed() {
        Log.e("tyoe", type)

        if (isChange && type.equals("non_direct")) {
            var intent = intent

            intent.putExtra("pos", pos)  //here u can pass data to previous activity
            intent.putExtra("fav", isFav)
            intent.putExtra("id", productId)
            intent.putExtra("agentId", agentId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            setResult(RESULT_OK, intent)
            finish()

        }else if (type.equals("exclusive_popup")) {
            finish()
            val bundle = Bundle()
            bundle.putString("return_back", "home")
            ErrorMessage.I/*_clear*/(this@LatestProductDetails, ExclusiveDeals::class.java,bundle)
        }else if (type.equals("direct")) {
            startActivity(Intent(this@LatestProductDetails, SplashActivity::class.java))
            finish()
        } else {
            finish()
        }
        super.onBackPressed()
        Log.e("tyo22e", type)



    }

    fun showComingSoonPopUp() {
        val dialog1 = Dialog(this@LatestProductDetails, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.ddredeemdialog)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { dialog1.dismiss() }

        val tvDays = dialog1.findViewById<TextView>(R.id.tvDays)
        val tvDate = dialog1.findViewById<TextView>(R.id.tvDate)
        val tvTime = dialog1.findViewById<TextView>(R.id.tvTime)

        tvDate.text = dealRedeemAlert
        tvDays.text = dealRedeemAlertDate
        tvTime.text = dealRedeemAlertTime

        try {
            dialog1.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    fun showRedeemedPOPUp() {
        val dialog1 = Dialog(this@LatestProductDetails, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.redeemeddialog)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { dialog1.dismiss() }

        val tvDate = dialog1.findViewById<TextView>(R.id.tvDate)
        val tvTime = dialog1.findViewById<TextView>(R.id.tvTime)
        var time = DateUtil.localToUTCTime(dealRedeemAlertDate + " " + dealRedeemAlertTime)
        tvDate.text = dealRedeemAlertDate
        tvTime.text = dealRedeemAlertTime

        try {
            dialog1.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    fun showRedeemedLOCKPOPUp() {
        val dialog1 = Dialog(this@LatestProductDetails, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.lockeddialog)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { dialog1.dismiss() }

        val tvDeals = dialog1.findViewById<TextView>(R.id.tvDeals)

        tvDeals.text = dealRedeemLockedAlert

        try {
            dialog1.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("OnaC", requestCode.toString())
        if (resultCode == Activity.RESULT_OK && requestCode == 80) {


            val dialog1 = Dialog(this@LatestProductDetails)
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1.setContentView(R.layout.popupwithok)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog1.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog1.window!!.attributes = lp

            val contentText = dialog1.findViewById(R.id.popup_content) as TextView
            contentText.text = data!!.getStringExtra("message")
            val btnOk = dialog1.findViewById(R.id.popup_yes_btn) as TextView
            btnOk.text = "OK"

            dialog1.setCancelable(false)
            dialog1.show()

            try {
                btnOk.setOnClickListener {
                    dialog1.dismiss()
                    startActivity(
                        Intent(this@LatestProductDetails, LatestProductDetails::class.java)
                            .putExtra("agentId", agentId)
                            .putExtra("type", "non_direct")
                            .putExtra("product_id", productId)
                            .putExtra("pos", pos)
                    )
                }

            } catch (e: Exception) {
            }
        }
        else if (resultCode == RESULT_OK && requestCode == 180) {
            getProductDetails()
        }
    }

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
    fun moveToDetails(productId1: String?, agentId1: String?, pos: Int, mainPos: Int) {
        ErrorMessage.E("productId>>"+productId1);
        ErrorMessage.E("agentId>>"+agentId);
        if (productId1 != null) {
            productId = productId1.toString()
        };
        if (agentId1 != null) {
            agentId = agentId1.toString()
        };
        getProductDetails()
    }
    fun refreshFavToDetails(productId1: String?) {
        ErrorMessage.E("productId>>"+productId1);

        if (productId1 != null) {
            if(productId == productId1.toString()){
                getProductDetails()
            }
        }
    }
    private fun infoPopup(responseMsg: String) {
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
            btnOk.setOnClickListener {
                dialog1.dismiss()
                finish()
            }
        } catch (e: java.lang.Exception) {
        }
    }


}