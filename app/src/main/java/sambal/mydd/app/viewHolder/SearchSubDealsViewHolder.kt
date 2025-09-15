package sambal.mydd.app.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.LinearLayout
import sambal.mydd.app.R

class SearchSubDealsViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val context: Context
) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.search_sub_deals_item_view, parent, false)) {
    @JvmField
    var tvCategoryName: TextView
    @JvmField
    var rootLayout: LinearLayout

    init {
        rootLayout = itemView.findViewById(R.id.root_layout)
        tvCategoryName = itemView.findViewById(R.id.tv_category_name)
    }
}