package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.NearMeModel
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.fragment.NearMeHomeFragment
import sambal.mydd.app.callback.RecyclerClickListener
import android.view.ViewGroup
import android.view.LayoutInflater
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.text.Html
import android.view.View
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NearMeItemViewBinding
import java.lang.Exception

class NearMeAdapter(
    private val context: Context,
    private val modelList: List<NearMeModel?>,
    recyclerView: RecyclerView?,
    private val nestedScrollView: NestedScrollView,
    private val fragment: NearMeHomeFragment,
    private val recyclerClickListener: RecyclerClickListener
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
                NearMeItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = modelList[position]
            val image = model!!.agentImage
            if (!image.equals("https://www.dealdio.com/upload/empty_deal.jpg", ignoreCase = true)) {
                try {
                    val transformation = RoundedTransformationBuilder()
                        .oval(false)
                        .build()
                    Picasso.with(context).load(image)
                        .transform(transformation)
                        .fit()
                        .placeholder(context.resources
                            .getDrawable(R.drawable.place_holder))
                        .error(context.resources
                            .getDrawable(R.drawable.place_holder))
                        .into(holder.binding.cardBgImg)
                } catch (e: Exception) {
                    holder.binding.cardBgImg.setImageResource(R.drawable.place_holder)
                }
            }
            val title = model.agentCompanyName
            holder.binding.tvProductName.text = title
            holder.binding.tvDsc.text = model.agentDescription
            try {
                val rating = model.agentRating?.toInt()
                holder.binding.tvRating.text = model.agentRating
            } catch (e: Exception) {
            }
            val text =
                "<font color=#101010>" + model.agentAddress + "</font> <font color=#007cfa>" + " (" + model.agentDistance + ")" + "</font>"
            holder.binding.tvMiles.text = Html.fromHtml(text)
            val productId = model.productId
            holder.binding.rootLayout.setOnClickListener {
                fragment.gotoAgentDetails(model.agentId.toString(),
                    productId.toString(),
                    holder.getAdapterPosition())
            }
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
        private val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
            progressBar.visibility = View.GONE
        }
    }

    private inner class ViewHolder internal constructor(var binding: NearMeItemViewBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}