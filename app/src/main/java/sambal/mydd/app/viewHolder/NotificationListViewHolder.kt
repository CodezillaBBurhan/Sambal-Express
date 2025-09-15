package sambal.mydd.app.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.LinearLayout
import sambal.mydd.app.R

class NotificationListViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val context: Context
) : RecyclerView.ViewHolder(
    inflater.inflate(
        R.layout.adapnotification, parent, false
    )
) {
    var imageView: ImageView? = null
    var ivLocation: ImageView
    var title: TextView
    var subHeading: TextView
    var tvNotiTime: TextView
    var tvLocationName: TextView
    var tvLocation: TextView
    var tvAgentName: TextView
    var ll: LinearLayout
    var llLocation: LinearLayout? = null

    init {
        //super(inflater.inflate(R.layout.notification_list_item_view, parent, false));

        //imageView = (ImageView) itemView.findViewById(R.id.image_view);
        title = itemView.findViewById(R.id.tv_title)
        tvNotiTime = itemView.findViewById(R.id.tv_noti_time)
        subHeading = itemView.findViewById(R.id.tv_sub_heading)
        tvLocationName = itemView.findViewById(R.id.tvLocationName)
        ivLocation = itemView.findViewById(R.id.ivLocation)
        tvLocation = itemView.findViewById(R.id.tvLocationDistance)
        tvAgentName = itemView.findViewById(R.id.tvAgentName)
        ll = itemView.findViewById(R.id.ll)
    }
}