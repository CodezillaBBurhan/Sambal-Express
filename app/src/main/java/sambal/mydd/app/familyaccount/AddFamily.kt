package sambal.mydd.app.familyaccount

import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.beans.FamilyList
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import android.content.Intent
import android.util.Log
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.databinding.Addfamily1Binding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AddFamily : AppCompatActivity() {
    private var binding: Addfamily1Binding? = null
    var mList = ArrayList<FamilyList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.addfamily1)
        mList.clear()
        init()
    }

    private val member: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(this)) {
                val dialogManager = DialogManager()
                dialogManager.showProcessDialog(this, "", false, null)
                val call = AppConfig.api_Interface().myFamilyMember
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("ADdFamily", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString(
                                        "status").equals("true", ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        try {
                                            dialogManager.stopProcessDialog()
                                            binding!!.llAddFamily.visibility = View.GONE
                                            binding!!.rvList.visibility = View.VISIBLE
                                            mList.clear()
                                            val objRes = resp.optJSONObject("response")
                                            val arr = objRes.optJSONArray("familyMemberList")
                                            for (i in 0 until arr.length()) {
                                                val objMembers = arr.optJSONObject(i)
                                                val userId = objMembers.optString("userId")
                                                val userName = objMembers.optString("userName")
                                                val userAcceptStatus =
                                                    objMembers.optString("userAcceptStatus")
                                                val userAcceptText =
                                                    objMembers.optString("userAcceptText")
                                                val userFamilytype =
                                                    objMembers.optString("userFamilytype")
                                                val userRemoveAccess =
                                                    objMembers.optString("userRemoveAccess")
                                                val fm = FamilyList(userId,
                                                    userName,
                                                    userAcceptStatus,
                                                    userAcceptText,
                                                    userFamilytype,
                                                    userRemoveAccess)
                                                mList.add(fm)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                } else {
                                    if (resp.optString("status")
                                            .equals("false", ignoreCase = true)
                                    ) {
                                        dialogManager.stopProcessDialog()
                                        runOnUiThread {
                                            try {
                                                AppUtil.showMsgAlert(binding!!.tv,
                                                    resp.optString(KeyConstant.KEY_MESSAGE))
                                                binding!!.llAddFamily.visibility = View.VISIBLE
                                                binding!!.btnFamily.visibility = View.VISIBLE
                                                binding!!.rvList.visibility = View.GONE
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                dialogManager.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tv,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                dialogManager.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tv,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            }
                        } else {
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tv,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tv, t.message)
                    }
                })
            } else {
                AppUtil.showMsgAlert(binding!!.tv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

    private fun init() {
        binding!!.btnFamily.setOnClickListener {
            startActivity(Intent(this@AddFamily,
                AddMember::class.java))
        }
        binding!!.header.ivBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (!AppUtil.isNetworkAvailable(this@AddFamily)) {
            AppUtil.showMsgAlert(binding!!.tv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            return
        } else {
            member
        }
    }
}