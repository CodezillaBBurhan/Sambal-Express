package sambal.mydd.app.fragment.MyRewards

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.beans.FreeDealsList
import sambal.mydd.app.adapter.AdapterHomeSignUpDels
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import sambal.mydd.app.databinding.FragmentVisitDealsBinding
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList

class VisitDealsFragment : Fragment() {
    var binding: FragmentVisitDealsBinding? = null
    var dialogManager: DialogManager? = null
    private var offset = 0
    private var isdealFirst = true
    var context1: Context? = null
    var mList = ArrayList<FreeDealsList>()
    var visit_adap: AdapterHomeSignUpDels? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var Check_Data = ""
    private var Count_item = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVisitDealsBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_visit_deals, container, false);
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(onNotice_refresh, IntentFilter("refresh_rewards"))
        context1 = activity
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvVisitdeals.setHasFixedSize(false)
        binding!!.rvVisitdeals.layoutManager = linearLayoutManager
        mList.clear()
        offset = 0
        visit_adap = AdapterHomeSignUpDels(activity!!, mList, binding!!.rvVisitdeals)
        binding!!.rvVisitdeals.setItemViewCacheSize(mList.size)
        binding!!.rvVisitdeals.adapter = visit_adap
        visit_adap!!.notifyDataSetChanged()
        isdealFirst = true
        binding!!.mainTitleTv.text = """You have not earned any 
 visit deals yet"""
        binding!!.subTitleTv.text = """Keep visiting DD Partners and collect 
 visits to earn free deals"""
        binding!!.llNoData.visibility = View.GONE
        binding!!.rvVisitdeals.visibility = View.VISIBLE
        getDeals(true)
        binding!!.rvVisitdeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        if (Check_Data == "") {
                            isdealFirst = false
                            offset = offset + 1
                            getDeals(true)
                        }
                    }
                } catch (e: Exception) {
                }
            }
        })
        binding!!.visitDealsErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(context1)) {
                binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                getDeals(true)
            } else {
                AppUtil.showMsgAlert(
                    binding!!.mainTitleTv,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
        return binding!!.root
    }

    private fun getDeals(showLoader: Boolean) {
        try {
            if (AppUtil.isNetworkAvailable(context1)) {
                binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                var materialDialog: Dialog? = null
                if (showLoader) {
                    materialDialog = ErrorMessage.initProgressDialog(context1)
                }
                val lat = MainActivity.userLat.toString()
                val lng = MainActivity.userLang.toString()
                Log.e("Latt", " : $lat")
                Log.e("Longii", " : $lng")
                val finalMaterialDialog = materialDialog
                val call =
                    AppConfig.api_Interface().getVisitDeals(lat, lng, offset.toString(), "10", "")
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                finalMaterialDialog?.dismiss()
                            } catch (e: Exception) {
                            }
                            if (response != null) {
                                try {
                                    binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility =
                                        View.GONE

                                    val resp = JSONObject(response.body()!!.string())
                                    Log.e("ExlusiveDeals", resp.toString())
                                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                        val objRes = resp.optJSONObject("response")
                                        val arr = objRes.optJSONArray("freeDealsList")
                                        if (isdealFirst && arr.length() > 0) {
                                            mList.clear()
                                        }
                                        if (arr.length() == 0) {
                                            Check_Data = "0"
                                        }
                                        for (i in 0 until arr.length()) {
                                            val objArr = arr.optJSONObject(i)
                                            val productId = objArr.optString("productId")
                                            val productName = objArr.optString("productName")
                                            val productDistance =
                                                objArr.optString("productDistance")
                                            val productAgentId = objArr.optString("productAgentId")
                                            val productAgentName =
                                                objArr.optString("productAgentName")
                                            val productAgentImage =
                                                objArr.optString("productAgentImage")
                                            val productImage = objArr.optString("productImage")
                                            val dealExpiredDate =
                                                objArr.optString("dealExpiredDate")
                                            val productType = objArr.optString("productType")
                                            val productTypeColor =
                                                objArr.optString("productTypeColor")
                                            val redeemedText = objArr.optString("redeemedText")
                                            val fl = FreeDealsList(
                                                productId,
                                                productName,
                                                productDistance,
                                                productAgentId,
                                                productAgentName,
                                                productAgentImage,
                                                productImage,
                                                dealExpiredDate,
                                                productType,
                                                productTypeColor,
                                                redeemedText
                                            )
                                            mList.add(fl)
                                        }
                                        Count_item = if (isdealFirst) {
                                            visit_adap!!.notifyItemInserted(mList.size)
                                            visit_adap!!.notifyDataSetChanged()
                                            mList.size
                                        } else {
                                            visit_adap!!.notifyItemInserted(mList.size)
                                            binding!!.rvVisitdeals.scrollToPosition(Count_item + 1)
                                            mList.size
                                        }
                                        val arrBanner = objRes.optJSONArray("BannerList")
                                        if (mList.size > 0) {
                                            binding!!.llNoData.visibility = View.GONE
                                            binding!!.rvVisitdeals.visibility = View.VISIBLE
                                        } else {
                                            binding!!.mainTitleTv.text = """You have not earned any 
 visit deals yet"""
                                            binding!!.subTitleTv.text =
                                                """Keep visiting DD Partners and collect 
 visits to earn free deals"""
                                            binding!!.llNoData.visibility = View.VISIBLE
                                            binding!!.rvVisitdeals.visibility = View.GONE
                                        }
                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                                resp.optString(
                                                    KeyConstant.KEY_STATUS
                                                ), ignoreCase = true
                                            )
                                        ) {

                                            //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility =
                                        View.VISIBLE

                                    //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                }
                            } else {
                                AppUtil.showMsgAlert(
                                    binding!!.mainTitleTv,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG
                                )
                                binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility =
                                    View.VISIBLE

                            }
                        } else {
                            dialogManager!!.stopProcessDialog()
                            Log.e("sendToken", "else is working" + response.code().toString())
                            binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility =
                                View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        finalMaterialDialog?.dismiss()
                        AppUtil.showMsgAlert(binding!!.mainTitleTv, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility =
                            View.VISIBLE
                    }
                })
            } else {
                AppUtil.showMsgAlert(
                    binding!!.mainTitleTv,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )

                binding!!.visitDealsErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
        }
    }

    private val onNotice_refresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Log.e("VisitDeals", "^^^^^^^^^^^^^^^^")
                mList.clear()
                offset = 0
                isdealFirst = true
                getDeals(true)
                visit_adap!!.notifyDataSetChanged()
            } catch (r: Exception) {
            }
        }
    }
}