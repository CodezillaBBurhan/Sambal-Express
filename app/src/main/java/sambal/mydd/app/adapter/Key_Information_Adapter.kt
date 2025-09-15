package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.models.Refer_Friends_Model.KeyInformation
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.databinding.KeyInformationAdapterBinding

class Key_Information_Adapter(var context: Context, var CustomerLists: List<KeyInformation>) :
    RecyclerView.Adapter<Key_Information_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            KeyInformationAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val customerList = CustomerLists[position]
        val count = position + 1
        holder.binding.countTv.text = "" + count
        holder.binding.titleTv.text = customerList.keyTitle
        holder.binding.describtionTv.text = customerList.keyDescription
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return CustomerLists.size
    }

    inner class MyViewHolder(var binding: KeyInformationAdapterBinding) : RecyclerView.ViewHolder(
        binding.root)
}