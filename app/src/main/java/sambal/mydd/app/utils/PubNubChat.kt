package sambal.mydd.app.utils

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.pubnub.api.models.consumer.history.PNHistoryResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import sambal.mydd.app.callback.ChatHistoryCallback
import sambal.mydd.app.constant.UrlConstant
import org.json.JSONObject
import java.util.*


class PubNubChat(
    private val context: Context,
    private val chatHistoryCallback: ChatHistoryCallback?
) {
    var uuidStr: String? = null
    private var pubnub: PubNub? = null
    private var pnConfiguration: PNConfiguration? = null
    private var subscribeCallback: SubscribeCallback? = null
    private var chatHistoryList: List<PNHistoryItemResult> = ArrayList()
    fun initPubNub() {
        pnConfiguration = PNConfiguration()
        pnConfiguration!!.subscribeKey = UrlConstant.kPubNubSubscribeKey
        pnConfiguration!!.publishKey = UrlConstant.kPubNubPublishKey
        pnConfiguration!!.isSecure = true
        pnConfiguration!!.logVerbosity = PNLogVerbosity.BODY
        uuidStr = pnConfiguration!!.uuid
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
                ErrorMessage.E("PNMessageResult>>"+message.toString())
               try{
                if (message.channel != null) {
                    val jsonMsg = message.message
                    val jsonElement = message.message
                    var jsonObject: JsonObject? = null
                    jsonObject = message.message as JsonObject
                    chatHistoryCallback?.onRefreshChatList(jsonObject)
                } else {

                    // Message has been received on channel stored in
                    // message.getSubscription()
                }}catch (e:Exception){
                  try{
                   val gson = JsonParser().parse("{\"id\":\"value\"}").asJsonObject
                   chatHistoryCallback?.onRefreshChatList(gson)}
                  catch (e:Exception){}
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

    fun publishPubNub(param: JSONObject?, subscribeChannel: String?) {
        //"hello", "message"
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

    fun fetchPubNubHistory(channel: String?, i: Int) {
        pubnub!!.history()
            .channel(channel) // where to fetch history from
            .count(i) // how many items to fetch
            .async(object : PNCallback<PNHistoryResult?>() {
                override fun onResponse(result: PNHistoryResult?, status: PNStatus) {
                    Log.e("history msg", result.toString() + "" + "," + "count=" + i + "")
                    if (result != null) {
                        chatHistoryList = result.messages
                        chatHistoryCallback?.onRefreshHistoryList(chatHistoryList)
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
}