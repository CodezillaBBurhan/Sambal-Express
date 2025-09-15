package sambal.mydd.app.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.StatusBarcolor
import me.zhanghai.android.materialratingbar.MaterialRatingBar.OnRatingChangeListener
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.app.Activity
import android.content.Context
import android.content.Intent
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import android.text.TextUtils
import android.util.Log
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NewratingreviewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class Rating_ReviewActivity() : BaseActivity(), View.OnClickListener {
    var binding: NewratingreviewBinding? = null
    var commentID: String? = ""
    var rating: String? = ""
    var review: String? = ""
    var agentId: String? = ""
    var isEdit: String? = "false"
    var context: Context? = null

    override val contentResId: Int
        get() = R.layout.newratingreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.newratingreview)
        setToolbarWithBackButton_colorprimary("Your Review")
        context = this@Rating_ReviewActivity
        init()
        intentData
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@Rating_ReviewActivity, "")
        } catch (e: Exception) {
        }
    }

    private fun init() {
        binding!!.tvsend.setOnClickListener(this)
        binding!!.rb.onRatingChangeListener = object : OnRatingChangeListener {
            override fun onRatingChanged(ratingBar: MaterialRatingBar, rating: Float) {
                binding!!.tvRating.text = rating.toString()
            }
        }
    }

    private val intentData: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                try {
                    agentId = bundle.getString("agentId")
                } catch (e: Exception) {
                }
                try {
                    isEdit = bundle.getString("Edit")
                    ErrorMessage.E("Edit : $isEdit")
                } catch (e: Exception) {
                }
                try {
                    commentID = bundle.getString("id")
                    Log.e("id", (commentID)!!)
                } catch (e: Exception) {
                }
                try {
                    rating = bundle.getString("rating")
                    binding!!.rb.rating = rating!!.toFloat()
                } catch (e: Exception) {
                }
                try {
                    review = bundle.getString("review")
                    binding!!.exComments.setText(review)
                } catch (e: Exception) {
                }
            }
        }

    private fun postComments() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().updateAgentRating(agentId,
                binding!!.rb.rating.toString(),
                binding!!.exComments.text.toString().trim { it <= ' ' },
                "")
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("PostComments", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if ((errorType == KeyConstant.KEY_RESPONSE_CODE_200)) {
                                (context as Activity?)!!.runOnUiThread(Runnable {
                                    binding!!.exComments.setText("")
                                    binding!!.exComments.clearFocus()
                                    binding!!.rb.rating = 0f
                                    commentID = ""
                                    binding!!.tvRating.text = "0.0"
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.exComments,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                    val `in` = Intent()
                                    setResult(RESULT_OK, `in`)
                                    finish()
                                })
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvsend,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvsend,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvsend,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvsend,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvsend, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvsend, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }

    private fun postEditCommentsComments(commentIds: String?) {
        Log.e("EditComment", "Edit")
        Log.e("CommentsId", (commentIds)!!)
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().updateAgentRating(agentId,
                binding!!.rb.rating.toString(),
                binding!!.exComments.text.toString().trim { it <= ' ' },
                commentIds)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if ((errorType == KeyConstant.KEY_RESPONSE_CODE_200)) {
                                (context as Activity?)!!.runOnUiThread(object : Runnable {
                                    override fun run() {
                                        binding!!.exComments.setText("")
                                        binding!!.exComments.clearFocus()
                                        binding!!.rb.rating = 0f
                                        commentID = ""
                                        binding!!.tvRating.text = "0.0"
                                        dialogManager.stopProcessDialog()
                                        AppUtil.showMsgAlert(binding!!.tvsend,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                        val `in` = Intent()
                                        setResult(RESULT_OK, `in`)
                                        finish()
                                    }
                                })
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvsend,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvsend,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvsend,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvsend,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvsend, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvsend, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvsend -> if (isEdit.equals("false", ignoreCase = true)) {
                if (binding!!.tvRating.text.toString().trim { it <= ' ' }
                        .equals("0.0", ignoreCase = true)) {
                    AppUtil.showMsgAlert(binding!!.tvsend, "Please give Rating")
                } else if (TextUtils.isEmpty(binding!!.exComments.text.toString()
                        .trim { it <= ' ' })
                ) {
                    AppUtil.showMsgAlert(binding!!.tvsend, "Please enter review")
                } else if (!AppUtil.isNetworkAvailable(context)) {
                    AppUtil.showMsgAlert(binding!!.tvsend,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION)
                } else {
                    postComments()
                }
            } else {
                if (TextUtils.isEmpty(binding!!.exComments.text.toString().trim { it <= ' ' })) {
                    AppUtil.showMsgAlert(binding!!.tvsend, "Please enter review")
                } else {
                    postEditCommentsComments(commentID)
                }
            }
        }
    }
}