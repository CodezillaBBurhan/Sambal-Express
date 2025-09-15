package sambal.mydd.app.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import sambal.mydd.app.activity.MY_PromotionActivity
import sambal.mydd.app.utils.MyLog
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import sambal.mydd.app.activity.LatestProductDetails
import android.widget.TextView
import android.widget.LinearLayout
import sambal.mydd.app.R
import sambal.mydd.app.models.MyDeal_Models.PromotionDeals
import java.lang.Exception

class My_Promotion_Adapter(
    var context: Context,
    var CustomerLists: List<PromotionDeals>,
    var Check: Int
) : RecyclerView.Adapter<My_Promotion_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View
        itemView = if (Check == -2) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_promotion_popup_adapter, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_promotion_adapter, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = CustomerLists[position]
        if (Check - 1 == position) {
            holder.more_layout.visibility = View.VISIBLE
        } else {
            holder.more_layout.visibility = View.GONE
        }
        holder.agent_tv.text = model.productAgentName
        holder.promotion_title_tv.text = model.productOffer
        holder.promotion_title_tv.setTextColor(Color.parseColor(model.productOfferColor))
        holder.more_layout.setOnClickListener {
            context.startActivity(Intent(context, MY_PromotionActivity::class.java))
            MyLog.onAnim(context as Activity)
        }
        holder.itemView.setOnClickListener {
            try {
                context.startActivity(Intent(context, LatestProductDetails::class.java)
                    .putExtra("agentId", model.productAgentId.toString() + "")
                    .putExtra("product_id", model.productId.toString() + ""))
            } catch (e: Exception) {
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
        return CustomerLists.size
    }

    inner class MyViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!) {
        var promotion_title_tv: TextView
        var agent_tv: TextView
        var more_layout: LinearLayout

        init {
            promotion_title_tv = itemView.findViewById(R.id.promotion_title_tv)
            agent_tv = itemView.findViewById(R.id.agent_tv)
            more_layout = itemView.findViewById(R.id.more_layout)
        }
    }
}