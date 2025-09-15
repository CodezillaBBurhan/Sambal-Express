package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.CharityListBean
import sambal.mydd.app.activity.ActivityStorePoints
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.content.Intent
import android.os.Build
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdaptercharitylistBinding
import sambal.mydd.app.utils.ErrorMessage
import java.lang.Exception
import java.util.ArrayList

class AdapterCharityListStorePoints(
    private val context: Context,
    private val arr: ArrayList<CharityListBean>,
    private val fragment: ActivityStorePoints,
    private val cb: RadioButton
) : RecyclerView.Adapter<AdapterCharityListStorePoints.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdaptercharitylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cm = arr[position]
        holder.binding.tvCharityName.setText(cm.charityName)
        holder.binding.tvCharityPooints.setText(cm.charitySubName)
        holder.binding.tvJoinred.setText(cm.charityJoinedText)
        if (cm.charityDescription == "") {
            holder.binding.tvCharityDescribtion.setVisibility(View.GONE)
        } else {
            holder.binding.tvCharityDescribtion.setVisibility(View.VISIBLE)
            holder.binding.tvCharityDescribtion.setText(cm.charityDescription)
        }
        if (cm.charityWebURL == "") {
            holder.binding.tvCharityurl.setVisibility(View.GONE)
        } else {
            holder.binding.tvCharityurl.setVisibility(View.VISIBLE)
            holder.binding.tvCharityurl.setText(cm.charityWebURL)
        }
        Glide.with(context)
            .load(cm.charityImage)
            .placeholder(R.drawable.sponplaceholder)
            .error(R.drawable.sponplaceholder)
            .into(holder.binding.ivMainImage)
        holder.binding.tvCharityName.setOnClickListener(View.OnClickListener {
            val uri = Uri.parse(cm.charityWebURL) // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.rg.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(
                context, R.color.defult_background)))
        }
        if (cm.charityStatus.equals("1", ignoreCase = true)) {
            holder.binding.rg.setChecked(true)
            AdapterStorePoints.charity_id = cm.charityId
            cb.isChecked = false
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.binding.rg.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(
                        context, R.color.colorPrimary)))
                }
            } catch (e: Exception) {
            }
        } else {
            holder.binding.rg.setChecked(false)
        }
        holder.binding.rg.setOnClickListener(View.OnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.binding.rg.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(
                        context, R.color.colorPrimary)))
                }
            } catch (e: Exception) {
            }
            if (holder.binding.rg.isChecked()) {
                for (i in arr.indices) {
                    if (i == position) {
                        holder.binding.rg.setChecked(true)
                        arr[i].charityStatus = "1"
                        AdapterStorePoints.charity_id = cm.charityId
                        ErrorMessage.E("charityId 4: " + cm.charityId)
                        fragment.getCharityList(cm.charityId)
                        cb.isChecked = false
                        notifyDataSetChanged()
                    } else {
                        holder.binding.rg.setChecked(false)
                        arr[i].charityStatus = "0"
                        cb.isChecked = false
                        notifyDataSetChanged()
                    }
                }
            } else {
                holder.binding.rg.setChecked(false)
                arr[position].charityStatus = "0"
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    inner class MyViewHolder(var binding: AdaptercharitylistBinding) : RecyclerView.ViewHolder(
        binding.root)
}