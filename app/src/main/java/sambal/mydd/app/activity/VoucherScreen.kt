package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.AppUtil
import android.text.TextWatcher
import android.text.Editable
import android.widget.Toast
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.android.volley.*
import sambal.mydd.app.utils.DialogManager
import com.android.volley.toolbox.StringRequest
import sambal.mydd.app.R
import sambal.mydd.app.constant.UrlConstant
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONObject
import kotlin.Throws
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.utils.SharedPreferenceVariable
import sambal.mydd.app.VolleySingleton.MySingleton
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.VoucherscreenBinding
import java.lang.Exception
import java.util.HashMap

class VoucherScreen : AppCompatActivity(), View.OnClickListener {
    var binding: VoucherscreenBinding? = null
    var agentUUID: String? = ""
    var voucherUUID: String? = ""
    var amount = 0.0
    var isStoreVouchers = false
    var currency: String? = ""
    var lat: String? = ""
    var lng: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.voucherscreen)
        initId()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding!!.etInput, InputMethodManager.SHOW_IMPLICIT)
        binding!!.etInput.isFocusable = true
        binding!!.etInput.isFocusableInTouchMode = true
        binding!!.etInput.requestFocus()
        val bundle = intent.extras
        if (bundle != null) {
            agentUUID = bundle.getString("agentUUID")
            voucherUUID = bundle.getString("voucherUUID")
            amount = bundle.getString("voucherPrice")!!.toDouble()
            isStoreVouchers = bundle.getBoolean("storevouchers")
            Log.e("Isss", isStoreVouchers.toString() + "")
            currency = bundle.getString("currency")
            lat = bundle.getString("lat")
            lng = bundle.getString("lng")
            Log.e("Late", lat + "")
            Log.e("1111", lng + "")
            val arr = amount.toString().split("\\.".toRegex()).toTypedArray()
            if (arr[1].length > 1) {
                amount = bundle.getString("voucherPrice")!!.toDouble()
                binding!!.etInput.setText(amount.toString())
                binding!!.etInput.isFocusable = false
                binding!!.etInput.isFocusableInTouchMode = false
                binding!!.etInput.isLongClickable = false
                binding!!.tvTitle.text = "Voucher Amount - $currency$amount"
            } else {
                amount = bundle.getString("voucherPrice")!!.toDouble() * 10
                binding!!.etInput.setText(amount.toString())
                binding!!.tvTitle.text =
                    "Voucher Amount - " + currency + binding!!.etInput.text.toString()
                binding!!.etInput.isFocusable = false
                binding!!.etInput.isFocusableInTouchMode = false
                binding!!.etInput.isLongClickable = false
            }
        }
    }

    private fun initId() {
        binding!!.ivBack.setOnClickListener(this)
        binding!!.btnSubmit.setOnClickListener(this)
        binding!!.rbFullAmount.isChecked = true
        binding!!.rbFullAmount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppUtil.hideSoftKeyboard(this@VoucherScreen)
                binding!!.rbFullAmount.isChecked = true
                binding!!.rbOtherAmount.isChecked = false
                binding!!.etInput.setText(amount.toString())
                binding!!.etInput.isFocusable = false
                binding!!.etInput.isFocusableInTouchMode = false
                binding!!.etInput.isLongClickable = false
            }
        }
        binding!!.rbOtherAmount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppUtil.showSoftKeyboard(this@VoucherScreen)
                binding!!.rbFullAmount.isChecked = false
                binding!!.rbOtherAmount.isChecked = true
                binding!!.etInput.setText("0.00")
                binding!!.etInput.isFocusableInTouchMode = true
                binding!!.etInput.isLongClickable = false
                binding!!.etInput.isFocusable = true
                binding!!.etInput.requestFocus()
            }
        }
        binding!!.etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                    Log.e("aamo", amount.toString() + "")
                    Log.e("asassasa", binding!!.etInput.cleanDoubleValue.toString() + "")
                    val retval =
                        java.lang.Double.compare(binding!!.etInput.cleanDoubleValue, amount)
                    Log.e("Re", retval.toString() + "")
                    if (retval > 0) {
                        binding!!.etInput.setText("0.00")
                        AppUtil.hideSoftKeyboard(this@VoucherScreen)
                        Toast.makeText(this@VoucherScreen,
                            "Amount should be equal or less than Voucher amount",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivBack -> if (isStoreVouchers) {
                val intent = Intent(this@VoucherScreen, ActivityStorePoints::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@VoucherScreen, ActivityMyRewards::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            R.id.btnSubmit -> if (!binding!!.rbFullAmount.isChecked && !binding!!.rbOtherAmount.isChecked) {
                Toast.makeText(this@VoucherScreen, "Select Amount type", Toast.LENGTH_SHORT).show()
                return
            } else if (binding!!.rbOtherAmount.isChecked && TextUtils.isEmpty(binding!!.etInput.text.toString())) {
                Toast.makeText(this@VoucherScreen, "Enter Amount", Toast.LENGTH_SHORT).show()
                return
            } else if (!AppUtil.isNetworkAvailable(this@VoucherScreen)) {
                Toast.makeText(this@VoucherScreen, "No Internet", Toast.LENGTH_SHORT).show()
                return
            } else {
                sendToken(agentUUID)
            }
        }
    }

    fun sendToken(agentUUID: String?) {
        Log.e("lta", lat + "")
        if (AppUtil.isNetworkAvailable(this@VoucherScreen)) {
            AppUtil.hideSoftKeyboard(this@VoucherScreen)
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this@VoucherScreen, "", false, null)
            var stringRequest: StringRequest? = null
            val finalStringRequest = stringRequest
            stringRequest = object : StringRequest(
                Method.POST,
                UrlConstant.BASE_URL + KeyConstant.KEY_SCAN_AGENT,
                Response.Listener { response ->
                    try {
                        val resp = JSONObject(response)
                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                        if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                ignoreCase = true)
                        ) {
                            dialogManager.stopProcessDialog()
                            if (isStoreVouchers) {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityStorePoints::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityMyRewards::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            dialogManager.stopProcessDialog()
                            Toast.makeText(this@VoucherScreen,
                                resp.optString("message"),
                                Toast.LENGTH_SHORT).show()
                            if (isStoreVouchers) {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityStorePoints::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityMyRewards::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                    }
                },
                Response.ErrorListener { error ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val obj = JSONObject(jsonError)
                            val message = obj.optString("error")
                            dialogManager.stopProcessDialog()
                            Toast.makeText(this@VoucherScreen, message, Toast.LENGTH_SHORT).show()
                            if (isStoreVouchers) {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityStorePoints::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent =
                                    Intent(this@VoucherScreen, ActivityMyRewards::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8"
                    if (PreferenceHelper.getInstance(this@VoucherScreen)?.isLogin == true) {
                        headers[UrlConstant.HEADER_AUTHORIZATION] =
                            "Bearer" + " " + PreferenceHelper.getInstance(this@VoucherScreen)?.accessToken
                    }
                    headers[UrlConstant.HEADER_DEVICE_ID] =
                        SharedPreferenceVariable.loadSavedPreferences(this@VoucherScreen,
                            KeyConstant.Shar_DeviceID).toString()
                    return headers
                }

                public override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["agentUUID"] = agentUUID!!
                    params["voucherUUID"] = voucherUUID!!
                    params["voucherAmount"] = binding!!.etInput.text.toString().trim { it <= ' ' }
                    params[KeyConstant.KEY_LATITUDE] = lat!!
                    params[KeyConstant.KEY_LONG] = lng!!
                    Log.e("Params", params.toString() + "")
                    return params
                }
            }
            stringRequest.setRetryPolicy(DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            // Access the RequestQueue through singleton class.
            MySingleton.getInstance(this@VoucherScreen).addToRequestQueue(stringRequest)

//
        } else {
            Toast.makeText(this@VoucherScreen,
                MessageConstant.MESSAGE_INTERNET_CONNECTION,
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isStoreVouchers) {
            val intent = Intent(this@VoucherScreen, ActivityStorePoints::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@VoucherScreen, ActivityMyRewards::class.java)
            startActivity(intent)
            finish()
        }
    }
}