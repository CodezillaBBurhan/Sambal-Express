package sambal.mydd.app.adapter

import android.util.Log
import sambal.mydd.app.familyaccount.FamilyMemberList
import sambal.mydd.app.beans.FamilyList
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import sambal.mydd.app.utils.SharedPreferenceVariable
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.databinding.AdapfamilydetailsBinding
import java.util.ArrayList

class AdapterFamilyList(
    private val context: FamilyMemberList,
    private val mlist: ArrayList<FamilyList>,
    var userAccess: String
) : RecyclerView.Adapter<AdapterFamilyList.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapfamilydetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val fm = mlist[position]
        holder.binding.tvName.text = fm.userName
        Log.e("userId",
            SharedPreferenceVariable.loadSavedPreferences(context, KeyConstant.KEY_USER_ID).toString())
        if (userAccess == "1" && fm.userRemoveAccess == "0") {
            holder.binding.llAdmin.visibility = View.GONE
            holder.binding.llExit.visibility = View.GONE
            holder.binding.tvExit.text = "REMOVE"
            holder.binding.tvRequest.visibility = View.GONE
            holder.binding.llCancel.visibility = View.GONE
        } else if (userAccess == "1" && fm.userRemoveAccess == "1" && fm.userAcceptStatus == "1") {
            holder.binding.llAdmin.visibility = View.GONE
            holder.binding.llExit.visibility = View.VISIBLE
            holder.binding.tvExit.text = "REMOVE"
            holder.binding.tvRequest.visibility = View.GONE
            holder.binding.llCancel.visibility = View.GONE
        } else if (userAccess == "1" && fm.userRemoveAccess == "1" && fm.userAcceptStatus == "0") {
            holder.binding.llAdmin.visibility = View.GONE
            holder.binding.llExit.visibility = View.GONE
            holder.binding.llCancel.visibility = View.VISIBLE
            holder.binding.tvExit.text = "CANCEL"
            holder.binding.tvRequest.visibility = View.VISIBLE
            holder.binding.tvRequest.text = fm.userAcceptText
        } else if (userAccess == "0" && SharedPreferenceVariable.loadSavedPreferences(context,
                KeyConstant.KEY_USER_ID) == fm.userId
        ) {
            holder.binding.llAdmin.visibility = View.GONE
            holder.binding.llExit.visibility = View.VISIBLE
            holder.binding.tvExit.text = "EXIT"
            holder.binding.tvRequest.visibility = View.GONE
            holder.binding.llCancel.visibility = View.GONE
            //}
        } else if (userAccess == "0" && SharedPreferenceVariable.loadSavedPreferences(context,
                KeyConstant.KEY_USER_ID) == fm.userId
        ) {
            holder.binding.llAdmin.visibility = View.GONE
            holder.binding.llExit.visibility = View.GONE
            holder.binding.tvExit.text = "EXIT"
            holder.binding.tvRequest.visibility = View.GONE
            holder.binding.llCancel.visibility = View.GONE
            //}
        }
        holder.binding.llExit.setOnClickListener {
            Log.e("if is working", ">1>")
            if (holder.binding.tvExit.text.toString() == "REMOVE") {
                context.removeMember(fm.userId, fm.userName)
            } else {
                context.directExitxMember()
            }
        }
        holder.binding.tvExit.setOnClickListener {
            Log.e("if is working", ">2>")
            if (holder.binding.tvExit.text.toString() == "REMOVE") {
                context.removeMember(fm.userId, fm.userName)
            } else {
                context.directExitxMember()
            }
        }
        holder.binding.llCancel.setOnClickListener { context.removeMember(fm.userId, fm.userName) }
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

    inner class MyViewHolder(var binding: AdapfamilydetailsBinding) : RecyclerView.ViewHolder(
        binding.root)
}