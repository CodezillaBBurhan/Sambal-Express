package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.databinding.AdapnewagentcatBinding

class AdapterAgentDD(private val context: Context, private val arr: JSONArray) :
    RecyclerView.Adapter<AdapterAgentDD.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapnewagentcatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = arr.optJSONObject(position)
        holder.binding.tvName.text = obj.optString("categoryName") + "  "
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arr.length()
    }

    inner class MyViewHolder(var binding: AdapnewagentcatBinding) : RecyclerView.ViewHolder(
        binding.root)
}