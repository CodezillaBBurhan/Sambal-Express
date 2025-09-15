package sambal.mydd.app.adapter

import android.app.Dialog
import sambal.mydd.app.activity.SearchMerchantChatList
import org.json.JSONArray
import sambal.mydd.app.callback.RecyclerClickListener
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import android.text.Html
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import android.content.Intent
import android.util.Log
import android.view.*
import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.authentication.SignUpActivity
import android.widget.TextView
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.AdapsearchchatBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class SearchChatNewAdapter(
    private val context: SearchMerchantChatList,
    private val searchKeyword: String,
    private val jsonArray: JSONArray,
    private val publishkey: String,
    private val subscribekey: String,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<SearchChatNewAdapter.MyViewHolder>() {
    var isLike = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapsearchchatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val jsonObject = getItem(position)
        holder.binding.tvCategoryName.text = jsonObject.optString("agentCompanyName")
        holder.binding.tvRating.text = jsonObject.optString("agentRating")
        val text =
            "<font color=#101010>" + jsonObject.optString("agentAddress") + "</font> <font color=#007cfa>" + " (" + jsonObject.optString(
                "agentDistance") + ")" + "</font>"
        holder.binding.tvLocation.text = Html.fromHtml(text)
        if (jsonObject.optString("agentFavourite").equals("0", ignoreCase = true)) {
            holder.binding.ivLike.visibility = View.GONE
            holder.binding.ivUnlike.visibility = View.VISIBLE
        } else {
            holder.binding.ivLike.visibility = View.VISIBLE
            holder.binding.ivUnlike.visibility = View.GONE
        }
        try {
            val transformation = RoundedTransformationBuilder()
                .oval(false)
                .build()
            Picasso.with(context).load(jsonObject.optString("agentImage"))
                .fit()
                .transform(transformation)
                .placeholder(context.resources.getDrawable(R.drawable.place_holder))
                .error(context.resources.getDrawable(R.drawable.place_holder))
                .into(holder.binding.cardBgImg)
        } catch (e: Exception) {
        }
        holder.binding.rootLayout.setOnClickListener {
            Log.e("Click", "Click")
            context.startActivity(Intent(context, ChatMain::class.java)
                .putExtra("id", jsonObject.optString("agentId"))
                .putExtra("name", jsonObject.optString("agentCompanyName"))
                .putExtra("isAdmin", jsonObject.optString("isAdmin"))
                .putExtra("subskey", subscribekey)
                .putExtra("pubskey", publishkey)
                .putExtra("followingstatus", jsonObject.optString("followingStatus"))
                .putExtra("type", "non_direct"))
            //recyclerClickListener.setCellClicked(jsonObject, "");
        }
        holder.binding.ivLike.setOnClickListener {
            if (PreferenceHelper.getInstance(context)?.isLogin!!) {
                //updateFavoriteProduct(productId, holder);
                isLike = true
                commonPopup(jsonObject.optString("agentId"), holder)
            } else {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
                //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
            }
        }
        holder.binding.ivUnlike.setOnClickListener {
            if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                isLike = false
                updateFavoriteProduct(jsonObject.optString("agentId"), holder)
            } else {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
                //AppUtil.signupPopup("You need to signup first. Do you want to signup?", context);
            }
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun getItem(position: Int): JSONObject {
        return jsonArray.optJSONObject(position)
    }

    inner class MyViewHolder(var binding: AdapsearchchatBinding) : RecyclerView.ViewHolder(
        binding.root)

    private fun commonPopup(productId: String, holder: RecyclerView.ViewHolder) {
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
            val call = AppConfig.api_Interface().updateFavouriteChatAgent(productId)
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
                                        (holder as MyViewHolder).binding.ivLike.visibility =
                                            View.GONE
                                        holder.binding.ivUnlike.visibility = View.VISIBLE
                                    } else {
                                        (holder as MyViewHolder).binding.ivLike.visibility =
                                            View.VISIBLE
                                        holder.binding.ivUnlike.visibility = View.GONE
                                    }
                                    try {
//                                            ChatLocationFavourite fragment = (ChatLocationFavourite) context.getSupportFragmentManager().findFragmentById(R.id.frame);
//                                            fragment.refreshScreen();

                                        //((ChatLocationFavourite)context.getSupportFragmentManager().findFragmentById(R.id.frame)).refreshScreen();
                                        //  ((MainActivity) activity).callChatLocation();
                                    } catch (e: Exception) {
                                        Log.e("ExxxChat", e.toString())
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert((holder as MyViewHolder).binding.tvCategoryName,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}