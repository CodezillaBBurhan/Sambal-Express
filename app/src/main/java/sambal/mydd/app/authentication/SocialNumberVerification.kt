package sambal.mydd.app.authentication

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import android.text.TextUtils
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import sambal.mydd.app.utils.AppUtil
import org.json.JSONArray
import android.widget.EditText
import android.widget.ImageButton
import sambal.mydd.app.adapter.CountryDialogAdapter
import sambal.mydd.app.callback.CountryCallback
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.text.TextWatcher
import android.text.Editable
import org.json.JSONException
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ListView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.SocialnumberverificationBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets

class SocialNumberVerification() : AppCompatActivity(), View.OnClickListener {
    private var binding: SocialnumberverificationBinding? = null
    var context: Context? = null
    private var userCountryCode = 44
    private var countryCodeKey = "228"
    private var selectedCountry = "United Kingdom"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.socialnumberverification)
        context = this@SocialNumberVerification
        binding!!.tvVerify.setOnClickListener(this)
        binding!!.llCountry.setOnClickListener(this)
        val bundle = intent.extras
        if (bundle != null) {
            binding!!.exReferaalCode.setText(bundle.getString("referralcode"))
        } else {
        }
        try {
            if (TextUtils.isEmpty(binding!!.exReferaalCode.text.toString().trim { it <= ' ' })) {
                FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this,
                        OnSuccessListener { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                            var deepLink: Uri? = null
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.link
                            }
                            Log.e("deepLink", deepLink.toString() + "")
                            if (deepLink != null) {
                                val url = deepLink.toString()
                                val referral = url.replace("https://ddpoints.page.link/", "")
                                Log.e("referral", referral + "")
                                binding!!.exReferaalCode.setText("")
                                binding!!.exReferaalCode.setText(referral)
                            }
                        })
                    .addOnFailureListener(this, object : OnFailureListener {
                        override fun onFailure(e: Exception) {
                            Log.e("Exce", e.toString())
                        }
                    })
            }
        } catch (e: Exception) {
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.llCountry -> {
                AppUtil.hideSoftKeyboard(this@SocialNumberVerification)
                showDialog(this@SocialNumberVerification, loadCountryJSONFromAsset())
            }
            R.id.tvVerify -> if (TextUtils.isEmpty(binding!!.exNo.text.toString())) {
                AppUtil.showMsgAlert(binding!!.tvCode, "Enter Phone Number")
                return
            } else if (!AppUtil.isNetworkAvailable(this@SocialNumberVerification)) {
                AppUtil.showMsgAlert(binding!!.tvCode, "No Internet")
                return
            } else {
                verifyNo()
            }
        }
    }

    fun showDialog(activity: Context?, list: JSONArray?) {
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
        val adapter = CountryDialogAdapter(context!!, list!!, object : CountryCallback {
            override fun setItemList(position: Int, jsonArray: JSONArray) {
                userCountryCode =
                    jsonArray.optJSONObject(position).optString("country_code").toInt()
                countryCodeKey =
                    jsonArray.optJSONObject(position).optString("id").toInt().toString() + ""
                selectedCountry = jsonArray.optJSONObject(position).optString("country_name")
                binding!!.tvCode.text = "+$userCountryCode"
                binding!!.tvCountry.text = selectedCountry
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

    private fun verifyNo() {
        val dialogManager = DialogManager()
        dialogManager.showProcessDialog(this, "", false, null)
        val call = AppConfig.api_Interface().updateUserMobileV1(
            binding!!.exNo.text.toString().trim { it <= ' ' },
            binding!!.exReferaalCode.text.toString().trim { it <= ' ' },
            countryCodeKey)
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    dialogManager.stopProcessDialog()
                    try {
                        AppUtil.hideSoftKeyboard(context)
                        val obj = JSONObject(response.body()!!.string())
                        if ((obj.optString("error_type") == "202")) {
                            context!!.startActivity(Intent(context, VerifyOTPActivity::class.java)
                                .putExtra("comment", "false")
                                .putExtra("userId", "")
                                .putExtra("userMobile",
                                    binding!!.tvCode.text.toString().replace("\\+".toRegex(),
                                        "") + binding!!.exNo.text.toString())
                                .putExtra("isFromForgotPassword", "false"))
                        } else {
                            AppUtil.hideSoftKeyboard(context)
                            AppUtil.showMsgAlert(binding!!.exNo, obj.optString("message"))
                        }
                    } catch (e: Exception) {
                    }
                } else {
                    dialogManager.stopProcessDialog()
                    AppUtil.hideSoftKeyboard(context)
                    Log.e("sendToken", "else is working" + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                ErrorMessage.E("ON FAILURE > " + t.message)
                dialogManager.stopProcessDialog()
                AppUtil.hideSoftKeyboard(context)
                AppUtil.showMsgAlert(binding!!.tvCode, t.message)
            }
        })
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }
}