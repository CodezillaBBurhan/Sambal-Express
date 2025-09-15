package sambal.mydd.app.fragment.chat

import sambal.mydd.app.adapter.AdapterChatByLocation
import sambal.mydd.app.adapter.AdapterChatFavourite
import sambal.mydd.app.beans.ChatBean
import sambal.mydd.app.beans.ChatFAvBean
import org.json.JSONArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.AppUtil
import android.content.Intent
import sambal.mydd.app.activity.SearchMerchantChatList
import sambal.mydd.app.constant.MessageConstant
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import sambal.mydd.app.utils.WrapContentLinearLayoutManager
import org.json.JSONObject
import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.SavedData
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.utils.StatusBarcolor
import android.os.PowerManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NewchatlistBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class ChatLocationFavourite : Fragment(), View.OnClickListener {
    var adapter: AdapterChatByLocation? = null
    var adapterChatFavourite: AdapterChatFavourite? = null
    var modelList: MutableList<ChatBean> = ArrayList()
    var modelfavList: MutableList<ChatFAvBean> = ArrayList()
    var handler: Handler? = null
    var context1: Context? = null
    var agentListArray: JSONArray? = null
    private var binding: NewchatlistBinding? = null
    private val count = "20"
    private var offset = 0
    private var isFirst = false
    private var subscribeKey: String? = null
    private var publishKey: String? = null
    var Check = "location"
    private var Count_item = 0
    private var isDeviceLocked = false
    var lat = ""
    var lang = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.newchatlist, container, false)
        val view = binding!!.getRoot()
        context1 = activity
        binding!!.llLocation.setOnClickListener(this)
        binding!!.llFavourite.setOnClickListener(this)
        handler = Handler()
        binding!!.msg.text = "No Data"
        MainActivity.llSearch!!.setOnClickListener {
            if (AppUtil.isNetworkAvailable(activity)) {
                val intent = Intent(context1, SearchMerchantChatList::class.java)
                intent.putExtra("page", "Merchant's Search")
                startActivityForResult(intent, 90)
            } else {
                AppUtil.showMsgAlert(
                    binding!!.tvLocation,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(onNotice, IntentFilter("Location"))
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(onNoticeRefresh, IntentFilter("Refresh_Chat_Fragment"))
        binding!!.recyclerView.setHasFixedSize(false)
        binding!!.recyclerView.layoutManager = WrapContentLinearLayoutManager(context1)
        adapter = AdapterChatByLocation(
            context1!!, modelList, binding!!.recyclerView, this@ChatLocationFavourite
        ) { jsonObject, eventHasMultipleParts -> Log.e("Start", jsonObject.toString() + "") }
        binding!!.recyclerView.adapter = adapter
        adapter!!.notifyDataSetChanged()
        isFirst = true
        getFollowingAgentList(true)
        if (AppUtil.isNetworkAvailable(activity)) {
        } else {
            AppUtil.showMsgAlert(binding!!.tvLocation, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        isFirst = false
                        offset++
                        Log.e("count", count)
                        getFollowingAgentList(true)

                        //scrolled to bottom
                    }
                } catch (e: Exception) {
                }
            }
        })

        binding!!.newChatPageErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(activity)) {
                getFollowingAgentList(true)
            } else {
                AppUtil.showMsgAlert(
                    binding!!.tvFavourite,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
        return view
    }

    private val onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            try {
                if (intent != null) {
                    MainActivity.userLat = intent.getStringExtra("Lat")!!.toDouble()
                    MainActivity.userLang = intent.getStringExtra("Long")!!.toDouble()
                    if (Check == "location") {
                        modelList.clear()
                        isFirst = true
                        offset = 0
                        adapter!!.notifyDataSetChanged()
                        getFollowingAgentList(true)
                    } else {
                        isFirst = true
                        offset = 0
                        modelfavList.clear()
                        getFavouriteAgentList(true)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
    private val onNoticeRefresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            try {
                if (MainActivity.userLat.toString() != lat) {
                    modelList.clear()
                    isFirst = true
                    offset = 0
                    adapter!!.notifyDataSetChanged()
                    getFollowingAgentList(true)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llLocation -> {
                Check = "location"
                binding!!.msg.visibility = View.GONE
                binding!!.llFavourite.background = null
                binding!!.llLocation.background =
                    activity!!.resources.getDrawable(R.drawable.llpurpleround)
                binding!!.tvFavourite.setTextColor(activity!!.resources.getColor(R.color.black))
                binding!!.tvLocation.setTextColor(activity!!.resources.getColor(R.color.white))
                binding!!.recyclerView.visibility = View.VISIBLE
                binding!!.recyclerViewFav.visibility = View.GONE
                binding!!.msg.text = "No Record"
                try {
                    modelList.clear()
                    adapter!!.notifyDataSetChanged()
                    adapter = null
                } catch (e: Exception) {
                }
                adapter = AdapterChatByLocation(
                    context1!!,
                    modelList,
                    binding!!.recyclerView,
                    this@ChatLocationFavourite
                ) { jsonObject, eventHasMultipleParts -> }
                binding!!.recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
                isFirst = true
                getFollowingAgentList(true)
            }
            R.id.llFavourite -> {
                Check = "favroite"
                binding!!.msg.visibility = View.GONE
                binding!!.llLocation.background = null
                binding!!.llFavourite.background =
                    activity!!.resources.getDrawable(R.drawable.llpurpleround)
                binding!!.tvLocation.setTextColor(activity!!.resources.getColor(R.color.black))
                binding!!.tvFavourite.setTextColor(activity!!.resources.getColor(R.color.white))
                binding!!.recyclerView.visibility = View.GONE
                binding!!.recyclerViewFav.visibility = View.VISIBLE
                binding!!.recyclerViewFav.setHasFixedSize(false)
                binding!!.recyclerViewFav.layoutManager = WrapContentLinearLayoutManager(context1)
                modelfavList.clear()
                adapterChatFavourite = AdapterChatFavourite(
                    context1!!,
                    modelfavList.toMutableList(),
                    binding!!.recyclerViewFav,
                    this@ChatLocationFavourite
                ) { jsonObject, eventHasMultipleParts -> }
                binding!!.recyclerViewFav.adapter = adapterChatFavourite
                isFirst = true
                offset = 0
                modelfavList.clear()
                binding!!.msg.text = "No Favourite"
                getFavouriteAgentList(true)
            }
        }
    }

    fun gotoChat(
        id: String?,
        name: String?,
        isAdmin: String?,
        followingstatus: String?,
        position: Int
    ) {
        if (AppUtil.isNetworkAvailable(activity)) {

            startActivityForResult(
                Intent(activity, ChatMain::class.java)
                    .putExtra("id", id)
                    .putExtra("name", name)
                    .putExtra("isAdmin", isAdmin)
                    .putExtra("subskey", subscribeKey)
                    .putExtra("pubskey", publishKey)
                    .putExtra("followingstatus", followingstatus)
                    .putExtra("position", position)
                    .putExtra("type", "non_direct"), 80
            )
        } else {
            AppUtil.showMsgAlert(binding!!.tvLocation, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 80) {
            try {
                val pos = data!!.getIntExtra("position", 0)
                modelList[pos].followingStatus = "1"
                adapter!!.notifyItemChanged(pos)
            } catch (e: Exception) {
                ErrorMessage.E("Exception >> $e")
            }
        }
    }

    private fun getFollowingAgentList(isShowingLoader: Boolean) {
        agentListArray = null
        if (MainActivity.userLat != 0.0 && MainActivity.userLang != 0.0) {
            lat = MainActivity.userLat.toString() + ""
            lang = MainActivity.userLang.toString() + ""
            ErrorMessage.E("getFollowingAgentList 1 >>$lat>>$lang")
        } else if (SavedData.getLatitude() != "0" && SavedData.getLongitude() != "0") {
            lat = SavedData.getLatitude().toString()
            lang = SavedData.getLongitude().toString()
            ErrorMessage.E("getFollowingAgentList 2$lat>>$lang")
        }
        if (lat != "" && lang != "") {
            if (AppUtil.isNetworkAvailable(activity)) {
                binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                val dialogManager = DialogManager()
                if (isShowingLoader) {
                    dialogManager.showProcessDialog(context1, "", false, null)
                }
                val call = AppConfig.api_Interface()
                    .getAllMerchantsChatList(lat, lang, offset.toString(), count)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                                ErrorMessage.E("isFirst$isFirst")
                                agentListArray = null
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("ChatByLocation", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                    if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                        val responseObj =
                                            resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                        publishKey = responseObj.optString("publishKey")
                                        subscribeKey = responseObj.optString("subscribeKey")
                                        agentListArray =
                                            responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                        (context1 as Activity?)!!.runOnUiThread {
                                            Log.e("1", "1")
                                            if (agentListArray != null) {
                                                for (i in 0 until agentListArray!!.length()) {
                                                    try {
                                                        val obj = agentListArray!!.optJSONObject(i)
                                                        Log.e("1", "2")
                                                        val agentId = obj.optString("agentId")
                                                        val agentCompanyName =
                                                            obj.optString("agentCompanyName")
                                                        val agentAddress =
                                                            obj.optString("agentAddress")
                                                        val agentImage = obj.optString("agentImage")
                                                        val agentEmail = obj.optString("agentEmail")
                                                        val agentMobile =
                                                            obj.optString("agentMobile")
                                                        val agentCountry =
                                                            obj.optString("agentCountry")
                                                        val agentCountryCode =
                                                            obj.optString("agentCountryCode")
                                                        val agentURL = obj.optString("agentURL")
                                                        val agentExternalURLEnable =
                                                            obj.optString("agentExternalURLEnable")
                                                        val agentExternalURL =
                                                            obj.optString("agentExternalURL")
                                                        val agentDescription =
                                                            obj.optString("agentDescription")
                                                        val agentLatitude =
                                                            obj.optString("agentLatitude")
                                                        val agentLongitude =
                                                            obj.optString("agentLongitude")
                                                        val agentDistance =
                                                            obj.optString("agentDistance")
                                                        val agentFavourite =
                                                            obj.optString("agentFavourite")
                                                        val agentVoucherEnabled =
                                                            obj.optString("agentVoucherEnabled")
                                                        val agentEnableDescription =
                                                            obj.optString("agentEnableDescription")
                                                        val agentAdsEnable =
                                                            obj.optString("agentAdsEnable")
                                                        val agentRating =
                                                            obj.optString("agentRating")
                                                        val agentDealEnabled =
                                                            obj.optString("agentDealEnabled")
                                                        val dealButtonEnable =
                                                            obj.optString("dealButtonEnable")
                                                        val moreProduct =
                                                            obj.optString("moreProduct")
                                                        val moreVouchers =
                                                            obj.optString("moreVouchers")
                                                        val productId = obj.optString("productId")
                                                        val ddPointsEnabled =
                                                            obj.optString("ddPointsEnabled")
                                                        val isAdmin = obj.optString("isAdmin")
                                                        val chatCount = obj.optString("chatCount")
                                                        val chatTime = obj.optString("chatTime")
                                                        val followingStatus =
                                                            obj.optString("followingStatus")
                                                        Log.e("folloq", followingStatus)
                                                        val cb = ChatBean(
                                                            agentId,
                                                            agentCompanyName,
                                                            agentAddress,
                                                            agentImage,
                                                            agentEmail,
                                                            agentMobile,
                                                            agentCountry,
                                                            agentCountryCode,
                                                            agentURL,
                                                            agentExternalURLEnable,
                                                            agentExternalURL,
                                                            agentDescription,
                                                            agentLatitude,
                                                            agentLongitude,
                                                            agentDistance,
                                                            agentFavourite,
                                                            agentVoucherEnabled,
                                                            agentEnableDescription,
                                                            agentAdsEnable,
                                                            agentRating,
                                                            agentDealEnabled,
                                                            dealButtonEnable,
                                                            moreProduct,
                                                            moreVouchers,
                                                            productId,
                                                            ddPointsEnabled,
                                                            isAdmin,
                                                            chatCount,
                                                            chatTime,
                                                            followingStatus
                                                        )
                                                        modelList.add(cb)
                                                    } catch (e: Exception) {
                                                        Log.e("ex", e.toString())
                                                    }
                                                }
                                            }
                                            try {
                                                adapter!!.notifyItemInserted(modelList.size)
                                                Count_item = if (isFirst) {
                                                    modelList.size
                                                } else {
                                                    binding!!.recyclerView.scrollToPosition(
                                                        Count_item + 1
                                                    )
                                                    modelList.size
                                                }
                                                adapter!!.setLoaded()
                                                dialogManager.stopProcessDialog()
                                            } catch (e: Exception) {
                                                Log.e("Lcoa", e.toString())
                                            }
                                            (context1 as Activity?)!!.runOnUiThread {
                                                if (isFirst && modelList.size > 0) {
                                                    binding!!.msg.visibility = View.GONE
                                                    binding!!.llNoData.visibility = View.GONE
                                                    binding!!.recyclerView.visibility = View.VISIBLE
                                                } else if (isFirst && modelList.size == 0) {
                                                    binding!!.msg.visibility = View.GONE
                                                    binding!!.llNoData.visibility = View.VISIBLE
                                                    binding!!.recyclerView.visibility = View.GONE
                                                }
                                            }
                                        }
                                    } else {
                                        dialogManager.stopProcessDialog()
                                        (context1 as Activity?)!!.runOnUiThread {
                                            if (isFirst && modelList.size > 0) {
                                                binding!!.msg.visibility = View.GONE
                                                binding!!.llNoData.visibility = View.GONE
                                                binding!!.recyclerView.visibility = View.VISIBLE
                                            } else if (isFirst && modelList.size == 0) {
                                                binding!!.msg.visibility = View.GONE
                                                binding!!.llNoData.visibility = View.VISIBLE
                                                binding!!.recyclerView.visibility = View.GONE
                                            }
                                        }
                                        //adapter.setLoaded();
                                    }
                                } else if (errorType == KeyConstant.KEY_RESPONSE_CODE_202) {
                                    dialogManager.stopProcessDialog()
                                    ErrorMessage.E("isFirst$isFirst")
                                    if (isFirst && modelList.size > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.llNoData.visibility = View.GONE
                                        binding!!.recyclerView.visibility = View.VISIBLE
                                    } else if (!isFirst && modelList.size > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.llNoData.visibility = View.GONE
                                        binding!!.recyclerView.visibility = View.VISIBLE
                                    } else {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.llNoData.visibility = View.VISIBLE
                                        binding!!.recyclerView.visibility = View.GONE
                                    }
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                            resp.optString(
                                                KeyConstant.KEY_STATUS
                                            ), ignoreCase = true
                                        )
                                    ) {
                                        dialogManager.stopProcessDialog()
                                        //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                dialogManager.stopProcessDialog()
                                binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                                //AppUtil.showMsgAlert(msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                            }
                        } else {
                            binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                            dialogManager.stopProcessDialog()
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvFavourite, t.message)
                        binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                    }
                })
            } else {
                binding!!.newChatPageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                AppUtil.showMsgAlert(
                    binding!!.tvLocation,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
    }

    private fun getFavouriteAgentList(isShowingLoader: Boolean) {
        agentListArray = null
        var lat = MainActivity.userLat.toString() + ""
        var lang = MainActivity.userLang.toString() + ""
        if (lat.equals(null, ignoreCase = true)) {
            lat = ""
        }
        if (lang.equals(null, ignoreCase = true)) {
            lang = ""
        }
        if (AppUtil.isNetworkAvailable(activity)) {
            val dialogManager = DialogManager()
            if (isShowingLoader) {
                dialogManager.showProcessDialog(context1, "", false, null)
            }
            val call = AppConfig.api_Interface()
                .getAllMerchantsFavoriteChatList(lat, lang, offset.toString(), count)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("FaV", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    publishKey = responseObj.optString("publishKey")
                                    subscribeKey = responseObj.optString("subscribeKey")
                                    agentListArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                    (context1 as Activity?)!!.runOnUiThread {
                                        if (agentListArray != null) {
                                            for (i in 0 until agentListArray!!.length()) {
                                                try {
                                                    val obj = agentListArray!!.optJSONObject(i)
                                                    val agentId = obj.optString("agentId")
                                                    val agentCompanyName =
                                                        obj.optString("agentCompanyName")
                                                    val agentAddress = obj.optString("agentAddress")
                                                    val agentImage = obj.optString("agentImage")
                                                    val agentEmail = obj.optString("agentEmail")
                                                    val agentMobile = obj.optString("agentMobile")
                                                    val agentCountry = obj.optString("agentCountry")
                                                    val agentCountryCode =
                                                        obj.optString("agentCountryCode")
                                                    val agentURL = obj.optString("agentURL")
                                                    val agentExternalURLEnable =
                                                        obj.optString("agentExternalURLEnable")
                                                    val agentExternalURL =
                                                        obj.optString("agentExternalURL")
                                                    val agentDescription =
                                                        obj.optString("agentDescription")
                                                    val agentLatitude =
                                                        obj.optString("agentLatitude")
                                                    val agentLongitude =
                                                        obj.optString("agentLongitude")
                                                    val agentDistance =
                                                        obj.optString("agentDistance")
                                                    val agentFavourite =
                                                        obj.optString("agentFavourite")
                                                    val agentVoucherEnabled =
                                                        obj.optString("agentVoucherEnabled")
                                                    val agentEnableDescription =
                                                        obj.optString("agentEnableDescription")
                                                    val agentAdsEnable =
                                                        obj.optString("agentAdsEnable")
                                                    val agentRating = obj.optString("agentRating")
                                                    val agentDealEnabled =
                                                        obj.optString("agentDealEnabled")
                                                    val dealButtonEnable =
                                                        obj.optString("dealButtonEnable")
                                                    val moreProduct = obj.optString("moreProduct")
                                                    val moreVouchers = obj.optString("moreVouchers")
                                                    val productId = obj.optString("productId")
                                                    val ddPointsEnabled =
                                                        obj.optString("ddPointsEnabled")
                                                    val isAdmin = obj.optString("isAdmin")
                                                    val chatCount = obj.optString("chatCount")
                                                    val chatTime = obj.optString("chatTime")
                                                    val followingStatus =
                                                        obj.optString("followingStatus")
                                                    val cb = ChatFAvBean(
                                                        agentId,
                                                        agentCompanyName,
                                                        agentAddress,
                                                        agentImage,
                                                        agentEmail,
                                                        agentMobile,
                                                        agentCountry,
                                                        agentCountryCode,
                                                        agentURL,
                                                        agentExternalURLEnable,
                                                        agentExternalURL,
                                                        agentDescription,
                                                        agentLatitude,
                                                        agentLongitude,
                                                        agentDistance,
                                                        agentFavourite,
                                                        agentVoucherEnabled,
                                                        agentEnableDescription,
                                                        agentAdsEnable,
                                                        agentRating,
                                                        agentDealEnabled,
                                                        dealButtonEnable,
                                                        moreProduct,
                                                        moreVouchers,
                                                        productId,
                                                        ddPointsEnabled,
                                                        isAdmin,
                                                        chatCount,
                                                        chatTime,
                                                        followingStatus
                                                    )
                                                    modelfavList.add(cb)
                                                } catch (e: Exception) {
                                                }
                                            }
                                        }
                                        try {
                                            adapterChatFavourite!!.notifyItemInserted(modelfavList.size)
                                            if (agentListArray?.length()!! > 9) {
                                                adapterChatFavourite!!.setLoaded()
                                            }
                                            try {
                                                dialogManager.stopProcessDialog()
                                            } catch (e: Exception) {
                                            }
                                        } catch (e: Exception) {
                                            Log.e("Lcoa", e.toString())
                                        }
                                        (context1 as Activity?)!!.runOnUiThread {
                                            if (isFirst && modelfavList.size > 0) {
                                                binding!!.msg.visibility = View.GONE
                                                binding!!.recyclerViewFav.visibility = View.VISIBLE
                                                binding!!.recyclerView.visibility = View.GONE
                                            } else if (isFirst && modelfavList.size == 0) {
                                                binding!!.msg.visibility = View.VISIBLE
                                                binding!!.recyclerViewFav.visibility = View.GONE
                                                binding!!.recyclerView.visibility = View.GONE
                                            }
                                        }
                                    }
                                } else {
                                    dialogManager.stopProcessDialog()
                                    (context1 as Activity?)!!.runOnUiThread {
                                        if (isFirst && modelfavList.size > 0) {
                                            binding!!.msg.visibility = View.GONE
                                            binding!!.recyclerViewFav.visibility = View.VISIBLE
                                        } else if (isFirst && modelfavList.size == 0) {
                                            binding!!.msg.visibility = View.VISIBLE
                                            binding!!.recyclerViewFav.visibility = View.GONE
                                        }
                                    }
                                    //adapter.setLoaded();
                                }
                            } else if (errorType.equals(
                                    KeyConstant.KEY_RESPONSE_CODE_202,
                                    ignoreCase = true
                                )
                            ) {
                                dialogManager.stopProcessDialog()
                                (context1 as Activity?)!!.runOnUiThread {
                                    if (isFirst && modelfavList.size > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.recyclerViewFav.visibility = View.VISIBLE
                                    } else if (isFirst && modelfavList.size == 0) {
                                        binding!!.msg.visibility = View.VISIBLE
                                        binding!!.recyclerViewFav.visibility = View.GONE
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            //AppUtil.showMsgAlert(msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvLocation, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isDeviceLocked) {
            isDeviceLocked = false
            modelList.clear()
            adapter!!.notifyDataSetChanged()
            isFirst = true
            getFollowingAgentList(true)
        }
        if (isVisible) {
            try {
                StatusBarcolor.setStatusbarColor(activity!!, "")
            } catch (e: Exception) {
            }
        }
    }

    override fun onPause() {
        val powerManager = activity!!.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean
        isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            powerManager.isScreenOn
        }
        if (!isScreenOn) {
            ErrorMessage.E("FROM HOME FRAGMENT SCREEN IS LOCKED")
            isDeviceLocked = true
        }
        super.onPause()
    }

    fun checkList() {
        try {
            if (modelfavList.size == 0) {
                binding!!.msg.visibility = View.VISIBLE
                binding!!.recyclerViewFav.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
    }
}