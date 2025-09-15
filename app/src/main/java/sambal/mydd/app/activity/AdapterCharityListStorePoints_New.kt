package sambal.mydd.app.activity

import android.content.Context
import sambal.mydd.app.beans.CharityListBean
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.makeramen.roundedimageview.RoundedImageView
import sambal.mydd.app.R
import java.lang.Exception
import java.util.ArrayList

class AdapterCharityListStorePoints_New(
    private val context: Context,
    private val arr: ArrayList<CharityListBean>,
    private val fragment: New_AgentDetails,
    private val cb: RadioButton
) : RecyclerView.Adapter<AdapterCharityListStorePoints_New.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.adaptercharitylist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cm = arr[position]
        holder.tvCharityName.text = cm.charityName
        holder.tvCharityPooints.text = cm.charitySubName
        holder.tvJoinred.text = cm.charityJoinedText
        if (cm.charityDescription == "") {
            holder.tvCharityDescribtion.visibility = View.GONE
        } else {
            holder.tvCharityDescribtion.visibility = View.VISIBLE
            holder.tvCharityDescribtion.text = cm.charityDescription
        }
        if (cm.charityWebURL == "") {
            holder.tvCharityurl.visibility = View.GONE
        } else {
            holder.tvCharityurl.visibility = View.VISIBLE
            holder.tvCharityurl.text = cm.charityWebURL
        }
        Glide.with(context)
            .load(cm.charityImage)
            .placeholder(R.drawable.sponplaceholder)
            .error(R.drawable.sponplaceholder)
            .into(holder.ivMainImage)
        holder.tvCharityName.setOnClickListener {
            val uri = Uri.parse(cm.charityWebURL) // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }

        //final RadioButton rb = new RadioButton(this.context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.rg.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context, R.color.defult_background))
        }

        //holder.rg.addView(rb);
        if (cm.charityStatus.equals("1", ignoreCase = true)) {
            holder.rg.isChecked = true
            New_AgentDetails.charity_id2 = cm.charityId
            cb.isChecked = false
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.rg.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(
                        context, R.color.colorPrimary))
                }
            } catch (e: Exception) {
            }
        } else {
            holder.rg.isChecked = false
        }
        holder.rg.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.rg.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(
                        context, R.color.colorPrimary))
                }
            } catch (e: Exception) {
            }
            if (holder.rg.isChecked) {
                for (i in arr.indices) {
                    if (i == position) {
                        holder.rg.isChecked = true
                        arr[i].charityStatus = "1"
                        New_AgentDetails.charity_id2 = cm.charityId
                        //fragment.getCharityList(cm.charityId)
                        cb.isChecked = false
                        notifyDataSetChanged()
                    } else {
                        holder.rg.isChecked = false
                        arr[i].charityStatus = "0"
                        cb.isChecked = false
                        notifyDataSetChanged()
                    }
                }
            } else {
                holder.rg.isChecked = false
                arr[position].charityStatus = "0"
                notifyDataSetChanged()
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
        return arr.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCharityName: TextView
        var tvCharityPooints: TextView
        var tvJoinred: TextView
        var tvCharityDescribtion: TextView
        var tvCharityurl: TextView
        var rg: RadioButton
        var ivMainImage: RoundedImageView

        init {
            tvCharityName = view.findViewById(R.id.tvCharityName)
            tvCharityPooints = view.findViewById(R.id.tvCharityPooints)
            tvJoinred = view.findViewById(R.id.tvJoinred)
            rg = view.findViewById(R.id.rg)
            ivMainImage = view.findViewById(R.id.ivMainImage)
            tvCharityDescribtion = view.findViewById(R.id.tvCharityDescribtion)
            tvCharityurl = view.findViewById(R.id.tvCharityurl)
        }
    }
}