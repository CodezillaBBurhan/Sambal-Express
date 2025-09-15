package sambal.mydd.app.adapter

import sambal.mydd.app.beans.ChatBean
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.fragment.chat.ChatLocationFavourite
import sambal.mydd.app.callback.RecyclerClickListener
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.text.Html
import sambal.mydd.app.utils.PreferenceHelper
import android.content.Intent
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.*
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import android.widget.TextView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapchatBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AdapterChatByLocation(
    private val context: Context,
    private val modelList: List<ChatBean?>,
    recyclerView: RecyclerView?,
    private val fragment: ChatLocationFavourite,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var loading = false
    fun setLoaded() {
        loading = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdapchatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            try {
                for (i in modelList.indices) {
                    if (modelList[i]!!.agentFavourite.equals("0", ignoreCase = true)) {
                        binding.ivLike.visibility = View.GONE
                        binding.ivUnlike.visibility = View.VISIBLE
                    } else {
                        binding.ivLike.visibility = View.VISIBLE
                        binding.ivUnlike.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
            }
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder!!.setIsRecyclable(false)
        if (holder is ViewHolder) {
            val model = modelList[position]
            try {
                if (model!!.chatCount.equals("0", ignoreCase = true)) {
                    holder.binding.llMsgCount.visibility = View.GONE
                    holder.binding.tvCount.text = ""
                } else {
                    holder.binding.llMsgCount.visibility = View.VISIBLE
                    holder.binding.tvCount.text = model.chatCount + ""
                    holder.binding.chatTimeTv.text = model.chatTime + ""
                }
            } catch (e: Exception) {
            }
            try {
                if (model!!.followingStatus.equals("0", ignoreCase = true)) {
                    holder.binding.llMsgCount.visibility = View.GONE
                    holder.binding.tvCount.text = ""
                } else if (model.chatCount.equals("0", ignoreCase = true)) {
                    holder.binding.llMsgCount.visibility = View.GONE
                }
            } catch (e: Exception) {
            }
            val image = model!!.agentImage
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()

//                Picasso.with(context).load(image)
//                    .fit()
//                    .transform(transformation)
//                    .placeholder(context.resources.getDrawable(R.drawable.place_holder))
//                    .error(context.resources.getDrawable(R.drawable.place_holder)).into(
//                        holder.binding.cardBgImg)

                Glide
                    .with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder)
                    .into(holder.binding.cardBgImg);


            } catch (e: Exception) {
                holder.binding.cardBgImg.setImageResource(R.drawable.place_holder)
            }
            if (model.agentFavourite.equals("0", ignoreCase = true)) {
                holder.binding.ivLike.visibility = View.GONE
                holder.binding.ivUnlike.visibility = View.VISIBLE
            } else {
                holder.binding.ivLike.visibility = View.VISIBLE
                holder.binding.ivUnlike.visibility = View.GONE
            }
            val agentId = model.agentId.toInt()
            val title = model.agentCompanyName
            holder.binding.tvTitle.text = title
            holder.binding.tvSubHeading.text = model.agentDescription
            val text =
                "<font color=#101010>" + model.agentAddress + "</font> <font color=#007cfa>" + " (" + model.agentDistance + ")" + "</font>"
            holder.binding.tvLocation.text = Html.fromHtml(text)
            val rating = model.agentRating.toInt()
            holder.binding.tvRatingText.text = "$rating  "
            holder.binding.tvRating.text = "$rating  "
            val productId = model.productId
            holder.binding.llMain.setOnClickListener {
                fragment.gotoChat(model.agentId,
                    model.agentCompanyName,
                    model.isAdmin,
                    model.followingStatus,
                    position)
                Handler().postDelayed({
                    try {
                        modelList[position]!!.chatCount = "0"
                        holder.binding.llMsgCount.visibility = View.GONE
                        holder.binding.tvCount.text = ""
                    } catch (e: Exception) {
                    }
                }, 2000)
            }
            holder.binding.ivLike.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                    commonPopup(model.agentId, holder, position)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }
            }
            holder.binding.ivUnlike.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                    unFavAgent(model.agentId, holder, position)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }
            }
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    private fun unFavAgent(agentId: String, holder: ViewHolder, pos: Int) {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = AppConfig.api_Interface().updateFavouriteChatAgent(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ResponFa", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                (context as Activity).runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (holder.binding.ivUnlike.visibility == View.VISIBLE) {
                                        holder.binding.ivUnlike.visibility = View.GONE
                                        holder.binding.ivLike.visibility = View.VISIBLE
                                        modelList[pos]!!.agentFavourite = "1"
                                    } else {
                                        holder.binding.ivLike.visibility = View.GONE
                                        holder.binding.ivUnlike.visibility = View.VISIBLE
                                        modelList[pos]!!.agentFavourite = "0"
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(holder.binding.tvLocation,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.tvLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.tvLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(holder.binding.tvLocation,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(holder.binding.tvLocation, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(holder.binding.tvLocation,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private fun commonPopup(agentId: String, holder: RecyclerView.ViewHolder, pos: Int) {
        val dialog1 = Dialog(context)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = "Are you sure you want to remove from favourites?"
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                unFavAgent(agentId, holder as ViewHolder, pos)
                dialog1.dismiss()
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    private inner class ViewHolder internal constructor(var binding: AdapchatBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}