package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.beans.DealComments
import sambal.mydd.app.adapter.AdapterAllcomments
import sambal.mydd.app.utils.DialogManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.CommentsBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sambal.mydd.app.fragment.NearMeHomeFragment.Companion.adapter
import sambal.mydd.app.utils.StatusBarcolor
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AgentAllComments : AppCompatActivity() {
    private lateinit var binding: CommentsBinding
    var productId: String? = null
    var agentId: String? = null
    var offset = 0
    var count = 10
    var context: Context? = null
    var mList = ArrayList<DealComments?>()
    var adapterAllcomments: AdapterAllcomments? = null
    var handler: Handler? = null
    var dialogManager: DialogManager? = null
    var isFirstTym = true
    private var mLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.comments)
        handler = Handler()
        context = this@AgentAllComments
        binding.ivBack.setOnClickListener {
            if (isDelete) {
                val `in` = Intent()
                setResult(RESULT_OK, `in`)
                finish()
            } else {
                finish()
            }
        }
        mList.clear()
        mLayoutManager = LinearLayoutManager(context)
        binding.rvComments.setHasFixedSize(false)
        binding.rvComments.layoutManager = mLayoutManager
        adapterAllcomments =
            AdapterAllcomments(this@AgentAllComments, mList, binding.rvComments, agentId, productId)
        binding.rvComments.adapter = adapterAllcomments
        intentdata
        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirstTym = false
                        getCommentsList(offset)
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    private val intentdata: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                mList.clear()
                agentId = bundle.getString("agentId")
                productId = bundle.getString("productId")
                dialogManager = DialogManager()
                dialogManager!!.showProcessDialog(this, "", false, null)
                isFirstTym = true
                offset = 0
                getCommentsList(offset)
            }
        }

    private fun getCommentsList(offset: Int) {
        if (AppUtil.isNetworkAvailable(this)) {
            val call = AppConfig.api_Interface()
                .getAgentUserReviews(agentId, offset.toString(), count.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("rAgentAllComments", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 && resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                val responseObj = resp.getJSONObject(KeyConstant.KEY_RESPONSE)
                                if (responseObj != null) {
                                    runOnUiThread {
                                        val arrRev = responseObj.optJSONArray("userReviews")
                                        if (isFirstTym && arrRev.length() == 0) {
                                            binding!!.tvComments.visibility = View.VISIBLE
                                            binding!!.rvComments.visibility = View.GONE
                                        } else {
                                            binding!!.tvComments.visibility = View.GONE
                                            binding!!.rvComments.visibility = View.VISIBLE
                                        }
                                        for (i in 0 until arrRev.length()) {
                                            val obj = arrRev.optJSONObject(i)
                                            val commentsId = obj.optString("commentsId")
                                            val userId = obj.optString("userId")
                                            val userOwnComments = obj.optString("userOwnComments")
                                            val userName = obj.optString("userName")
                                            val userImage = obj.optString("userImage")
                                            val userRating = obj.optString("userRating")
                                            val userComments = obj.optString("userComments")
                                            val userCommentsDate = obj.optString("userCommentsDate")
                                            val array = obj.optJSONArray("userReviewReply")
                                            val dc = DealComments(
                                                commentsId,
                                                userId,
                                                userOwnComments,
                                                userName,
                                                userImage,
                                                userRating,
                                                userComments,
                                                userCommentsDate,
                                                array
                                            )
                                            mList.add(dc)
                                            adapterAllcomments!!.notifyItemInserted(mList.size)
                                        }
                                        if (mList.size > 9) {
                                            adapterAllcomments!!.setLoaded()
                                        }
                                        dialogManager!!.stopProcessDialog()
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    if (isFirstTym) {
                                        AppUtil.showMsgAlert(
                                            binding!!.tvComments,
                                            MessageConstant.MESSAGE_SOMETHING_WRONG
                                        )
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvComments,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding!!.tvComments,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvComments, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvComments, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun editComment(id: String?, rating: String?, review: String?) {
        startActivityForResult(
            Intent(this@AgentAllComments, Rating_ReviewActivity::class.java)
                .putExtra("agentId", agentId)
                .putExtra("Edit", "true")
                .putExtra("id", id)
                .putExtra("rating", rating)
                .putExtra("review", review), 80
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 80) {
            if (AppUtil.isNetworkAvailable(context)) {
                dialogManager = DialogManager()
                dialogManager!!.showProcessDialog(this, "", false, null)
                mList.clear()
                adapterAllcomments!!.notifyDataSetChanged()
                mLayoutManager = LinearLayoutManager(context)
                binding!!.rvComments.setHasFixedSize(false)
                binding!!.rvComments.layoutManager = mLayoutManager
                adapterAllcomments = AdapterAllcomments(
                    this@AgentAllComments,
                    mList,
                    binding!!.rvComments,
                    agentId,
                    productId
                )
                binding!!.rvComments.adapter = adapterAllcomments
                isDelete = true
                offset = 0
                getCommentsList(0)
            } else {
                AppUtil.showMsgAlert(
                    binding!!.tvComments,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.e("isDe", isDelete.toString() + "")
        if (isDelete) {
            val `in` = Intent()
            setResult(RESULT_OK, `in`)
            finish()
        } else {
            finish()
        }
    }

    fun deleteComment(commentId: String?, tvComment: TextView?, pos: Int) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            Log.e("omme", commentId!!)
            val call = AppConfig.api_Interface().deleteProductRating(commentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("DeleetCometAllComment", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                try {
                                    mList.removeAt(pos)
                                    adapterAllcomments!!.notifyItemRemoved(pos)
                                    adapterAllcomments!!.notifyItemRangeChanged(pos, mList.size)
                                    isDelete = true
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        tvComment,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                } catch (e: Exception) {
                                    Log.e("Removeddd", e.toString())
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        tvComment,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(tvComment, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(tvComment, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvComments, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvComments, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    companion object {
        @JvmField
        var isDelete = false
    }
    public override fun onResume() {
        super.onResume()
        StatusBarcolor.setStatusbarColor(this, "")
    }
}