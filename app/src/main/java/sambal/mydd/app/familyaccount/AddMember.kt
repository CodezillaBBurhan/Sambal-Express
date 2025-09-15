package sambal.mydd.app.familyaccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.text.TextWatcher
import android.text.Editable
import sambal.mydd.app.utils.AppUtil
import android.text.TextUtils
import android.util.Log
import android.view.View
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.widget.Toast
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AddfamilymemberBinding
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AddMember : AppCompatActivity() {
    private var binding: AddfamilymemberBinding? = null
    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.addfamilymember)
        init()
    }

    private fun init() {
        binding!!.phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    binding!!.btnRequest.text = "Add"
                    binding!!.llMember.visibility = View.GONE
                    binding!!.tvName.text = ""
                    binding!!.tvemail.text = ""
                    binding!!.profileImage.setImageResource(R.drawable.place_holder)
                }
            }
        })
        binding!!.header.ivBack.setOnClickListener { finish() }
        binding!!.btnRequest.setOnClickListener(View.OnClickListener {
            AppUtil.hideSoftKeyboard(this@AddMember)
            if (TextUtils.isEmpty(binding!!.phone.text.toString())) {
                AppUtil.showMsgAlert(binding!!.phone, "Enter family member mobile number")
                return@OnClickListener
            } else if (binding!!.phone.text.toString().length < 8) {
                AppUtil.showMsgAlert(binding!!.phone, "Enter minimum 8-13 digits mobile number")
                return@OnClickListener
            } else if (!AppUtil.isNetworkAvailable(this@AddMember)) {
                AppUtil.showMsgAlert(binding!!.phone, MessageConstant.MESSAGE_INTERNET_CONNECTION)
                return@OnClickListener
            } else if (binding!!.btnRequest.text.toString().equals("Add", ignoreCase = true)) {
                checkMember()
            } else if (userId.equals("", ignoreCase = true)) {
                checkMember()
            } else {
                addMember()
            }
        })
    }

    private fun checkMember() {
        binding!!.llMember.visibility = View.GONE
        if (AppUtil.isNetworkAvailable(this@AddMember)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().checkFamilyMember(
                binding!!.phone.text.toString().trim { it <= ' ' })
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("checkMember", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                runOnUiThread {
                                    binding!!.llMember.visibility = View.VISIBLE
                                    binding!!.tvName.text = resp.optString("userName")
                                    binding!!.tvemail.text = resp.optString("userEmail")
                                    binding!!.btnRequest.text = "Send Request"
                                    Toast.makeText(this@AddMember,
                                        resp.optString(KeyConstant.KEY_MESSAGE),
                                        Toast.LENGTH_SHORT).show()
                                    try {
                                        Picasso.with(this@AddMember)
                                            .load(resp.optString("userProfileName"))
                                            .placeholder(R.drawable.place_holder)
                                            .error(R.drawable.place_holder)
                                            .into(binding!!.profileImage)
                                    } catch (e: Exception) {
                                    }
                                    userId = resp.optString("userId")
                                    dialogManager.stopProcessDialog()
                                }
                            } else {
                                if (resp.optString("status").equals("false", ignoreCase = true)) {
                                    runOnUiThread {
                                        dialogManager.stopProcessDialog()
                                        binding!!.llMember.visibility = View.GONE
                                        userId = ""
                                        binding!!.btnRequest.text = "Add"
                                        binding!!.llMember.visibility = View.GONE
                                        Toast.makeText(this@AddMember,
                                            resp.optString(KeyConstant.KEY_MESSAGE),
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            userId = ""
                            binding!!.btnRequest.text = "Add"
                            runOnUiThread {
                                Toast.makeText(this@AddMember,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            userId = ""
                            binding!!.btnRequest.text = "Add"
                            runOnUiThread {
                                Toast.makeText(this@AddMember,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            dialogManager.stopProcessDialog()
                            userId = ""
                            binding!!.btnRequest.text = "Add"
                            binding!!.llMember.visibility = View.GONE
                            Toast.makeText(this@AddMember,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    runOnUiThread {
                        dialogManager.stopProcessDialog()
                        userId = ""
                        binding!!.btnRequest.text = "Add"
                        binding!!.llMember.visibility = View.GONE
                        Toast.makeText(this@AddMember, t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            runOnUiThread {
                userId = ""
                binding!!.btnRequest.text = "Add"
                AppUtil.showMsgAlert(binding!!.tvemail, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }
    }

    private fun addMember() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().addFamilyMember(userId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("AddMobileNumber", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                binding!!.btnRequest.text = "Add"
                                runOnUiThread {
                                    Toast.makeText(this@AddMember,
                                        resp.optString(KeyConstant.KEY_MESSAGE),
                                        Toast.LENGTH_SHORT).show()
                                    getMember(dialogManager)
                                }
                            } else {
                                if (resp.optString("status").equals("false", ignoreCase = true)) {
                                    dialogManager.stopProcessDialog()
                                    runOnUiThread {
                                        binding!!.llMember.visibility = View.GONE
                                        binding!!.btnRequest.text = "Add"
                                        Toast.makeText(this@AddMember,
                                            resp.optString(KeyConstant.KEY_MESSAGE),
                                            Toast.LENGTH_SHORT).show()
                                        binding!!.phone.setText("")
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Toast.makeText(this@AddMember,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Toast.makeText(this@AddMember,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvemail,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    runOnUiThread {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvemail, t.message)
                    }
                }
            })
        } else {
            ErrorMessage.T(this, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun getMember(dialogManager: DialogManager?) {
        val call = AppConfig.api_Interface().myFamilyMember
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    try {
                        val resp = JSONObject(response.body()!!.string())
                        Log.e("GetMember", resp.toString())
                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                        if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 || resp.optString("status")
                                .equals("true", ignoreCase = true)
                        ) {
                            val objRe = resp.optJSONObject("response")
                            runOnUiThread {
                                dialogManager?.stopProcessDialog()
                                if (objRe.optString("responseType")
                                        .equals("1", ignoreCase = true)
                                ) {
                                    finish()
                                }
                            }
                        } else {
                            if (resp.optString("status").equals("false", ignoreCase = true)) {
                                dialogManager?.stopProcessDialog()
                                runOnUiThread {
                                    Toast.makeText(this@AddMember,
                                        resp.optString(KeyConstant.KEY_MESSAGE),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dialogManager?.stopProcessDialog()
                        Toast.makeText(this@AddMember,
                            MessageConstant.MESSAGE_SOMETHING_WRONG,
                            Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        dialogManager?.stopProcessDialog()
                        Toast.makeText(this@AddMember,
                            MessageConstant.MESSAGE_SOMETHING_WRONG,
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    dialogManager?.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvemail, MessageConstant.MESSAGE_SOMETHING_WRONG)
                    Log.e("sendToken", "else is working" + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                ErrorMessage.E("ON FAILURE > " + t.message)
                dialogManager?.stopProcessDialog()
                AppUtil.showMsgAlert(binding!!.tvemail, t.message)
            }
        })
    }
}