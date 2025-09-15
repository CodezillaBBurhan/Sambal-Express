package sambal.mydd.app.FCMUtils

import sambal.mydd.app.utils.AppUtil.isForeground
import sambal.mydd.app.utils.ErrorMessage.E
import sambal.mydd.app.utils.PreferenceHelper.Companion.getInstance
import sambal.mydd.app.utils.DateUtil.UTCToLocalSec
import sambal.mydd.app.utils.GPSTracker.Companion.requestSingleUpdate
import sambal.mydd.app.utils.AppUtil.isNetworkAvailable
import sambal.mydd.app.utils.AppConfig.api_Interface
import com.google.firebase.messaging.FirebaseMessagingService
import android.media.Ringtone
import android.graphics.Bitmap
import android.app.PendingIntent
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONObject
import android.widget.RemoteViews
import android.graphics.BitmapFactory
import sambal.mydd.app.MainActivity
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import sambal.mydd.app.fragment.chat.ChatMain
import sambal.mydd.app.utils.GPSTracker
import sambal.mydd.app.utils.GPSTracker.GPSCoordinates
import android.location.Geocoder
import sambal.mydd.app.activity.ActivityGroceryList
import android.media.RingtoneManager
import sambal.mydd.app.activity.New_AgentDetails
import android.location.LocationManager
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.activity.Webview
import android.os.Environment
import android.media.MediaPlayer
import android.annotation.SuppressLint
import android.app.Notification
import android.os.AsyncTask
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import android.graphics.drawable.Drawable
import sambal.mydd.app.constant.UrlConstant
import android.content.ServiceConnection
import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.request.transition.Transition
import sambal.mydd.app.R
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var id = 0
    var imageUrl: String? = null
    var vibration = longArrayOf(500, 1000)
    var sound // = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            : Uri? = null

    // sound;//= Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.noti_tone);
    var r: Ringtone? = null
    var bit: Bitmap? = null
    var title1: String? = ""
    var body1: String? = ""
    var pendingIntentPromo: PendingIntent? = null


    //MyLog myLog = new MyLog();
    var numMessages = 1
    var city = ""
    val ANDROID_CHANNEL_ID = "sambal.mydd.app.ANDROID"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        sound = Uri.parse("android.resource://sambal.mydd.app/" + R.raw.noti_tone)

        //sound = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));
        //android.os.Debug.waitForDebugger();
        // Check if message contains a data payload.
        //android.os.Debug.isDebuggerConnected();

        ErrorMessage.E("notificationMessage" + remoteMessage.data)
        ErrorMessage.E("notificationMessage" + remoteMessage.data["onlineStoreURL"])
        ErrorMessage.E("notificationMessagedealId" + remoteMessage.data["dealId"])


        if (remoteMessage.data.size > 0) {

            //  Log.e(TAG, "Message data payload2: " + remoteMessage.data.toString())
            try {
                val intent = Intent("update_noti")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            } catch (e: Exception) {
            }
            try {
                val trim = remoteMessage.data.toString().split("=".toRegex())
                    .toTypedArray() // because the json not have  "=" so we have to split it
                val jsonStr = trim[1].substring(0, trim[1].length - 1)
                // Log.e("format", jsonStr)
                val obj = JSONObject(jsonStr)


                if (obj.optString("type").equals("NOTIF_TYPE_AGENT_CHAT", ignoreCase = true)) {
                    // Log.e("format", "1")
                    customChatNotification(
                        obj.optInt("agentId"),
                        obj.optInt("isAdmin"),
                        obj.optString("title"),
                        obj.optString("subscribeKey"),
                        obj.optString("publishKey"),
                        obj.optInt("followingStatus"),
                        obj.optString("message"),
                        obj.optString("time"),
                        obj.optString("agentCompanyName"),
                        obj.optString("body"),
                        obj.optString("dealId"),
                        obj.optString("dealName"),
                    )
                } else if (obj.optString("type")
                        .equals("NOTIF_TYPE_AGENT_DETAILS", ignoreCase = true)
                ) {
                    //  Log.e("format", "2")
                    customAgentDetailsNotification(obj.optString("agentId"),
                        obj.optString("title"),
                        obj.optString("message"))
                } else if (obj.optString("type")
                        .equals("NOTIF_TYPE_DEAL_DETAILS", ignoreCase = true)
                ) {
                    //  Log.e("format", "3")
                    customProductDetailsNotification(obj.optString("agentId"),
                        obj.optString("dealId"),
                        obj.optString("title"),
                        obj.optString("message"))
                } else if (obj.optString("type")
                        .equals("NOTIF_TYPE_AGENT_ONLINE_STORE", ignoreCase = true)
                ) {
                    // Log.e("format", "4")
                    customWebsNotification(obj.optString("title"),
                        obj.optString("message"),
                        obj.optString("onlineStoreURL"))
                } else if (obj.optString("type")
                        .equals("NOTIF_TYPE_DEFAULT_PROMOTED", ignoreCase = true)
                ) {
                    //  Log.e("format", "5")
                    customopensNotification(obj.optString("title"), obj.optString("message"))
                } else if (obj.optString("type")
                        .equals("NOTIF_TYPE_DD_GROCER", ignoreCase = true)
                ) {
                    //  Log.e("format", "6")
                    customDDGrocerNotification(obj.optString("title"), obj.optString("message"))
                } else {
                    try {
                        val title = remoteMessage.data["title"]
                        val body = remoteMessage.data["body"]
                        val image = remoteMessage.data["image_url"]
                        val type = remoteMessage.data["type"]
                        val id = remoteMessage.data["id"]
                        try {
                            if (isForeground(applicationContext)) {
                                //  Log.e("format", "7")
                                showTopBanner(title, body, image, type, id)
                                customNotification(title, body, image, type, id)
                                //backGroundNotification(title, body, image, type, id);
                            } else {
                                //  Log.e("1", "2")
                                backGroundNotification(title, body, image, type, id)
                            }
                        } catch (e: Exception) {
                            //  Log.e("format", "8")
                            showTopBanner(title, body, image, type, id)
                            customNotification(title, body, image, type, id)
                        }

                        //final String tone = remoteMessage.getData().get("sound");
                        //sound = Uri.parse("res/raw/" + tone);
                    } catch (e: Exception) {
                        //  Log.e("Exception1", e.toString())
                    }
                }
            }
            catch (e: Exception) {
                E("First else is printed>>$e")

                try {

                    if (remoteMessage.data["notificationType"].equals("NOTIF_TYPE_AGENT_CHAT",
                            ignoreCase = true)
                    ) {

                        customChatNotification(
                            remoteMessage.data["agentId"]!!.toInt(),
                            remoteMessage.data["isAdmin"]!!.toInt(),
                            "" + remoteMessage.data["title"],
                            "" + remoteMessage.data["subscribeKey"],
                            ""+remoteMessage.data["publishKey"],
                            remoteMessage.data["followingStatus"]!!.toInt(),
                            "" + remoteMessage.data["message"],
                            ""+ remoteMessage.data["time"],
                            "" + remoteMessage.data["agentCompanyName"],
                            "" + remoteMessage.data["body"],
                            remoteMessage.data["dealId"],
                            remoteMessage.data["dealName"])


                    } else if (remoteMessage.data["notificationType"]
                            .equals("NOTIF_TYPE_AGENT_DETAILS", ignoreCase = true)
                    ) {
                        customAgentDetailsNotification("" + remoteMessage.data["agentId"],
                            "" + remoteMessage.data["title"],
                            "" + remoteMessage.data["message"])
                    } else if (remoteMessage.data["notificationType"]
                            .equals("NOTIF_TYPE_DEAL_DETAILS", ignoreCase = true)
                    ) {
                        //  Log.e("format", "3")
                        customProductDetailsNotification("" + remoteMessage.data["agentId"],
                            "" + remoteMessage.data["dealId"],
                            "" + remoteMessage.data["title"],
                            "" + remoteMessage.data["message"])
                    } else if (remoteMessage.data["notificationType"]
                            .equals("NOTIF_TYPE_AGENT_ONLINE_STORE", ignoreCase = true)
                    ) {
                        // Log.e("format", "4")
                        customWebsNotification("" + remoteMessage.data["title"],
                            "" + remoteMessage.data["message"],
                            "" + remoteMessage.data["onlineStoreURL"])
                    } else if (remoteMessage.data["notificationType"]
                            .equals("NOTIF_TYPE_DEFAULT_PROMOTED", ignoreCase = true)
                    ) {
                        //  Log.e("format", "5")
                        customopensNotification("" + remoteMessage.data["title"],
                            "" + remoteMessage.data["message"])
                    } else if (remoteMessage.data["notificationType"]
                            .equals("NOTIF_TYPE_DD_GROCER", ignoreCase = true)
                    ) {
                        //  Log.e("format", "6")
                        customDDGrocerNotification("" + remoteMessage.data["title"],
                            "" + remoteMessage.data["message"])
                    } else {

                        try {
                            val title = remoteMessage.data["title"]
                            val body = remoteMessage.data["body"]
                            val image = remoteMessage.data["image_url"]
                            val type = remoteMessage.data["type"]
                            val id = remoteMessage.data["id"]
                            try {
                                if (isForeground(applicationContext)) {
                                    //  Log.e("format", "7")
                                    showTopBanner(title, body, image, type, id)
                                    customNotification(title, body, image, type, id)
                                    //backGroundNotification(title, body, image, type, id);
                                } else {
                                    //  Log.e("1", "2")
                                    backGroundNotification(title, body, image, type, id)
                                }
                            } catch (e: Exception) {
                                //  Log.e("format", "8")
                                showTopBanner(title, body, image, type, id)
                                customNotification(title, body, image, type, id)
                            }

                            //final String tone = remoteMessage.getData().get("sound");
                            //sound = Uri.parse("res/raw/" + tone);
                        } catch (e: Exception) {

                        }
                    }
                } catch (e: Exception) {
                    E("First else is printed>>$e")
                }

            }
        }

//        else {
//            ErrorMessage.E("notificationMessage11" + remoteMessage.data.toString())
//
//            try {
//                val intent = Intent("update_noti")
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//            } catch (e: Exception) {
//            }
//
//            try {
//
//                if (remoteMessage.data["notificationType"]
//                        .equals("NOTIF_TYPE_AGENT_DETAILS", ignoreCase = true)
//                ) {
//                    customAgentDetailsNotification("" + remoteMessage.data["agentId"],
//                        "" + remoteMessage.data["title"],
//                        "" + remoteMessage.data["message"])
//                } else if (remoteMessage.data["notificationType"]
//                        .equals("NOTIF_TYPE_DEAL_DETAILS", ignoreCase = true)
//                ) {
//                    //  Log.e("format", "3")
//                    customProductDetailsNotification("" + remoteMessage.data["agentId"],
//                        "" + remoteMessage.data["dealId"],
//                        "" + remoteMessage.data["title"],
//                        "" + remoteMessage.data["message"])
//                } else if (remoteMessage.data["notificationType"]
//                        .equals("NOTIF_TYPE_AGENT_ONLINE_STORE", ignoreCase = true)
//                ) {
//                    // Log.e("format", "4")
//                    customWebsNotification("" + remoteMessage.data["title"],
//                        "" + remoteMessage.data["message"],
//                        "" + remoteMessage.data["onlineStoreURL"])
//                } else if (remoteMessage.data["notificationType"]
//                        .equals("NOTIF_TYPE_DEFAULT_PROMOTED", ignoreCase = true)
//                ) {
//                    //  Log.e("format", "5")
//                    customopensNotification("" + remoteMessage.data["title"],
//                        "" + remoteMessage.data["message"])
//                } else if (remoteMessage.data["notificationType"]
//                        .equals("NOTIF_TYPE_DD_GROCER", ignoreCase = true)
//                ) {
//                    //  Log.e("format", "6")
//                    customDDGrocerNotification("" + remoteMessage.data["title"],
//                        "" + remoteMessage.data["message"])
//                } else {
//
//                }
//            }
//            catch (e: Exception) {
//                E("First else is printed>>$e")
//            }
//
//        }

    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
          Log.e("token", "fcm token:$s")
        try {
            getInstance(this)!!.fcmToken = s
            if (getInstance(this)!!.accessToken != null) {
                val deviceId = getInstance(this)!!.deviceId
                updateDeviceToken(deviceId, s)
            }
        } catch (e: Exception) {
            E("onNewToken>>$e")
        }
    }

    fun customNotification(
        title: String?,
        body: String?,
        imageUrl: String?,
        type: String?,
        id: String?,
    ) {

        // Using RemoteViews to bind custom layouts into Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remoteViews = RemoteViews(packageName,
                R.layout.custom_notification_view)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, body)
            remoteViews.setTextViewText(R.id.tv_noti_time,
                DateFormat.getDateInstance().format(Date()))
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                remoteViews.setImageViewBitmap(R.id.iv_image, myBitmap)
            } catch (e: Exception) {
                // Log exception
                //  Log.e("Ex", e.toString())
                remoteViews.setImageViewResource(R.id.iv_image, R.drawable.app_icon_new)
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            //  Log.e("1", "2")
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker("DealDio") // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(ANDROID_CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(ANDROID_CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(0, builder.build())
            //Log.e("1", "4")
        } else {
            val remoteViews = RemoteViews(packageName,
                R.layout.custom_notification_view)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, body)
            remoteViews.setTextViewText(R.id.tv_noti_time,
                DateFormat.getDateInstance().format(Date()))
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                remoteViews.setImageViewBitmap(R.id.iv_image, myBitmap)
            } catch (e: IOException) {
                // Log exception
                //  Log.e("Ex", e.toString())
                remoteViews.setImageViewResource(R.id.iv_image, R.drawable.app_icon_new)
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val CHANNEL_ID = "my_channel_01"
            //  Log.e("1", "2")
            val builder = NotificationCompat.Builder(this) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker("DealDio") // Dismiss Notification
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(0, builder.build())
            //  Log.e("1", "4")
        }
    }

    fun customChatNotification(
        id: Int,
        isAdmin: Int,
        name: String?,
        subskey: String?,
        pubskey: String?,
        followingstatus: Int,
        title: String?,
        time: String?,
        agentCompanyName: String?,
        body:String?,
        dealId:String?,
        dealName:String?,
    ) {


        // Using RemoteViews to bind custom layouts into Notification
        var time = time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            try {
                val inputPattern = "dd-MM-dd hh:mm a"
                val outputPattern = "dd-MM-yyyy hh:mm a"
                val inputFormat = SimpleDateFormat(inputPattern)
                val outputFormat = SimpleDateFormat(outputPattern)
                val date: Date? = null
                val str: String? = null
                try {
                    time = UTCToLocalSec(time)
                    /*    date = inputFormat.parse(time);
                str = outputFormat.format(date);*/remoteViews.setTextViewText(R.id.tv_noti_time,
                        time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ChatMain::class.java)
            // Send data to NotificationView Class
            intent.putExtra("id", id.toString() + "")
            intent.putExtra("isAdmin", isAdmin.toString() + "")
            intent.putExtra("name", agentCompanyName)
            intent.putExtra("subskey", subskey)
            intent.putExtra("pubskey", pubskey)
            intent.putExtra("followingstatus", followingstatus.toString() + "")
            intent.putExtra("type", "direct")
            intent.putExtra("body", body)
            intent.putExtra("dealId", dealId)
            intent.putExtra("dealName", dealName)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setGroup(id.toString() + "")
                .setChannelId(ANDROID_CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(ANDROID_CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(id, builder.build())
        }

        else {
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            remoteViews.setTextViewText(R.id.tv_noti_heading, name)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            try {
                val inputPattern = "dd-MM-dd hh:mm a"
                val outputPattern = "dd-MM-yyyy hh:mm a"
                val inputFormat = SimpleDateFormat(inputPattern)
                val outputFormat = SimpleDateFormat(outputPattern)
                val date: Date? = null
                val str: String? = null
                try {
                    time = UTCToLocalSec(time)
                    /*    date = inputFormat.parse(time);
                str = outputFormat.format(date);*/remoteViews.setTextViewText(R.id.tv_noti_time,
                        time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
            }

            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ChatMain::class.java)
            // Send data to NotificationView Class
            intent.putExtra("id", id.toString() + "")
            intent.putExtra("isAdmin", isAdmin.toString() + "")
            intent.putExtra("name", agentCompanyName)
            intent.putExtra("subskey", subskey)
            intent.putExtra("pubskey", pubskey)
            intent.putExtra("followingstatus", followingstatus.toString() + "")
            intent.putExtra("type", "direct")
            intent.putExtra("body", body)
            intent.putExtra("dealId", dealId)
            intent.putExtra("dealName", dealName)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val builder = NotificationCompat.Builder(this) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setGroup(id.toString() + "")
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(id, builder.build())
        }
    }

    fun customDDGrocerNotification(title: String?, message: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        try {
            requestSingleUpdate(this,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSCoordinates?) {
                        // Log.e("Location on MainAct is ", location.toString())
                        MainActivity.userLat = location!!.latitude.toDouble()
                        MainActivity.userLang = location.longitude.toDouble()
                        /*  userLat = 51.5758719;
                            userLang = -0.421236;*/
                        val geocoder =
                            Geocoder(this@MyFirebaseMessagingService, Locale.getDefault())
                        var addresses: List<Address>? = null
                        try {
                            addresses = geocoder.getFromLocation(location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                1)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        try {
                            if (addresses != null && addresses.size > 0) {
                                val cityName = addresses[0].getAddressLine(0)
                                val fullAddress = addresses[0].getAddressLine(1)
                                city = addresses[0].locality
                                val countryName = addresses[0].getAddressLine(2)
                                // Log.e("City", "$city,$cityName")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, message)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ActivityGroceryList::class.java)
            // Send data to NotificationView Class
            intent.putExtra("type", "direct")
            intent.putExtra("name", city)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(notificationIcon)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("my_channel_01",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, message)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, ActivityGroceryList::class.java)
            // Send data to NotificationView Class
            intent.putExtra("type", "direct")
            intent.putExtra("name", city)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(notificationIcon)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("my_channel_01",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private val notificationIcon: Int
        private get() {
            val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            return if (useWhiteIcon) R.drawable.appicon else R.drawable.appicon
        }

    fun customAgentDetailsNotification(id: String, name: String?, title: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        try {
            requestSingleUpdate(this,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSCoordinates?) {
                        // Log.e("Location on MainAct is ", location.toString())
                        MainActivity.userLat = location!!.latitude.toDouble()
                        MainActivity.userLang = location.longitude.toDouble()
                        /*  userLat = 51.5758719;
                            userLang = -0.421236;*/Log.e("Location",
                            MainActivity.userLat.toString() + "")
                    }
                })
        } catch (e: Exception) {
        }
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val remoteViews = RemoteViews(packageName,
            R.layout.chatnotification)
        remoteViews.setTextViewText(R.id.tv_noti_heading, name)
        remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
        remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Open NotificationView Class on Notification Click
            val intent = Intent(this, New_AgentDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("position", 0)
            intent.putExtra("direct", "true")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else {
            val intent = Intent(this, New_AgentDetails::class.java)
            // Send data to NotificationView Class
            intent.putExtra("agentId", id + "")
            intent.putExtra("position", 0)
            intent.putExtra("direct", "true")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val builder = NotificationCompat.Builder(this) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)

            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    fun customProductDetailsNotification(id: String, pId: String?, name: String?, title: String?) {
        // Using RemoteViews to bind custom layouts into Notification


        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }


        if (!gps_enabled && !network_enabled) {
            // notify user
        } else {

            try {
                requestSingleUpdate(this,
                    object : GPSTracker.LocationCallback {
                        override fun onNewLocationAvailable(location: GPSCoordinates?) {
                            //  Log.e("Location on MainAct is ", location.toString())
                            MainActivity.userLat = location!!.latitude.toDouble()
                            MainActivity.userLang = location.longitude.toDouble()
                            /*  userLat = 51.5758719;
                            userLang = -0.421236;*/Log.e("Location",
                                MainActivity.userLat.toString() + "")
                        }
                    })
            } catch (e: Exception) {
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                val currentDateandTime = sdf.format(Date())
                val remoteViews = RemoteViews(packageName,
                    R.layout.chatnotification)
                remoteViews.setTextViewText(R.id.tv_noti_heading, name)
                remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
                remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


                // Open NotificationView Class on Notification Click
                val intent = Intent(this, LatestProductDetails::class.java)
                // Send data to NotificationView Class
                intent.putExtra("agentId", id + "")
                intent.putExtra("product_id", pId)
                intent.putExtra("type", "direct")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // Open NotificationView.java Activity
                val pIntent =
                    PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val CHANNEL_ID = "my_channel_01"
                val builder = Notification.Builder(this, ANDROID_CHANNEL_ID) // Set Icon
                    .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                    .setTicker(getString(R.string.app_name)) // Dismiss Notification
                    .setAutoCancel(true)
                   // Set PendingIntent into Notification
                    .setContentIntent(pIntent)
                    .setSound(sound)
                    .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration) // Set RemoteViews into Notification
                    .setCustomBigContentView(remoteViews)


                // Create Notification Manager
                val notificationmanager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(CHANNEL_ID,
                        "Channel human readable title01a",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationmanager.createNotificationChannel(channel)
                }
                notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
            } else {


                val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                val currentDateandTime = sdf.format(Date())
                val remoteViews = RemoteViews(packageName,
                    R.layout.chatnotification)
                remoteViews.setTextViewText(R.id.tv_noti_heading, name)
                remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
                remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


                // Open NotificationView Class on Notification Click
                val intent = Intent(this, LatestProductDetails::class.java)
                // Send data to NotificationView Class
                intent.putExtra("agentId", id + "")
                intent.putExtra("product_id", pId)
                intent.putExtra("type", "direct")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // Open NotificationView.java Activity
                val pIntent =
                    PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                val CHANNEL_ID = "my_channel_01"
                val builder = NotificationCompat.Builder(this) // Set Icon
                    .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                    .setTicker(getString(R.string.app_name)) // Dismiss Notification
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
                    .setContentIntent(pIntent)
                    .setSound(sound)
                    .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration) // Set RemoteViews into Notification
                    .setCustomBigContentView(remoteViews)


                // Create Notification Manager
                val notificationmanager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(CHANNEL_ID,
                        "Channel human readable title01a",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationmanager.createNotificationChannel(channel)
                }
                notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    fun customopensNotification(title: String?, msg: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, msg)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            // Open NotificationView Class on Notification Click
            val intent = Intent(this, SplashActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("type", "")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)


            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val remoteViews = RemoteViews(packageName,
                R.layout.chatnotification)
            remoteViews.setTextViewText(R.id.tv_noti_heading, title)
            remoteViews.setTextViewText(R.id.tv_noti_sub_heading, msg)
            remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)


            // Open NotificationView Class on Notification Click
            val intent = Intent(this, SplashActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("type", "")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Open NotificationView.java Activity
            val pIntent =
                PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val CHANNEL_ID = "my_channel_01"
            val builder = NotificationCompat.Builder(this) // Set Icon
                .setSmallIcon(R.drawable.appicon) // Set Ticker Message
                .setTicker(getString(R.string.app_name)) // Dismiss Notification
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(sound)
                .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setVibrate(vibration) // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews)


            // Create Notification Manager
            val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title01a",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationmanager.createNotificationChannel(channel)
            }
            notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    fun customWebsNotification(name: String?, title: String?, url: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val remoteViews = RemoteViews(packageName,
            R.layout.chatnotification)
        remoteViews.setTextViewText(R.id.tv_noti_heading, name)
        remoteViews.setTextViewText(R.id.tv_noti_sub_heading, title)
        remoteViews.setTextViewText(R.id.tv_noti_time, currentDateandTime)

        // Open NotificationView Class on Notification Click
        val intent = Intent(this, Webview::class.java)
        // Send data to NotificationView Class
        intent.putExtra("url", url)
        intent.putExtra("title", name)
        intent.putExtra("type", "direct")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Open NotificationView.java Activity
        val pIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val CHANNEL_ID = "my_channel_01"
        val builder = NotificationCompat.Builder(this) // Set Icon
            .setSmallIcon(R.drawable.appicon) // Set Ticker Message
            .setTicker(getString(R.string.app_name)) // Dismiss Notification
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set PendingIntent into Notification
            .setContentIntent(pIntent)
            .setSound(sound)
            .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
            .setVibrate(vibration) // Set RemoteViews into Notification
            .setCustomBigContentView(remoteViews)


        // Create Notification Manager
        val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title01a",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationmanager.createNotificationChannel(channel)
        }
        notificationmanager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun showTopBanner(
        title: String?,
        body: String?,
        imageUrl: String?,
        type: String?,
        id: String?,
    ) {

        //Sound
        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Vibration
        val CHANNEL_ID = "my_channel_02"
        val vibration = longArrayOf(500, 1000)
        //new long[] { 1000, 1000, 1000, 1000, 1000 }
        //Build notification
        val noBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.appicon)
            .setTicker("DealDio")
            .setContentTitle(title)
            .setContentText(body)
            .setChannelId(CHANNEL_ID) //.setSound(sound)
            .setVibrate(vibration) //.setDefaults(Notification.DEFAULT_ALL)
            //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        Log.e("message background2", body!!)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.e("message background1", body)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                "Channel human readable title1",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, noBuilder.build())
    }

    fun backGroundNotification(
        title: String?,
        body: String?,
        imgUrl: String?,
        type: String?,
        id: String?,
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("BAck", "1")
            imageUrl = imgUrl
            title1 = title
            body1 = body
            val CHANNEL_ID = "my_channel_03"
            val intent = Intent(this, MainActivity::class.java)


            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LoadBitmap().execute("")

                //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);

                /*try {
                final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title1)
                        .setContentText(body1)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setLargeIcon(icon)
                        .setVibrate(vibration)
                        .setContentIntent(pendingIntentPromo)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bit)
                                .bigLargeIcon(null)
                                .setSummaryText(body1));

                notificationBuilder.setLights(Color.YELLOW, 1000, 300);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

            } catch  (Exception e) {
                e.printStackTrace();
            }*/
            }
            else if (bit == null) {
                val noBuilder = Notification.Builder(this)
                    .setSmallIcon(R.drawable.appicon)
                    .setTicker("DealDio")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntentPromo)
                    .setNumber(numMessages)
                    .setSound(sound)
                    .setChannelId(ANDROID_CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                //notificationManager.notify(((int) (Math.random() * 9000) + 1000), noBuilder.build());
                notificationManager.notify(0, noBuilder.build())
            }
            else {
                val noBuilder = Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_new)
                    .setTicker("DealDio")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntentPromo)
                    .setNumber(numMessages)
                    .setSound(sound)
                    .setChannelId(ANDROID_CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, noBuilder.build())
            }
        } else {
            Log.e("BAck", "1")
            imageUrl = imgUrl
            title1 = title
            body1 = body
            val CHANNEL_ID = "my_channel_03"
            val intent = Intent(this, MainActivity::class.java)
            // Send data to NotificationView Class
            intent.putExtra("title", title)
            intent.putExtra("text", body)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("deals", "3")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntentPromo =
                PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LoadBitmap().execute("")

                //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);

                /*try {
                final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title1)
                        .setContentText(body1)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setLargeIcon(icon)
                        .setVibrate(vibration)
                        .setContentIntent(pendingIntentPromo)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bit)
                                .bigLargeIcon(null)
                                .setSummaryText(body1));

                notificationBuilder.setLights(Color.YELLOW, 1000, 300);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

            } catch  (Exception e) {
                e.printStackTrace();
            }*/
            } else if (bit == null) {
                val noBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.appicon)
                    .setTicker("DealDio")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntentPromo)
                    .setNumber(numMessages)
                    .setSound(sound)
                    .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                //notificationManager.notify(((int) (Math.random() * 9000) + 1000), noBuilder.build());
                notificationManager.notify(0, noBuilder.build())
            } else {
                val noBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_new)
                    .setTicker("DealDio")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntentPromo)
                    .setNumber(numMessages)
                    .setSound(sound)
                    .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setVibrate(vibration)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, noBuilder.build())
            }
        }
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            Log.e("image", imageUrl!!)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }

    private fun playTone() {
        val downloadsDirectoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val audioFilePath = downloadsDirectoryPath + "noti_tone.mp3"
        val mPlayer = MediaPlayer()
        try {
            sound = Uri.parse(audioFilePath)
            /*mPlayer.setDataSource(audioFilePath);
            mPlayer.prepare();
            mPlayer.start();*/
        } catch (e: Exception) {
            Log.e("AUDIO PLAYBACK", "prepare() failed")
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadBitmap : AsyncTask<String?, Void?, String?>() {
        protected override fun doInBackground(vararg p0: String?): String? {
            try {
                Glide.with(applicationContext)
                    .asBitmap().load(imageUrl).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : SimpleTarget<Bitmap?>(100, 100) {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?,
                        ) {
                            bit = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                        }
                    })
                /*bit = Glide.
                        with(MyFirebaseMessagingService.this).
                        asBitmap().
                        load(imageUrl).
                        into(100, 100). // Width and height
                        get();*/
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("noti exp", e.toString() + "")
            }
            return null
        }

        override fun onPostExecute(s1: String?) {
            if (bit != null) {
                val CHANNEL_ID = "my_channel_04"
                val notificationBuilder =
                    NotificationCompat.Builder(this@MyFirebaseMessagingService)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(title1)
                        .setContentText(body1)
                        .setAutoCancel(true)
                        .setTicker("DealDio")
                        .setSound(sound)
                        .setChannelId(CHANNEL_ID) //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setVibrate(vibration)
                        .setContentIntent(pendingIntentPromo)
                        .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(bit)
                            .setSummaryText(body1))

                //notificationBuilder.setLights(Color.YELLOW, 1000, 300);
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(CHANNEL_ID,
                        "Channel human readable title4",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(0, notificationBuilder.build())
            }
        }
    }

    private fun updateDeviceToken(deviceId: String?, deviceToken: String) {
        var lat: String? = "0.0"
        var lng: String? = "0.0"
        try {
            if (isNetworkAvailable(this)) {
                if (getInstance(this)!!.lat != null) {
                    lat = getInstance(this)!!.lat
                    lng = getInstance(this)!!.lng
                }
                val call = api_Interface().updateDeviceToken(deviceId,
                    "A",
                    deviceToken,
                    UrlConstant.DEVICE_DEBUG_MODE.toString() + "",
                    "1",
                    lat,
                    lng, deviceToken)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        E("updateDeviceToken:" + response.message())
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        E("updateDeviceToken:" + t.message)
                    }
                })
            } else {
            }
        } catch (e: Exception) {
        }
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }


}