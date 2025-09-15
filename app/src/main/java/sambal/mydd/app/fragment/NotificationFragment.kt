package sambal.mydd.app.fragment

import sambal.mydd.app.callback.RecyclerClickListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import org.json.JSONArray
import sambal.mydd.app.adapter.NotificationListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.constant.MessageConstant
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.MainActivity
import sambal.mydd.app.activity.NewNotification
import com.google.gson.Gson
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONException
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import sambal.mydd.app.apiResponse.ApiResponse
import sambal.mydd.app.databinding.FragmentNotificationBinding
import sambal.mydd.app.models.Example
import sambal.mydd.app.models.Notification
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class NotificationFragment : Fragment(), RecyclerClickListener, OnRefreshListener {
    private var binding: FragmentNotificationBinding? = null
    private var notificationArray: JSONArray? = null
    private var fragmentContext: Context? = null
    private val count = "10"
    private var offset = 0
    private var adapter: NotificationListAdapter? = null
    var jsonArrays = ArrayList<Notification>()
    private var Check = "0"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        fragmentContext = activity
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)

        getNotifications()

        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    if (Check == "0" || Check == "2") {
                        offset += 1
                        Log.e("count", ">>$offset")
                        Check = "2"
                        getNotifications()
                    }
                }
            }
        })
       binding!!.notificationPageErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(fragmentContext)) {
                binding!!.notificationPageErrorLayout.someThingWentWrongLayout.visibility=View.GONE
                getNotifications()}
            else {
                AppUtil.showMsgAlert(
                    binding!!.recyclerView,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION
                )
            }
        }
        return binding!!.root
    }

    override fun setCellClicked(newsJSONObject: JSONObject, eventHasMultipleParts: String) {}
    private fun initView() {
        adapter =
            fragmentContext?.let {
                NotificationListAdapter(jsonArrays, it) { jsonObject, eventHasMultipleParts ->
                    val notificationId = jsonObject.optInt(KeyConstant.KEY_NOTI_ID)
                    updateNotificationDetails(notificationId.toString() + "")
                }
            }
        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerView.adapter = adapter
        if (notificationArray!!.length() > 0) {
            binding!!.msg.visibility = View.GONE
            binding!!.frame.visibility = View.GONE
            binding!!.recyclerView.visibility = View.VISIBLE
        } else {
            binding!!.msg.visibility = View.VISIBLE
            binding!!.frame.visibility = View.VISIBLE
            binding!!.recyclerView.visibility = View.GONE
        }


    }


    private fun getNotifications() {
        if (AppUtil.isNetworkAvailable(fragmentContext)) {
            binding!!.notificationPageErrorLayout.someThingWentWrongLayout.visibility=View.GONE
            val apiResponse: ApiResponse = object : ApiResponse() {}
            apiResponse.getNotifications(
                fragmentContext!!,
                offset.toString(),
                count,
                object : ResponseListener {

                    override fun onSuccess(response: ResponseBody?) {
                        try {
                            NewNotification.agentId = ""
                            notificationArray = null
                            val gson = Gson()
                            var resp: JSONObject? = null
                            try {
                                resp = JSONObject(response!!.string())
                                Log.e("NotiFys", resp.toString())
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val example = gson.fromJson(resp.toString(), Example::class.java)
                            val errorType = resp!!.optString(KeyConstant.KEY_ERROR_TYPE)
                            binding!!.shimmerViewContainer.visibility = View.GONE
                            binding!!.shimmerViewContainer.stopShimmerAnimation()
                            //To make Notification Count 0
                            val intent = Intent("refresh_Page")
                            LocalBroadcastManager.getInstance(fragmentContext!!)
                                .sendBroadcast(intent)
                            if (KeyConstant.KEY_MESSAGE_True == resp.optString(KeyConstant.KEY_STATUS)) {
                                if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                    val responseObj =
                                        resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    notificationArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_NOTIFICATION_LIST)
                                    Log.e(
                                        "notificationArray",
                                        ">>" + (notificationArray?.length() ?: 0)
                                    )
                                    for (i in example.response.notification.indices) {
                                        val notification = Notification()
                                        notification.notificationType =
                                            example.response.notification[i].notificationType
                                        notification.apPnotificationType =
                                            example.response.notification[i].apPnotificationType
                                        notification.notificationAgentId =
                                            example.response.notification[i].notificationAgentId
                                        notification.notificationAgentName =
                                            example.response.notification[i].notificationAgentName
                                        notification.notificationCreatedDate =
                                            example.response.notification[i].notificationCreatedDate
                                        notification.notificationDescription =
                                            example.response.notification[i].notificationDescription
                                        notification.notificationId =
                                            example.response.notification[i].notificationId
                                        notification.notificationLocation =
                                            example.response.notification[i].notificationLocation
                                        notification.notificationMemberId =
                                            example.response.notification[i].notificationMemberId
                                        notification.notificationTypeStatus =
                                            example.response.notification[i].notificationTypeStatus
                                        notification.webPageURL =
                                            example.response.notification[i].webPageURL
                                        notification.notificationStatus =
                                            example.response.notification[i].notificationStatus
                                        notification.notificationSubject =
                                            example.response.notification[i].notificationSubject
                                        jsonArrays.add(notification)
                                    }
                                    Log.e("jsonArrays", "Size" + jsonArrays.size)
                                    if (Check == "0") {
                                        initView()
                                        Check = "0"
                                    }
                                    try {
                                        adapter!!.notifyDataSetChanged()
                                    } catch (e: Exception) {
                                    }
                                } else {
                                    activity!!.runOnUiThread {
                                        try {
                                            if (jsonArrays.size > 0) {
                                            } else {
                                                binding!!.msg.visibility = View.VISIBLE
                                                binding!!.recyclerView.visibility = View.GONE
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(
                                            KeyConstant.KEY_STATUS
                                        ), ignoreCase = true
                                    )
                                ) {
                                    AppUtil.showMsgAlert(
                                        binding!!.msg,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    );
                                    activity!!.runOnUiThread {
                                        Check = "1"
                                        try {
                                            if (jsonArrays.size > 0) {
                                            } else {
                                                binding!!.frame.visibility = View.VISIBLE
                                                binding!!.msg.visibility = View.VISIBLE
                                                binding!!.recyclerView.visibility = View.GONE
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            AppUtil.showMsgAlert(
                                binding!!.msg,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            );
                            binding!!.notificationPageErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                        }
                    }

                    override fun onFailure(text: String?) {
                        ErrorMessage.E("ON FAILURE > $text")
                        AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        binding!!.notificationPageErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
                    }
                })
        }else {
            AppUtil.showMsgAlert(
                binding!!.recyclerView,
                MessageConstant.MESSAGE_INTERNET_CONNECTION
            )
            binding!!.notificationPageErrorLayout.someThingWentWrongLayout.visibility=View.VISIBLE
        }
    }

    override fun onRefresh() {
        Handler().postDelayed({
            offset = offset + 10
            getNotifications()
            try {
                adapter!!.notifyDataSetChanged()
            } catch (e: Exception) {
            }
        }, 1000)
    }

    private fun updateNotificationDetails(notificationId: String) {
        if (AppUtil.isNetworkAvailable(fragmentContext)) {
            val call = AppConfig.api_Interface().getNotificationsDetails(notificationId)
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
                                (fragmentContext as Activity?)!!.runOnUiThread { getNotifications() }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
                                    AppUtil.showMsgAlert(binding!!.msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    AppUtil.showMsgAlert(binding!!.msg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    companion object {
        val newInstance: NotificationFragment
            get() = NotificationFragment()
    }
}