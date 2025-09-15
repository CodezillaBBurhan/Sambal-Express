package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.LocationModel
import sambal.mydd.app.callback.LocationDBListClickListener
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.viewHolder.SearchLocationDBViewHolder
import sambal.mydd.app.database.DatabaseHandler
import android.view.ViewGroup
import android.view.LayoutInflater

class SearchLocationDBAdapter(
    private val context: Context,
    private val list: List<LocationModel>,
    private val dbListClickListener: LocationDBListClickListener
) : RecyclerView.Adapter<SearchLocationDBViewHolder>() {
    private val db: DatabaseHandler

    init {
        db = DatabaseHandler(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationDBViewHolder {
        return SearchLocationDBViewHolder(LayoutInflater.from(parent.context), parent, context)
    }

    override fun onBindViewHolder(holder: SearchLocationDBViewHolder, position: Int) {
        val model = list[position]
        holder.tvLocationName.text = model.cityName
        val distance = model.distance.toInt() + 1
        holder.tvDistance.text = "(+$distance miles)"
        holder.rootLayout.setOnClickListener { dbListClickListener.setClicked(model) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}