package sambal.mydd.app.fragment.chat

import sambal.mydd.app.callback.ChatHistoryCallback
import com.pubnub.api.PNConfiguration
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.models.consumer.PNTimeResult
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.history.PNHistoryResult
import sambal.mydd.app.beans.ChatMainPubNubBean
import sambal.mydd.app.adapter.AdapterMainChat
import androidx.recyclerview.widget.RecyclerView
import com.pubnub.api.models.consumer.history.PNDeleteMessagesResult
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pubnub.api.PubNub
import sambal.mydd.app.MainActivity
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class PubNub(private val context: Context, private val chatHistoryCallback: ChatHistoryCallback?) {
    private var pubnub: PubNub? = null
    private var pnConfiguration: PNConfiguration? = null
    private var subscribeCallback: SubscribeCallback? = null
    private var chatHistoryList: List<PNHistoryItemResult> = ArrayList()
    private var time: String? = null
    fun initPubNub(agentUUID: String?, subKey: String?, pubKeys: String?) {
        pnConfiguration = PNConfiguration()
        pnConfiguration!!.subscribeKey = subKey
        pnConfiguration!!.publishKey = pubKeys
        pnConfiguration!!.uuid = agentUUID
        pnConfiguration!!.isSecure = true
        pnConfiguration!!.logVerbosity = PNLogVerbosity.BODY
        pubnub = PubNub(pnConfiguration)
    }

    fun subscribePubNubListener() {
        subscribeCallback = object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {
                when (status.operation) {
                    PNOperationType.PNSubscribeOperation, PNOperationType.PNUnsubscribeOperation -> {
                        when (status.category) {
                            PNStatusCategory.PNConnectedCategory, PNStatusCategory.PNReconnectedCategory, PNStatusCategory.PNDisconnectedCategory, PNStatusCategory.PNUnexpectedDisconnectCategory -> {
                                pubnub.reconnect()
                                pubnub.reconnect()
                            }
                            PNStatusCategory.PNAccessDeniedCategory -> pubnub.reconnect()
                            else -> pubnub.reconnect()
                        }
                        // heartbeat operations can in fact have errors, so it is important to check first for an error.
                        // For more information on how to configure heartbeat notifications through the status
                        // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                        if (status.isError) {
                            // There was an error with the heartbeat operation, handle here
                        } else {
                            // heartbeat operation was successful
                        }
                        run {}
                    }
                    PNOperationType.PNHeartbeatOperation -> {
                        if (status.isError) {
                        } else {
                        }
                        run {}
                    }
                    else -> {}
                }
            }

            override fun message(pubnub: PubNub, message: PNMessageResult) {
                Log.e("Pub/nubChat", message.toString() + "")
                if (message.channel != null) {
                    val jsonMsg = message.message
                    val jsonElement = message.message
                    Log.e("Chat", jsonElement.toString() + "")
                    var jsonObject: JsonObject? = null
                    jsonObject = message.message as JsonObject
                    Log.e("Message", "3")
                    chatHistoryCallback?.onRefreshChatList(jsonObject)
                } else {
                }
            }

            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {}
        }
        pubnub!!.addListener(subscribeCallback)
    }

    fun unSubscribePubNubListener() {
        pubnub!!.removeListener(subscribeCallback)
    }

    fun subscribePubNubChannel(subscribeChannel: String) {
        pubnub!!.subscribe()
            .channels(Arrays.asList(subscribeChannel)) // subscribe to channels
            .execute()
        Log.e("channel subscribe", "success")
    }

    fun publishPubNub(param: HashMap<String, String>, subscribeChannel: String?) {
        //"hello", "message"
        pubnub!!.time().async(object : PNCallback<PNTimeResult>() {
            override fun onResponse(result: PNTimeResult, status: PNStatus) {
                time = result.timetoken.toString() + ""
                param["timeToken"] = time + ""
                Log.e("Params", param.toString() + "")
                pubnub!!.publish()
                    .message(param)
                    .channel(subscribeChannel)
                    .async(object : PNCallback<PNPublishResult?>() {
                        override fun onResponse(result: PNPublishResult?, status: PNStatus) {
                            // handle publish result, status always present, result if successful
                            // status.isError to see if error happened
                            if (!status.isError) {
                                chatHistoryCallback?.clearData()
                                // Message successfully published to specified channel.
                            } else {
                            }
                        }
                    })
            }
        })
    }

    fun fetchPubNubHistory(channel: String?, i: Int) {
        pubnub!!.history()
            .channel(channel) // where to fetch history from
            .count(i) // how many items to fetch
            .async(object : PNCallback<PNHistoryResult?>() {
                override fun onResponse(result: PNHistoryResult?, status: PNStatus) {
                    Log.e("history msg", result.toString() + "" + "," + "count=" + i + "")
                    if (result != null) {
                        chatHistoryList = result.messages
                        Log.e("Result", result.messages.toString() + "")
                        chatHistoryCallback?.onRefreshHistoryList(chatHistoryList)
                    }

                    else{
                        val alertDialog = AlertDialog.Builder(
                            context).create()
                        alertDialog.setCanceledOnTouchOutside(false)
                        alertDialog.setTitle("No Data Found")
                        alertDialog.setMessage("")
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"
                        ) { dialog, which ->
                            dialog.dismiss()
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }

                        alertDialog.show()
                    }
                }
            })
    }

    fun unSubscribePubNubChannel(channel: String) {
        pubnub!!.unsubscribe()
            .channels(Arrays.asList(channel))
            .execute()
    }

    fun destroyPubNub() {
        pubnub!!.destroy()
    }

    fun getTimeToken(context: Context, agentId: String): String? {
        pubnub!!.time().async(object : PNCallback<PNTimeResult>() {
            override fun onResponse(result: PNTimeResult, status: PNStatus) {
                time = result.timetoken.toString() + ""
                sendChatExitId(context, agentId, time!!)
            }
        })
        return time
    }

    fun deleteMsg(
        channel: String,
        timeToken: Long?,
        endTime: Long,
        mChatList: ArrayList<ChatMainPubNubBean>,
        adap: AdapterMainChat,
        pos: Int,
        rv: RecyclerView
    ) {
        val mList: MutableList<String> = ArrayList()
        mList.add(channel)
        pubnub!!.deleteMessages()
            .channels(Arrays.asList(channel))
            .start(timeToken)
            .end(endTime)
            .async(object : PNCallback<PNDeleteMessagesResult?>() {
                override fun onResponse(result: PNDeleteMessagesResult?, status: PNStatus) {
                    try {
                        Log.e("time", "$timeToken,$endTime")
                        mChatList.removeAt(pos)
                        adap.notifyItemRemoved(pos)
                        try {
                            rv.smoothScrollToPosition(
                                rv.adapter!!.itemCount - 1)
                        } catch (e: Exception) {
                            Log.e("Exxx", e.toString())
                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }

    private fun sendChatExitId(contexts: Context, agentId: String, timeToken: String) {
        Log.e("TimeToken", timeToken)
        if (AppUtil.isNetworkAvailable(contexts)) {
            val call = AppConfig.api_Interface().exitChatAgent(agentId, timeToken, agentId)
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
                                Log.e("DoCharity", resp.toString() + "")

                                // dialogManager.stopProcessDialog();
                            } else {
                                (context as Activity).runOnUiThread { // dialogManager.stopProcessDialog();
                                    Toast.makeText(context,
                                        resp.optString("message"),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Ex", e.toString())
                            Toast.makeText(context,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("Ex", e.toString())
                            Toast.makeText(context,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                }
            })
        } else {
        }
    }
}