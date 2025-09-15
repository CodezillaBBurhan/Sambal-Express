package sambal.mydd.app.adapter

import android.content.Context
import android.graphics.Color
import sambal.mydd.app.beans.CategoryModel
import sambal.mydd.app.fragment.NearMeHomeFragment
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NewcatddBinding
import java.lang.Exception
import java.util.ArrayList

class AdapterCategoriesNearMe(
    private val context: Context,
    private val mList: ArrayList<CategoryModel>,
    var fragment: NearMeHomeFragment,
    check_id: String
) : RecyclerView.Adapter<AdapterCategoriesNearMe.MyViewHolder>() {
    var `val` = false
    var Check_id = ""
    var index = -1

    init {
        Check_id = check_id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NewcatddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cm = mList[position]
        if (Check_id == cm.id) {
            holder.binding.ll.background =
                context.resources.getDrawable(R.drawable.background_selected_yellow)
        } else if (index == position) {
            holder.binding.ll.background =
                context.resources.getDrawable(R.drawable.background_selected_yellow)
        } else if (Check_id == "-2") {
            if (position == 0) {
                holder.binding.ll.background =
                    context.resources.getDrawable(R.drawable.background_selected_yellow)
            } else {
                holder.binding.ll.setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            holder.binding.ll.setBackgroundColor(Color.TRANSPARENT)
        }
        if (poss == -1) {
            `val` = true
            poss = 0
            holder.binding.tvCatName.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            if (position == poss) {
                `val` = true
                holder.binding.tvCatName.setTextColor(context.resources.getColor(R.color.white))
            } else {
                holder.binding.tvCatName.setTextColor(context.resources.getColor(R.color.colorPrimary))
            }
        }
        holder.binding.tvCatName.text = cm.name
        try {
            Picasso.with(context)
                .load(cm.image)
                .placeholder(R.drawable.roundplaceholder)
                .error(R.drawable.roundplaceholder)
                .into(holder.binding.iv)
        } catch (e: Exception) {
        }
        holder.binding.ll.setOnClickListener {
            poss = position
            index = position
            Check_id = ""
            notifyDataSetChanged()
            fragment.refreshDetals(cm.id)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class MyViewHolder(var binding: NewcatddBinding) : RecyclerView.ViewHolder(
        binding.root)

    companion object {
        var poss = -1
    }
}