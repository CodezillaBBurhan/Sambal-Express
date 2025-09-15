package sambal.mydd.app.fragment

import sambal.mydd.app.activity.BaseActivity
import sambal.mydd.app.callback.RecyclerClickListener
import org.json.JSONArray
import sambal.mydd.app.adapter.AdapterFollowing
import sambal.mydd.app.beans.FollowingAgentModel
import sambal.mydd.app.beans.CategoryModel
import sambal.mydd.app.adapter.FollowingCategories_Adapter
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.util.Log
import android.view.View
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.activity.Categories
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.FragmentFollowingBinding
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class FollowingFragment : BaseActivity(), RecyclerClickListener {
    private var binding: FragmentFollowingBinding? = null
    var agentListArray: JSONArray? = null
    private var adapter: AdapterFollowing? = null
    private var count = "10"
    private var offset = 0
    private var isFirst = false
    private val modelList: MutableList<FollowingAgentModel> = ArrayList()
    var mCatlist = ArrayList<CategoryModel>()
    private var adapNEarMeCat: FollowingCategories_Adapter? = null
    var Cat_id: String? = "0"
    private var Count_item = 0
    override val contentResId: Int
        protected get() = R.layout.fragment_following

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_following)
        setToolbarWithBackButton_colorprimary("Following")
        binding!!.msg.text = "No Followings"
        binding!!.recyclerView.setHasFixedSize(false)
        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(this@FollowingFragment, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterFollowing(this@FollowingFragment,
            modelList,
            binding!!.recyclerView) { jsonObject, eventHasMultipleParts -> }
        binding!!.recyclerView.adapter = adapter
        isFirst = true
        getFollowingAgentList(true, "")
        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        isFirst = false
                        offset++
                        Log.e("count", count)
                        getFollowingAgentList(false, "onload")

                        //scrolled to bottom
                    }
                } catch (e: Exception) {
                }
            }
        })
        binding!!.tvAll.setOnClickListener {
            startActivityForResult(Intent(this@FollowingFragment,
                Categories::class.java).putExtra("Check", "viewAll")
                .putExtra("list", mCatlist), 210)
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@FollowingFragment, "")
        } catch (e: Exception) {
        }
    }

    override fun setCellClicked(newsJSONObject: JSONObject, eventHasMultipleParts: String) {
        unFollowAgent("")
    }

    private fun initView(check: String) {
        if (agentListArray != null && agentListArray!!.length() > 0) {
            if (check != "onload") {
                Count_item = agentListArray!!.length()
            }
            for (i in 0 until agentListArray!!.length()) {
                val jsonObject = agentListArray!!.optJSONObject(i)
                val model = FollowingAgentModel()
                model.agentAdsEnable =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_ADS_ENABLE).toString() + ""
                model.agentImage = jsonObject.optString(KeyConstant.KEY_AGENT_IMAGE)
                model.agentId = jsonObject.optInt(KeyConstant.KEY_AGENT_ID).toString() + ""
                model.agentAddress = jsonObject.optString(KeyConstant.KEY_AGENT_ADDRESS + "")
                model.agentCompanyName = jsonObject.optString(KeyConstant.KEY_AGENT_COMPANY_NAME)
                model.agentDescription = jsonObject.optString(KeyConstant.KEY_AGENT_DESCRIPTION)
                model.agentRating = jsonObject.optInt(KeyConstant.KEY_AGENT_RATING).toString() + ""
                model.agentEnableDescription =
                    jsonObject.optString(KeyConstant.KEY_AGENT_ENABLE_DESCRIPTION)
                model.agentDistance = jsonObject.optString(KeyConstant.KEY_AGENT_DISTANCE)
                model.agentDealEnabled =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_DEAL_ENABLED).toString() + ""
                model.agentVoucherEnabled =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_VOUCHER_ENABLED).toString() + ""
                model.agentRatingCount = jsonObject.optString("agentRatingCount")
                model.moreVouchers = jsonObject.optString(KeyConstant.KEY_MORE_VOUCHERS)
                model.dealButtonEnable = jsonObject.optString(KeyConstant.KEY_DEAL_BUTTON_ENABLE)
                model.moreProduct = jsonObject.optString(KeyConstant.KEY_MORE_PRODUCT)
                model.productId = jsonObject.optInt(KeyConstant.KEY_PRODUCT_ID).toString() + ""
                model.agentExternalUrlEnable =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_EXTERNAL_URL_ENABLE).toString() + ""
                model.agentExternalUrl = jsonObject.optString(KeyConstant.KEY_AGENT_EXTERNAL_URL)
                modelList.add(model)
            }
            adapter!!.notifyItemInserted(modelList.size)
            if (check == "onload") {
                binding!!.recyclerView.scrollToPosition(Count_item + 1)
                Count_item = modelList.size
            }
            adapter!!.setLoaded()
            if (check == "By_Cat") {
                adapter!!.notifyDataSetChanged()
            }
        }
        if (isFirst && modelList.size > 0) {
            binding!!.msg.visibility = View.GONE
            binding!!.recyclerView.visibility = View.VISIBLE
        } else if (isFirst && modelList.size == 0) {
            binding!!.msg.visibility = View.VISIBLE
            binding!!.recyclerView.visibility = View.GONE
        }
    }

    private fun getFollowingAgentList(isShowingLoader: Boolean, Check: String) {
        if (isShowingLoader) {
            agentListArray = null
        }
        val lat = MainActivity.userLat.toString() + ""
        val lang = MainActivity.userLang.toString() + ""
        val distance = MainActivity.distance + ""
        if (AppUtil.isNetworkAvailable(this@FollowingFragment)) {
            val dialogManager = DialogManager()
            if (isShowingLoader || !isShowingLoader) {
                dialogManager.showProcessDialog(this@FollowingFragment, "", false, null)
            }
            val call = AppConfig.api_Interface()
                .getMyFollowMerchantsList(lat, lang, distance, offset.toString(), count, Cat_id)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            if (isShowingLoader) {
                                modelList.clear()
                                agentListArray = null
                            }
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("FollowingList", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    agentListArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                    if (Check != "By_Cat") {
                                        val arrCatList = responseObj.optJSONArray("categoryList")
                                        mCatlist.clear()
                                        if (arrCatList.length() > 0) {
                                            for (i in 0 until arrCatList.length()) {
                                                val ojCat = arrCatList.optJSONObject(i)
                                                val categoryId = ojCat.optString("categoryId")
                                                val categoryName = ojCat.optString("categoryName")
                                                val categoryImage = ojCat.optString("categoryImage")
                                                val cm = CategoryModel(categoryId,
                                                    categoryName,
                                                    categoryImage)
                                                mCatlist.add(cm)
                                            }
                                            Log.e("mCatlist", ">>" + mCatlist.size)
                                        }
                                    } else if (Check == "") {
                                        try {
                                            if (modelList.size > 0) {
                                                modelList.clear()
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                    dialogManager.stopProcessDialog()
                                    runOnUiThread {
                                        if (Check != "By_Cat") {
                                            refresh_background("-2")
                                        }
                                        initView(Check)
                                    }
                                } else {
                                    if (isFirst && modelList.size > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.recyclerView.visibility = View.VISIBLE
                                    } else if (isFirst && modelList.size == 0) {
                                        binding!!.msg.visibility = View.VISIBLE
                                        binding!!.recyclerView.visibility = View.GONE
                                    }
                                    dialogManager.stopProcessDialog()
                                    runOnUiThread { initView(Check) }
                                }
                            } else if (errorType == KeyConstant.KEY_RESPONSE_CODE_202) {
                                dialogManager.stopProcessDialog()
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    if (isFirst) {
                                        if (modelList.size > 0) {
                                            binding!!.msg.visibility = View.GONE
                                            binding!!.recyclerView.visibility = View.VISIBLE
                                        } else {
                                            binding!!.msg.visibility = View.VISIBLE
                                            binding!!.recyclerView.visibility = View.GONE
                                        }
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    if (isFirst && modelList.size > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.recyclerView.visibility = View.VISIBLE
                                    } else if (isFirst && modelList.size == 0) {
                                        binding!!.msg.visibility = View.VISIBLE
                                        binding!!.recyclerView.visibility = View.GONE
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", "" + e.toString())
                            //AppUtil.showMsgAlert(msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAll, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun unFollowAgent(agentId: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().updateUnFollowAgent(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("UnFollow", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.getJSONObject(KeyConstant.KEY_RESPONSE)
                                if (responseObj != null) {
                                    runOnUiThread { dialogManager.stopProcessDialog() }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.msg,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.msg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.msg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.msg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun refreshDetals(catIds: String?) {
        if (AppUtil.isNetworkAvailable(this@FollowingFragment)) {
            count = "10"
            offset = 0
            Cat_id = catIds
            modelList.clear()
            getFollowingAgentList(true, "By_Cat")
        }
    }

    fun refresh_background(id: String?) {
        adapNEarMeCat = FollowingCategories_Adapter(this@FollowingFragment,
            mCatlist,
            this@FollowingFragment,
            id!!)
        binding!!.rvCat.layoutManager =
            LinearLayoutManager(this@FollowingFragment, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvCat.adapter = adapNEarMeCat
        binding!!.rvCat.isNestedScrollingEnabled = false
        adapNEarMeCat!!.notifyDataSetChanged()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 210 && resultCode == 210) {
            Log.e("Cat_id", "" + data!!.getStringExtra("Cat_id"))
            try {
                refresh_background(data.getStringExtra("Cat_id"))
                refreshDetals(data.getStringExtra("Cat_id"))
            } catch (e: Exception) {
            }
        }
    }
}