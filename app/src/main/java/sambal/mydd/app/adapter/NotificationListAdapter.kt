package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.callback.RecyclerClickListener
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.viewHolder.NotificationListViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.text.TextUtils
import android.content.Intent
import android.util.Log
import android.view.View
import sambal.mydd.app.activity.LatestProductDetails
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.activity.Webview
import sambal.mydd.app.activity.ActivityGroceryList
import sambal.mydd.app.models.Notification
import java.util.ArrayList

class NotificationListAdapter(
    var notifications: ArrayList<Notification>,
    private val context: Context,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<NotificationListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(LayoutInflater.from(parent.context), parent, context)
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val notification = notifications[position]
        //final JSONObject jsonObject = getItem(position);
        val notiType = notification.notificationType!!
        val notiStatus = notification.notificationStatus!!
        if (TextUtils.isEmpty(notification.notificationLocation)) {
            holder.ivLocation.visibility = View.GONE
            holder.tvLocationName.visibility = View.GONE
        } else {
            holder.ivLocation.visibility = View.VISIBLE
            holder.tvLocationName.visibility = View.VISIBLE
            holder.tvLocationName.text = notification.notificationLocation
        }
        val notiTime = notification.notificationCreatedDate
        holder.tvNotiTime.text = notiTime
        holder.tvAgentName.text = notification.notificationAgentName
        holder.title.text = notification.notificationSubject
        holder.subHeading.text = notification.notificationDescription
        val notificationId = notification.notificationId!!
        val notificationMemberId = notification.notificationMemberId!!
        val agentId = notification.notificationAgentId!!
        holder.ll.setOnClickListener {
            //    recyclerClickListener.setCellClicked(jsonObject, "");
            Log.e("Notiri", notiType.toString() + "")

            /* if (notiType == 1) {
                        //open product details
                        Log.e("notificationMemberId", notificationMemberId + "");
                        Log.e("agentId", agentId + "");
                        Intent intent = new Intent(context, LatestProductDetails.class);
                        intent.putExtra("direct", "false");
                        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, notificationMemberId + "");
                        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "");
                        context.startActivity(intent);
    
                    }
    
                    else*/if (notiType == 2) {
            //open product details
            Log.e("notificationMemberId", notificationMemberId.toString() + "")
            Log.e("agentId", agentId.toString() + "")
            val intent = Intent(context, LatestProductDetails::class.java)
            intent.putExtra("direct", "false")
            intent.putExtra(
                IntentConstant.INTENT_KEY_PRODUCT_ID,
                notificationMemberId.toString() + ""
            )
            intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId.toString() + "")
            context.startActivity(intent)
        } else if (notiType == 3) {
            //open agent details
            Log.e("notificationMemberId", notificationMemberId.toString() + "")
            Log.e("agentId", agentId.toString() + "")
            val intent = Intent(context, New_AgentDetails::class.java)
            intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, "")
            intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId.toString() + "")
            intent.putExtra("direct", "false")
            context.startActivity(intent)
        } else if (notiType == 6) {
            //open agent details
            val intent = Intent(context, Webview::class.java)
            intent.putExtra("url", notification.webPageURL)
            intent.putExtra("title", notification.notificationAgentName + "")
            intent.putExtra("type", "non_direct")
            context.startActivity(intent)
        } else if (notiType == 5) {
            //open agent details
            val intent = Intent(context, ActivityGroceryList::class.java)
            intent.putExtra("type", "non_direct")
            context.startActivity(intent)
        }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}