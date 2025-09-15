package sambal.mydd.app.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sambal.mydd.app.R
import sambal.mydd.app.activity.ActivityStorePoints
import sambal.mydd.app.activity.ScanQrVisitBusiness
import sambal.mydd.app.beans.AgentVoucherListBean
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.VoucherListLayoutBinding
import sambal.mydd.app.fragment.MyRewards.MyVouchersFragment
import sambal.mydd.app.models.RefreshCard
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.ErrorMessage
import java.io.IOException
import java.lang.Exception

class VoucherListAdapter(var context: Context, private val list: List<AgentVoucherListBean>, private val agentWalletType:String?, val agentId:String, var fragment: ActivityStorePoints, private val type:String?) :
    RecyclerView.Adapter<VoucherListAdapter.MyViewHolder>() {
    var dialogManager: DialogManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            VoucherListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.binding.llVoucher.backgroundTintList = ColorStateList.valueOf(Color.parseColor(model.voucherColorCode))
        holder.binding.tvRedeem.setTextColor(ColorStateList.valueOf(Color.parseColor(model.voucherColorCode)))
        holder.binding.rightArrayImg.imageTintList =(ColorStateList.valueOf(Color.parseColor(model.voucherColorCode)))

        holder.binding.tvAvailable.text = model.voucherText
        holder.binding.tvBalance.text =
            model.currency + model.voucherPrice

        holder.binding.tvBalance.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.tvBalance.text =
            model.currency + model.voucherPrice

        holder.binding.tvRedeemedPrice.visibility = View.VISIBLE
        holder.binding.tvRedeemedPrice.text =
            model.voucherRedeemedText + " "
        try {
            if (model.voucherRedeemEnabled.equals(
                    "1",
                    ignoreCase = true
                )
            ) {
                holder.binding.llRedeem.visibility = View.VISIBLE
                holder.binding.llRedeem.isEnabled = true
                holder.binding.llRedeem.background =
                    context.resources.getDrawable(R.drawable.llyellow)
            } else {
                holder.binding.llRedeem.visibility = View.VISIBLE
                holder.binding.llRedeem.isEnabled = false
                holder.binding.llRedeem.background =
                    context.resources.getDrawable(R.drawable.llredeemgrey)
            }
        }catch (e: Exception) {
            Log.e("Ex1", e.toString())
        }

        holder.binding.llRedeem.setOnClickListener {
            if (agentWalletType.equals("1", ignoreCase = true)) {
                holder.binding.llRedeem.isEnabled = false
               if(type.equals("voucher")){
                   redeemFromVoucher(
                       model.voucherId,
                       agentId,
                       model.voucherPrice,
                       true,
                       holder.binding.tvRedeemedPrice
                   ) { holder.binding.llRedeem.isEnabled = true }
               }
                else {
                   redeemVoucher(
                       model.voucherId,
                       agentId,
                       model.voucherPrice,
                       true,
                       holder.binding.tvRedeemedPrice
                   ) { holder.binding.llRedeem.isEnabled = true }
               }
            } else {
                context.startActivity(
                    Intent(context, ScanQrVisitBusiness::class.java)
                        .putExtra("UUID", model.voucherUUID)
                        .putExtra("amount", model.voucherPrice)
                        .putExtra("currency", model.currency)
                        .putExtra("storevouchers", true)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: VoucherListLayoutBinding) : RecyclerView.ViewHolder(
        binding.root)


    private fun redeemVoucher(
        voucherId: String,
        agentId: String,
        voucherPrice: String,
        b: Boolean,
        tvAgentName: TextView,
        refreshCard: RefreshCard
    ) {
        dialogManager = DialogManager()
        if (AppUtil.isNetworkAvailable(context)) {
            if (b) {
                dialogManager!!.showProcessDialog(context, "", false, null)
            }
            val call = AppConfig.api_Interface().getVoucherRedeem(voucherId, agentId, voucherPrice)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("getVoucherRedeem ", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                dialogManager!!.stopProcessDialog()
                                fragment.showQRCODE(responseObj, refreshCard)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(tvAgentName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }


    private fun redeemFromVoucher(
        voucherId: String,
        agentId: String,
        voucherPrice: String,
        b: Boolean,
        tvAgentName: TextView,
        refreshCard: RefreshCard
    ) {
        dialogManager = DialogManager()
        if (AppUtil.isNetworkAvailable(context)) {
            if (b) {
                dialogManager!!.showProcessDialog(context, "", false, null)
            }
            val call = AppConfig.api_Interface().getVoucherRedeem(voucherId, agentId, voucherPrice)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("getVoucherRedeem", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                dialogManager!!.stopProcessDialog()
                                val mfragment = MyVouchersFragment()
                                mfragment.showQRCODE(responseObj, refreshCard, context)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(tvAgentName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}