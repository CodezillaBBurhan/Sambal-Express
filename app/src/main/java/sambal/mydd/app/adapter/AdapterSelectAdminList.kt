package sambal.mydd.app.adapter

import sambal.mydd.app.familyaccount.SelectAdmin
import sambal.mydd.app.beans.FamilyList
import androidx.recyclerview.widget.RecyclerView
import android.widget.RadioGroup
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.RadioButton
import android.os.Build
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapselectadminBinding
import java.util.ArrayList

class AdapterSelectAdminList(
    private val context: SelectAdmin,
    private val mlist: ArrayList<FamilyList>
) : RecyclerView.Adapter<AdapterSelectAdminList.MyViewHolder>() {
    private var lastCheckedRadioGroup: RadioGroup? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapselectadminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val fm = mlist[position]
        holder.binding.tvName.text = fm.userName
        val rb = RadioButton(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rb.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context, R.color.colrpurple))
        }
        holder.binding.rg.addView(rb)
        rb.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                context.selectAdmin(mlist[position].userId)
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
        return mlist.size
    }

    inner class MyViewHolder(var binding: AdapselectadminBinding) : RecyclerView.ViewHolder(
        binding.root) {
        init {
            binding.rg.setOnCheckedChangeListener { radioGroup, i -> //since only one package is allowed to be selected
                //this logic clears previous selection
                //it checks state of last radiogroup and
                // clears it if it meets conditions
                if (lastCheckedRadioGroup != null && (lastCheckedRadioGroup!!.getCheckedRadioButtonId()
                            != radioGroup.checkedRadioButtonId) && lastCheckedRadioGroup!!.getCheckedRadioButtonId() != -1
                ) {
                    lastCheckedRadioGroup!!.clearCheck()
                }
                lastCheckedRadioGroup = radioGroup
            }
        }
    }
}