package sambal.mydd.app.adapter

import android.app.Dialog
import sambal.mydd.app.activity.MyFavorites
import sambal.mydd.app.beans.ExpiredSoonModel
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.callback.RecyclerClickListener
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import sambal.mydd.app.utils.DateConversion
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.*
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.authentication.SignUpActivity
import android.widget.TextView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import android.widget.ProgressBar
import sambal.mydd.app.R
import sambal.mydd.app.databinding.FavBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AdapterFavourite(
    private val context: MyFavorites,
    private val modelList: MutableList<ExpiredSoonModel?>,
    recyclerView: RecyclerView?,
    private val recyclerClickListener: RecyclerClickListener
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
            val binding = FavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh

        //return new NearMeViewHolder(LayoutInflater.from(parent.getContext()), parent, context);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            //final JSONObject jsonObject = getItem(position);
            val model = modelList[position]
            holder.binding.ivCloseIcon.setVisibility(View.INVISIBLE)
            holder.binding.tvagentName.setText(
                model!!.productAgentName
            )
            val image = model.productImage
            val priceEnabledId = model.priceEnabledId.toInt()
            val discountPriceEnabledId = model.discountPriceEnabledId.toInt()
            val productDiscountPercentageEnabled = model.productDiscountPercentageEnabled.toInt()
            if (productDiscountPercentageEnabled == 0 && priceEnabledId == 0 && discountPriceEnabledId == 0) {
            } else if (productDiscountPercentageEnabled == 0 && priceEnabledId == 1 && discountPriceEnabledId == 0) {
                holder.binding.tvPrice.text = model.productCurrency + model.productPrice
                holder.binding.tvFinalPrice.visibility = View.GONE
                holder.binding.tvPrice.setTextColor(Color.parseColor("#101010"))
            } else if (productDiscountPercentageEnabled == 1 && priceEnabledId == 1 && discountPriceEnabledId == 1) {
                holder.binding.tvFinalPrice.visibility = View.VISIBLE
                holder.binding.tvFinalPrice.text = model.productCurrency + model.productFinalPrice
                holder.binding.tvPrice.setTextColor(Color.parseColor("#75787b"))
                holder.binding.tvPrice.paintFlags =
                    holder.binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.tvPrice.text = model.productCurrency + model.productPrice
                holder.binding.tvDiscount.text = model.productDiscountPercentage + " OFF"
            }
            val productId = model.dealId
            val agentId = model.agentId
            try {
                val transformation = RoundedTransformationBuilder()
                    .oval(false)
                    .build()
                Picasso.with(context).load(image)
                    .fit()
                    .transform(transformation)
                    .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                    .error(context.resources.getDrawable(R.drawable.mainimageplaceholder)).into(
                        holder.binding.cardBgImg
                    )
            } catch (e: Exception) {
                holder.binding.cardBgImg.setImageResource(R.drawable.mainimageplaceholder)
            }
            if (!model.productAgentImage.equals(
                    "https://www.dealdio.com/upload/empty_deal.jpg",
                    ignoreCase = true
                )
            ) {
                try {
                    val transformation = RoundedTransformationBuilder()
                        .oval(false)
                        .build()
                    Picasso.with(context)
                        .load(model.productAgentImage)
                        .fit()
                        .transform(transformation)
                        .placeholder(R.drawable.sponplaceholder)
                        .error(R.drawable.sponplaceholder)
                        .into(holder.binding.ivAgentImage)
                } catch (e: Exception) {
                }
            }

            //((ExpiredsoonAdapterNew.ViewHolder) holder).tvProductAgentName.setText(finalText);
            holder.binding.tvProductName.text = model.productName
            holder.binding.tvLocationmiles.setText(
                model.productDistance
            )
            holder.binding.tvLeftDays.text = "Expire on " + DateConversion.local(
                model.dealExpiredDate
            )
            val productFavorite = model.productFavourite.toInt()
            if (productFavorite == 1) {
                holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                holder.binding.ivNonFavoriteIcon.visibility = View.GONE
            } else {
                holder.binding.ivFavoriteIcon.visibility = View.GONE
                holder.binding.ivNonFavoriteIcon.visibility = View.VISIBLE
            }
            holder.binding.llAgent.setOnClickListener {
                val intent = Intent(context, New_AgentDetails::class.java)
                intent.putExtra("direct", "false")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
                context.startActivity(intent)
            }
            holder.binding.ivFavoriteIcon.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                    //updateFavoriteProduct(productId, holder);
                    commonPopup(model.productId, holder, position)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                    //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
                }
            }
            holder.binding.ivNonFavoriteIcon.setOnClickListener {
                if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                    updateFavoriteProduct(productId, holder, position)
                } else {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                    //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
                }
            }
            holder.binding.rootLayout.setOnClickListener {
                context.moveToDetails(
                    model.productId,
                    model.agentId,
                    position
                )
            }
            //recyclerClickListener.setCellClicked(jsonObject, "");
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    fun removeAt(position: Int) {
        modelList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, modelList.size)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private fun commonPopup(productId: String, holder: RecyclerView.ViewHolder, pos: Int) {
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
                updateFavoriteProduct(productId, holder as ViewHolder, pos)
                dialog1.dismiss()
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun updateFavoriteProduct(productId: String, holder: ViewHolder, posi: Int) {
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
                            ErrorMessage.E("updateFavoriteProduct >> $resp")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                context.runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (holder.binding.ivNonFavoriteIcon.visibility == View.VISIBLE) {
                                        holder.binding.ivNonFavoriteIcon.visibility = View.INVISIBLE
                                        holder.binding.ivFavoriteIcon.visibility = View.VISIBLE
                                    } else {
                                        modelList.removeAt(posi)
                                        notifyItemRemoved(posi)
                                        notifyItemRangeChanged(posi, itemCount - posi)
                                        if (modelList.size == 0) {
                                            context.getMyFavourite(true, true)
                                        }
                                        /* holder.ivLikeIcon.setVisibility(View.INVISIBLE);
                                                                        holder.ivUnlikeIcon.setVisibility(View.VISIBLE);
                                                                        recyclerClickListener.setCellClicked(null, "");
                                                              */
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        holder.binding.cardBgImg,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                holder.binding.cardBgImg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                holder.binding.cardBgImg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(
                            holder.binding.cardBgImg,
                            MessageConstant.MESSAGE_SOMETHING_WRONG
                        )
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
            AppUtil.showMsgAlert(
                holder.binding.cardBgImg,
                MessageConstant.MESSAGE_INTERNET_CONNECTION
            )
        }
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    private inner class ViewHolder internal constructor(var binding: FavBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}