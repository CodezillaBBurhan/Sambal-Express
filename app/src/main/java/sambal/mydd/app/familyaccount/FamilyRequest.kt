package sambal.mydd.app.familyaccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.content.Intent
import android.util.Log
import android.view.View
import sambal.mydd.app.R
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.AdddeclinememberBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FamilyRequest : AppCompatActivity(), View.OnClickListener {
    private var binding: AdddeclinememberBinding? = null
    var familyHeadId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.adddeclinemember)
        init()
        val bundle = intent.extras
        if (bundle != null) {
            val note = bundle.getString("note")
            familyHeadId = bundle.getString("familyHeadId")
            binding!!.tvNote.text = note
        }
    }

    private fun init() {
        binding!!.btncancel.setOnClickListener(this)
        binding!!.btnConfirm.setOnClickListener(this)
        binding!!.header.ivBack.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivBack -> finish()
            R.id.btnConfirm -> acceptrequest()
            R.id.btncancel -> declinerequest()
        }
    }

    private fun acceptrequest() {
        if (AppUtil.isNetworkAvailable(this@FamilyRequest)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().acceptFamilyMember(familyHeadId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("acceptrequest", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvNote,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                    startActivity(Intent(this@FamilyRequest,
                                        FamilyMemberList::class.java))
                                    finish()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvNote,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNote,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNote,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvNote,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvNote, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvNote, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun declinerequest() {
        if (AppUtil.isNetworkAvailable(this@FamilyRequest)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().cancelFamilyMember(familyHeadId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("acceptrequest", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvNote,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                    startActivity(Intent(this@FamilyRequest, AddFamily::class.java))
                                    finish()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvNote,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNote,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNote,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvNote,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvNote, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvNote, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}