package sambal.mydd.app.service

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import sambal.mydd.app.R

class CustomNotificationStyle (private val context: Context) : NotificationCompat.Style() {

     fun makeContentView(notification: Notification.Builder): android.widget.RemoteViews? {
        return android.widget.RemoteViews(context.packageName, R.layout.custom_notification_layout)
    }
}