package sambal.mydd.app.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.graphics.Typeface
import android.widget.TextView
import android.content.Intent
import android.graphics.Color
import android.text.style.StyleSpan
import sambal.mydd.app.R
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.databinding.MyPromotionListAdapterBinding
import sambal.mydd.app.models.MyPromotion.PromotionDeals
import java.lang.Exception

class My_Promotion_List_Adapter(
    var context: Context,
    var CustomerLists: List<PromotionDeals>,
    var Check: Int
) : RecyclerView.Adapter<My_Promotion_List_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MyPromotionListAdapterBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = CustomerLists[position]
        holder.binding.agentTv.text = model.productAgentName
        holder.binding.agentAddressTv.text = model.productAgentAddress
        holder.binding.validUntileTv.text = "Valid until : " + model.dealExpiredDate
        holder.binding.promotionTitleTv.text = model.productOffer
        holder.binding.promotionTitleTv.setTextColor(Color.parseColor(model.productOfferColor))
        holder.binding.agentTv.isSingleLine = false
        val builder = SpannableStringBuilder()
        val str3 = SpannableString(model.productAgentAddress)
        str3.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.shader_color)),
            0,
            str3.length,
            0)
        str3.setSpan(StyleSpan(Typeface.BOLD), 0, str3.length, 0)
        builder.append(str3)
        val str6 = SpannableString("(" + model.productDistance + ")")
        str6.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.bluesheme)),
            0,
            str6.length,
            0)
        builder.append(str6)
        holder.binding.agentAddressTv.setText(builder, TextView.BufferType.SPANNABLE)
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

    inner class MyViewHolder(var binding: MyPromotionListAdapterBinding) : RecyclerView.ViewHolder(
        binding.root)
}