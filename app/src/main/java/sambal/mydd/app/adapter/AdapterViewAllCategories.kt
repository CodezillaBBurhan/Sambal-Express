package sambal.mydd.app.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.activity.Categories
import sambal.mydd.app.activity.DealByCat_IDActivity
import sambal.mydd.app.beans.CategoryModel
import sambal.mydd.app.databinding.AdapviewallcardBinding


class AdapterViewAllCategories(private val mList: ArrayList<CategoryModel>, var Check1: String, var context: Categories) : RecyclerView.Adapter<AdapterViewAllCategories.MyViewHolder>() {


    inner class MyViewHolder(view: AdapviewallcardBinding) : RecyclerView.ViewHolder(view.root) {

        var binding: AdapviewallcardBinding
        
        init {
            binding=view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdapviewallcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val cm = mList[position]

        Log.e("cm0", cm.id)
        Log.e("cm0", Check1)
        holder.binding.tvCatName.text = cm.name

        try {

            Picasso.with(context)
                    .load(cm.image)
                    .placeholder(R.drawable.roundplaceholder)
                    .error(R.drawable.roundplaceholder)
                    .into(holder.binding.iv)

        } catch (e: Exception) {
        }


        holder.itemView.setOnClickListener {

            if (Check1.equals("viewAll")) {

                val intent = Intent()
                intent.putExtra("Cat_id", cm.id)
                intent.putExtra("Cat_Name", cm.name)
                context.setResult(210, intent)
                context.finish()
            } else {
                val intent = Intent(context, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", cm.id)
                intent.putExtra("Cat_Name", cm.name)
                context.startActivity(intent)
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
                Log.e("mtir",mList.size.toString())
            return mList.size
    }

    companion object {
        var poss = -1
    }

}