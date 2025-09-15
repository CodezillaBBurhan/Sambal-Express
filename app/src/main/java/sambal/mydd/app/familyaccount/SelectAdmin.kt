package sambal.mydd.app.familyaccount

import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.beans.FamilyList
import sambal.mydd.app.adapter.AdapterSelectAdminList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.databinding.SelectadminBinding
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

class SelectAdmin : AppCompatActivity(), View.OnClickListener {
    private var binding: SelectadminBinding? = null
    var userId: String? = null
    var userName: String? = null
    var array: String? = ""
    var mlist: ArrayList<FamilyList>? = ArrayList()
    var adap: AdapterSelectAdminList? = null
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.selectadmin)
        init()
        intentData
    }

    private val intentData: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                userId = bundle.getString("userId")
                userName = bundle.getString("userName")
                array = bundle.getString("array")
                binding!!.tvName.text = userName
                mlist = intent.getSerializableExtra("arraylist") as ArrayList<FamilyList>?
                binding!!.llAdmin.visibility = View.GONE
                adap = AdapterSelectAdminList(this@SelectAdmin, mlist!!)
                binding!!.rvMember.layoutManager =
                    LinearLayoutManager(this@SelectAdmin, LinearLayoutManager.VERTICAL, false)
                binding!!.rvMember.adapter = adap
            }
        }

    private fun init() {
        binding!!.header.ivBack.setOnClickListener(this)
        binding!!.btnConfirm.setOnClickListener(this)
        binding!!.btncancel.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivBack -> finish()
            R.id.btncancel -> finish()
            R.id.btnConfirm -> if (id.equals("", ignoreCase = true)) {
                AppUtil.showMsgAlert(binding!!.tvName, "Please select any member to make him admin")
            } else if (!AppUtil.isNetworkAvailable(this@SelectAdmin)) {
                AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
                return
            } else {
                existAssignFamilyHead()
            }
        }
    }

    fun selectAdmin(userId: String) {
        id = userId
        Log.e("Iddd", id)
    }

    private fun existAssignFamilyHead() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().existAssignFamilyHead(id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ExistsMember", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                runOnUiThread {
                                    if (resp.optString("error_type")
                                            .equals("200", ignoreCase = true)
                                    ) {
                                        dialogManager.stopProcessDialog()
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        finish()
                                    } else {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        dialogManager.stopProcessDialog()
                                    }
                                }
                            } else {
                                if (resp.optString("status").equals("false", ignoreCase = true)) {
                                    dialogManager.stopProcessDialog()
                                    runOnUiThread {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvName,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}