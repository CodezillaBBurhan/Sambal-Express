package sambal.mydd.app.adapter

import android.util.Log
import sambal.mydd.app.activity.NewProfile
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.databinding.AdapprofileqroceBinding

class AdapterNewProfileCards(private val context: NewProfile, private val array: JSONArray?) :
    RecyclerView.Adapter<AdapterNewProfileCards.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapprofileqroceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = array!!.optJSONObject(position)
        Log.e("Arrray", obj.optString("userDDCardNo"))
        holder.binding.tvNumber.text = obj.optString("userDDCardNo")
        holder.binding.ivDelete.setOnClickListener { context.deleteCard(obj.optString("userDDCardId")) }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return array?.length() ?: 0
    }

    inner class MyViewHolder(var binding: AdapprofileqroceBinding) : RecyclerView.ViewHolder(
        binding.root)
}