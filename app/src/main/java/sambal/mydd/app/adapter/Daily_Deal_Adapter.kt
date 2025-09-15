package sambal.mydd.app.adapter

import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.content.Intent
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.activity.NewMyDealsActivity
import sambal.mydd.app.activity.SearchAllDealActivity
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.constant.IntentConstant
import android.widget.TextView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.*
import sambal.mydd.app.R
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.DailyDealAdapterBinding
import sambal.mydd.app.databinding.DailydealPopupBinding
import sambal.mydd.app.databinding.SearchDailyDealAdapterBinding
import sambal.mydd.app.models.MyDeal_Models.DailyDeals
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class Daily_Deal_Adapter(
    var context: Context,
    var CustomerLists: List<DailyDeals>,
    var Check: String,
    var Search: String
) : RecyclerView.Adapter<Daily_Deal_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (Search == "") {
            val itemView =
                DailyDealAdapterBinding.inflate(LayoutInflater.from(
                    parent.context), parent, false)
            MyViewHolder(itemView)
        } else if (Search == "popup") {
            val itemView =
                DailydealPopupBinding.inflate(LayoutInflater.from(parent.context),
                    parent,
                    false)
            MyViewHolder(itemView)
        } else {
            val itemView1 =
                SearchDailyDealAdapterBinding.inflate(LayoutInflater.from(
                    parent.context), parent, false)
            MyViewHolder(itemView1)
        }
        /* View vh = new Daily_Deal_Adapter.MyViewHolder(itemView);*/
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val hd = CustomerLists[position]
        if (Search == "") {
            if (hd.productFavourite == 1) {
                holder.binding!!.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_active))
            } else if (hd.productFavourite == 0) {
                holder.binding!!.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_deactive))
            }
            holder.binding!!.ddLoyaltyPriceLayout.visibility = View.GONE
            holder.binding!!.regularPriceLayout.visibility = View.GONE
            holder.binding!!.llAgent.setVisibility(View.VISIBLE)
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context)
                    .load(hd.productImage)
                    .transform(transformation)
                    .fit()
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.place_holder)
                    .into(holder.binding!!.cardBgImg)
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
                        .into(holder.binding!!.ivMainImage)
                } catch (e: Exception) {
                }
            }
            holder.binding!!.tvProductName.text = hd.productName
            holder.binding!!.tvLocationmiles.setText(hd.productDistance)
            holder.binding!!.tvLeftDays.text = "Expire on " + hd.dealExpiredDate
            try {
                if (Check == "DailyDeal") {
                    if (hd.productDDLoyaltyPrice == "") {
                        holder.binding!!.ddLoyaltyPriceLayout.visibility = View.INVISIBLE
                    } else {
                        holder.binding!!.ddLoyaltyPriceLayout.visibility = View.VISIBLE
                        holder.binding!!.ddPriceTv.text =
                            "" + hd.productCurrency + hd.productDDLoyaltyPrice
                    }
                    if (hd.productPrice == "") {
                        holder.binding!!.regularPriceLayout.visibility = View.INVISIBLE
                    } else {
                        holder.binding!!.regularPriceLayout.visibility = View.VISIBLE
                        holder.binding!!.regularPriceTv.text =
                            "" + hd.productCurrency + hd.productPrice
                    }
                }
            } catch (e: Exception) {
            }
            holder.binding!!.llAgent.setOnClickListener(View.OnClickListener {
                context.startActivity(Intent(context, New_AgentDetails::class.java)
                    .putExtra("agentId", hd.productAgentId.toString())
                    .putExtra("direct", "non_direct"))
            })
            if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 0 && hd.discountPriceEnabledId == 0) {
            } else if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 0) {
                holder.binding!!.tvFinalPrice.text = hd.productCurrency + hd.productPrice
                holder.binding!!.tvFinalPrice.setTextColor(Color.parseColor("#101010"))
            } else if (hd.productDiscountPercentageEnabled == 1 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 1) {
                holder.binding!!.tvPrice.setTextColor(Color.parseColor("#AAAAAA"))
                holder.binding!!.tvFinalPrice.visibility = View.VISIBLE
                holder.binding!!.tvFinalPrice.text = hd.productCurrency + hd.productFinalPrice
                holder.binding!!.tvPrice.paintFlags =
                    holder.binding!!.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding!!.tvPrice.text = hd.productCurrency + hd.productPrice
                holder.binding!!.tvDiscount.text = hd.productDiscountPercentage + "off"
            }
            holder.binding!!.rootLayout.setOnClickListener {
                if (Search == "") {
                    ErrorMessage.E("Daily_Deal_Adapter 1")
                    (context as NewMyDealsActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                } else {
                    (context as SearchAllDealActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                }
            }
            try {
                Log.e("Lines", holder.binding!!.tvProductName.lineCount.toString() + "")
            } catch (e: Exception) {
            }

            /*===============================*/holder.binding!!.ivFavoriteIcon.setOnClickListener(
                View.OnClickListener {
                    if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                        if (hd.productFavourite == 1) {
                            commonPopup(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        } else {
                            updateFavoriteProduct(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        }
                    } else {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                    }
                })
        } else if (Search == "popup") {
            holder.binding12!!.ddLoyaltyPriceLayout.visibility = View.GONE
            holder.binding12!!.regularPriceLayout.visibility = View.GONE
            holder.binding12!!.llAgent.setVisibility(View.VISIBLE)
            /*============*/try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context)
                    .load(hd.productImage)
                    .transform(transformation)
                    .fit()
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.mainimageplaceholder)
                    .into(holder.binding12!!.cardBgImg)
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
                        .into(holder.binding12!!.ivMainImage)
                } catch (e: Exception) {
                }
            }
            holder.binding12!!.tvProductName.text = hd.productName
            holder.binding12!!.tvLocationmiles.setText(hd.productDistance)
            holder.binding12!!.tvLeftDays.text = "Expire on " + hd.dealExpiredDate
            try {
                if (Check == "DailyDeal") {
                    if (hd.productDDLoyaltyPrice == "") {
                        holder.binding12!!.ddLoyaltyPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding12!!.ddLoyaltyPriceLayout.visibility = View.VISIBLE
                        holder.binding12!!.ddPriceTv.text =
                            "" + hd.productCurrency + hd.productDDLoyaltyPrice
                    }
                    if (hd.productPrice == "") {
                        holder.binding12!!.regularPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding12!!.regularPriceLayout.visibility = View.VISIBLE
                        holder.binding12!!.regularPriceTv.text =
                            "" + hd.productCurrency + hd.productPrice
                    }
                }
            } catch (e: Exception) {
            }
            holder.binding12!!.llAgent.setOnClickListener(View.OnClickListener {
                context.startActivity(Intent(context, New_AgentDetails::class.java)
                    .putExtra("agentId", hd.productAgentId.toString())
                    .putExtra("direct", "non_direct"))
            })
            if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 0 && hd.discountPriceEnabledId == 0) {
            } else if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 0) {
                holder.binding12!!.tvFinalPrice.text = hd.productCurrency + hd.productPrice
                holder.binding12!!.tvFinalPrice.setTextColor(Color.parseColor("#101010"))
            } else if (hd.productDiscountPercentageEnabled == 1 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 1) {
                holder.binding12!!.tvPrice.setTextColor(Color.parseColor("#AAAAAA"))
                holder.binding12!!.tvFinalPrice.visibility = View.VISIBLE
                holder.binding12!!.tvFinalPrice.text = hd.productCurrency + hd.productFinalPrice
                holder.binding12!!.tvPrice.paintFlags =
                    holder.binding12!!.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding12!!.tvPrice.text = hd.productCurrency + hd.productPrice
                holder.binding12!!.tvDiscount.text = hd.productDiscountPercentage + " OFF"
            }
            holder.binding12!!.rootLayout.setOnClickListener {
                if (Search == "") {
                    ErrorMessage.E("Daily_Deal_Adapter 2")
                    (context as NewMyDealsActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                } else if (Search == "popup") {
                    val intent = Intent(context, LatestProductDetails::class.java)
                    intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, hd.productId.toString())
                    intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID,
                        hd.productAgentId.toString())
                    intent.putExtra("type", "non_direct")
                    intent.putExtra("pos", "")
                    context.startActivity(intent)
                } else {
                    ErrorMessage.E("Daily_Deal_Adapter 3")
                    (context as SearchAllDealActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                }
            }
            try {
                Log.e("Lines", holder.binding12!!.tvProductName.lineCount.toString() + "")
            } catch (e: Exception) {
            }

            /*===============================*/holder.binding12!!.ivFavoriteIcon.setOnClickListener(
                View.OnClickListener {
                    if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                        if (hd.productFavourite == 1) {
                            commonPopup(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        } else {
                            updateFavoriteProduct(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        }
                    } else {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                    }
                })
        } else {
            if (hd.productFavourite == 1) {
                holder.binding1!!.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_active))
            } else if (hd.productFavourite == 0) {
                holder.binding1!!.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_deactive))
            }
            holder.binding1!!.ddLoyaltyPriceLayout.visibility = View.VISIBLE
            holder.binding1!!.regularPriceLayout.visibility = View.VISIBLE
            holder.binding1!!.llAgent.visibility = View.VISIBLE
            /*============*/try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context)
                    .load(hd.productImage)
                    .transform(transformation)
                    .fit()
                    .placeholder(R.drawable.mainimageplaceholder)
                    .error(R.drawable.mainimageplaceholder)
                    .into(holder.binding1!!.cardBgImg)
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
                        .into(holder.binding1!!.ivMainImage)
                } catch (e: Exception) {
                }
            }
            holder.binding1!!.tvProductName.text = hd.productName
            holder.binding1!!.tvLocationmiles.setText(hd.productDistance)
            holder.binding1!!.tvLeftDays.text = "Expire on " + hd.dealExpiredDate
            try {
                if (Check == "DailyDeal") {
                    if (hd.productDDLoyaltyPrice == "") {
                        holder.binding1!!.ddLoyaltyPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding1!!.ddLoyaltyPriceLayout.visibility = View.VISIBLE
                        holder.binding1!!.ddPriceTv.text =
                            "" + hd.productCurrency + hd.productDDLoyaltyPrice
                    }
                    if (hd.productPrice == "") {
                        holder.binding1!!.regularPriceLayout.visibility = View.GONE
                    } else {
                        holder.binding1!!.regularPriceLayout.visibility = View.VISIBLE
                        holder.binding1!!.regularPriceTv.text =
                            "" + hd.productCurrency + hd.productPrice
                    }
                }
            } catch (e: Exception) {
            }
            holder.binding1!!.llAgent.setOnClickListener {
                context.startActivity(Intent(context, New_AgentDetails::class.java)
                    .putExtra("agentId", hd.productAgentId.toString())
                    .putExtra("direct", "non_direct"))
            }
            if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 0 && hd.discountPriceEnabledId == 0) {
            } else if (hd.productDiscountPercentageEnabled == 0 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 0) {
                holder.binding1!!.tvFinalPrice.text = hd.productCurrency + hd.productPrice
                holder.binding1!!.tvFinalPrice.setTextColor(Color.parseColor("#101010"))
            } else if (hd.productDiscountPercentageEnabled == 1 && hd.priceEnabledId == 1 && hd.discountPriceEnabledId == 1) {
                holder.binding1!!.tvPrice.setTextColor(Color.parseColor("#AAAAAA"))
                holder.binding1!!.tvFinalPrice.visibility = View.VISIBLE
                holder.binding1!!.tvFinalPrice.text = hd.productCurrency + hd.productFinalPrice
                holder.binding1!!.tvPrice.paintFlags =
                    holder.binding1!!.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding1!!.tvPrice.text = hd.productCurrency + hd.productPrice
                holder.binding1!!.tvDiscount.text = hd.productDiscountPercentage + " OFF"
            }
            holder.binding1!!.rootLayout.setOnClickListener {
                if (Search == "") {
                    ErrorMessage.E("Daily_Deal_Adapter 4")
                    (context as NewMyDealsActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                } else {
                    (context as SearchAllDealActivity).moveToDetails(hd.productId.toString() + "",
                        hd.productAgentId.toString() + "",
                        position)
                }
            }
            try {
                Log.e("Lines", holder.binding1!!.tvProductName.lineCount.toString() + "")
            } catch (e: Exception) {
            }

            /*===============================*/holder.binding1!!.ivFavoriteIcon.setOnClickListener(
                View.OnClickListener {
                    if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                        if (hd.productFavourite == 1) {
                            commonPopup(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        } else {
                            updateFavoriteProduct(hd.productId.toString(),
                                holder,
                                position,
                                hd.productFavourite,
                                Search)
                        }
                    } else {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                    }
                })
        }
        if (Search == "") {
            holder.binding!!.tvagentName.setText(hd.productAgentName + " (" + hd.productDistance + ")")
        } else if (Search == "popup") {
            holder.binding12!!.tvagentName.setText(hd.productAgentName)
        } else {
            holder.binding1!!.tvagentName.setText(hd.productAgentName)
        }


        /* holder.binding.ivNonFavoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferenceHelper.getInstance(context).getIsLogin()) {
                      //  updateFavoriteProduct(hd.getProductId(), holder, position);
                    } else {
                        Intent intent = new Intent(context, SignUpActivity.class);
                        context.startActivity(intent);
                        //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
                    }
                }
            });

*/
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return CustomerLists.size
    }

    inner class MyViewHolder : RecyclerView.ViewHolder {
        var binding: DailyDealAdapterBinding? = null
        var binding1: SearchDailyDealAdapterBinding? = null
        var binding12: DailydealPopupBinding? = null

        constructor(view: DailyDealAdapterBinding) : super(view.root) {
            binding = view
        }

        constructor(view: SearchDailyDealAdapterBinding) : super(view.root) {
            binding1 = view
        }

        constructor(view: DailydealPopupBinding) : super(view.root) {
            binding12 = view
        }
    }

    private fun commonPopup(
        productId: String,
        holder: MyViewHolder,
        pos: Int,
        productFavourite: Int,
        Search: String
    ) {
        /**
         * Show Dialog....
         */
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
            btnOk.setOnClickListener {
                updateFavoriteProduct(productId, holder, pos, productFavourite, Search)
                dialog1.dismiss()
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun updateFavoriteProduct(
        productId: String,
        holder: MyViewHolder,
        posi: Int,
        productFavourite: Int,
        Search1: String
    ) {
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
                            if (resp.getString("error_type") == "200") {
                                (context as Activity).runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (Search1 == "") {
                                        if (productFavourite == 0) {
                                            CustomerLists[posi].productFavourite = 1
                                            holder.binding!!.ivFavoriteIcon.setImageDrawable(
                                                context.resources.getDrawable(R.drawable.ic_heart_active))
                                        } else if (productFavourite == 1) {
                                            CustomerLists[posi].productFavourite = 0
                                            holder.binding!!.ivFavoriteIcon.setImageDrawable(
                                                context.resources.getDrawable(R.drawable.ic_heart_deactive))
                                        }
                                    } else {
                                        if (productFavourite == 0) {
                                            CustomerLists[posi].productFavourite = 1
                                            holder.binding1!!.ivFavoriteIcon.setImageDrawable(
                                                context.resources.getDrawable(R.drawable.ic_heart_active))
                                        } else if (productFavourite == 1) {
                                            CustomerLists[posi].productFavourite = 0
                                            holder.binding1!!.ivFavoriteIcon.setImageDrawable(
                                                context.resources.getDrawable(R.drawable.ic_heart_deactive))
                                        }
                                    }
                                    /* if (holder.binding.ivNonFavoriteIcon.getVisibility() == View.VISIBLE) {
                            
                                                                        holder.binding.ivNonFavoriteIcon.setVisibility(View.INVISIBLE);
                                                                        holder.binding.ivFavoriteIcon.setVisibility(View.VISIBLE);
                            
                                                                    } else {
                                                                        holder.binding.ivNonFavoriteIcon.setVisibility(View.VISIBLE);
                                                                        holder.binding.ivFavoriteIcon.setVisibility(View.INVISIBLE);
                                                                    */
                                    /*    mList.remove(posi);
                                                                        notifyItemRemoved(posi);
                                                                        notifyItemRangeChanged(posi, getItemCount() - posi);*/
                                    /*
                                                                     */
                                    /* holder.ivLikeIcon.setVisibility(View.INVISIBLE);
                                                                        holder.ivUnlikeIcon.setVisibility(View.VISIBLE);
                                                                        recyclerClickListener.setCellClicked(null, "");
                                                              */
                                    /*
                                                                    }*/
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(holder.binding!!.cardBgImg,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", ">>$e")
                            AppUtil.showMsgAlert(holder.binding!!.cardBgImg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(holder.binding!!.cardBgImg,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(holder.binding!!.cardBgImg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(holder.binding!!.cardBgImg,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}