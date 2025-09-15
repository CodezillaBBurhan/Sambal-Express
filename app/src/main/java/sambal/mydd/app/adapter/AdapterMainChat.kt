package sambal.mydd.app.adapter

import android.app.AlertDialog
import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.beans.ChatMainPubNubBean
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.utils.PreferenceHelper
import android.text.TextUtils
import sambal.mydd.app.utils.DateUtil
import com.bumptech.glide.Glide
import android.util.Log
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapmainchatBinding
import java.lang.Exception
import java.util.ArrayList

class AdapterMainChat(
    private val context: ChatMain,
    private val mlist: ArrayList<ChatMainPubNubBean>,
    var isAdmin: String
) : RecyclerView.Adapter<AdapterMainChat.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val adapmainchatBinding =
            AdapmainchatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(adapmainchatBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cm = mlist[position]
        val userId = PreferenceHelper.getInstance(context)?.userDetail?.userId.toString()
        if (cm.isAdmin.equals("1", ignoreCase = true)) {
            holder.binding.tvRightAdmin.visibility = View.VISIBLE
            holder.binding.tvLeftAdmin.visibility = View.VISIBLE
        } else {
            holder.binding.tvRightAdmin.visibility = View.GONE
            holder.binding.tvLeftAdmin.visibility = View.GONE
        }
        if (cm.userId.equals(userId, ignoreCase = true)) {
            if (cm.message.equals("", ignoreCase = true) || TextUtils.isEmpty(cm.message)) {
                holder.binding.tvRightMsg.visibility = View.GONE
            } else {
                holder.binding.tvRightMsg.visibility = View.VISIBLE
            }
            holder.binding.rootLayoutLeft.visibility = View.GONE
            holder.binding.rootLayoutRight.visibility = View.VISIBLE
            holder.binding.tvrighName.text = cm.userName
            holder.binding.tvRightMsg.text = cm.message
            holder.binding.tvRightDate.text = DateUtil.UTCToLocal(cm.date)
            if (!TextUtils.isEmpty(cm.image)) {
                holder.binding.ivRightimage.visibility = View.VISIBLE
                try {
                    Glide.with(context)
                        .load(cm.image)
                        .placeholder(R.drawable.mainimageplaceholder)
                        .error(R.drawable.mainimageplaceholder)
                        .into(holder.binding.ivRightimage)
                } catch (e: Exception) {
                }
            } else {
                holder.binding.ivRightimage.visibility = View.GONE
            }
        } else {
            holder.binding.rootLayoutLeft.visibility = View.VISIBLE
            holder.binding.rootLayoutRight.visibility = View.GONE
            holder.binding.tvleftName.text = cm.userName
            holder.binding.tvLeftMsg.text = cm.message
            holder.binding.tvLeftDate.text = DateUtil.UTCToLocal(cm.date)
            if (!TextUtils.isEmpty(cm.image)) {
                holder.binding.ivLeftimage.visibility = View.VISIBLE
                try {
                    Glide.with(context)
                        .load(cm.image)
                        .placeholder(R.drawable.mainimageplaceholder)
                        .error(R.drawable.mainimageplaceholder)
                        .into(holder.binding.ivLeftimage)
                } catch (e: Exception) {
                }
            } else {
                holder.binding.ivLeftimage.visibility = View.GONE
            }
        }
        holder.binding.ivLeftimage.setOnClickListener { context.fullscreenImage(cm.image) }
        holder.binding.ivRightimage.setOnClickListener { context.fullscreenImage(cm.image) }
        if (cm.userId.equals(userId, ignoreCase = true) || isAdmin.equals("1", ignoreCase = true)) {
            holder.binding.rootLayoutRight.setOnLongClickListener {
                val builder = AlertDialog.Builder(
                    context)
                builder.setTitle("Are you sure want to delete this message ?")
                builder.setPositiveButton("Yes") { dialog, id ->
                    if (position != mlist.size - 1) {
                        context.deleteMessage(cm.timeToken, position, mlist[position + 1].timeToken)
                        dialog.dismiss()
                    } else {
                        Log.e("1", "2")
                        context.deleteMessage("", position, mlist[position].timeToken)
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.show()
                false
            }
            holder.binding.rootLayoutLeft.setOnLongClickListener {
                val builder = AlertDialog.Builder(
                    context)
                builder.setTitle("Are you sure want to delete this message ?")
                builder.setPositiveButton("Yes") { dialog, id ->
                    if (position != mlist.size - 1) {
                        Log.e("1", "1")
                        Log.e("TinmeTo", cm.timeToken)
                        context.deleteMessage(cm.timeToken, position, mlist[position + 1].timeToken)
                        dialog.dismiss()
                    } else {
                        Log.e("1", "2")
                        context.deleteMessage("", position, mlist[position].timeToken)
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.show()
                false
            }
            holder.binding.ivRightimage.setOnLongClickListener {
                val builder = AlertDialog.Builder(
                    context)
                builder.setTitle("Are you sure want to delete this message ?")
                builder.setPositiveButton("Yes") { dialog, id ->
                    dialog.dismiss()
                    if (position != mlist.size - 1) {
                        Log.e("1", "1")
                        context.deleteMessage(cm.timeToken, position, mlist[position + 1].timeToken)
                        dialog.dismiss()
                    } else {
                        Log.e("1", "2")
                        context.deleteMessage("", position, mlist[position].timeToken)
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.show()
                false
            }
            holder.binding.ivLeftimage.setOnLongClickListener {
                val builder = AlertDialog.Builder(
                    context)
                builder.setTitle("Are you sure want to delete this message ?")
                builder.setPositiveButton("Yes") { dialog, id ->
                    dialog.dismiss()
                    if (position != mlist.size - 1) {
                        Log.e("1", "1")
                        context.deleteMessage(cm.timeToken, position, mlist[position + 1].timeToken)
                        dialog.dismiss()
                    } else {
                        Log.e("1", "2")
                        context.deleteMessage("", position, mlist[position].timeToken)
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.show()
                false
            }
        } else {
            holder.binding.rootLayoutRight.setOnLongClickListener {
                context.reportToAdmin(cm.userName, cm.message, cm.image, cm.timeToken, cm.userId)
                false
            }
            holder.binding.rootLayoutLeft.setOnLongClickListener {
                context.reportToAdmin(cm.userName, cm.message, cm.image, cm.timeToken, cm.userId)
                false
            }
            holder.binding.ivRightimage.setOnLongClickListener {
                context.reportToAdmin(cm.userName, cm.message, cm.image, cm.timeToken, cm.userId)
                false
            }
            holder.binding.ivLeftimage.setOnLongClickListener {
                context.reportToAdmin(cm.userName, cm.message, cm.image, cm.timeToken, cm.userId)
                false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    inner class MyViewHolder(var binding: AdapmainchatBinding) : RecyclerView.ViewHolder(
        binding.root)
}