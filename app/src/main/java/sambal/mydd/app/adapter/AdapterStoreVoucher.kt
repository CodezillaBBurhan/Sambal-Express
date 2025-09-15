package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.AgentMainBean
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.DialogManager
import android.view.ViewGroup
import android.view.LayoutInflater
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.models.RefreshCard
import android.content.Intent
import sambal.mydd.app.activity.ScanQrVisitBusiness
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.activity.NewNotification
import androidx.recyclerview.widget.DefaultItemAnimator
import android.widget.TextView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.fragment.MyRewards.MyVouchersFragment
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import android.widget.ProgressBar
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapstorevouchersBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class AdapterStoreVoucher(
    private val context: Context?,
    private val list: List<AgentMainBean?>,
    recyclerView: RecyclerView?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loading = false
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var dialogManager: DialogManager? = null
    private var lastClickTime: Long = 0
    private var checkClick = true
    fun setLoaded() {
        loading = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdapstorevouchersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        ErrorMessage.E("list>>>" + list.size)
        holder!!.setIsRecyclable(false)
        try {
            (holder as ProgressViewHolder?)!!.progressBar.visibility = View.GONE
        } catch (e: Exception) {
        }


        try {
            if (holder is ViewHolder) {
                //final JSONObject jsonObject = getItem(position);
                try {
                    val model = list[position]
                    if (model != null) {
                        try {
                            if (model.getmList() != null && model.getmList().size > 0) {
                                holder.binding.voucherListRcv.visibility = View.VISIBLE

                                val adap = MyVoucherListAdapter(
                                    context!!,
                                    model.getmList(),
                                    model.agentWalletType,
                                    model.agentId,
                                    "voucher"
                                )

                                holder.binding.voucherListRcv.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.HORIZONTAL, // Change this to HORIZONTAL
                                    false
                                )
                                holder.binding.voucherListRcv.adapter = adap
                                holder.binding.voucherListRcv.isNestedScrollingEnabled = false
                                adap.notifyDataSetChanged()
                            } else {
                                holder.binding.voucherListRcv.visibility = View.GONE
                            }
                        }catch (e:Exception){}
                    }

                    holder.binding.llRedeem.setOnClickListener {
                        val currentTime = System.currentTimeMillis()
                        val elapsedTime = currentTime - lastClickTime
                        if (elapsedTime >= 1500) { // Allow only one click per second
                            holder.binding.llRedeem.isEnabled = false
                            holder.binding.llRedeem.isClickable = false
                            if (checkClick) {
                                checkClick = false
                                if (model!!.agentWalletType.equals("1", ignoreCase = true)) {
                                    redeemVoucher(
                                        model.getmList()[0].voucherId,
                                        model.agentId,
                                        model.getmList()[0].voucherPrice,
                                        true,
                                        holder.binding.tvAgentCompanyName
                                    ) {
                                        checkClick = true
                                        holder.binding.llRedeem.isEnabled = true
                                        holder.binding.llRedeem.isClickable = true
                                    }
                                } else {
                                    context!!.startActivity(
                                        Intent(context, ScanQrVisitBusiness::class.java)
                                            .putExtra("UUID", model.getmList()[0].voucherUUID)
                                            .putExtra("amount", model.getmList()[0].voucherPrice)
                                            .putExtra("currency", model.getmList()[0].currency)
                                            .putExtra("storevouchers", false)
                                    )
                                    checkClick = true
                                }
                            }
                            lastClickTime = currentTime
                        }
                    }
                    if (model!!.agentNotificationEnabled.equals("1", ignoreCase = true)) {
                        holder.binding.ivNoti.visibility = View.GONE
                    } else {
                        holder.binding.ivNoti.visibility = View.GONE
                    }
                    if (model.getmDealList().size > 0) {
                        holder.binding.lloffer.visibility = View.VISIBLE
                        holder.binding.tvAvailable.text = model.getmList()[0].voucherText
                        if (!TextUtils.isEmpty(model.getmList()[0].voucherRedeemedPrice) || !model.getmList()[0].voucherRedeemedPrice.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            holder.binding.tvRedeemedPrice.visibility = View.VISIBLE
                            holder.binding.tvRedeemedPrice.text =
                                model.getmList()[0].voucherRedeemedText + " " + model.getmList()[0].currency + "" + model.getmList()[0].voucherRedeemedPrice
                        }
                        val adap = AdapterStoreVoucherDealList(
                            context!!, model.getmDealList()
                        )
                        holder.binding.rvOffer.layoutManager = LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false
                        )
                        holder.binding.rvOffer.adapter = adap
                        adap.notifyDataSetChanged()
                    } else {
                        holder.binding.lloffer.visibility = View.GONE
                    }
                    holder.binding.tvAgentCompanyName.text = model.agentCompanyName
                    val textDistance =
                        "<font color=#101010>" + model.agentAddress + "</font> <font color=#007cfa>" + " (" + model.distance + ")" + "</font>"
                    holder.binding.tvagentAddress.text = Html.fromHtml(textDistance)
                    try {
                        if (model.getmList().size == 0) {
                            holder.binding.view.visibility = View.GONE
                            holder.binding.llVoucher.visibility = View.GONE
                        } else {
                            holder.binding.tvBalance.text =
                                model.getmList()[0].currency + model.getmList()[0].voucherPrice
                            holder.binding.view.visibility = View.GONE

                            if (model.getmList() != null && model.getmList().size ==1) {
                                holder.binding.llVoucher.visibility = View.VISIBLE
                                holder.binding.voucherListRcv.visibility = View.GONE
                            }else {
                                holder.binding.llVoucher.visibility = View.GONE
                                holder.binding.voucherListRcv.visibility = View.VISIBLE
                            }
                            if (!TextUtils.isEmpty(model.getmList()[0].voucherRedeemedPrice) || !model.getmList()[0].voucherRedeemedPrice.equals(
                                    "",
                                    ignoreCase = true
                                )
                            ) {
                                holder.binding.tvRedeemedPrice.visibility = View.VISIBLE
                                holder.binding.tvRedeemedPrice.text =
                                    model.getmList()[0].voucherRedeemedText + " " + model.getmList()[0].currency + "" + model.getmList()[0].voucherRedeemedPrice
                            }
                        }
                    } catch (e: Exception) {
                    }
                    try {
                        if (model.getmList()[0].voucherRedeemEnabled.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            holder.binding.llRedeem.visibility = View.VISIBLE
                            holder.binding.llRedeem.isEnabled = true
                            holder.binding.llRedeem.background =
                                context!!.resources.getDrawable(R.drawable.llyellow)
                        } else {
                            holder.binding.llRedeem.visibility = View.VISIBLE
                            holder.binding.llRedeem.isEnabled = false
                            holder.binding.llRedeem.background =
                                context!!.resources.getDrawable(R.drawable.llredeemgrey)
                        }
                    } catch (e: Exception) {
                        Log.e("Ex1", e.toString())
                    }
                    holder.binding.tvAgentCompanyName.setOnClickListener {
                        context!!.startActivity(
                            Intent(context, New_AgentDetails::class.java)
                                .putExtra("agentId", model.agentId)
                                .putExtra("direct", "false")
                        )
                    }
                    holder.binding.ivNoti.setOnClickListener {
                        context!!.startActivity(
                            Intent(context, NewNotification::class.java)
                                .putExtra("agentId", model.agentId)
                                .putExtra("title", model.agentCompanyName)
                        )
                    }
                    try {
                        if (model.giftCardslist.size > 0) {
                            holder.binding.giftcardLayout.visibility = View.VISIBLE
                            val listview_adater = WalletGiftCardAdapter(
                                context!!, model.giftCardslist
                            )
                            holder.binding.giftCardRcv.layoutManager = LinearLayoutManager(
                                context, RecyclerView.HORIZONTAL, false
                            )
                            holder.binding.giftCardRcv.itemAnimator = DefaultItemAnimator()
                            holder.binding.giftCardRcv.scheduleLayoutAnimation()
                            holder.binding.giftCardRcv.isNestedScrollingEnabled = false
                            holder.binding.giftCardRcv.setItemViewCacheSize(
                                model.giftCardslist.size
                            )
                            holder.binding.giftCardRcv.adapter = listview_adater
                            listview_adater.notifyDataSetChanged()
                        } else {
                            holder.binding.giftcardLayout.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                    Log.e("Ex", e.toString())
                }
            } else if (holder is ProgressViewHolder) {
                holder.progressBar.isIndeterminate = false
            }
        } catch (e: Exception) {
        }
    }

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

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
        }
    }

    private inner class ViewHolder internal constructor(var binding: AdapstorevouchersBinding) :
        RecyclerView.ViewHolder(binding.getRoot())
}