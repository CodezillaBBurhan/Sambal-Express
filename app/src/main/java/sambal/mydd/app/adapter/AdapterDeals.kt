package sambal.mydd.app.adapter

import sambal.mydd.app.beans.DealListModel
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.content.Intent
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.authentication.SignUpActivity
import android.widget.ProgressBar
import android.widget.TextView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.*
import sambal.mydd.app.R
import sambal.mydd.app.activity.DailyDeals
import sambal.mydd.app.activity.LatestDeals
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.ExpiredSoonItemView2Binding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AdapterDeals(
    private val context: Context,
    private val mList: ArrayList<DealListModel?>,
    var recyclerView: RecyclerView,
    var Check: String
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
            val binding = ExpiredSoonItemView2Binding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
            vh = MyViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val hd = mList[position]
            if ((Check == "Location")) {
                holder.binding.favraiteMain.visibility = View.VISIBLE
                if ((hd!!.productFavourite == "1")) {
                    holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                    holder.binding.ivNonFavoriteIcon.visibility = View.GONE
                } else if ((hd.productFavourite == "0")) {
                    holder.binding.ivFavoriteIcon.visibility = View.GONE
                    holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
                }
            } else if ((Check == "DailyDeal")) {
                holder.binding.favraiteMain.visibility = View.VISIBLE
                if ((hd!!.productFavourite == "1")) {
                    holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                    holder.binding.ivNonFavoriteIcon.visibility = View.GONE
                } else if ((hd.productFavourite == "0")) {
                    holder.binding.ivFavoriteIcon.visibility = View.GONE
                    holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
                }
                holder.binding.ddLoyaltyPriceLayout.visibility = View.VISIBLE
                holder.binding.regularPriceLayout.visibility = View.VISIBLE
            } else {
                holder.binding.favraiteMain.visibility = View.VISIBLE
                if ((hd!!.productFavourite == "1")) {
                    holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                    holder.binding.ivNonFavoriteIcon.visibility = View.GONE
                } else if ((hd.productFavourite == "0")) {
                    holder.binding.ivFavoriteIcon.visibility = View.GONE
                    holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
                }
            }
            holder.binding.tvagentName.setText(
                hd.productAgentName)
            holder.binding.llAgent.visibility = View.VISIBLE
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context)
                    .load(hd.productImage)
                    .transform(transformation)
                    .fit()
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.mainimageplaceholder)
                    .into(holder.binding.cardBgImg)
            } catch (e: Exception) {
            }
            if (!hd.productAgentImage.equals("https://www.dealdio.com/upload/empty_deal.jpg",
                    ignoreCase = true)
            ) {
                try {
                    val transformation = RoundedTransformationBuilder()
                        .oval(false)
                        .build()
                    Picasso.with(context)
                        .load(hd.productAgentImage)
                        .transform(transformation)
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.place_holder)
                        .into(holder.binding.ivMainImage)
                } catch (e: Exception) {
                }
            }
            holder.binding.tvProductName.text = hd.productName
            holder.binding.tvLocationmiles.setText(
                hd.productDistance)
            holder.binding.tvLeftDays.text = "Expire on " + hd.dealExpiredDate
            try {
                if ((Check == "DailyDeal")) {
                    if ((hd.productDDLoyaltyPrice == "")) {
                        holder.binding.ddLoyaltyPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding.ddLoyaltyPriceLayout.visibility = View.VISIBLE
                        holder.binding.ddPriceTv.text =
                            "" + hd.productCurrency + hd.productDDLoyaltyPrice
                    }
                    if ((hd.productPrice == "")) {
                        holder.binding.regularPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding.regularPriceLayout.visibility = View.VISIBLE
                        holder.binding.regularPriceTv.text =
                            "" + hd.productCurrency + hd.productPrice
                    }
                }
            } catch (e: Exception) {
            }
            holder.binding.llAgent.setOnClickListener(
                View.OnClickListener {
                    context.startActivity(Intent(context, New_AgentDetails::class.java)
                        .putExtra("agentId", hd.productAgentId)
                        .putExtra("direct", "non_direct"))
                })
            if ((hd.productDiscountPercentageEnabled.toInt() == 0) && (hd.priceEnabledId.toInt() == 0) && (hd.discountPriceEnabledId.toInt() == 0)) {
            } else if ((hd.productDiscountPercentageEnabled.toInt() == 0) && (hd.priceEnabledId.toInt() == 1) && (hd.discountPriceEnabledId.toInt() == 0)) {
                holder.binding.tvFinalPrice.text = hd.productCurrency + hd.productPrice
                holder.binding.tvFinalPrice.setTextColor(Color.parseColor("#101010"))
            } else if ((hd.productDiscountPercentageEnabled.toInt() == 1) && (hd.priceEnabledId.toInt() == 1) && (hd.discountPriceEnabledId.toInt() == 1)) {
                holder.binding.tvPrice.setTextColor(Color.parseColor("#AAAAAA"))
                holder.binding.tvFinalPrice.visibility = View.VISIBLE
                holder.binding.tvFinalPrice.text = hd.productCurrency + hd.productFinalPrice
                holder.binding.tvPrice.paintFlags =
                    (holder.binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
                holder.binding.tvPrice.text = hd.productCurrency + hd.productPrice
                holder.binding.tvDiscount.text = hd.productDiscountPercentage + " OFF"
            }
            holder.binding.rootLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if ((Check == "")) {
                        (context as LatestDeals).moveToDetails(hd.productId + "",
                            hd.productAgentId + "",
                            position)
                    } else if ((Check == "DailyDeal")) {
                        (context as DailyDeals).moveToDetails(hd.productId + "",
                            hd.productAgentId + "",
                            position)
                    } /* else if (Check.equals("Location")) {
                        ((ByLocationDeals) context).moveToDetails(hd.getProductId() + "", hd.getProductAgentId() + "", position);
                    }*/ else {
                        context.startActivity(Intent(context, LatestProductDetails::class.java)
                            .putExtra("agentId", hd.productAgentId + "")
                            .putExtra("product_id", hd.productId + ""))
                    }
                }
            })
            try {
                Log.e("Lines", holder.binding.tvProductName.lineCount.toString() + "")
            } catch (e: Exception) {
            }

            /*===============================*/holder.binding.ivFavoriteIcon.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                            //updateFavoriteProduct(productId, holder);
                            commonPopup(hd.productId, holder, position)
                        } else {
                            val intent = Intent(context, SignUpActivity::class.java)
                            context.startActivity(intent)
                            //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
                        }
                    }
                })
            holder.binding.ivNonFavoriteIcon.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                        updateFavoriteProduct(hd.productId, holder, position)
                    } else {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                        //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
                    }
                }
            })
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    inner class MyViewHolder(var binding: ExpiredSoonItemView2Binding) : RecyclerView.ViewHolder(
        binding.root)

    /*======================*/
    private fun commonPopup(productId: String, holder: MyViewHolder, pos: Int) {
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

        //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    updateFavoriteProduct(productId, holder, pos)
                    dialog1.dismiss()
                }
            })
            btnNo.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    dialog1.dismiss()
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun updateFavoriteProduct(productId: String, holder: MyViewHolder, posi: Int) {
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
                            Log.e("updateFavoriteProduct", "" + resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                    ignoreCase = true)
                            ) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                (context as Activity).runOnUiThread(object : Runnable {
                                    override fun run() {
                                        dialogManager.stopProcessDialog()
                                        if (holder.binding.ivNonFavoriteIcon.visibility == View.VISIBLE) {
                                            holder.binding.ivNonFavoriteIcon.visibility =
                                                View.INVISIBLE
                                            holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                                        } else {
                                            holder.binding.ivNonFavoriteIcon.visibility =
                                                View.VISIBLE
                                            holder.binding.ivFavoriteIcon.visibility =
                                                View.INVISIBLE
                                        }
                                    }
                                })
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(holder.binding.cardBgImg,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.cardBgImg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(holder.binding.cardBgImg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(holder.binding.cardBgImg,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(holder.binding.cardBgImg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(holder.binding.cardBgImg,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}