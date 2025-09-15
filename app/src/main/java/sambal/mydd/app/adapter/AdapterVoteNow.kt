package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.models.new_agent_details.CategoryName
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import android.os.Build
import android.content.res.ColorStateList
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.databinding.AdapterVoteNowBinding

class AdapterVoteNow(var context: Context, private val list: List<CategoryName>, var awardID: Int) :
    RecyclerView.Adapter<AdapterVoteNow.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapterVoteNowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val catList = list[position]
        holder.binding.tvCatName.text = catList.categoryName
        if (catList.voteStatus == 1) {
            holder.binding.tvThankyou.visibility = View.VISIBLE
            holder.binding.tvAlreadyVoted.setTextColor(context.resources.getColor(R.color.black))
            holder.binding.voteNowBtn.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(
                    R.color.already_voted_grey))
        } else if (catList.voteStatus == 0) {
            holder.binding.tvThankyou.visibility = View.GONE
            holder.binding.voteNowBtn.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(
                    R.color.blue_shade))
            holder.binding.tvAlreadyVoted.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.tvAlreadyVoted.text = "VOTE NOW"
        }
        holder.binding.voteNowBtn.setOnClickListener {
            if (catList.voteStatus == 1) {
                holder.binding.voteNowBtn.isClickable = false
            } else if (catList.voteStatus == 0) {
                (context as New_AgentDetails).emailValidation(1,
                    catList.categoryId,
                    awardID,
                    position) {
                    holder.binding.tvThankyou.visibility = View.VISIBLE
                    holder.binding.tvAlreadyVoted.setTextColor(context.resources.getColor(R.color.black))
                    holder.binding.voteNowBtn.backgroundTintList = ColorStateList.valueOf(
                        context.resources.getColor(R.color.already_voted_grey))
                    holder.binding.tvAlreadyVoted.text = "ALREADY VOTED"
                    holder.binding.voteNowBtn.isClickable = false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: AdapterVoteNowBinding) : RecyclerView.ViewHolder(
        binding.root)
}