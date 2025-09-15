package sambal.mydd.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import sambal.mydd.app.R
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.PromocodeBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromoCode : BaseActivity(), View.OnClickListener {
    private lateinit var binding: PromocodeBinding
    private lateinit var context: Context
    private lateinit var dialogManager: DialogManager

    private var promo_code_popup: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.promocode)
        setToolbarWithBackButton_colorprimary("Add Promo Code")
        context = this@PromoCode
        binding.llAdd.setOnClickListener(this)
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("check") != null && bundle.getString("check") == "Refer") {
                binding.exPromoCode.setText(bundle.getString("Code"))
            }
        }

        binding.exPromoCode.setFocusableInTouchMode(true);
        binding.exPromoCode.setFocusable(true);
        binding.exPromoCode.requestFocus();
    }

    private fun addPromoCode() {
        if (AppUtil.isNetworkAvailable(context)) {
            dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call =
                AppConfig.api_Interface().applyPromoCode(binding.exPromoCode.text.toString().trim())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("PromoCode", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)) {
                                dialogManager.stopProcessDialog()

                                (context as Activity).runOnUiThread {

                                    Toast.makeText(this@PromoCode,
                                        resp.optString(KeyConstant.KEY_MESSAGE),
                                        Toast.LENGTH_SHORT).show()
                                    finish()

                                }
                            }

                            else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202)) {

                                dialogManager.stopProcessDialog()

                                promo_code_popup = Dialog(this@PromoCode)
                                promo_code_popup!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                promo_code_popup!!.setContentView(R.layout.promo_code_popup)
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


                                if (resp.optString("buttonText") != null && !resp.optString(
                                        "buttonText").equals("")
                                ) {
                                    popup_yes_btn.visibility = View.VISIBLE
                                    popup_yes_btn.setText("${resp.optString("buttonText")}")


                                } else {
                                    popup_yes_btn.visibility = View.GONE
                                }

                                if (resp.optString("message") != null && !resp.optString("message")
                                        .equals("")
                                ) {
                                    tvTitle.visibility = View.VISIBLE
                                    tvTitle.setText("${resp.optString("message")}")
                                } else {
                                    tvTitle.visibility = View.GONE
                                }

                                popup_yes_btn.setOnClickListener(View.OnClickListener {
                                    if (resp.optString("externalURLStatus") != null && resp.optInt(
                                            "externalURLStatus") == 1
                                    ) {

                                        promo_code_popup!!.dismiss()

                                        val intent = Intent(this@PromoCode, WebViewActivity::class.java)
                                        intent.putExtra("url", resp.optString("externalURL"))
                                        intent.putExtra("title", resp.optString("message"))
                                        startActivity(intent)

                                    } else {
                                        promo_code_popup!!.dismiss()
                                    }
                                })

                                cancel_button.setOnClickListener(View.OnClickListener {
                                    promo_code_popup!!.dismiss()
                                })
                                if(popup_yes_btn.visibility == View.GONE && tvTitle.visibility == View.GONE ){
                                }
                                else {
                                    promo_code_popup!!.show()
                                }
                            }

                            else {
                                dialogManager.stopProcessDialog()
                                AppUtil.showMsgAlert(binding.tv,
                                    resp.optString(KeyConstant.KEY_MESSAGE))

                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding.tv,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.tv, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.tv, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.tv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }


    override fun onClick(v: View) {

        when (v.id) {

            R.id.llAdd -> {
                if (TextUtils.isEmpty(binding.exPromoCode.text.toString().trim())) {
                    Toast.makeText(this@PromoCode, "Enter Promo Code", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    addPromoCode()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@PromoCode, "")
        } catch (e: Exception) {
        }
    }


    override val contentResId: Int
        get() = R.layout.promocode


}