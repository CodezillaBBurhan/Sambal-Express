package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.AgentList
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.content.Intent
import android.view.View
import sambal.mydd.app.activity.WebviewStore
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapgrocerylistBinding
import java.lang.Exception
import java.util.ArrayList

class AdapterGroceryList(
    private val context: Context,
    private val mList: ArrayList<AgentList>,
    rvMemberDetails: RecyclerView?,
    isGrocery: Boolean,
    nestedScrollView: NestedScrollView?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var loading = false
    private var isGrocery = true

    init {
        this.isGrocery = isGrocery
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdapgrocerylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = MyViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.progressbar, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val alm = mList[position]
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context)
                    .load(alm!!.agentImage)
                    .transform(transformation)
                    .placeholder(R.drawable.placeholder_smallsize)
                    .error(R.drawable.placeholder_smallsize)
                    .into(holder.binding.iv)
            } catch (e: Exception) {
            }
            if (isGrocery) {
                holder.binding.llOrder.visibility = View.VISIBLE
            } else {
                holder.binding.llOrder.visibility = View.GONE
            }
            holder.binding.tvName.text = alm!!.agentName
            holder.binding.tvAddress.text = alm.agentAddress
            holder.binding.tvLocation.text = alm.agentDistance
            holder.binding.tvPostCode.text = alm.agentPostcode
            if (alm.agentStoreClickAndCollect.equals("0", ignoreCase = true)) {
                holder.binding.tvClick.visibility = View.GONE
                holder.binding.view.visibility = View.GONE
            } else {
                holder.binding.tvClick.visibility = View.VISIBLE
                holder.binding.view.visibility = View.VISIBLE
            }
            if (alm.agentStoreDelivery.equals("0", ignoreCase = true)) {
                holder.binding.tvDelivery.visibility = View.GONE
                holder.binding.view.visibility = View.GONE
            } else {
                holder.binding.tvDelivery.visibility = View.VISIBLE
                holder.binding.view.visibility = View.VISIBLE
            }
            holder.binding.tvOrder.setOnClickListener(
                View.OnClickListener {
                    context.startActivity(Intent(context, WebviewStore::class.java)
                        .putExtra("title", "Store")
                        .putExtra("url", alm.agentStoreURL))
                })
            holder.binding.ivOrder.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    holder.binding.tvOrder.performClick()
                }
            })
        } else {
            ProgressViewHolder.progressBar.isIndeterminate = true
        }
    }

    fun setLoaded() {
        loading = false
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }

        companion object {
            lateinit var progressBar: ProgressBar
        }
    }

    inner class MyViewHolder(var binding: AdapgrocerylistBinding) : RecyclerView.ViewHolder(
        binding.root)
}