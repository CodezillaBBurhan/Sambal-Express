package sambal.mydd.app.adapter

import android.content.Context
import android.graphics.Color
import sambal.mydd.app.beans.GiftCard
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.os.Bundle
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.activity.StoreGiftCardActivity
import sambal.mydd.app.databinding.RewardGiftCardBinding
import java.lang.Exception
import java.util.*

class WalletGiftCardAdapter(var context: Context, private val list: List<GiftCard>) :
    RecyclerView.Adapter<WalletGiftCardAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RewardGiftCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val giftCard = list[position]
            holder.binding.giftPriceTv.text =
                if (giftCard.currency != null) if (giftCard.currency != "null") giftCard.currency + giftCard.giftPrice else "" + giftCard.giftPrice else "" + giftCard.giftPrice
            holder.binding.actGiftPriceTv.text =
                if (giftCard.currency != null) if (giftCard.currency != "null") giftCard.currency + giftCard.giftPrice else "" + giftCard.giftPrice else "" + giftCard.giftPrice
            holder.binding.giftTextremarlTv.text = giftCard.giftTextRemark
            holder.binding.discountTv.text = giftCard.discountValue
            if (giftCard.giftStatus == 1) {
                holder.binding.buyLl.visibility = View.VISIBLE
                holder.binding.activeLl.visibility = View.GONE
                Glide.with(context).load(giftCard.giftBackgroundImage)
                    .placeholder(R.drawable.place_holder).error(
                    R.drawable.place_holder).into(holder.binding.cardBgImg)
                Glide.with(context).load(giftCard.agentImage).placeholder(R.drawable.place_holder)
                    .into(holder.binding.agentImage)
                val builder1 = SpannableStringBuilder()
                val str1 = SpannableString(giftCard.giftTextRemark)
                val str2 = SpannableString(giftCard.currency + giftCard.giftSellingPrice)
                str1.setSpan(ForegroundColorSpan(Color.parseColor("#111111")), 0, str1.length, 0)
                builder1.append(str1)
                str2.setSpan(ForegroundColorSpan(Color.parseColor("#2961F4")), 0, str2.length, 0)
                builder1.append(str2)
                holder.binding.sellingPriceTv.setText(builder1, TextView.BufferType.SPANNABLE)
                holder.binding.giftTextBuyTv.text = giftCard.giftText.uppercase(Locale.getDefault())
            } else if (giftCard.giftStatus == 2) {
                holder.binding.buyLl.visibility = View.GONE
                holder.binding.activeLl.visibility = View.VISIBLE
                Glide.with(context).load(giftCard.giftBackgroundImage)
                    .placeholder(R.drawable.place_holder).error(
                    R.drawable.place_holder).into(holder.binding.actCardBgImg)
                Glide.with(context).load(giftCard.agentImage).placeholder(R.drawable.place_holder)
                    .into(holder.binding.actAgentImage)
                holder.binding.cardLayout.setCardBackgroundColor(Color.parseColor(giftCard.borderColor))
                holder.binding.giftTextActiveTv.text =
                    giftCard.giftText.uppercase(Locale.getDefault())
                holder.binding.tvExpiriyDate.text = giftCard.giftExpireDate
                holder.binding.activeLl.setBackgroundResource(R.drawable.card_view_bg)
            }
            holder.binding.cardLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("Data", giftCard)
                ErrorMessage.I(context, StoreGiftCardActivity::class.java, bundle)
            }
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: RewardGiftCardBinding) : RecyclerView.ViewHolder(
        binding.root)
}