package sambal.mydd.app.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import sambal.mydd.app.R

class AgentContactListViewHolder(inflater: LayoutInflater, parent: ViewGroup?) :
    RecyclerView.ViewHolder(inflater.inflate(
        R.layout.agent_contact_list_item_view, parent, false)) {
    var tvContact: TextView
    var tvCallNow: TextView
    var view: View

    init {
        tvContact = itemView.findViewById(R.id.tv_contact)
        tvCallNow = itemView.findViewById(R.id.tv_call_now)
        view = itemView.findViewById(R.id.view)
    }
}