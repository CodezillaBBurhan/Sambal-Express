package sambal.mydd.app.adapter

import android.app.Dialog
import android.content.Context
import sambal.mydd.app.beans.StorePointsDealsList
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sambal.mydd.app.utils.PreferenceHelper
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.*
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.activity.LatestProductDetails
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.activity.ActivityMyRewards
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.AdapstorepointdealslistBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AdapterStoreVoucherDealList(
    private val context: Context,
    private val mList: ArrayList<StorePointsDealsList>
) : RecyclerView.Adapter<AdapterStoreVoucherDealList.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdapstorepointdealslistBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val hd = mList[position]
        holder.binding.tvName.text = hd.dealName
        try {
            Picasso.with(context)
                .load(hd.dealImage)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
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
        holder.binding.ll.setOnClickListener {
            context.startActivity(Intent(context, LatestProductDetails::class.java)
                .putExtra("agentId", hd.agentId)
                .putExtra("product_id", hd.productId)
                .putExtra("", hd.productId))
        }
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
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                (context as ActivityMyRewards).runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (holder.binding.ivUnlike.visibility == View.VISIBLE) {
                                        holder.binding.ivUnlike.visibility = View.INVISIBLE
                                        holder.binding.ivLike.visibility = View.VISIBLE
                                    } else {
                                        holder.binding.ivLike.visibility = View.INVISIBLE
                                        holder.binding.ivUnlike.visibility = View.VISIBLE
                                    }
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
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.tvDays,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
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

    inner class MyViewHolder(var binding: AdapstorepointdealslistBinding) : RecyclerView.ViewHolder(
        binding.root)
}