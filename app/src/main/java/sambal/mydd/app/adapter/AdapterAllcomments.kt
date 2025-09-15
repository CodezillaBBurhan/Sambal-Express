package sambal.mydd.app.adapter

import android.app.Dialog
import android.util.Log
import android.view.*
import sambal.mydd.app.activity.AgentAllComments
import sambal.mydd.app.beans.DealComments
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONArray
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.AdapcommentsBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AdapterAllcomments(
    private val context: AgentAllComments,
    private val mList: ArrayList<DealComments?>,
    rvMemberDetails: RecyclerView?,
    var agentId: String?,
    var productId: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var loading = false
    override fun getItemViewType(position: Int): Int {
        return if (mList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdapcommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = MyViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.progressbar, parent, false
            )
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val alm = mList[position]
            try {
                Picasso.with(context).load(mList[position]!!.userImage)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder)
                    .into(holder.binding.ivCommentPhoto)
            } catch (e: Exception) {
                holder.binding.ivCommentPhoto.setImageResource(R.drawable.sponplaceholder)
            }
            holder.binding.tvRating.text = alm!!.userRating
            holder.binding.ratingBars.rating = alm.userRating?.toFloat() ?: 0f
            holder.binding.tvName.text = alm.userName
            holder.binding.tvComment.text = alm.userComments
            holder.binding.tvTime.text = alm.userCommentsDate

            /*if (alm.getUserOwnComments().equalsIgnoreCase("1")) {
                ((MyViewHolder) holder).ivOwn.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).ivOwn.setVisibility(View.GONE);
            }*/try {
                val arr = JSONArray(mList[position]!!.getmReplyList().toString())
                if (arr.length() > 0) {
                    holder.binding.rvReply.visibility = View.VISIBLE
                    val adap = AdapterAllReplyComments(context, arr)
                    holder.binding.rvReply.layoutManager = LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false
                    )
                    holder.binding.rvReply.adapter = adap
                    adap.notifyDataSetChanged()
                } else {
                    holder.binding.rvReply.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("ExConm", e.toString())
            }
            holder.binding.ivOwn.setOnClickListener {
                val dialog1 = Dialog(context, R.style.NewDialog)
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog1.setContentView(R.layout.editcomment)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog1.window!!.attributes)
                lp.width = WindowManager.LayoutParams.FILL_PARENT
                lp.height = WindowManager.LayoutParams.FILL_PARENT
                dialog1.window!!.attributes = lp
                val llEdit = dialog1.findViewById<LinearLayout>(R.id.llEdit)
                val llDelete = dialog1.findViewById<LinearLayout>(R.id.llDelete)
                val llCancel = dialog1.findViewById<LinearLayout>(R.id.Cancel)
                llEdit.setOnClickListener {
                    dialog1.dismiss()
                    context.editComment(alm.commentsId, alm.userRating, alm.userComments)
                }
                llDelete.setOnClickListener {
                    dialog1.dismiss()
                    context.deleteComment(alm.commentsId, holder.binding.tvComment, position)
                }
                llCancel.setOnClickListener { dialog1.dismiss() }
                dialog1.show()
            }
        } else {
            ProgressViewHolder.progressBar.isIndeterminate = true
        }
    }

    fun setLoaded() {
        loading = false
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }

        companion object {
            lateinit var progressBar: ProgressBar
        }
    }

    inner class MyViewHolder(var binding: AdapcommentsBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    private fun deleteComment(commentId: String, tvComment: TextView, pos: Int) {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            Log.e("omme", commentId)
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
                                context.runOnUiThread {
                                    try {
                                        mList.removeAt(pos)
                                        notifyItemRemoved(pos)
                                        notifyItemRangeChanged(pos, mList.size)
                                        AgentAllComments.isDelete = true
                                    } catch (e: Exception) {
                                        Log.e("Removeddd", e.toString())
                                    }
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        tvComment,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
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
                        AppUtil.showMsgAlert(tvComment, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(tvComment, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(tvComment, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }
}