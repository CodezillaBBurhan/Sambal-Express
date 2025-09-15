package sambal.mydd.app.adapter

import sambal.mydd.app.activity.ActivitLinkCard
import sambal.mydd.app.beans.DDCardList
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.databinding.AdapcardBinding
import java.util.ArrayList

class AdapterDdCardList(
    private val context: ActivitLinkCard,
    private val mList: ArrayList<DDCardList>
) : RecyclerView.Adapter<AdapterDdCardList.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdapcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dd = mList[position]
        holder.binding.tvCardNo.text = dd.userDDCardNo
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

    inner class MyViewHolder(var binding: AdapcardBinding) : RecyclerView.ViewHolder(
        binding.root)
}