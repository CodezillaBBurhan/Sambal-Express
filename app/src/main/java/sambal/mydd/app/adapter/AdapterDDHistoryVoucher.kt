package sambal.mydd.app.adapter

import android.content.Context
import android.graphics.Color
import sambal.mydd.app.beans.VoucherBean
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.callback.RecyclerClickListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.PorterDuff
import android.view.View
import android.widget.ProgressBar
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapnewvoucherBinding

class AdapterDDHistoryVoucher(
    private val context: Context,
    private val modelList: List<VoucherBean?>,
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
                AdapnewvoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            holder.binding.tvagentName.text = model!!.agentName
            holder.binding.tvPoints.text = model.ticketCurrency + " " + model.ticketPrice
            holder.binding.tvDate.text = model.redeemDate
            holder.binding.tvPts.visibility = View.GONE
            holder.binding.tvCollected.text = model.redeemTypeStatus
            holder.binding.ll.background.setColorFilter(Color.parseColor(
                model.colorCode), PorterDuff.Mode.SRC_IN)
        } else {
            (holder as ProgressViewHolder?)!!.progressBar.isIndeterminate = true
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
            progressBar.visibility = View.GONE
        }
    }

    private inner class ViewHolder internal constructor(var binding: AdapnewvoucherBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}