package sambal.mydd.app.adapter

import sambal.mydd.app.beans.FollowingAgentModel
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.callback.RecyclerClickListener
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.text.Html
import android.widget.TextView
import android.content.Intent
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.KeyConstant
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.*
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import android.widget.ProgressBar
import sambal.mydd.app.R
import sambal.mydd.app.databinding.FollowAgentListItemViewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AdapterFollowing(
    private val context: Context,
    private val modelList: MutableList<FollowingAgentModel>,
    recyclerView: RecyclerView?,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var loading = false
    fun setLoaded() {
        loading = false
    }

    fun setLoadedfalse() {
        loading = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                FollowAgentListItemViewBinding.inflate(LayoutInflater.from(parent.context),
                    parent,
                    false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = modelList[position]
            val image = model!!.agentImage
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context).load(image)
                    .fit()
                    .transform(transformation)
                    .placeholder(context.resources.getDrawable(R.drawable.place_holder))
                    .error(context.resources.getDrawable(R.drawable.place_holder)).into(
                        holder.binding.cardBgImg)
            } catch (e: Exception) {
                holder.binding.cardBgImg.setImageResource(R.drawable.place_holder)
            }
            val agentId = model.agentId.toInt()
            val title = model.agentCompanyName
            holder.binding.tvTitle.text = title
            holder.binding.tvSubHeading.text = model.agentDescription
            holder.binding.tvLocationName.text = model.agentAddress
            val rating = model.agentRating.toInt()
            holder.binding.tvRating.text = rating.toString() + ""
            holder.binding.ratingBar.rating = rating.toFloat()
            holder.binding.tvratingssss.text =
                "$rating  "
            holder.binding.tvSponsered.text = model.agentEnableDescription
            holder.binding.tvRatingText.text = "(" + model.agentRatingCount + " ratings)"
            val text =
                "<font color=#101010>" + model.agentAddress + "</font> <font color=#007cfa>" + " (" + model.agentDistance + "mi)" + "</font>"
            holder.binding.tvLocation.text = Html.fromHtml(text)
            val productId = model.productId
            holder.binding.llUnfollowBtn.setOnClickListener {
                val dialog1 = Dialog(context)
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog1.setContentView(R.layout.popup_common)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog1.window!!.attributes)
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog1.window!!.attributes = lp
                val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
                contentText.text = "Are you sure you want to unfollow ?"
                val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
                btnNo.text = "No"
                val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
                btnOk.text = "Yes"
                dialog1.setCancelable(false)
                dialog1.show()
                try {
                    btnOk.setOnClickListener {
                        dialog1.dismiss()
                        unFollowAgent(agentId.toString() + "", position, holder)
                    }
                    btnNo.setOnClickListener { dialog1.dismiss() }
                } catch (e: Exception) {
                }
            }
            holder.binding.cardView.setOnClickListener {
                val intent = Intent(context, New_AgentDetails::class.java)
                intent.putExtra("direct", "false")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId.toString() + "")
                intent.putExtra("product_id", productId + "")
                context.startActivity(intent)
            }
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.visibility = View.GONE
            holder.progressBar.isIndeterminate = false
        }
    }

    fun removeAt(position: Int) {
        modelList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, modelList.size)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private fun unFollowAgent(agentId: String, position: Int, holder: RecyclerView.ViewHolder) {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = AppConfig.api_Interface().updateUnFollowAgent(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            ErrorMessage.E("unFollowAgent >> $resp")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                (context as Activity).runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    removeAt(position)
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert((holder as ViewHolder).binding.tvSponsered,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    private inner class ViewHolder internal constructor(var binding: FollowAgentListItemViewBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}