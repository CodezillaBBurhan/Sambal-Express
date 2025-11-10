package sambal.mydd.app.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.content.Intent
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import android.content.ClipData
import sambal.mydd.app.utils.AppUtil
import android.view.WindowManager
import sambal.mydd.app.utils.UserAccount
import android.text.TextWatcher
import android.text.Editable
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import com.google.gson.Gson
import sambal.mydd.app.adapter.Key_Information_Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.graphics.Typeface
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.ShortDynamicLink
import sambal.mydd.app.utils.ErrorMessage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.Manifest.permission
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.transition.Transition
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityReferFriendBinding
import sambal.mydd.app.models.Refer_Friends_Model.Example
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URLDecoder
import java.util.*

class Refer_FriendActivity : BaseActivity() {
    var example: Example? = null
    var submit_btn: Button? = null
    var bottom_content_tv: TextView? = null
    private var shortLinks = ""
    private var Image_bitmap: Bitmap? = null
    private val PERMISSION_REQUEST_CODE = 1111
    var screenshotUri: Uri? = null
    private var binding: ActivityReferFriendBinding? = null
    var agentId: String? = ""

    override val contentResId: Int
        get() = R.layout.activity_refer__friend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer__friend)
        setToolbarWithBackButton_colorprimary("Recommend a Friend")
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
            } else {
                requestPermission()
            }
        }
        try {
            val bundle = intent.extras
            if (bundle != null) {
                val mainIntent = intent
                if (mainIntent != null && mainIntent.data != null && mainIntent.data!!
                        .scheme == "https"
                ) {
                    val data = mainIntent.data
                    val str = data.toString()
                    FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(intent)
                        .addOnSuccessListener(this@Refer_FriendActivity) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                            var deepLink: Uri? = null
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.link
                                Log.e("deepLink", "" + deepLink.toString())
                                try {
                                    var afterDecode =
                                        URLDecoder.decode(deepLink.toString(), "UTF-8")
                                    afterDecode =
                                        afterDecode.substring(afterDecode.lastIndexOf("?"))
                                    val separated: Array<String?> =
                                        afterDecode.split("&".toRegex()).toTypedArray()
                                    if (separated[0] != null) {
                                        val product_id = separated[0]!!
                                            .split("=".toRegex()).toTypedArray()
                                        Log.e("deepLink product_id>>", "" + product_id[1])
                                        binding!!.enterPromoCodeEtv.setText(product_id[1])
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        .addOnFailureListener(this) { e ->
                            Log.e("TAG",
                                "getDynamicLink:onFailure",
                                e)
                        }
                    //  Get_Coupon_detail(str.substring(str.lastIndexOf("/") + 1));
                    Refer_Friend()
                } else {
                    if (bundle.getString("check") == "Refer") {
                        binding!!.enterPromoCodeEtv.setText(bundle.getString("Code"))
                        Refer_Friend()
                    } else if (bundle.getString("check") == "my_wallet") {
                        binding!!.earningPointsLayout.visibility = View.GONE
                        binding!!.referallCodeLayout.visibility = View.GONE
                        binding!!.appliedPromoCodeSectionLayout.visibility = View.GONE
                        binding!!.agentNameTv.visibility = View.VISIBLE
                        binding!!.promoCodeLayout.visibility = View.VISIBLE
                        agentId = bundle.getString("id")
                        Recommanded_Friend()
                    }
                }
            } else {
                Refer_Friend()
            }
        } catch (w: Exception) {
        }
        binding!!.shareBtn.setOnClickListener { Share_Methode() }
        binding!!.referralCodeTv.setOnClickListener {
            try {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", binding!!.referralCodeTv.text.toString())
                clipboard.setPrimaryClip(clip)
                AppUtil.showMsgAlert(binding!!.tvAvailable, "Referral code copied")
            } catch (e: Exception) {
            }
        }
        binding!!.referralURLTv.setOnClickListener {
            try {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", binding!!.referralURLTv.text.toString())
                clipboard.setPrimaryClip(clip)
                AppUtil.showMsgAlert(binding!!.tvAvailable, "Referral url copied")
            } catch (e: Exception) {
            }
        }
        binding!!.llRedeem.setOnClickListener { Share_PopUP() }
        binding!!.applyPromoCodeBtn.setOnClickListener {
            if (binding!!.applyPromoCodeBtn.text.toString() == "Apply") {
                if (binding!!.enterPromoCodeEtv.text.toString() != "") {
                    Apply_PromoCode(binding!!.enterPromoCodeEtv.text.toString())
                } else {
                    AppUtil.showMsgAlert(binding!!.tvAvailable, "Promo Code field is not Empty !")
                }
            } else {
                Remove_PromoCode(binding!!.enterPromoCodeEtv.text.toString())
            }
        }
    }

    fun Share_PopUP() {
        val dialog = Dialog(this@Refer_FriendActivity)
        dialog.setContentView(R.layout.share_refer_popup)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        val price_tv = dialog.findViewById<TextView>(R.id.price_tv)
        bottom_content_tv = dialog.findViewById(R.id.bottom_content_tv)
        val bank_detail_layout = dialog.findViewById<LinearLayout>(R.id.bank_detail_layout)
        val account_detail_submit_btn = dialog.findViewById<Button>(R.id.account_detail_submit_btn)
        val account_name_etv = dialog.findViewById<EditText>(R.id.account_name_etv)
        val account_number_etv = dialog.findViewById<EditText>(R.id.account_number_etv)
        val sort_code_first_etv = dialog.findViewById<EditText>(R.id.sort_code_first_etv)
        val sort_code_second_etv = dialog.findViewById<EditText>(R.id.sort_code_second_etv)
        val sort_code_third_etv = dialog.findViewById<EditText>(R.id.sort_code_third_etv)
        val email_address_etv = dialog.findViewById<EditText>(R.id.email_address_etv)
        try {
            if (example!!.response.userRefer[0].voucherList.size > 0) {
                price_tv.text =
                    example!!.response.userRefer[0].voucherList[0].currency + " " + example!!.response.userRefer[0].voucherList[0].voucherPrice
            }
        } catch (e: Exception) {
        }
        submit_btn = dialog.findViewById(R.id.submit_btn)
        val cancel_img = dialog.findViewById<ImageView>(R.id.cancel_img)
        cancel_img.setOnClickListener { dialog.dismiss() }
        try {
            if (example!!.response.userRefer[0].voucherList[0].voucherCaimStatus == 1) {
                submit_btn!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                submit_btn!!.setEnabled(true)
                bottom_content_tv!!.setVisibility(View.GONE)
            } else if (example!!.response.userRefer[0].voucherList[0].voucherCaimStatus == 2) {
                submit_btn!!.setBackgroundColor(resources.getColor(R.color.background_color))
                submit_btn!!.setEnabled(false)
                bottom_content_tv!!.setVisibility(View.VISIBLE)
                bottom_content_tv!!.setText(example!!.response.userRefer[0].voucherList[0].voucherClainText)
            }
        } catch (e: Exception) {
        }
        submit_btn!!.setOnClickListener(View.OnClickListener {
            try {
                if (example!!.response.userRefer[0].voucherList[0].voucherCaimStatus == 1) {
                    bank_detail_layout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
            }
        })
        account_detail_submit_btn.setOnClickListener {
            try {
                if (UserAccount.isEmpty(account_name_etv,
                        account_number_etv,
                        sort_code_first_etv,
                        sort_code_second_etv,
                        sort_code_third_etv,
                        email_address_etv)
                ) {
                    if (example!!.response.userRefer[0].voucherList[0].voucherCaimStatus == 1) {
                        dialog.dismiss()
                        Share_Voucher(example!!.response.userRefer[0].voucherList[0].voucherUUID,
                            account_name_etv.text.toString(),
                            account_number_etv.text.toString(),
                            sort_code_first_etv.text.toString() + "-" + sort_code_second_etv.text.toString() + "-" + sort_code_third_etv.text.toString(),
                            email_address_etv.text.toString())
                    }
                } else {
                    UserAccount.EditTextPointer?.error = "This Field Can't be Empty !"
                    UserAccount.EditTextPointer?.requestFocus()
                }
            } catch (e: Exception) {
            }
        }
        sort_code_first_etv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (sort_code_first_etv.text.toString().length == 2) {
                    sort_code_second_etv.isFocusable = true
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        sort_code_second_etv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (sort_code_second_etv.text.toString().length == 2) {
                    sort_code_third_etv.isFocusable = true
                } else if (sort_code_second_etv.text.toString().length == 0) {
                    sort_code_first_etv.isFocusable = true
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        sort_code_third_etv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (sort_code_third_etv.text.toString().length == 0) {
                    sort_code_second_etv.isFocusable = true
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialog.show()
    }

    fun Confirmation_PopUP() {
        val dialog = Dialog(this@Refer_FriendActivity)
        dialog.setContentView(R.layout.payment_confirmlayout)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        val cancel_img = dialog.findViewById<ImageView>(R.id.cancel_img)
        cancel_img.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@Refer_FriendActivity, "")
        } catch (e: Exception) {
        }
    }

    private fun Refer_Friend() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().referFriend
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("ResssPor", response.toString())
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            binding!!.loadingDefaultView.visibility = View.GONE
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                example = gson.fromJson(obj.toString(), Example::class.java)
                                if (example!!.getResponse().userRefer[0].referTitle != null && example!!.getResponse().userRefer[0].referTitle != "") {
                                    binding!!.referTitleTv.visibility = View.VISIBLE
                                    binding!!.referTitleTv.text =
                                        "" + example!!.getResponse().userRefer[0].referTitle
                                } else {
                                    binding!!.referTitleTv.visibility = View.GONE
                                }
                                binding!!.referDescriptionTv.text =
                                    "" + example!!.getResponse().userRefer[0].referDescription
                                binding!!.referEarningsTv.text =
                                    "" + example!!.getResponse().userRefer[0].referEarnings
                                binding!!.referBalanceTextTv.text =
                                    "" + example!!.getResponse().userRefer[0].referBalanceText
                                binding!!.referralCodeTv.text =
                                    "" + example!!.getResponse().userRefer[0].referralCode
                                // binding.referralURLTv.setText("" + example.getResponse().getUserRefer().get(0).getReferralURL());
                                binding!!.earnedTv.text =
                                    "" + example!!.getResponse().userRefer[0].earned
                                binding!!.shareBtn.visibility = View.VISIBLE


                                if( example!!.getResponse().userRefer[0].referralType!=null && example!!.getResponse().userRefer[0].referralType==1){
                                    binding!!.referTitleHighlightedLayout.visibility=View.VISIBLE
                                }
                                else{
                                    binding!!.referTitleHighlightedLayout.visibility=View.GONE
                                }

                                if (example!!.getResponse().userRefer[0].keyInformation.size > 0) {
                                    val side_rv_adapter =
                                        Key_Information_Adapter(this@Refer_FriendActivity,
                                            example!!.getResponse().userRefer[0].keyInformation)
                                    binding!!.keyInformationRcv.layoutManager = LinearLayoutManager(
                                        this@Refer_FriendActivity,
                                        RecyclerView.VERTICAL,
                                        false)
                                    binding!!.keyInformationRcv.isNestedScrollingEnabled = false
                                    binding!!.keyInformationRcv.setItemViewCacheSize(example!!.getResponse().userRefer[0].keyInformation.size)
                                    binding!!.keyInformationRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                }
                                if (example!!.getResponse().userRefer[0].visitList.size > 0) {
                                    Log.e("tag",
                                        ">>" + example!!.getResponse().userRefer[0].visitList.size)
                                    val total_colour_count =
                                        100 / example!!.getResponse().userRefer[0].visitList.size
                                    binding!!.circleView.blockCount =
                                        example!!.getResponse().userRefer[0].visitList.size
                                    binding!!.circleView.setValue(example!!.getResponse().userRefer[0].visitList.size.toString()
                                        .toFloat())
                                    binding!!.circleView.blockScale = 0.9.toString().toFloat()
                                    binding!!.circleView.maxValue = total_colour_count.toFloat()
                                    val colors = ArrayList<Int>()
                                    for (i in example!!.getResponse().userRefer[0].visitList.indices) {
                                        colors.add(Color.parseColor(example!!.getResponse().userRefer[0].visitList[i].visitColor))
                                    }
                                    if (colors.size == 10) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4],
                                            colors[5],
                                            colors[6],
                                            colors[7],
                                            colors[8],
                                            colors[9])
                                    } else if (colors.size == 9) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4],
                                            colors[5],
                                            colors[6],
                                            colors[7],
                                            colors[8])
                                    } else if (colors.size == 8) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4],
                                            colors[5],
                                            colors[6],
                                            colors[7])
                                    } else if (colors.size == 7) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4],
                                            colors[5],
                                            colors[6])
                                    } else if (colors.size == 6) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4],
                                            colors[5])
                                    } else if (colors.size == 5) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3],
                                            colors[4])
                                    } else if (colors.size == 4) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2],
                                            colors[3])
                                    } else if (colors.size == 3) {
                                        binding!!.circleView.setBarColor(colors[0],
                                            colors[1],
                                            colors[2])
                                    } else if (colors.size == 2) {
                                        binding!!.circleView.setBarColor(colors[0], colors[1])
                                    } else if (colors.size == 1) {
                                        binding!!.circleView.setBarColor(colors[0])
                                    }
                                }
                                if (example!!.getResponse().userRefer[0].voucherList.size > 0) {
                                    binding!!.llVoucher.visibility = View.VISIBLE
                                    binding!!.tvBalance.text =
                                        example!!.getResponse().userRefer[0].voucherList[0].currency + example!!.getResponse().userRefer[0].voucherList[0].voucherPrice
                                    if (example!!.getResponse().userRefer[0].voucherList[0].voucherPrice != null && example!!.getResponse().userRefer[0].voucherList[0].voucherPrice != "") {
                                        binding!!.tvAvailable.visibility = View.VISIBLE
                                        binding!!.tvAvailable.text =
                                            example!!.getResponse().userRefer[0].voucherList[0].voucherText
                                    } else {
                                        binding!!.tvAvailable.visibility = View.GONE
                                    }
                                    if (example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice != null && example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice != "") {
                                        binding!!.tvRedeemedPrice.visibility = View.VISIBLE
                                        binding!!.tvRedeemedPrice.text =
                                            example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedText + " " + example!!.getResponse().userRefer[0].voucherList[0].currency + "" + example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice
                                    } else {
                                        binding!!.tvRedeemedPrice.visibility = View.GONE
                                    }
                                    try {
                                        if (example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemEnabled == 1) {
                                            binding!!.llRedeem.visibility = View.VISIBLE
                                            binding!!.llRedeem.isEnabled = true
                                            binding!!.llRedeem.background =
                                                resources.getDrawable(R.drawable.llyellow)
                                        } else {
                                            binding!!.llRedeem.visibility = View.VISIBLE
                                            binding!!.llRedeem.isEnabled = false
                                            binding!!.llRedeem.background =
                                                resources.getDrawable(R.drawable.llredeemgrey)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("Ex1", e.toString())
                                    }
                                } else {
                                    binding!!.llVoucher.visibility = View.GONE
                                }
                                if (example!!.getResponse().userRefer[0].promocode.size > 0) {
                                    binding!!.appliedPromoCodeLayout.visibility = View.VISIBLE
                                    binding!!.promoCodeTitleTv.visibility = View.VISIBLE
                                    binding!!.promoCodeTitleTv.text =
                                        example!!.getResponse().userRefer[0].promocode[0].promocodeText
                                    binding!!.validePromoCodeDateTv.text =
                                        "Valid until: " + example!!.getResponse().userRefer[0].promocode[0].promocodeExpireDate
                                    val builder = SpannableStringBuilder()
                                    val str3 = SpannableString("Promo code applied: ")
                                    str3.setSpan(ForegroundColorSpan(resources.getColor(R.color.bluesheme)),
                                        0,
                                        str3.length,
                                        0)
                                    str3.setSpan(StyleSpan(Typeface.BOLD), 0, str3.length, 0)
                                    builder.append(str3)
                                    val str6 =
                                        SpannableString(example!!.getResponse().userRefer[0].promocode[0].promocode)
                                    str6.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                                        0,
                                        str6.length,
                                        0)
                                    builder.append(str6)
                                    binding!!.appliedPromoCodeTv.setText(builder,
                                        TextView.BufferType.SPANNABLE)
                                    binding!!.enterPromoCodeEtv.isEnabled = false
                                    binding!!.enterPromoCodeEtv.setText(example!!.getResponse().userRefer[0].promocode[0].promocode)
                                    binding!!.enterPromoCodeEtv.setTextColor(resources.getColor(R.color.shader_color))
                                    binding!!.applyPromoCodeBtn.setBackgroundColor(resources.getColor(
                                        R.color.red))
                                    binding!!.applyPromoCodeBtn.text = "REMOVE"
                                } else {
                                    binding!!.appliedPromoCodeLayout.visibility = View.GONE
                                    binding!!.promoCodeTitleTv.visibility = View.GONE
                                }
                                binding!!.referralURLTv.text = "" + example!!.getResponse().userRefer[0].referralShareURL
                                binding!!.ivQR.setImageBitmap(generateQR(
                                    example!!.getResponse().userRefer[0].getReferralShareURL()))
                               /* Generate_Bitmap(example!!.getResponse().userRefer[0].referralShareURL)*/
                               /* val dynamicLink = FirebaseDynamicLinks.getInstance()
                                    .createDynamicLink() *//*refer?promocode=*//*
                                    .setLink(Uri.parse("https://ddpoints.page.link/" + example!!.getResponse().userRefer[0].referralCode))
                                    .setDomainUriPrefix("https://ddpoints.page.link")
                                    .setAndroidParameters(AndroidParameters.Builder("sambal.mydd.app")
                                        .build())
                                    .setIosParameters(IosParameters.Builder("com.dealdio.iosapp")
                                        .setAppStoreId("1380777980").build())
                                    .buildDynamicLink()
                                val dynamicLinkUri = dynamicLink.uri
                                Log.e("dynamicLinkUri", dynamicLinkUri.toString() + "")
                                val shortLinkTask =
                                    FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLongLink(dynamicLinkUri)
                                        .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                                        .addOnCompleteListener(this@Refer_FriendActivity) { task ->
                                            if (task.isSuccessful) {
                                                // Short link created
                                                val shortLink = task.result.shortLink
                                                shortLinks = shortLink.toString()
                                                binding!!.referralURLTv.text = "" + shortLinks
                                                // Bitmap bitmap = QRCode.from(String.valueOf(shortLinks)).bitmap();
                                                binding!!.ivQR.setImageBitmap(generateQR(
                                                    shortLinks))
                                            } else {
                                                Log.e("Errro", task.exception.toString() + "")
                                            }
                                        }*/
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    private fun Share_Voucher(
        voucherUUID: String,
        account_name: String,
        account_number: String,
        sort_code: String,
        email: String
    ) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface()
                .claimMyMoneyV1(voucherUUID, account_name, account_number, sort_code, email)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                Confirmation_PopUP()
                                example!!.response.userRefer[0].voucherList[0].voucherCaimStatus = 2
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAvailable, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    private fun Apply_PromoCode(promo_code: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().applyReferralPromocode(promo_code)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                                binding!!.enterPromoCodeEtv.isEnabled = false
                                binding!!.enterPromoCodeEtv.setTextColor(resources.getColor(R.color.shader_color))
                                binding!!.applyPromoCodeBtn.setBackgroundColor(resources.getColor(R.color.red))
                                binding!!.applyPromoCodeBtn.text = "REMOVE"
                                Refer_Friend()
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAvailable, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    private fun Remove_PromoCode(promo_code: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().removeReferralPromocode(promo_code)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                                binding!!.enterPromoCodeEtv.isEnabled = true
                                binding!!.enterPromoCodeEtv.setText("")
                                binding!!.enterPromoCodeEtv.setTextColor(resources.getColor(R.color.black))
                                binding!!.applyPromoCodeBtn.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                                binding!!.applyPromoCodeBtn.text = "Apply"
                                binding!!.appliedPromoCodeLayout.visibility = View.GONE
                                binding!!.promoCodeTitleTv.visibility = View.GONE
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAvailable, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    fun Share_Methode() {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            share.putExtra(Intent.EXTRA_TEXT, """${example!!.response.userRefer[0].referralText} 
${binding!!.referralURLTv.text}""")
            /*  share.putExtra(Intent.EXTRA_SUBJECT, example.getResponse().getUserRefer().get(0).getReferralText());*/startActivity(
                Intent.createChooser(share, "MyDD Points Google Playstore URL"))
        } catch (e: Exception) {
        }
    }

    fun Generate_Bitmap(url: String?) {
        Glide.with(applicationContext)
            .asBitmap().load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : SimpleTarget<Bitmap?>(250, 250) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    Image_bitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                }
            })
    }

    fun share_item(url: String?, context: Context?) {
        if (Image_bitmap != null) {
            val path =
                MediaStore.Images.Media.insertImage(this@Refer_FriendActivity.contentResolver,
                    Image_bitmap,
                    "share",
                    null)
            screenshotUri = Uri.parse(path)
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                intent.putExtra(Intent.EXTRA_TEXT,
                    """${example!!.response.userRefer[0].referralText} 
${binding!!.referralURLTv.text}""")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.type = "image/*"
                startActivity(Intent.createChooser(intent, "share via"))
            } catch (e: Exception) {
                Log.e("Burhan>", "Exception$e")
            }
        } else {
            Glide.with(applicationContext)
                .asBitmap().load(url).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : SimpleTarget<Bitmap?>(250, 250) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        if (checkPermission()) {
                            Log.e("Burhan>", "checkPermission is come")
                            try {
                                val intent = Intent(Intent.ACTION_SEND)
                                // intent.putExtra(Intent.EXTRA_TEXT, "Hey view/download this image");
                                val path =
                                    MediaStore.Images.Media.insertImage(this@Refer_FriendActivity.contentResolver,
                                        resource,
                                        "share",
                                        null)
                                val screenshotUri = Uri.parse(path)
                                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                                //   intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.coupon_share_meassage)+" "+homeCoupon.getCompanyName());
                                // intent.putExtra(Intent.EXTRA_TEXT,  context.getResources().getString(R.string.coupon_share_meassage)+" "+homeCoupon.getCompanyName()+"\n\n"+"https://meezah.com/" + BuildConfig.APPLICATION_ID + "/" + Coupon_id+"\n\n"+context.getResources().getString(R.string.coupon_share_discribtion));
                                intent.putExtra(Intent.EXTRA_TEXT,
                                    """${example!!.response.userRefer[0].referralText} 
${binding!!.referralURLTv.text}""")
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                intent.type = "image/*"
                                startActivity(Intent.createChooser(intent, "share via"))
                            } catch (e: Exception) {
                                Log.e("Burhan>", "Exception$e")
                            }
                        } else {
                            Log.e("Burhan>", "requestPermission is come")
                            requestPermission()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onLoadStarted(placeholder: Drawable?) {
                        Toast.makeText(applicationContext, "Waiting", Toast.LENGTH_SHORT).show()
                        super.onLoadStarted(placeholder)
                    }
                })
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE, " "),
            PERMISSION_REQUEST_CODE)
    }

    private fun checkPermission(): Boolean {
        val result3 =
            ContextCompat.checkSelfPermission(applicationContext, permission.READ_EXTERNAL_STORAGE)
        val result4 =
            ContextCompat.checkSelfPermission(applicationContext, permission.WRITE_EXTERNAL_STORAGE)
        return result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> try {
                if (grantResults.size > 0) {
                    val ExtStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED
                    val WriteStorageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED
                    if (ExtStorageAccepted && WriteStorageAccepted) {
                        //   ErrorMessage.T(SplashActivity.this, "Permission Granted, Now you can access location data and camera.");
                    } //Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    else {
                        //   ErrorMessage.T(SplashActivity.this, "Permission Denied, You cannot access location data and camera.");
                        //   Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(arrayOf(permission.READ_EXTERNAL_STORAGE,
                                        permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                                }
                            }
                        }
                        return
                    }
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun Recommanded_Friend() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().getAgentRecommendFriend(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            binding!!.loadingDefaultView.visibility = View.GONE
                            Log.e("Recommanded_Friend", obj.toString())
                            dialogManager.stopProcessDialog()
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                example = gson.fromJson(obj.toString(), Example::class.java)
                                if (example!!.getResponse().userRefer[0].referTitle != null && example!!.getResponse().userRefer[0].referTitle != "") {
                                    binding!!.referTitleTv.visibility = View.VISIBLE
                                    binding!!.referTitleTv.text =
                                        "" + example!!.getResponse().userRefer[0].referTitle
                                } else {
                                    binding!!.referTitleTv.visibility = View.GONE
                                }
                                binding!!.referDescriptionTv.text =
                                    "" + example!!.getResponse().userRefer[0].referDescription
                                binding!!.referEarningsTv.text =
                                    "" + example!!.getResponse().userRefer[0].referEarnings
                                binding!!.referBalanceTextTv.text =
                                    "" + example!!.getResponse().userRefer[0].referBalanceText
                                binding!!.referralCodeTv.text =
                                    "" + example!!.getResponse().userRefer[0].referralCode
                                binding!!.earnedTv.text =
                                    "" + example!!.getResponse().userRefer[0].earned
                                binding!!.agentNameTv.text =
                                    "" + example!!.getResponse().userRefer[0].agentName
                                binding!!.promoCodeTv.text =
                                    "" + example!!.getResponse().userRefer[0].referralCode
                                binding!!.shareBtn.visibility = View.VISIBLE
                                binding!!.promoCodeTitleTv.visibility = View.VISIBLE
                                binding!!.promoCodeTitleTv.text =
                                    example!!.getResponse().userRefer[0].referralText
                                if (example!!.getResponse().userRefer[0].keyInformation.size > 0) {
                                    Log.e("KeyInformation",
                                        "<Size>" + example!!.getResponse().userRefer[0].keyInformation.size)
                                    val side_rv_adapter =
                                        Key_Information_Adapter(this@Refer_FriendActivity,
                                            example!!.getResponse().userRefer[0].keyInformation)
                                    binding!!.keyInformationRcv.layoutManager = LinearLayoutManager(
                                        this@Refer_FriendActivity,
                                        RecyclerView.VERTICAL,
                                        false)
                                    binding!!.keyInformationRcv.isNestedScrollingEnabled = false
                                    binding!!.keyInformationRcv.setItemViewCacheSize(example!!.getResponse().userRefer[0].keyInformation.size)
                                    binding!!.keyInformationRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                }
                                try {
                                    if (example!!.getResponse().userRefer[0].visitList.size > 0) {
                                        Log.e("tag",
                                            ">>" + example!!.getResponse().userRefer[0].visitList.size)
                                        val total_colour_count =
                                            100 / example!!.getResponse().userRefer[0].visitList.size
                                        binding!!.circleView.blockCount =
                                            example!!.getResponse().userRefer[0].visitList.size
                                        binding!!.circleView.setValue(example!!.getResponse().userRefer[0].visitList.size.toString()
                                            .toFloat())
                                        binding!!.circleView.blockScale = 0.9.toString().toFloat()
                                        binding!!.circleView.maxValue = total_colour_count.toFloat()
                                        val colors = ArrayList<Int>()
                                        for (i in example!!.getResponse().userRefer[0].visitList.indices) {
                                            colors.add(Color.parseColor(example!!.getResponse().userRefer[0].visitList[i].visitColor))
                                        }
                                        if (colors.size == 10) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4],
                                                colors[5],
                                                colors[6],
                                                colors[7],
                                                colors[8],
                                                colors[9])
                                        } else if (colors.size == 9) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4],
                                                colors[5],
                                                colors[6],
                                                colors[7],
                                                colors[8])
                                        } else if (colors.size == 8) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4],
                                                colors[5],
                                                colors[6],
                                                colors[7])
                                        } else if (colors.size == 7) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4],
                                                colors[5],
                                                colors[6])
                                        } else if (colors.size == 6) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4],
                                                colors[5])
                                        } else if (colors.size == 5) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3],
                                                colors[4])
                                        } else if (colors.size == 4) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2],
                                                colors[3])
                                        } else if (colors.size == 3) {
                                            binding!!.circleView.setBarColor(colors[0],
                                                colors[1],
                                                colors[2])
                                        } else if (colors.size == 2) {
                                            binding!!.circleView.setBarColor(colors[0], colors[1])
                                        } else if (colors.size == 1) {
                                            binding!!.circleView.setBarColor(colors[0])
                                        }
                                    }
                                    if (example!!.getResponse().userRefer[0].voucherList.size > 0) {
                                        binding!!.llVoucher.visibility = View.VISIBLE
                                        binding!!.tvBalance.text =
                                            example!!.getResponse().userRefer[0].voucherList[0].currency + example!!.getResponse().userRefer[0].voucherList[0].voucherPrice
                                        if (example!!.getResponse().userRefer[0].voucherList[0].voucherPrice != null && example!!.getResponse().userRefer[0].voucherList[0].voucherPrice != "") {
                                            binding!!.tvAvailable.visibility = View.VISIBLE
                                            binding!!.tvAvailable.text =
                                                example!!.getResponse().userRefer[0].voucherList[0].voucherText
                                        } else {
                                            binding!!.tvAvailable.visibility = View.GONE
                                        }
                                        if (example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice != null && example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice != "") {
                                            binding!!.tvRedeemedPrice.visibility = View.VISIBLE
                                            binding!!.tvRedeemedPrice.text =
                                                example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedText + " " + example!!.getResponse().userRefer[0].voucherList[0].currency + "" + example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemedPrice
                                        } else {
                                            binding!!.tvRedeemedPrice.visibility = View.GONE
                                        }
                                        try {
                                            if (example!!.getResponse().userRefer[0].voucherList[0].voucherRedeemEnabled == 1) {
                                                binding!!.llRedeem.visibility = View.VISIBLE
                                                binding!!.llRedeem.isEnabled = true
                                                binding!!.llRedeem.background =
                                                    resources.getDrawable(
                                                        R.drawable.llyellow)
                                            } else {
                                                binding!!.llRedeem.visibility = View.VISIBLE
                                                binding!!.llRedeem.isEnabled = false
                                                binding!!.llRedeem.background =
                                                    resources.getDrawable(
                                                        R.drawable.llredeemgrey)
                                            }
                                        } catch (e: Exception) {
                                            Log.e("Ex1", e.toString())
                                        }
                                    } else {
                                        binding!!.llVoucher.visibility = View.GONE
                                    }
                                    if (example!!.getResponse().userRefer[0].promocode.size > 0) {
                                        binding!!.appliedPromoCodeLayout.visibility = View.VISIBLE
                                        binding!!.promoCodeTitleTv.visibility = View.VISIBLE
                                        binding!!.promoCodeTitleTv.text =
                                            example!!.getResponse().userRefer[0].promocode[0].promocodeText
                                        binding!!.validePromoCodeDateTv.text =
                                            "Valid until: " + example!!.getResponse().userRefer[0].promocode[0].promocodeExpireDate
                                        val builder = SpannableStringBuilder()
                                        val str3 = SpannableString("Promo code applied: ")
                                        str3.setSpan(ForegroundColorSpan(resources.getColor(R.color.bluesheme)),
                                            0,
                                            str3.length,
                                            0)
                                        str3.setSpan(StyleSpan(Typeface.BOLD), 0, str3.length, 0)
                                        builder.append(str3)
                                        val str6 =
                                            SpannableString(example!!.getResponse().userRefer[0].promocode[0].promocode)
                                        str6.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                                            0,
                                            str6.length,
                                            0)
                                        builder.append(str6)
                                        binding!!.appliedPromoCodeTv.setText(builder,
                                            TextView.BufferType.SPANNABLE)
                                        binding!!.enterPromoCodeEtv.isEnabled = false
                                        binding!!.enterPromoCodeEtv.setText(example!!.getResponse().userRefer[0].promocode[0].promocode)
                                        binding!!.enterPromoCodeEtv.setTextColor(resources.getColor(
                                            R.color.shader_color))
                                        binding!!.applyPromoCodeBtn.setBackgroundColor(resources.getColor(
                                            R.color.red))
                                        binding!!.applyPromoCodeBtn.text = "REMOVE"
                                    } else {
                                        binding!!.appliedPromoCodeLayout.visibility = View.GONE
                                        binding!!.promoCodeTitleTv.visibility = View.GONE
                                    }
                                } catch (e: Exception) {
                                }
                                /*Generate_Bitmap(example!!.getResponse().userRefer[0].referralImage)*/
                                binding!!.referralURLTv.text = "" + example!!.getResponse().userRefer[0].referralShareURL
                                binding!!.ivQR.setImageBitmap(generateQR(
                                    example!!.getResponse().userRefer[0].getReferralShareURL()))
                               // Generate_Bitmap(example!!.getResponse().userRefer[0].referralShareURL)
                               /* val dynamicLink = FirebaseDynamicLinks.getInstance()
                                    .createDynamicLink() *//*refer?promocode=*//*
                                    .setLink(Uri.parse("https://ddpoints.page.link/" + example!!.getResponse().userRefer[0].referralCode))
                                    .setDomainUriPrefix("https://ddpoints.page.link")
                                    .setAndroidParameters(AndroidParameters.Builder("sambal.mydd.app")
                                        .build())
                                    .setIosParameters(IosParameters.Builder("com.dealdio.iosapp")
                                        .setAppStoreId("1380777980").build())
                                    .buildDynamicLink()
                                val dynamicLinkUri = dynamicLink.uri
                                Log.e("dynamicLinkUri", dynamicLinkUri.toString() + "")
                                val shortLinkTask =
                                    FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLongLink(dynamicLinkUri)
                                        .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                                        .addOnCompleteListener(this@Refer_FriendActivity) { task ->
                                            if (task.isSuccessful) {
                                                // Short link created
                                                val shortLink = task.result.shortLink
                                                shortLinks = shortLink.toString()
                                                binding!!.referralURLTv.text = "" + shortLinks
                                                // Bitmap bitmap = QRCode.from(String.valueOf(shortLinks)).bitmap();
                                                binding!!.ivQR.setImageBitmap(generateQR(
                                                    shortLinks))
                                            } else {
                                                Log.e("Errro", task.exception.toString() + "")
                                            }
                                        }*/
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvAvailable,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            dialogManager.stopProcessDialog()
                            Log.e("Ex1", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAvailable, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    companion object {
        fun generateQR(content: String?): Bitmap? {
            var returnBitmap: Bitmap? = null
            val QR_SIZE = 580
            try {
                val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel?>()
                hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
                val qrCodeWriter = QRCodeWriter()
                val matrix =
                    qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hintMap)
                //int width = matrix.getWidth();
                val width = matrix.height
                val height = matrix.height
                val pixels = IntArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val grey = if (matrix[x, y]) 0x00 else 0xff
                        pixels[y * width + x] = -0x1000000 or 0x00010101 * grey
                    }
                }
                returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                //returnBitmap.set
            } catch (e: Exception) {
                Log.d("LOGTAG", e.toString())
            }
            return returnBitmap
        }
    }
}