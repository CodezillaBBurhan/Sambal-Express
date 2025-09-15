package sambal.mydd.app.activity

//import com.stripe.android.TokenCallback
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import com.stripe.android.view.CardMultilineWidget
import sambal.mydd.app.DealDioApplication
import sambal.mydd.app.R
import sambal.mydd.app.adapter.CountryDialogAdapter
import sambal.mydd.app.beans.GiftCard
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.callback.CountryCallback
import sambal.mydd.app.databinding.ActivityStoreGiftCardBinding
import sambal.mydd.app.utils.ATMEditText
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.ErrorMessage.E
import sambal.mydd.app.utils.PreferenceHelper.Companion.getInstance
import sambal.mydd.app.utils.PubNubChat
import sambal.mydd.app.utils.StatusBarcolor
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class StoreGiftCardActivity() : BaseActivity(), ChatHistoryCallback {
    private var binding: ActivityStoreGiftCardBinding? = null
    var context1: Context? = null
    //var userName_tv: TextView? = null
    //var message_etv: EditText? = null
    var userMobile_etv: EditText? = null
    //var gift_amount_etv: ATMEditText? = null
    var ivTick: ImageView? = null
    var iv_qr_code: ImageView? = null
    var tvPrice: TextView? = null
    var tv_successfull_text: TextView? = null
    private var pubNubChat: PubNubChat? = null
   /* var userId = 0*/
    var giftId = 0
    var GiftVoucherId = 0
    private var dialog: Dialog? = null
    private var dialog3: Dialog? = null
    private var dialogQR: Dialog? = null
    private var selectedCountry = "United Kingdom"
    private var stripePublishKey: String? = null
    private var paymentIntentId: String? = null
    private var stripeSecretKey: String? = null
    var card_img_url: String? = null
    var qrCode_uuid: String? = null
    var giftValue: String? = null
    var giftPrice: String? = null
    /*var userPhoto: String = ""
    var userName: String = ""
    var mobileNo: String = ""*/
    var sellingPrice: String? = null
    var amountStr = ""
    var customer: String = getInstance(DealDioApplication.appContext)!!.userDetail!!.name.toString()
    /*fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(this@StoreGiftCardActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }*/
    var paymentSheet: PaymentSheet? = null
    var customerConfig: PaymentSheet.CustomerConfiguration? = null

    override val contentResId: Int
        get() = R.layout.activity_store_gift_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_gift_card)
        setToolbarWithBackButton_colorprimary("Store Gift Card")
        context1 = this@StoreGiftCardActivity
        paymentSheet = PaymentSheet(
            this@StoreGiftCardActivity,
            PaymentSheetResultCallback { paymentSheetResult: PaymentSheetResult? ->
                if (paymentSheetResult != null) {
                    this@StoreGiftCardActivity.onPaymentSheetResult(
                        paymentSheetResult
                    )
                }
            })

        val bundle = intent.extras
        if (bundle != null) {
            val giftCard = bundle.getSerializable("Data") as GiftCard?
            giftId = giftCard!!.giftId
            ErrorMessage.E("GiftId : $giftId")
            GiftVoucherId = giftCard.giftVoucherId
            card_img_url = giftCard.giftBackgroundImage
            giftPrice = giftCard.currency + giftCard.giftPrice
            sellingPrice = giftCard.currency + giftCard.giftSellingPrice
            try {
                giftValue =
                    giftCard.giftPrice.replace(",".toRegex(), "").replace("^\\s*".toRegex(), "")
                        .trim { it <= ' ' }
                giftCardToServer(giftId, giftCard.giftVoucherId)
            } catch (e: Exception) {
            }
        }
        binding!!.giftBtn.setOnClickListener(View.OnClickListener {
            if (dialog == null) {
                binding!!.giftBtn.isEnabled = false
                giftClickPopup()
            }
        })
        binding!!.redeemBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (dialogQR == null) {
                    binding!!.redeemBtn.isEnabled = false
                    qrCodePopup()
                }
            }
        })
        binding!!.buyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (dialog3 == null) {
                    binding!!.buyBtn.isEnabled = false
                   // buyClickPopup()
                    presentPaymentSheet();
                }
            }
        })
    }

    private fun giftCardToServer(giftID: Int, giftVoucherId: Int) {
        if (AppUtil.isNetworkAvailable(this@StoreGiftCardActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().getGiftCardDetails(giftID, giftVoucherId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()
                            try {
                                val obj = JSONObject(response.body()!!.string())
                                Log.e("getGiftCardDetails", obj.toString())
                                val jsonObject = obj.getJSONObject("response")
                                val arr = jsonObject.getJSONArray("giftCardDetails")
                                val obj1 = arr.getJSONObject(0)
                                if ((obj.getString("error_type") == "200")) {
                                    try {
                                        binding!!.cardMain.visibility = View.VISIBLE
                                        binding!!.tvDetails.visibility = View.VISIBLE
                                        binding!!.agentNameTv.text =
                                            "" + obj1.getString("agentName")
                                        binding!!.agentAddressTv.text =
                                            "" + obj1.getString("agentAddress")
                                        binding!!.giftDescriptionTv.text =
                                            "" + obj1.getString("giftDescription")
                                        binding!!.giftTextremarlTv.text =
                                            "" + obj1.getString("giftTextRemark")
                                        qrCode_uuid = obj1.getString("giftCardUUID")
                                        Glide.with(this@StoreGiftCardActivity)
                                            .load(obj1.getString("giftBackgroundImage"))
                                            .placeholder(
                                                R.drawable.place_holder)
                                            .error(R.drawable.place_holder).into(
                                            binding!!.actCardBgImg)
                                        if ((obj1.getString("giftText") == "Active")) {
                                            binding!!.buttonsLl.visibility = View.VISIBLE
                                            binding!!.buyBtn.visibility = View.GONE
                                        } else {
                                            binding!!.buttonsLl.visibility = View.GONE
                                            binding!!.buyBtn.visibility = View.VISIBLE
                                        }
                                        stripePublishKey = obj1.getString("stripePublishKey")
                                        paymentIntentId = obj1.getString("paymentIntentId")
                                        stripeSecretKey = obj1.getString("stripeSecretKey")

                                        Glide.with(this@StoreGiftCardActivity)
                                            .load(obj1.getString("giftBackgroundImage"))
                                            .placeholder(
                                                R.drawable.place_holder)
                                            .error(R.drawable.place_holder).into(
                                            binding!!.cardBgImg)
                                        Glide.with(this@StoreGiftCardActivity)
                                            .load(obj1.getString("giftBackgroundImage"))
                                            .placeholder(
                                                R.drawable.place_holder)
                                            .error(R.drawable.place_holder).into(
                                            binding!!.actCardBgImg)
                                        binding!!.giftPriceTv.text =
                                            if (obj1.getString("currency") != null) if (!(obj1.getString(
                                                    "currency") == "null")
                                            ) obj1.getString("currency") + obj1.getString("giftPrice") else "" + obj1.getString(
                                                "giftPrice") else "" + obj1.getString("giftPrice")
                                        binding!!.actGiftPriceTv.text =
                                            if (obj1.getString("currency") != null) if (!(obj1.getString(
                                                    "currency") == "null")
                                            ) obj1.getString("currency") + obj1.getString("giftPrice") else "" + obj1.getString(
                                                "giftPrice") else "" + obj1.getString("giftPrice")
                                        //binding.giftPriceTv.setText(giftCard.getCurrency() + giftCard.getGiftPrice());
                                        giftPrice =
                                            obj1.getString("currency") + obj1.getString("giftPrice")
                                        Glide.with(this@StoreGiftCardActivity)
                                            .load(obj1.getString("agentImage")).placeholder(
                                            R.drawable.place_holder).into(
                                            binding!!.agentImage)
                                        Glide.with(this@StoreGiftCardActivity)
                                            .load(obj1.getString("agentImage")).placeholder(
                                            R.drawable.place_holder).into(
                                            binding!!.actAgentImage)
                                        if (tvPrice != null) {
                                            tvPrice!!.text = giftPrice
                                        }
                                        binding!!.discountTv.text = obj1.getString("discountValue")
                                        if (obj1.getString("giftStatus").toInt() == 1) {
                                            binding!!.buyLl.visibility = View.VISIBLE
                                            binding!!.activeLl.visibility = View.GONE
                                            val builder1 = SpannableStringBuilder()
                                            val str1 =
                                                SpannableString(obj1.getString("giftTextRemark"))
                                            val str2 =
                                                SpannableString(obj1.getString("currency") + obj1.getString(
                                                    "giftSellingPrice"))
                                            str1.setSpan(ForegroundColorSpan(Color.parseColor("#111111")),
                                                0,
                                                str1.length,
                                                0)
                                            builder1.append(str1)
                                            str2.setSpan(ForegroundColorSpan(Color.parseColor("#2961F4")),
                                                0,
                                                str2.length,
                                                0)
                                            builder1.append(str2)
                                            binding!!.sellingPriceTv.setText(builder1,
                                                TextView.BufferType.SPANNABLE)
                                        } else if (obj1.getString("giftStatus").toInt() == 2) {
                                            binding!!.buyLl.visibility = View.GONE
                                            binding!!.activeLl.visibility = View.VISIBLE
                                            binding!!.giftTextActiveTv.text =
                                                obj1.getString("giftText").uppercase(
                                                    Locale.getDefault())
                                            binding!!.cardMain.setCardBackgroundColor(Color.parseColor(
                                                obj1.getString("borderColor")))
                                            binding!!.activeLl.setBackgroundResource(R.drawable.card_view_bg)
                                            binding!!.tvExpiriyDate.text =
                                                obj1.getString("giftExpireDate")
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        dialogManager.stopProcessDialog()
                                        Log.e("Exception", e.toString())
                                    }
                                } else {
                                    infoPopup(obj.getString("message"))
                                }
                            } catch (e: Exception) {
                                Log.e("Exception  >>", e.toString())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", e.toString())
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
            ErrorMessage.T(this@StoreGiftCardActivity, "No Internet Found!")
        }
    }

    private fun checkCustomerMobile(userMobile: String) {
        if (AppUtil.isNetworkAvailable(this@StoreGiftCardActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().checkCustomerMobile(userMobile)
            call!!.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()

                            try {
                                val obj = JSONObject(response.body()!!.string())
                                Log.e("checkCustomerMobile", obj.toString())
                                try {
                                    if ((obj.getString("error_type") == "200")) {
                                       /* userName = obj.getString("userName")
                                        userPhoto = obj.getString("userPhoto")
                                        userId = obj.getInt("userId")*/
                                      /*  mobileNo = userMobile*/
                                        dialog!!.dismiss()
                                        submitClickSuccessPopup(userMobile,obj.getString("userName"),obj.getString("userPhoto"),obj.getInt("userId"))
                                    } else if ((obj.getString("error_type") == "202")) {
                                        dialog!!.dismiss()
                                        submitClickFailurePopup(userMobile)
                                    } else {
                                        infoPopup(obj.getString("message"))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dialogManager.stopProcessDialog()
                                    Log.e("Exception", e.toString())
                                }
                            } catch (e: Exception) {
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
                    println("============update profile fail  :$t")
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@StoreGiftCardActivity, "No Internet Found!")
        }
    }

    private fun sendGiftCard(userId: Int, giftId: Int, message: String, giftAmount: String) {
        ErrorMessage.E("GiftId : $giftId")
        ErrorMessage.E("GiftId : $userId")
        if (AppUtil.isNetworkAvailable(this@StoreGiftCardActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().sendGiftCard(userId, giftId, message, giftAmount)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()
                            try {
                                val obj = JSONObject(response.body()!!.string())
                                Log.e("sendGiftCard", obj.toString())
                                if ((obj.getString("error_type") == "200")) {
                                    giftCardToServer(giftId, GiftVoucherId)
                                    infoPopup(obj.getString("message"))
                                    /*Intent intent = new Intent("refresh");
                                    LocalBroadcastManager.getInstance((context)).sendBroadcast(intent);*/
                                    /*Intent intent2 = new Intent("refresh_rewards");
                                    LocalBroadcastManager.getInstance((context)).sendBroadcast(intent2);*/somethingDone =
                                        true
                                } else {
                                    infoPopup(obj.getString("message"))
                                }
                            } catch (e: Exception) {
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken ", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    // ErrorMessage.T(getActivity(), "Response Fail");
                    println("============update profile fail  :$t")
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@StoreGiftCardActivity, "No Internet Found!")
        }
    }

    private fun sendGiftInvitationToServer(userMobile: String, country: String, price: String) {
        if (AppUtil.isNetworkAvailable(this@StoreGiftCardActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call =
                AppConfig.api_Interface().sendGiftInvitation(userMobile, country, price, giftId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()
                            try {
                                val obj = JSONObject(response.body()!!.string())
                                Log.e("sendGiftInvitation", obj.toString())
                                if ((obj.getString("error_type") == "200")) {
                                    infoPopup(obj.getString("message"))
                                    giftCardToServer(giftId, GiftVoucherId)
                                    /*Intent intent = new Intent("refresh");
                                    LocalBroadcastManager.getInstance((context)).sendBroadcast(intent);*/somethingDone =
                                        true
                                } else {
                                    infoPopup(obj.getString("message"))
                                }
                            } catch (e: Exception) {
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
                    println("============update profile fail  :$t")
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@StoreGiftCardActivity, "No Internet Found!")
        }
    }

    private fun giftCardPaymentProcess(stripeToken: String) {
        if (AppUtil.isNetworkAvailable(this@StoreGiftCardActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call =
                AppConfig.api_Interface().giftCardPaymentProcess(giftId, giftValue, stripeToken,stripeToken,"1")
            call!!.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("sendToken", response.code().toString())
                        try {
                            dialogManager.stopProcessDialog()
                            try {
                                val obj = JSONObject(response.body()!!.string())
                                Log.e("giftCardPaymentProcess", obj.toString())
                                if ((obj.getString("error_type") == "200")) {
                                    infoPopup(obj.getString("message"))
                                    /*Intent intent = new Intent("refresh");
                                    LocalBroadcastManager.getInstance((context)).sendBroadcast(intent);*/somethingDone =
                                        true
                                } else {
                                    infoPopup(obj.getString("message"))
                                }
                            } catch (e: Exception) {
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
                    println("============update profile fail  :$t")
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@StoreGiftCardActivity, "No Internet Found!")
        }
    }

    private fun sendGiftPopup(giftAmount: String, msg: String, name: String, userId: Int) {
        val dialog2 = Dialog(this@StoreGiftCardActivity)
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setContentView(R.layout.send_gift_popup)
        dialog2.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val ivClose = dialog2.findViewById<ImageView>(R.id.ivClose)
        val confimation_tv = dialog2.findViewById<TextView>(R.id.confimation_tv)
        val btn_yes = dialog2.findViewById<Button>(R.id.btn_yes)
        ivClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dialog2.dismiss()
            }
        })
        val builder1 = SpannableStringBuilder()
        val str1 = SpannableString("Are you sure? You want to gift ")
        val str2 = SpannableString("£$giftAmount")
        val str3 = SpannableString(" to " + name)
        str1.setSpan(ForegroundColorSpan(Color.parseColor("#111111")), 0, str1.length, 0)
        builder1.append(str1)
        str2.setSpan(ForegroundColorSpan(Color.parseColor("#2961F4")), 0, str2.length, 0)
        builder1.append(str2)
        str3.setSpan(ForegroundColorSpan(Color.parseColor("#111111")), 0, str3.length, 0)
        builder1.append(str3)
        confimation_tv.setText(builder1, TextView.BufferType.SPANNABLE)
        dialog2.show()
        btn_yes.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dialog2.dismiss()
                dialog = null
                sendGiftCard(userId, giftId, msg, amountStr)
            }
        })
    }

    private fun submitClickSuccessPopup(
        userMobile: String,
        name: String,
        photo: String,
        userId: Int
    ) {
        try{
        val dialog_success = Dialog(this@StoreGiftCardActivity, R.style.NewDialog)
        dialog_success.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_success.setContentView(R.layout.submit_click_success_popup)
        dialog_success.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog_success.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog_success.window!!.attributes = lp
        val ivClose = dialog_success.findViewById<ImageView>(R.id.ivClose)
        val userMobile_tv = dialog_success.findViewById<TextView>(R.id.userMobile_tv)
        val gift_amount_etv = dialog_success.findViewById<ATMEditText>(R.id.gift_amount_etv)
        val userName_tv = dialog_success.findViewById<TextView>(R.id.userName_tv)
        val profile_image = dialog_success.findViewById<ImageView>(R.id.profile_image)
        val message_etv = dialog_success.findViewById<EditText>(R.id.message_etv)
        userMobile_tv.text = userMobile
        val into = Glide.with(this@StoreGiftCardActivity).load(photo)
            .placeholder(R.drawable.userplaceholder).into(profile_image)
        userName_tv!!.setText(name)
        val btn_send_gift = dialog_success.findViewById<Button>(R.id.btn_send_gift)
        ivClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dialog_success.dismiss()
                dialog = null
            }
        })
        dialog_success.show()
        gift_amount_etv!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    gift_amount_etv!!.setSelection(gift_amount_etv!!.getText()!!.length)
                    //amountStr = gift_amount_etv.getText().toString().trim().replaceAll(",", "");
                    amountStr = gift_amount_etv!!.getText().toString().replace(",".toRegex(), "")
                        .replace("^\\s*".toRegex(), "")
                    ErrorMessage.E("TEXTVIEW $amountStr")
                    ErrorMessage.E("TEXTVIEW $amountStr")
                    if (gift_amount_etv!!.getText().toString() != "") {
                        if (amountStr.toDouble() > 0) {
                            if (amountStr.toDouble() > giftValue!!.toDouble()) {
                                gift_amount_etv!!.setError("Value can not be greater than gift price")
                            }
                        } else {
                            gift_amount_etv!!.setError("Please enter some amount")
                        }
                    } else {
                        gift_amount_etv!!.setError("Please enter some amount")
                    }
                } catch (e: Exception) {
                    ErrorMessage.E("Exception:$e")
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        btn_send_gift.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                try {
                    if (gift_amount_etv!!.getText().toString() != "") {
                        if (amountStr.toDouble() > 0) {
                            if (amountStr.toDouble() <= giftValue!!.toDouble()) {
                                dialog_success.dismiss()
                                dialog = null
                                sendGiftPopup(gift_amount_etv.text.toString(),message_etv.text.toString(),name,userId)
                            } else {
                                gift_amount_etv!!.setError("Value can not be greater than gift price")
                            }
                        } else {
                            gift_amount_etv!!.setError("Please enter some amount")
                        }
                    } else {
                    }
                    gift_amount_etv!!.setError("Please enter some amount")
                } catch (w: Exception) {
                    ErrorMessage.E("Exception:$w")
                }
            }
        })
        }catch (e: Exception) {
            ErrorMessage.E("Exception:$e")
        }
    }

    private fun submitClickFailurePopup(userMobile: String) {
        val dialog_failure = Dialog(this@StoreGiftCardActivity, R.style.NewDialog)
        dialog_failure.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_failure.setContentView(R.layout.submit_click_failure_popup)
        dialog_failure.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog_failure.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog_failure.window!!.attributes = lp
        val ivClose = dialog_failure.findViewById<ImageView>(R.id.ivClose)
        val sign_up_country = dialog_failure.findViewById<TextView>(R.id.sign_up_country)
        val btn_sendInvitation = dialog_failure.findViewById<Button>(R.id.btn_send_invitation)
        val userMobile_etv = dialog_failure.findViewById<TextView>(R.id.userMobile_etv)
        val gift_amount_tv = dialog_failure.findViewById<ATMEditText>(R.id.gift_amount_tv)
        userMobile_etv.text = userMobile
        ivClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dialog_failure.dismiss()
                dialog = null
            }
        })
        sign_up_country.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                showDialog(this@StoreGiftCardActivity, loadCountryJSONFromAsset(), sign_up_country)
            }
        })
        gift_amount_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    gift_amount_tv.setSelection(gift_amount_tv.text!!.length)
                    amountStr = gift_amount_tv.text.toString().trim { it <= ' ' }
                        .replace(",".toRegex(), "").replace("^\\s*".toRegex(), "")
                    if (gift_amount_tv.text.toString() != "") {
                        if (amountStr.toDouble() > 0) {
                            if (amountStr.toDouble() > giftValue!!.toDouble()) {
                                gift_amount_tv.error = "Value can not be greater than gift price"
                            }
                        } else {
                            gift_amount_tv.error = "Please enter some amount"
                        }
                    } else {
                        gift_amount_tv.error = "Please enter some amount"
                    }
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        btn_sendInvitation.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (gift_amount_tv.text.toString() != "") {
                    if (amountStr.toDouble() > 0) {
                        if (amountStr.toDouble() > giftValue!!.toDouble()) {
                            gift_amount_tv.error = "Value can not be greater than gift price"
                        } else {
                            sendGiftInvitationToServer(userMobile_etv.text.toString(),
                                sign_up_country.text.toString(),
                                amountStr)
                            dialog_failure.dismiss()
                            dialog = null
                        }
                    } else {
                        gift_amount_tv.error = "Please enter some amount"
                    }
                } else {
                    gift_amount_tv.error = "Please enter some amount"
                }
            }
        })
        dialog_failure.show()
    }

    private fun buyClickPopup() {
        try {
            if (dialog3 == null) {
                dialog3 = Dialog(this@StoreGiftCardActivity, R.style.NewDialog)
                dialog3!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog3!!.setContentView(R.layout.buy_click_popup)
                dialog3!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog3!!.window!!.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
                dialog3!!.window!!.attributes = lp
                dialog3!!.setCanceledOnTouchOutside(false)
                dialog3!!.setCancelable(false)
                val ivClose = dialog3!!.findViewById<ImageView>(R.id.ivClose)
                val buy_card_bg_img = dialog3!!.findViewById<ImageView>(R.id.buy_card_bg_img)
                val payable_amount_tv = dialog3!!.findViewById<TextView>(R.id.payable_amount_tv)
                val nameOnCard = dialog3!!.findViewById<TextView>(R.id.name_on_card_etv)
                val btn_submit = dialog3!!.findViewById<Button>(R.id.btn_submit)
                btn_submit.isEnabled = true
                Glide.with(dialog3!!.context).load(card_img_url)
                    .placeholder(R.drawable.place_holder).error(
                    R.drawable.place_holder).into(buy_card_bg_img)
                payable_amount_tv.text = sellingPrice
                val cardMultilineWidget =
                    dialog3!!.findViewById<CardMultilineWidget>(R.id.card_input_widget)
              /*  cardMultilineWidget.setCardInputListener(object : CardInputListener {
                     fun onFocusChange(focusField: String) {}
                    override fun onCardComplete() {}
                    override fun onExpirationComplete() {}
                    override fun onCvcComplete() {
                        //hideKeyboard(btn_submit)
                        AppUtil.hideKeyboard(btn_submit,context1);
                    }

                    override fun onPostalCodeComplete() {}
                })*/
                ivClose.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        dialog3!!.dismiss()
                        dialog3 = null
                    }
                })
                btn_submit.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                      /*  val card = cardMultilineWidget.card
                        if (nameOnCard.text.toString() == null || (nameOnCard.text.toString() == "")) {
                            nameOnCard.requestFocus()
                            nameOnCard.error = "Please enter name"
                        }
                        else {
                            btn_submit.isEnabled = false
                            if (card != null) {
                                val dialogManager = DialogManager()
                                dialogManager.showProcessDialog(this@StoreGiftCardActivity,
                                    "",
                                    false,
                                    null)
                                if (!card.validateCard()) {
                                    // Do not continue token creation.
                                    dialogManager.stopProcessDialog()
                                    btn_submit.isEnabled = true
                                    Toast.makeText(applicationContext,
                                        "Invalid card",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    var stripe: Stripe? = null
                                    try {
                                        //stripePublishKey = "pk_test_3nyelf0qFlix0lorDVBpi0Ze";
                                        stripe =
                                            Stripe(this@StoreGiftCardActivity, stripePublishKey)
                                    } catch (e: Exception) {
                                        dialogManager.stopProcessDialog()
                                        e.printStackTrace()
                                    }
                                    stripe!!.createToken(
                                        card,
                                        object : TokenCallback {
                                            override fun onSuccess(token: Token) {
                                                // Send token to your server
                                                dialogManager.stopProcessDialog()
                                                dialog3!!.dismiss()
                                                dialog3 = null
                                                giftCardPaymentProcess(token.id)
                                                ErrorMessage.E("Stripe Token 1 : " + token.id)
                                                ErrorMessage.E("Stripe Token 2 : $token")
                                            }

                                            override fun onError(error: Exception) {
                                                // Show localized error message
                                                dialogManager.stopProcessDialog()
                                                btn_submit.isEnabled = true
                                                Log.d("token", "excep" + error.message)
                                                ErrorMessage.E(" ErrorStripeToken : " + error.message)
                                                infoPopup(error.message)
                                            }
                                        }
                                    )


                                }
                            } else {
                                btn_submit.isEnabled = true
                            }
                        }*/
                    }
                })
                if (!dialog3!!.isShowing) {
                    dialog3!!.show()
                    //checkClick = true;
                    binding!!.buyBtn.isEnabled = true
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun qrCodePopup() {
        try {
            if (dialogQR == null) {
                dialogQR = Dialog(this@StoreGiftCardActivity, R.style.NewDialog)
                dialogQR!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogQR!!.setContentView(R.layout.qr_popup)
                dialogQR!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialogQR!!.window!!.attributes)
                lp.width = WindowManager.LayoutParams.FILL_PARENT
                lp.height = WindowManager.LayoutParams.FILL_PARENT
                dialogQR!!.window!!.attributes = lp
                dialogQR!!.setCanceledOnTouchOutside(false)
                dialogQR!!.setCancelable(false)
                val ivClose = dialogQR!!.findViewById<ImageView>(R.id.ivClose)
                iv_qr_code = dialogQR!!.findViewById(R.id.iv_qr_code)
                //rl_scanned_tag = dialogQR.findViewById(R.id.rl_scanned_tag);
                tvPrice = dialogQR!!.findViewById(R.id.tvPrice)
                val title_tv = dialogQR!!.findViewById<TextView>(R.id.title_tv)
                tv_successfull_text = dialogQR!!.findViewById(R.id.tv_successfull_text)
                ivTick = dialogQR!!.findViewById(R.id.ivTick)
                ivClose.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        dialogQR!!.dismiss()
                        dialogQR = null
                    }
                })
                val myBitmap = QRCode.from(qrCode_uuid)
                    .bitmap()
                iv_qr_code!!.setImageBitmap(myBitmap)
                pubNubChat = PubNubChat(context1!!, this)
                pubNubChat!!.initPubNub()
                pubNubChat!!.subscribePubNubChannel(qrCode_uuid + "")
                pubNubChat!!.subscribePubNubListener()
                tvPrice!!.setText(giftPrice)
                title_tv.text = binding!!.agentNameTv.text.toString()
                if (!dialogQR!!.isShowing) {
                    dialogQR!!.show()
                    binding!!.redeemBtn.isEnabled = true
                }
            }
        } catch (e: Exception) {
        }
    }

    fun showDialog(activity: Activity?, list: JSONArray?, signup_country: TextView) {
        val dialog = Dialog((activity)!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val searchText = dialog.findViewById<EditText>(R.id.country_dialog_search_text)
        val searchBtn = dialog.findViewById<ImageButton>(R.id.country_dialog_search_btn)
        searchBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                requestFocus(searchText)
            }
        })
        val listView = dialog.findViewById<ListView>(R.id.country_dialog_list_view)
        val adapter =
            CountryDialogAdapter(this@StoreGiftCardActivity, list!!, object : CountryCallback {
                override fun setItemList(position: Int, jsonArray: JSONArray) {
                    // userCountryCode = Integer.parseInt(jsonArray.optJSONObject(position).optString("country_code"));
                    //countryCodeKey = Integer.parseInt(jsonArray.optJSONObject(position).optString("id")) + "";
                    selectedCountry = jsonArray.optJSONObject(position).optString("country_name")
                    signup_country.text = selectedCountry
                    dialog.dismiss()
                }
            })
        listView.adapter = adapter
        dialog.show()
        listView.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {}
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun giftClickPopup() {
        if (dialog == null) {
            dialog = Dialog(this@StoreGiftCardActivity, R.style.NewDialog)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.gift_click_popup)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.FILL_PARENT
            lp.height = WindowManager.LayoutParams.FILL_PARENT
            dialog!!.window!!.attributes = lp
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            val ivClose = dialog!!.findViewById<ImageView>(R.id.ivClose)
            userMobile_etv = dialog!!.findViewById(R.id.userMobile_etv)
            userMobile_etv!!.setFocusable(true)
            userMobile_etv!!.setEnabled(true)
            userMobile_etv!!.requestFocus()
            AppUtil.showSoftKeyboard(this@StoreGiftCardActivity)
            val btn_submit = dialog!!.findViewById<Button>(R.id.btn_submit)
            ivClose.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //hideKeyboard(view)
                    AppUtil.hideKeyboard(view,context1);
                    dialog!!.dismiss()
                    dialog = null
                }
            })
            btn_submit.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    if (userMobile_etv!!.getText().toString() != "") {
                        //hideKeyboard(view)
                        AppUtil.hideKeyboard(view,context1);
                        checkCustomerMobile(userMobile_etv!!.getText().toString())
                    } else {
                        userMobile_etv!!.setError("Please enter mobile Number")
                    }
                }
            })
            if (!dialog!!.isShowing) {
                dialog!!.show()
                binding!!.giftBtn.isEnabled = true
            }
        }
    }

    fun loadCountryJSONFromAsset(): JSONArray? {
        var json: String? = null
        var countryArray: JSONArray? = null
        try {
            val `is` = application.assets.open("country.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        if (json != null) {
            try {
                countryArray = JSONArray(json)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return countryArray
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@StoreGiftCardActivity, "")
        } catch (e: Exception) {
        }
    }

    override fun onRefreshHistoryList(list: List<PNHistoryItemResult>) {}
    override fun clearData() {}
    override fun onRefreshChatList(jsonObject: JsonObject) {
        runOnUiThread(object : Runnable {
            override fun run() {
                giftCardToServer(giftId, GiftVoucherId)
                ivTick!!.visibility = View.VISIBLE
                tvPrice!!.text = giftPrice
                // iv_qr_code.setVisibility(View.GONE);
                tv_successfull_text!!.visibility = View.VISIBLE
                /* Intent intent = new Intent("refresh");
                LocalBroadcastManager.getInstance((context)).sendBroadcast(intent);*/somethingDone =
                    true

                // rl_scanned_tag.setVisibility(View.VISIBLE);
            }
        })
    }

    private fun infoPopup(responseMsg: String?) {
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
            btnOk.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    dialog1.dismiss()
                }
            })
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmField
        var somethingDone = false
    }

    private fun presentPaymentSheet() {
        PaymentConfiguration.init(applicationContext, stripePublishKey!!)

        val configuration: PaymentSheet.Configuration =
            PaymentSheet.Configuration.Builder(customer).customer(customerConfig).allowsDelayedPaymentMethods(true).build()
        paymentSheet!!.presentWithPaymentIntent(paymentIntentId!!, configuration)
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Canceled) {
            E("Canceled")


            // check_order_complete = false;
        } else if (paymentSheetResult is PaymentSheetResult.Failed) {
            E("Got error: " + (paymentSheetResult as PaymentSheetResult.Failed).error)
            /* new DialogManager().showDialog(CheckoutActivity.this, "Error " + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            check_order_complete = false;*/
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(
                this,
                "Error " + (paymentSheetResult as PaymentSheetResult.Failed).error,
                false,
                null
            )
        } else if (paymentSheetResult is PaymentSheetResult.Completed) {
            E("paymentSheetResult<><>$paymentSheetResult")
          //  completeMemberShip()
            giftCardPaymentProcess(paymentIntentId.toString());
            /*   if (paymentId != null && paymentIntentId != null && !paymentId.isEmpty() && !paymentIntentId.isEmpty()) {

//                CompleteOrderPayNow(agentId, SavedData.get_order_type(), SavedData.get_booking_slot_date(), SavedData.get_booking_slot_time(), SavedData.getContact_name(), "",
//                        SavedData.getUser_mobile(), SavedData.getUser_email(), doorNumber, street, city, postcode, country);

                ErrorMessage.E("SavedData<><><>" + SavedData.get_booking_mobile_number() + "<><><>" + SavedData.get_booking_mail_id() + "<><><>" + SavedData.get_booking_incharge_name() + "<><><>" +
                        SavedData.get_booking_contact_name());


                CompleteOrderPayNow(agentId, SavedData.get_order_type(), SavedData.get_booking_slot_date(), SavedData.get_booking_slot_time(), SavedData.get_booking_contact_name(), SavedData.get_booking_incharge_name(),
                        SavedData.get_booking_mobile_number(), SavedData.get_booking_mail_id(), doorNumber, street, city, postcode, country);

            }*/
        }
    }

}