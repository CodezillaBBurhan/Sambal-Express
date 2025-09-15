package sambal.mydd.app.adapter

import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.beans.ReportedChatListBean
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapreprtchatBinding
import sambal.mydd.app.utils.DateConversion
import java.lang.Exception
import java.util.ArrayList

class AdapterReportChat(
    private val context: ChatMain,
    private val mList: ArrayList<ReportedChatListBean>
) : RecyclerView.Adapter<AdapterReportChat.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            AdapreprtchatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rb = mList[position]
        if (TextUtils.isEmpty(rb.imageURL)) {
            holder.binding.iv.visibility = View.GONE
        } else {
            holder.binding.iv.visibility = View.VISIBLE
            try {
                Glide.with(context)
                    .load(rb.imageURL)
                    .dontAnimate() // will load image
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.mainimageplaceholder)
                    .into(holder.binding.iv)
            } catch (e: Exception) {
            }
        }
        if (TextUtils.isEmpty(rb.comments)) {
            holder.binding.textComment.visibility = View.GONE
            holder.binding.tvComent.visibility = View.GONE
        } else {
            holder.binding.textComment.visibility = View.VISIBLE
            holder.binding.tvComent.text = rb.comments
            holder.binding.tvComent.visibility = View.VISIBLE
        }
        holder.binding.tvMsg.text = rb.message
        holder.binding.tvMsgName.text = rb.reportedUserName
        holder.binding.tvReportTime.text = DateConversion.Datechangeformat(rb.reportDate)
        holder.binding.tvReportedBy.text = "Reported By " + rb.userName
        holder.binding.iv.setOnClickListener { context.fullscreenImage(rb.imageURL) }
        holder.binding.tvDelete.setOnClickListener {
            context.deleteReportedChat(rb.reportId,
                position,
                rb.timeToken)
        }
        holder.binding.tvKeep.setOnClickListener {
            context.keepReportedChat(rb.reportId,
                position,
                rb.timeToken)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolder(var binding: AdapreprtchatBinding) : RecyclerView.ViewHolder(
        binding.root)
}