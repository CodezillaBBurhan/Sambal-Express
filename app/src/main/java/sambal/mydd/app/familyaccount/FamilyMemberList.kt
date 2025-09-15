package sambal.mydd.app.familyaccount

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.beans.FamilyList
import sambal.mydd.app.adapter.AdapterFamilyList
import sambal.mydd.app.utils.DialogManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.MemberlistBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class FamilyMemberList : AppCompatActivity(), View.OnClickListener {
    private var binding: MemberlistBinding? = null
    var mlist = ArrayList<FamilyList>()
    var adapterFamilyList: AdapterFamilyList? = null
    var userId = ""
    var dialogManager: DialogManager? = null
    var addMoreUserEnabled: String? = ""
    var addMoreUserText: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.memberlist)
        init()
        intentData
        mlist.clear()
    }

    private val intentData: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                addMoreUserEnabled = bundle.getString("addMoreUserEnabled")
                addMoreUserText = bundle.getString("addMoreUserText")
            }
        }

    private fun init() {
        dialogManager = DialogManager()
        binding!!.ivBack.setOnClickListener(this)
        binding!!.llExit.setOnClickListener(this)
        binding!!.btnAdd.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAdd -> if (addMoreUserEnabled.equals("0", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@FamilyMemberList)
                builder.setMessage(addMoreUserText)
                builder.setPositiveButton("Ok") { dialog, id -> dialog.dismiss() }
                val dialog = builder.create()
                dialog.show()
                val b = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                b?.setTextColor(resources.getColor(R.color.colorPrimary))
                // Create the AlertDialog object and return it
            } else {
                startActivity(Intent(this@FamilyMemberList, AddMember::class.java))
            }
            R.id.ivBack -> finish()
            R.id.llExit -> if (mlist.size > 0) {
                startActivity(Intent(this@FamilyMemberList, SelectAdmin::class.java)
                    .putExtra("userId", userId)
                    .putExtra("userName", binding!!.tvName.text.toString().trim { it <= ' ' })
                    .putExtra("arraylist", mlist))
            } else {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialogManager!!.showProcessDialog(this, "", false, null)
        memberList
    }

    private val memberList: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(this)) {
                val call = AppConfig.api_Interface().myFamilyMember
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("FamilyMemberList", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString(
                                        "status").equals("true", ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        try {
                                            val objRe = resp.optJSONObject("response")
                                            if (resp.optString("error_type")
                                                    .equals("200", ignoreCase = true)
                                            ) {
                                                dialogManager!!.stopProcessDialog()
                                                addMoreUserEnabled =
                                                    objRe.optString("addMoreUserEnabled")
                                                addMoreUserText = objRe.optString("addMoreUserText")
                                                userId = objRe.optString("userId")
                                                val userName = objRe.optString("userName")
                                                val userFamilytype =
                                                    objRe.optString("userFamilytype")
                                                val userRemoveAccess =
                                                    objRe.optString("userRemoveAccess")
                                                binding!!.tvName.text = userName
                                                binding!!.llAdmin.visibility = View.VISIBLE
                                                if (userRemoveAccess.equals("1",
                                                        ignoreCase = true)
                                                ) {
                                                    binding!!.llExit.visibility = View.VISIBLE
                                                    binding!!.tvExit.text = "EXIT"
                                                    binding!!.btnAdd.visibility = View.VISIBLE
                                                } else {
                                                    binding!!.llExit.visibility = View.GONE
                                                    binding!!.tvExit.text = "REMOVE"
                                                    binding!!.btnAdd.visibility = View.GONE
                                                }
                                                val array = objRe.optJSONArray("familyMemberList")
                                                if (array.length() > 0) {
                                                    mlist.clear()
                                                    for (i in 0 until array.length()) {
                                                        val `object` = array.optJSONObject(i)
                                                        val userIds = `object`.optString("userId")
                                                        val userNames =
                                                            `object`.optString("userName")
                                                        val userAcceptStatus =
                                                            `object`.optString("userAcceptStatus")
                                                        val userAcceptText =
                                                            `object`.optString("userAcceptText")
                                                        val userFamilytypes =
                                                            `object`.optString("userFamilytype")
                                                        val userRemoveAccesss =
                                                            `object`.optString("userRemoveAccess")
                                                        val fm = FamilyList(userIds,
                                                            userNames,
                                                            userAcceptStatus,
                                                            userAcceptText,
                                                            userFamilytypes,
                                                            userRemoveAccesss)
                                                        mlist.add(fm)
                                                    }
                                                    adapterFamilyList =
                                                        AdapterFamilyList(this@FamilyMemberList,
                                                            mlist,
                                                            userRemoveAccess)
                                                    binding!!.rvMember.layoutManager =
                                                        LinearLayoutManager(this@FamilyMemberList,
                                                            LinearLayoutManager.VERTICAL,
                                                            false)
                                                    binding!!.rvMember.adapter = adapterFamilyList
                                                    adapterFamilyList!!.notifyDataSetChanged()
                                                } else {
                                                    mlist.clear()
                                                    adapterFamilyList =
                                                        AdapterFamilyList(this@FamilyMemberList,
                                                            mlist,
                                                            userRemoveAccess)
                                                    binding!!.rvMember.layoutManager =
                                                        LinearLayoutManager(this@FamilyMemberList,
                                                            LinearLayoutManager.VERTICAL,
                                                            false)
                                                    binding!!.rvMember.adapter = adapterFamilyList
                                                    adapterFamilyList!!.notifyDataSetChanged()
                                                }
                                            } else {
                                                dialogManager!!.stopProcessDialog()
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                } else {
                                    if (resp.optString("status")
                                            .equals("false", ignoreCase = true)
                                    ) {
                                        dialogManager!!.stopProcessDialog()
                                        runOnUiThread {
                                            try {
                                                AppUtil.showMsgAlert(binding!!.tvName,
                                                    resp.optString(KeyConstant.KEY_MESSAGE))
                                                startActivity(Intent(this@FamilyMemberList,
                                                    AddFamily::class.java))
                                                finish()
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                dialogManager!!.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tvName,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                dialogManager!!.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.tvName,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
                            }
                        } else {
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvName, t.message)
                    }
                })
            } else {
                AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

    fun removeMember(id: String, name: String) {
        val dialog1 = Dialog(this@FamilyMemberList)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = "Are you sure Want to Remove $name?"
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        btnNo.setOnClickListener { dialog1.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (!AppUtil.isNetworkAvailable(this@FamilyMemberList)) {
                dialog1.dismiss()
                AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
                return@OnClickListener
            } else {
                removeFamilyMember(id, dialog1)
            }
        })
    }

    private fun removeFamilyMember(id: String, dialog1: Dialog) {
        if (AppUtil.isNetworkAvailable(this@FamilyMemberList)) {
            dialogManager!!.showProcessDialog(this@FamilyMemberList, "", false, null)
            val call = AppConfig.api_Interface().removeFamilyMember(id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("FamilyMemberListRemove", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                runOnUiThread {
                                    val objRe = resp.optJSONObject("response")
                                    dialog1.dismiss()
                                    if (resp.optString("error_type")
                                            .equals("200", ignoreCase = true)
                                    ) {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        memberList
                                    } else {
                                        dialogManager!!.stopProcessDialog()
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                    }
                                }
                            } else {
                                if (resp.optString("status").equals("false", ignoreCase = true)) {
                                    dialogManager!!.stopProcessDialog()
                                    runOnUiThread {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        startActivity(Intent(this@FamilyMemberList,
                                            AddFamily::class.java))
                                        finish()
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvName,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun directExitxMember() {
        val dialog1 = Dialog(this@FamilyMemberList)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = "Are you sure Want to Exit?"
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener(View.OnClickListener {
                if (!AppUtil.isNetworkAvailable(this@FamilyMemberList)) {
                    AppUtil.showMsgAlert(binding!!.tvName,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION)
                    return@OnClickListener
                } else {
                    exitFamilyMember()
                }
                dialog1.dismiss()
            })
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun exitFamilyMember() {
        if (AppUtil.isNetworkAvailable(this@FamilyMemberList)) {
            dialogManager!!.showProcessDialog(this@FamilyMemberList, "", false, null)
            val call = AppConfig.api_Interface().exitFamilyMember()
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ExitFamilyMember", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                runOnUiThread {
                                    val objRe = resp.optJSONObject("response")
                                    if (resp.optString("error_type")
                                            .equals("200", ignoreCase = true)
                                    ) {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        memberList
                                    } else {
                                        dialogManager!!.stopProcessDialog()
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                    }
                                }
                            } else {
                                if (resp.optString("status").equals("false", ignoreCase = true)) {
                                    dialogManager!!.stopProcessDialog()
                                    runOnUiThread {
                                        AppUtil.showMsgAlert(binding!!.tvName,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        startActivity(Intent(this@FamilyMemberList,
                                            AddFamily::class.java))
                                        finish()
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvName,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}