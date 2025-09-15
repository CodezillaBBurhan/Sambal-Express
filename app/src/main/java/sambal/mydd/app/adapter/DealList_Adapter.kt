package sambal.mydd.app.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.activity.NewLeaflet_DetailActivity
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.beans.StorePointsDealsList
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.NewDealListAdapterBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DealList_Adapter(
    private val context: Context,
    private val mList: ArrayList<StorePointsDealsList?>,
    private val posi: Int,
) : RecyclerView.Adapter<DealList_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            NewDealListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val hd = mList[position]
        holder.binding.ll.visibility = View.VISIBLE
        holder.binding.tvName.text = hd!!.dealName
        try {
            Picasso.with(context)
                .load(hd.dealImage)
                .placeholder(R.drawable.mainimageplaceholder)
                .error(R.drawable.mainimageplaceholder)
                .into(holder.binding.iv)
        } catch (e: Exception) {
        }
        if (hd.productDiscountPercentageEnabled.equals("",
                ignoreCase = true) && hd.priceEnabledId.equals("0",
                ignoreCase = true) && hd.discountPriceEnabledId.equals("0", ignoreCase = true)
        ) {
        } else if (hd.productDiscountPercentageEnabled.equals("0",
                ignoreCase = true) && hd.priceEnabledId.equals("1",
                ignoreCase = true) && hd.discountPriceEnabledId.equals("0", ignoreCase = true)
        ) {
            holder.binding.tvPrice.text = hd.productCurrency + hd.productPrice
            holder.binding.tvFinalPrice.visibility = View.GONE
        } else if (hd.productDiscountPercentageEnabled.equals("1",
                ignoreCase = true) && hd.priceEnabledId.equals("1",
                ignoreCase = true) && hd.discountPriceEnabledId.equals("1", ignoreCase = true)
        ) {
            holder.binding.tvFinalPrice.visibility = View.VISIBLE
            holder.binding.tvFinalPrice.text = hd.productCurrency + hd.productFinalPrice
            holder.binding.tvPrice.paintFlags =
                holder.binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvPrice.text = hd.productCurrency + hd.productPrice
            holder.binding.tvDiscount.text = hd.productDiscountPercentage + " Off"
        }
        if (hd.dealFavourite.equals("0", ignoreCase = true)) {
            holder.binding.ivLike.visibility = View.GONE
            holder.binding.ivUnlike.visibility = View.VISIBLE
        } else {
            holder.binding.ivLike.visibility = View.VISIBLE
            holder.binding.ivUnlike.visibility = View.GONE
        }
        holder.binding.ivLike.setOnClickListener {
            if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                commonPopup(hd.dealId, holder)
            } else {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            }
        }
        holder.binding.ivUnlike.setOnClickListener {
            if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                updateFavoriteProduct(hd.dealId, holder)
            } else {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            }
        }
        holder.binding.tvDays.text = "Expire on " + hd.dealExpiredDate
        if (hd.dealExclusiveStatus == "1") {
            holder.binding.llExclusive.visibility = View.VISIBLE
        } else {
            holder.binding.llExclusive.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            if (hd.type == "1") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", hd.agentId)
                    .putExtra("productId", hd.productId))
            } else if (hd.type == "2") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", hd.agentId)
                    .putExtra("productId", hd.productId))
            } else if (hd.type == "3") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", hd.agentId)
                    .putExtra("productId", hd.productId))
            } else if (hd.type == "4") {
                (context as LatestProductDetails).moveToDetails(hd.dealId,
                    hd.agentId,
                    position,
                    posi)
            }
        }

        // Set referenced IDs f
    // or the Flow

        // Set referenced IDs for the Flow
        holder.binding.flow.referencedIds = intArrayOf(
            holder.binding.tvFinalPrice.id,
            holder.binding.tvPrice.id,
            holder.binding.tvDiscount.id
        )

/*        // Create Flow

        val constraintLayout = ConstraintLayout(context)
        constraintLayout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val flow = Flow(context)
        flow.setId(View.generateViewId())
        flow.setWrapMode(Flow.WRAP_CHAIN)
        flow.setHorizontalStyle(Flow.CHAIN_SPREAD_INSIDE)
        flow.setVerticalAlign(Flow.VERTICAL_ALIGN_TOP)
        flow.setVerticalStyle(Flow.CHAIN_PACKED)
        flow.setHorizontalGap(8)
        flow.setMaxElementsWrap(2)
        flow.setReferencedIds(intArrayOf( holder.binding.tvFinalPrice.getId(), holder.binding.tvPrice.getId(), holder.binding.tvDiscount.getId()))

        // Add Flow to the ConstraintLayout

        // Add Flow to the ConstraintLayout
        constraintLayout.addView(flow)


        // Create TextViews
        val tvFinalPrice = TextView(context).apply {
            id = View.generateViewId()
            text =  holder.binding.tvFinalPrice.text.toString()
            textSize = 12f
            ellipsize = android.text.TextUtils.TruncateAt.END
            setSingleLine(false)
        }

        val tvPrice = TextView(context).apply {
            id = View.generateViewId()
            text =  holder.binding.tvPrice.text.toString()
            textSize = 12f
            ellipsize = android.text.TextUtils.TruncateAt.END
            setSingleLine(false)
        }

        val tvDiscount = TextView(context).apply {
            id = View.generateViewId()
            text =  holder.binding.tvDiscount.text.toString()
            textSize = 12f
            ellipsize = android.text.TextUtils.TruncateAt.END
            setSingleLine(false)
        }

        // Add TextViews to the ConstraintLayout
        constraintLayout.addView(tvFinalPrice)
        constraintLayout.addView(tvPrice)
        constraintLayout.addView(tvDiscount)
        // Apply constraints to Flow

        // Apply constraints to Flow
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(flow.getId(),
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP)
        constraintSet.connect(flow.getId(),
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START)
        constraintSet.applyTo(constraintLayout)

        // Set the ConstraintLayout as the content view

        // Set the ConstraintLayout as the content view
        setContentView(constraintLayout)*/
    }

    private fun commonPopup(productId: String, holder: RecyclerView.ViewHolder) {
        val dialog1 = Dialog(context)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = "Are you sure you want to remove from favourites?"
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                updateFavoriteProduct(productId, holder as MyViewHolder)
                dialog1.dismiss()
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun updateFavoriteProduct(productId: String, holder: MyViewHolder) {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = AppConfig.api_Interface().updateFavouriteDeal(productId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            ErrorMessage.E("updateFavoriteProduct>12>$resp")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                ErrorMessage.E("updateFavoriteProduct>>$resp")
                                (context as Activity).runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (holder.binding.ivUnlike.visibility == View.VISIBLE) {
                                        holder.binding.ivUnlike.visibility = View.GONE
                                        holder.binding.ivLike.visibility = View.VISIBLE
                                    } else {
                                        holder.binding.ivLike.visibility = View.GONE
                                        holder.binding.ivUnlike.visibility = View.VISIBLE
                                    }
                                    (context as LatestProductDetails).refreshFavToDetails(productId)
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(holder.binding.tvDays,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.tvDays,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(holder.binding.tvDays,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(holder.binding.tvDays, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(holder.binding.tvDays, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    inner class MyViewHolder(var binding: NewDealListAdapterBinding) : RecyclerView.ViewHolder(
        binding.root)
}