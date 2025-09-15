package sambal.mydd.app.fragment.History

import android.content.Context
import sambal.mydd.app.beans.PointsBean
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.adapter.AdapterDDHistoryPoints
import sambal.mydd.app.utils.DialogManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.activity.NewhistoryDashboardDetials
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.FragmentHistoryPointsBinding
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class HistoryPointsFragment : Fragment() {
    var binding: FragmentHistoryPointsBinding? = null
    var offset = 0
    var count = 10
    var mPointsBean = ArrayList<PointsBean>()
    var linearLayoutManager: LinearLayoutManager? = null
    var context1: Context? = null
    var adapterPoints: AdapterDDHistoryPoints? = null
    var isFirstTym = true
    var dialogManager: DialogManager? = null
    var voucherUpdatedDate = ""
    var pointsUpdatedDate = ""
    var totalPoints = ""
    private var Count_item = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryPointsBinding.inflate(inflater, container, false)
        context1 = activity
        offset = 0
        mPointsBean.clear()
        binding!!.rv.visibility = View.VISIBLE
        binding!!.rv.setHasFixedSize(false)
        linearLayoutManager = LinearLayoutManager(context1, LinearLayoutManager.VERTICAL, false)
        binding!!.rv.layoutManager = linearLayoutManager
        adapterPoints = AdapterDDHistoryPoints(context1!!, mPointsBean, binding!!.rv)
        adapterPoints!!.notifyDataSetChanged()
        binding!!.rv.adapter = adapterPoints
        adapterPoints!!.notifyDataSetChanged()
        isFirstTym = true
        getAllDetails(true)
        binding!!.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirstTym = false
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
                                pointsUpdatedDate = responseobj.optString("pointsUpdatedDate")
                                totalPoints = responseobj.optString("userTotalPoints")

                                val arVoucher = responseobj.optJSONArray("pointHistory")
                                if (isFirstTym) {
                                    (context1 as NewhistoryDashboardDetials?)!!.setDataPoints(
                                        responseobj.optString("userTotalVisitCount"),
                                        responseobj.optString("userTotalPoints"))
                                    if (arVoucher.length() > 0) {
                                        binding!!.ivVoucher.visibility = View.GONE
                                        binding!!.tvNOData.visibility = View.GONE
                                    } else {
                                        binding!!.ivVoucher.visibility = View.VISIBLE
                                        binding!!.tvNOData.visibility = View.VISIBLE
                                        binding!!.tvNOData.text =
                                            "You haven't redeemed any points yet"
                                    }
                                }
                                for (i in 0 until arVoucher.length()) {
                                    val obPo = arVoucher.optJSONObject(i)
                                    val agentId = obPo.optString("agentId")
                                    val agentName = obPo.optString("agentName")
                                    val pointId = obPo.optString("pointId")
                                    val totalPoints = obPo.optString("totalPoints")
                                    val visitCount = obPo.optString("visitCount")
                                    val pointCreatedDate = obPo.optString("pointCreatedDate")
                                    val pointType = obPo.optString("pointType")
                                    val colorCode = obPo.optString("colorCode")
                                    val pb = PointsBean(agentId,
                                        agentName,
                                        pointId,
                                        totalPoints,
                                        visitCount,
                                        pointCreatedDate,
                                        pointType,
                                        colorCode)
                                    mPointsBean.add(pb)
                                }
                                adapterPoints!!.notifyItemInserted(mPointsBean.size)
                                Count_item = if (isFirstTym) {
                                    mPointsBean.size
                                } else {
                                    binding!!.rv.scrollToPosition(Count_item + 1)
                                    mPointsBean.size
                                }
                                adapterPoints!!.setLoaded()
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