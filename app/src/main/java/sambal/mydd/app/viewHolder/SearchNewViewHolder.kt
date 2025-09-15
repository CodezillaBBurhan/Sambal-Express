package sambal.mydd.app.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.R

class SearchNewViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val context: Context
) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.search_new_item_view, parent, false)) {
    var tvCategoryName: TextView
    var recyclerView: RecyclerView
    var rootLayout: LinearLayout
    var llCategoryLayout: LinearLayout
    var ivDropdownIcon: ImageView

    init {
        recyclerView = itemView.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rootLayout = itemView.findViewById(R.id.root_layout)
        tvCategoryName = itemView.findViewById(R.id.tv_category_name)
        ivDropdownIcon = itemView.findViewById(R.id.iv_drop_down_icon)
        llCategoryLayout = itemView.findViewById(R.id.ll_category_layout)
    }
}