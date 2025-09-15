package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.models.MyDeal_Models.SignupDeals
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import sambal.mydd.app.activity.NewMyDealsActivity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.activity.New_AgentDetails
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.widget.LinearLayout
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SignUp_Deal_Adapter(
    var context: Context,
    var CustomerLists: MutableList<SignupDeals>,
    var Check: String
) : RecyclerView.Adapter<SignUp_Deal_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View
        itemView = if (Check == "") {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.signup_deal_adapter, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_signup_deal_adapter_, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = CustomerLists[position]
        try {
            Picasso.with(context).load(model.productImage)
                .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                .error(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                .into(holder.card_bg_img)
        } catch (e: Exception) {
            holder.card_bg_img.setImageResource(R.drawable.mainimageplaceholder)
        }
        if (Check == "") {
            holder.tvagentName.text = model.productAgentName + "(" + model.productDistance + ")"
        } else {
            holder.tvagentName.text = model.productAgentName
        }
        try {
            val transformation = RoundedTransformationBuilder()
                .oval(false)
                .build()
            Picasso.with(context).load(model.productAgentImage)
                .transform(transformation)
                .fit()
                .placeholder(context.resources.getDrawable(R.drawable.sponplaceholder))
                .error(context.resources.getDrawable(R.drawable.sponplaceholder))
                .into(holder.ivAgentImage)
        } catch (e: Exception) {
            holder.ivAgentImage.setImageResource(R.drawable.mainimageplaceholder)
        }
        if (model.productFavourite == 1) {
            holder.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_active))
        } else if (model.productFavourite == 0) {
            holder.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_heart_deactive))
        }
        holder.tvName.text = model.productName
        holder.tvMiles.text = model.productDistance
        holder.tvFree.text = model.productType
        holder.tvFree.setTextColor(Color.parseColor(model.productTypeColor))
        holder.tvRedeemTex.text = model.redeemedText
        holder.tvExpiryDate.text = "Expire on - " + model.dealExpiredDate
        holder.llMain.setOnClickListener {
            (context as NewMyDealsActivity).moveToDetails(model.productId.toString() + "",
                model.productAgentId.toString() + "",
                position)
            /*context.startActivity(new Intent(context, LatestProductDetails.class)
                            .putExtra("agentId", model.getProductAgentId().toString())
                            .putExtra("product_id", model.getProductId().toString()));*/
        }
        holder.llAgent.setOnClickListener {
            context.startActivity(Intent(context, New_AgentDetails::class.java)
                .putExtra("agentId", model.productAgentId.toString())
                .putExtra("direct", "false"))
        }
        holder.ivFavoriteIcon.setOnClickListener { v ->
            likeProduct(v,
                model.productFavourite!!,
                holder,
                position,
                model)
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
        var card_bg_img: ImageView
        var ivAgentImage: ImageView
        var ivFavoriteIcon: ImageView
        var tvName: TextView
        var tvMiles: TextView
        var tvExpiryDate: TextView
        var tvFree: TextView
        var tvRedeemTex: TextView
        var tvagentName: TextView
        var root_layout: CardView
        var llMain: LinearLayout
        var llAgent: LinearLayout

        init {
            root_layout = itemView.findViewById(R.id.root_layout)
            card_bg_img = itemView.findViewById(R.id.card_bg_img)
            ivAgentImage = itemView.findViewById(R.id.ivAgentImage)
            tvName = itemView.findViewById(R.id.tv_product_name)
            tvMiles = itemView.findViewById(R.id.tvLocationmiles)
            tvExpiryDate = itemView.findViewById(R.id.tv_left_days)
            tvFree = itemView.findViewById(R.id.tvFree)
            tvRedeemTex = itemView.findViewById(R.id.tvRedeemTex)
            llMain = itemView.findViewById(R.id.llMain)
            llAgent = itemView.findViewById(R.id.llAgent)
            tvagentName = itemView.findViewById(R.id.tvagentName)
            ivFavoriteIcon = itemView.findViewById(R.id.iv_favorite_icon)
        }
    }

    private fun likeProduct(
        view: View,
        agentFavourite: Int,
        holder: MyViewHolder,
        position: Int,
        model: SignupDeals
    ) {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = AppConfig.api_Interface().updateFavouriteDeal(model.productId.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            if (obj.optString("error_type") == "200") {
                                ErrorMessage.E("### PRODUCT ID >>>" + model.productId)
                                if (agentFavourite == 1) {
                                    holder.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(
                                        R.drawable.heartfulledpdetails))
                                } else if (agentFavourite == 0) {
                                    holder.ivFavoriteIcon.setImageDrawable(context.resources.getDrawable(
                                        R.drawable.heartfulled))
                                }
                                val deal = SignupDeals()
                                deal.productId = model.productId
                                deal.productName = model.productName
                                deal.productDistance = model.productDistance
                                deal.productAgentId = model.productAgentId
                                deal.productAgentName = model.productAgentName
                                deal.productAgentImage = model.productAgentImage
                                deal.productImage = model.productImage
                                deal.dealImage = model.dealImage
                                deal.dealExpiredDate = model.dealExpiredDate
                                deal.productType = model.productType
                                deal.productTypeColor = model.productTypeColor
                                deal.redeemedText = model.redeemedText
                                if (model.productFavourite == 0) {
                                    deal.productFavourite = 1
                                }
                                if (model.productFavourite == 1) {
                                    deal.productFavourite = 0
                                }
                                CustomerLists[position] = deal
                                notifyDataSetChanged()
                            } else {
                                AppUtil.showMsgAlert(view, obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(view, t.message)
                }
            })
        } else {
            ErrorMessage.T(context, "No Internet Found!")
        }
    }
}