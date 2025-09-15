package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import sambal.mydd.app.R
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.databinding.AdapterAwardListBinding

class Adapter_awardList(var context: Context, private val awardlist: JSONArray) :
    RecyclerView.Adapter<Adapter_awardList.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapterAwardListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = awardlist.optJSONObject(position)
        Glide.with(context)
            .load(obj.optString("awardBanner"))
            .placeholder(R.drawable.mainimageplaceholder)
            .error(R.drawable.mainimageplaceholder)
            .into(holder.binding.ivVoteNow)
        holder.binding.cvVoteNow.setOnClickListener {
            holder.binding.cvVoteNow.isEnabled = false
            (context as New_AgentDetails).agentAwardDetailsToServer(obj.optInt("awardId"),
                true,
                -1) { holder.binding.cvVoteNow.isEnabled = true }
        }
    }

    override fun getItemCount(): Int {
        return awardlist.length()
    }

    inner class MyViewHolder(var binding: AdapterAwardListBinding) : RecyclerView.ViewHolder(
        binding.root)
}