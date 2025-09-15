package sambal.mydd.app.adapter

import android.app.Dialog
import sambal.mydd.app.activity.New_AgentDetails
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import sambal.mydd.app.utils.PreferenceHelper
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.*
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.activity.NewLeaflet_DetailActivity
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.AdaprecommendeddealsBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AdapterRecommendedHorizontal(
    private val context: New_AgentDetails,
    private val arr: JSONArray,
    productId: String
) : RecyclerView.Adapter<AdapterRecommendedHorizontal.MyViewHolder>() {
    private val poss = -1
    private var `val` = false
    var isLike = false
    var productId = ""

    init {
        this.productId = productId
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = arr.optJSONObject(position)
        Log.e("Adap", obj.toString() + "")
        if (position == poss) {
            `val` = true
            if (holder.binding.ivFavoriteIcon.visibility == View.VISIBLE) {
            }
        } else {
        }
        if (obj.optString("type") == "1") {
            try {
                holder.binding.ll.visibility = View.GONE
                holder.binding.playIconImgBtn.visibility = View.GONE
                holder.binding.newVideoLayout.visibility = View.VISIBLE
                val params =
                    holder.binding.mainConstraint.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                holder.binding.mainConstraint.layoutParams = params
                try {
                    val set = ConstraintSet()
                    set.clone(holder.binding.mainConstraint)
                    set.setDimensionRatio(holder.binding.imageLayout.id, "1:0.6")
                    set.applyTo(holder.binding.mainConstraint)
                } catch (ew: Exception) {
                }
                Glide.with(context)
                    .load(obj.optString("dealImage"))
                    .error(R.drawable.mainimageplaceholder) // show error drawable if the image is not a gif
                    .into(holder.binding.videoImg)
                holder.binding.agentNameTv.text = "" + obj.optString("dealName")
                holder.binding.totalViewCountTv.text = "" + obj.optString("viewCount")
            } catch (e: Exception) {
            }
        } else if (obj.optString("type") == "2") {
            try {
                holder.binding.ll.visibility = View.GONE
                holder.binding.playIconImgBtn.visibility = View.GONE
                holder.binding.newVideoLayout.visibility = View.VISIBLE
                Glide.with(context)
                    .load(obj.optString("dealImage"))
                    .error(R.drawable.mainimageplaceholder) // show error drawable if the image is not a gif
                    .into(holder.binding.videoImg)
                holder.binding.agentNameTv.text = "" + obj.optString("dealName")
                holder.binding.totalViewCountTv.text = "" + obj.optString("viewCount")
            } catch (e: Exception) {
            }
        } else if (obj.optString("type") == "3") {
            try {
                holder.binding.ll.visibility = View.GONE
                holder.binding.newVideoLayout.visibility = View.VISIBLE
                Picasso.with(context)
                    .load(obj.optString("dealImage"))
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.mainimageplaceholder)
                    .into(holder.binding.videoImg)
                holder.binding.agentNameTv.text = "" + obj.optString("dealName")
                holder.binding.totalViewCountTv.text = "" + obj.optString("viewCount")
            } catch (e: Exception) {
            }
        } else if (obj.optString("type") == "4") {
            holder.binding.ll.visibility = View.VISIBLE
            holder.binding.newVideoLayout.visibility = View.GONE
            if (obj.optString("dealExclusiveStatus").equals("1", ignoreCase = true)) {
                holder.binding.llExclusive.visibility = View.VISIBLE
            } else {
                holder.binding.llExclusive.visibility = View.GONE
            }
            if (obj.optString("dealFavourite").equals("0", ignoreCase = true)) {
                holder.binding.ivFavoriteIcon.visibility = View.GONE
                holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
            } else {
                holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                holder.binding.ivNonFavoriteIcon.visibility = View.GONE
            }
            try {
                Picasso.with(context)
                    .load(obj.optString("dealImage"))
                    .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                    .error(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                    .into(holder.binding.ivDealImage)
            } catch (e: Exception) {
                Log.e("ex", e.toString())
            }
            holder.binding.tvDealName.text = obj.optString("dealName")
            if (obj.optString("productDiscountPercentageEnabled")
                    .equals("", ignoreCase = true) && obj.optString("priceEnabledId")
                    .equals("0", ignoreCase = true) && obj.optString("discountPriceEnabledId")
                    .equals("0", ignoreCase = true)
            ) {
            } else if (obj.optString("productDiscountPercentageEnabled")
                    .equals("0", ignoreCase = true) && obj.optString("priceEnabledId")
                    .equals("1", ignoreCase = true) && obj.optString("discountPriceEnabledId")
                    .equals("0", ignoreCase = true)
            ) {
                holder.binding.tvFinalPrice.visibility = View.VISIBLE
                holder.binding.tvFinalPrice.text =
                    obj.optString("productCurrency") + obj.optString("productPrice")
            } else if (obj.optString("productDiscountPercentageEnabled")
                    .equals("1", ignoreCase = true) && obj.optString("priceEnabledId")
                    .equals("1", ignoreCase = true) && obj.optString("discountPriceEnabledId")
                    .equals("1", ignoreCase = true)
            ) {
                holder.binding.tvPrice.setTextColor(Color.parseColor("#AAAAAA"))
                holder.binding.tvFinalPrice.visibility = View.VISIBLE
                holder.binding.tvFinalPrice.text =
                    obj.optString("productCurrency") + obj.optString("productFinalPrice")
                holder.binding.tvPrice.paintFlags =
                    holder.binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.tvPrice.text =
                    obj.optString("productCurrency") + obj.optString("productPrice")
                holder.binding.tvDiscount.text = obj.optString("productDiscountPercentage") + " OFF"
            }
            val productFavorite = obj.optString("dealFavourite").toInt()
            if (productFavorite == 1) {
                holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                holder.binding.ivNonFavoriteIcon.visibility = View.GONE
            } else {
                holder.binding.ivFavoriteIcon.visibility = View.GONE
                holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
            }
            holder.binding.tvDays.text = "Expires on  " + obj.optString("dealExpiredDate")
            holder.binding.ivFavoriteIcon.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                    isLike = true
                    commonPopup(obj.optString("dealId"), holder)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }
            }
            holder.binding.ivNonFavoriteIcon.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                    isLike = false
                    updateFavoriteProduct(obj.optString("dealId"), holder)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
        val agentId = obj.optString("agentId")
        val dealId = obj.optString("dealId")
        holder.itemView.setOnClickListener {
            if (obj.optString("type") == "1") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", obj.optString("agentId"))
                    .putExtra("productId", obj.optString("dealId")))
            } else if (obj.optString("type") == "2") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", obj.optString("agentId"))
                    .putExtra("productId", obj.optString("dealId")))
            } else if (obj.optString("type") == "3") {
                context.startActivity(Intent(context, NewLeaflet_DetailActivity::class.java)
                    .putExtra("agentId", obj.optString("agentId"))
                    .putExtra("productId", obj.optString("dealId")))
            } else if (obj.optString("type") == "4") {
                context.moveToDetailsScreen(agentId, dealId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdaprecommendeddealsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    fun updateData(pos: Int, fav: String) {
        try {
            arr.optJSONObject(pos).put("dealFavourite", fav.toInt())
            notifyItemChanged(pos)
        } catch (e: Exception) {
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
                updateFavoriteProduct(productId, holder)
                dialog1.dismiss()
            }
            //
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun updateFavoriteProduct(productId: String, holder: RecyclerView.ViewHolder) {
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
                                context.runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (isLike) {
                                        (holder as MyViewHolder).binding.ivFavoriteIcon.visibility =
                                            View.GONE
                                        holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
                                    } else {
                                        (holder as MyViewHolder).binding.ivFavoriteIcon.visibility =
                                            View.VISIBLE
                                        holder.binding.ivNonFavoriteIcon.visibility = View.GONE
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvDays,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arr.length()
    }

    inner class MyViewHolder(var binding: AdaprecommendeddealsBinding) : RecyclerView.ViewHolder(
        binding.root)
}