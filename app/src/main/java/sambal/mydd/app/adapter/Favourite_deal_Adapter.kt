package sambal.mydd.app.adapter

import sambal.mydd.app.models.MyDeal_Models.FavouriteDeals
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import sambal.mydd.app.utils.DateConversion
import android.content.Intent
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.activity.NewMyDealsActivity
import sambal.mydd.app.activity.SearchAllDealActivity
import androidx.cardview.widget.CardView
import android.widget.TextView
import android.widget.LinearLayout
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
import android.widget.ImageView
import sambal.mydd.app.R
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class Favourite_deal_Adapter(
    var context: Context,
    var CustomerLists: List<FavouriteDeals>,
    var Check: String
) : RecyclerView.Adapter<Favourite_deal_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View
        itemView = if (Check == "") {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.favourite_deal_adapter, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_favourite_deal_adapter, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = CustomerLists[position]
        if (Check == "") {
            holder.tvagentName.text = model.productAgentName + " (" + model.productDistance + ")"
        } else {
            holder.tvagentName.text = model.productAgentName
        }
        val image = model.productImage
        val priceEnabledId = model.priceEnabledId
        val discountPriceEnabledId = model.discountPriceEnabledId
        val productDiscountPercentageEnabled = model.productDiscountPercentageEnabled
        if (productDiscountPercentageEnabled == 0 && priceEnabledId == 0 && discountPriceEnabledId == 0) {
        } else if (productDiscountPercentageEnabled == 0 && priceEnabledId == 1 && discountPriceEnabledId == 0) {
            holder.tvPrice.text = model.productCurrency + model.productPrice
            holder.tvFinalPrice.visibility = View.GONE
            holder.tvPrice.setTextColor(Color.parseColor("#101010"))
        } else if (productDiscountPercentageEnabled == 1 && priceEnabledId == 1 && discountPriceEnabledId == 1) {
            holder.tvFinalPrice.visibility = View.VISIBLE
            holder.tvFinalPrice.text = model.productCurrency + model.productFinalPrice
            holder.tvPrice.setTextColor(Color.parseColor("#75787b"))
            holder.tvPrice.paintFlags = holder.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvPrice.text = model.productCurrency + model.productPrice
            holder.tvDiscount.text = model.productDiscountPercentage + "off"
        }
        try {
            val transformation = RoundedTransformationBuilder()
                .oval(false)
                .build()
            Picasso.with(context).load(image)
                .fit()
                .transform(transformation)
                .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                .error(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                .into(holder.ivBgImg)
        } catch (e: Exception) {
            holder.ivBgImg.setImageResource(R.drawable.mainimageplaceholder)
        }
        holder.tvProductName.text = model.productName
        holder.tvLocationMiles.text = model.productDistance
        holder.tvDaysLeft.text = "Expire on " + DateConversion.local(model.dealExpiredDate)
        if (model.productFavourite == 1) {
            holder.ivLikeIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_active))
        } else if (model.productFavourite == 0) {
            holder.ivLikeIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_deactive))
        }
        holder.llAgent.setOnClickListener {
            val intent = Intent(context, New_AgentDetails::class.java)
            intent.putExtra("direct", "false")
            intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, model.productAgentId.toString())
            context.startActivity(intent)
        }
        holder.ivLikeIcon.setOnClickListener {
            if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                if (model.productFavourite == 1) {
                    commonPopup(model.productId.toString(),
                        holder,
                        position,
                        model.productFavourite)
                } else {
                    updateFavoriteProduct(model.productId.toString(),
                        holder,
                        position,
                        model.productFavourite)
                }
            } else {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
                //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
            }
        }
        holder.rootLayout.setOnClickListener {
            if (Check == "") {
                ErrorMessage.E("Favourite_Deal_Adapter")
                (context as NewMyDealsActivity).moveToDetails(model.productId.toString(),
                    model.productAgentId.toString(),
                    position)
            } else {
                (context as SearchAllDealActivity).moveToDetails(model.productId.toString(),
                    model.productAgentId.toString(),
                    position)
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
        return CustomerLists.size
    }

    inner class MyViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!) {
        var ivBgImg: ImageView
        var ivLikeIcon: ImageView
        var rootLayout: CardView
        var tvProductName: TextView
        var tvDaysLeft: TextView
        var tvLocationMiles: TextView
        var tvFinalPrice: TextView
        var tvPrice: TextView
        var tvDiscount: TextView
        var tvagentName: TextView
        val llAgent: LinearLayout

        init {
            rootLayout = itemView.findViewById(R.id.root_layout)
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvDiscount = itemView.findViewById(R.id.tvDiscount)
            ivBgImg = itemView.findViewById(R.id.card_bg_img)
            tvLocationMiles = itemView.findViewById(R.id.tvLocationmiles)
            ivLikeIcon = itemView.findViewById(R.id.iv_favorite_icon)
            tvProductName = itemView.findViewById(R.id.tv_product_name)
            tvDaysLeft = itemView.findViewById(R.id.tv_left_days)
            tvagentName = itemView.findViewById(R.id.tvagentName)
            llAgent = itemView.findViewById(R.id.llAgent)
        }
    }

    private fun commonPopup(
        productId: String,
        holder: MyViewHolder,
        pos: Int,
        productFavourite: Int
    ) {
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
                updateFavoriteProduct(productId, holder, pos, productFavourite)
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
        productFavourite: Int
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
                                    if (productFavourite == 0) {
                                        CustomerLists[posi].productFavourite = 1
                                        holder.ivLikeIcon.setImageDrawable(context.resources.getDrawable(
                                            R.drawable.ic_heart_active))
                                    } else if (productFavourite == 1) {
                                        CustomerLists[posi].productFavourite = 0
                                        holder.ivLikeIcon.setImageDrawable(context.resources.getDrawable(
                                            R.drawable.ic_heart_deactive))
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(holder.tvProductName,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", ">>$e")
                            AppUtil.showMsgAlert(holder.tvProductName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(holder.tvProductName,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(holder.tvProductName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(holder.tvProductName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}