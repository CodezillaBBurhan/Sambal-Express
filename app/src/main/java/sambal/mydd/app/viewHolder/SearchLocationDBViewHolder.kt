package sambal.mydd.app.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.RelativeLayout
import android.widget.TextView
import sambal.mydd.app.R

class SearchLocationDBViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val context: Context
) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.search_location_db_item_view, parent, false)) {
    var rootLayout: RelativeLayout
    var view: View
    var tvLocationName: TextView
    var tvDistance: TextView

    init {
        rootLayout = itemView.findViewById(R.id.root_layout)
        view = itemView.findViewById(R.id.view)
        tvLocationName = itemView.findViewById(R.id.tv_location_name)
        tvDistance = itemView.findViewById(R.id.tv_distance_in_miles)
    }
}