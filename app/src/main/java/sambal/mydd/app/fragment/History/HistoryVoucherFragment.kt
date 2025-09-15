package sambal.mydd.app.fragment.History

import android.content.Context
import sambal.mydd.app.beans.VoucherBean
import sambal.mydd.app.adapter.AdapterDDHistoryVoucher
import sambal.mydd.app.utils.DialogManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.activity.NewhistoryDashboardDetials
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.FragmentHistoryVoucherBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class HistoryVoucherFragment : Fragment() {
    var binding: FragmentHistoryVoucherBinding? = null
    var context1: Context? = null
    var offset = 0
    var mVoucherList = ArrayList<VoucherBean>()
    var isFirst = true
    var adapVoucher: AdapterDDHistoryVoucher? = null
    var dialogManager: DialogManager? = null
    var count = 10
    var voucherUpdatedDate = ""
    var totalVoucher = ""
    private var Count_item = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryVoucherBinding.inflate(inflater, container, false)
        context1 = activity
        try {
            mVoucherList.clear()
            offset = 0
            binding!!.rvVoucher.setHasFixedSize(false)
            val linearLayoutManagers =
                LinearLayoutManager(context1, LinearLayoutManager.VERTICAL, false)
            binding!!.rvVoucher.layoutManager = linearLayoutManagers
            adapVoucher = AdapterDDHistoryVoucher(context1!!, mVoucherList, binding!!.rvVoucher)
            binding!!.rvVoucher.adapter = adapVoucher
            adapVoucher!!.notifyDataSetChanged()
            getAllDetails(true)
        } catch (e: Exception) {
        }
        binding!!.rvVoucher.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirst = false
                        getAllDetails(true)
                    }
                } catch (e: Exception) {
                }
            }
        })
        return binding!!.root
    }

    private fun getAllDetails(isShowingLoader: Boolean) {
        dialogManager = DialogManager()
        if (AppUtil.isNetworkAvailable(context1)) {
            if (isShowingLoader) {
                dialogManager!!.showProcessDialog(context1, "", false, null)
            }
            val call =
                AppConfig.api_Interface().getMyPointsHistory(offset.toString(), count.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("Histrpy", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                    ignoreCase = true)
                            ) {
                                dialogManager!!.stopProcessDialog()
                                val responseobj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                voucherUpdatedDate = responseobj.optString("voucherUpdatedDate")
                                totalVoucher =
                                    responseobj.optString("currency") + responseobj.optString("totalVoucher")
                                val arrHis = responseobj.optJSONArray("voucherList")
                                if (isFirst) {
                                    if (arrHis.length() > 0) {
                                        binding!!.ivVoucher.visibility = View.GONE
                                        binding!!.tvNOData.visibility = View.GONE
                                    } else {
                                        binding!!.ivVoucher.visibility = View.VISIBLE
                                        binding!!.tvNOData.visibility = View.VISIBLE
                                        binding!!.tvNOData.text =
                                            "You haven't redeemed any vouchers yet"
                                    }
                                }
                                for (i in 0 until arrHis.length()) {
                                    val obj = arrHis.optJSONObject(i)
                                    val redeemVoucherId = obj.optString("redeemVoucherId")
                                    val redeemType = obj.optString("redeemType")
                                    val agentName = obj.optString("agentName")
                                    val redeemTypeStatus = obj.optString("redeemTypeStatus")
                                    val ticketCurrency = obj.optString("ticketCurrency")
                                    val ticketPrice = obj.optString("ticketPrice")
                                    val redeemDate = obj.optString("redeemDate")
                                    val colorCode = obj.optString("colorCode")
                                    val vb = VoucherBean(redeemVoucherId,
                                        redeemType,
                                        agentName,
                                        redeemTypeStatus,
                                        ticketCurrency,
                                        ticketPrice,
                                        redeemDate,
                                        colorCode)
                                    mVoucherList.add(vb)
                                }
                                adapVoucher!!.notifyItemInserted(mVoucherList.size)
                                Count_item = if (isFirst) {
                                    mVoucherList.size
                                } else {
                                    binding!!.rvVoucher.scrollToPosition(Count_item + 1)
                                    mVoucherList.size
                                }
                                adapVoucher!!.setLoaded()
                                if (arrHis.length() > 0) {
                                    (context1 as NewhistoryDashboardDetials?)!!.setDataVoucher(
                                        totalVoucher)
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.tvNOData,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNOData,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvNOData,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvNOData,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvNOData, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvNOData, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }
}