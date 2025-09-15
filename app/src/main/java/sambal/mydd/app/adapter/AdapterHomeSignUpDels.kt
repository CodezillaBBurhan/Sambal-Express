package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.FreeDealsList
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import android.content.Intent
import android.graphics.Color
import android.view.View
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.activity.New_AgentDetails
import android.widget.ProgressBar
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdaphomefreedealsBinding
import java.lang.Exception
import java.util.ArrayList

class AdapterHomeSignUpDels(
    private val context: Context,
    private val modelList: ArrayList<FreeDealsList>,
    recyclerView: RecyclerView?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var loading = false
    fun setLoaded() {
        loading = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdaphomefreedealsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh

        //return new NearMeViewHolder(LayoutInflater.from(parent.getContext()), parent, context);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is ViewHolder) {
                //final JSONObject jsonObject = getItem(position);
                val model = modelList[position]
                try {
                    Picasso.with(context).load(model!!.productImage)
                        .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                        .error(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                        .into(holder.binding.cardBgImg)
                } catch (e: Exception) {
                    holder.binding.cardBgImg.setImageResource(R.drawable.mainimageplaceholder)
                }
                holder.binding.tvagentName.text = model!!.productAgentName
                try {
                    val transformation = RoundedTransformationBuilder()
                        .oval(false)
                        .build()
                    Picasso.with(context).load(model.productAgentImage)
                        .transform(transformation)
                        .fit()
                        .placeholder(context.resources.getDrawable(R.drawable.sponplaceholder))
                        .error(context.resources.getDrawable(R.drawable.sponplaceholder))
                        .into(holder.binding.ivAgentImage)
                } catch (e: Exception) {
                    holder.binding.ivAgentImage.setImageResource(R.drawable.mainimageplaceholder)
                }
                holder.binding.tvProductName.text = model.productName
                holder.binding.tvLocationmiles.text = model.productDistance
                holder.binding.tvFree.text = model.productType
                holder.binding.tvFree.setTextColor(Color.parseColor(
                    model.productTypeColor))
                holder.binding.tvRedeemTex.text = model.redeemedText
                holder.binding.tvLeftDays.text = "Expire on - " + model.dealExpiredDate
                holder.binding.llMain.setOnClickListener {
                    context.startActivity(Intent(context, LatestProductDetails::class.java)
                        .putExtra("agentId", model.productAgentId)
                        .putExtra("product_id", model.productId))

                    //recyclerClickListener.setCellClicked(jsonObject, "");
                }
                holder.binding.llAgent.setOnClickListener {
                    context.startActivity(Intent(context, New_AgentDetails::class.java)
                        .putExtra("agentId", model.productAgentId)
                        .putExtra("direct", "false"))
                }
            } else if (holder is ProgressViewHolder) {
                holder.progressBar.isIndeterminate = false
            }
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    private inner class ViewHolder internal constructor(var binding: AdaphomefreedealsBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}