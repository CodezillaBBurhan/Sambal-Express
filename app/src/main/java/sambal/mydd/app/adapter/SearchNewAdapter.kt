package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import sambal.mydd.app.callback.RecyclerClickListener
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.viewHolder.SearchNewViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import sambal.mydd.app.R
import org.json.JSONObject

class SearchNewAdapter(
    private val context: Context,
    private val searchKeyword: String,
    private val jsonArray: JSONArray,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<SearchNewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNewViewHolder {
        return SearchNewViewHolder(LayoutInflater.from(parent.context), parent, context)
    }

    override fun onBindViewHolder(holder: SearchNewViewHolder, position: Int) {
        val jsonObject = getItem(position)
        val categoryName = jsonObject.optString("categoryName")
        holder.tvCategoryName.text = categoryName
        holder.llCategoryLayout.setOnClickListener {
            if (holder.recyclerView.visibility == View.VISIBLE) {
                holder.recyclerView.visibility = View.GONE
                holder.ivDropdownIcon.setImageResource(R.drawable.ic_expand_more_black_24dp)
            } else {
                holder.recyclerView.visibility = View.VISIBLE
                holder.ivDropdownIcon.setImageResource(R.drawable.ic_expand_less_black_24dp)
            }
        }
        val categoryData = jsonObject.optJSONArray("categoryData")
        if (categoryName.equals("Deals", ignoreCase = true)) {
            val dealsAdapter = SearchSubDealsAdapter(context,
                searchKeyword,
                categoryData) { jsonObject, eventHasMultipleParts -> }
            holder.recyclerView.adapter = dealsAdapter
        } else if (categoryName.equals("Partners", ignoreCase = true)) {
            val agentsAdapter = SearchSubAgentsAdapter(context,
                searchKeyword,
                categoryData) { jsonObject, eventHasMultipleParts -> }
            holder.recyclerView.adapter = agentsAdapter
        } else if (categoryName.equals("Category", ignoreCase = true)) {
            val categoryAdapter = SearchSubCategoryAdapter(context,
                searchKeyword,
                categoryData) { jsonObject, eventHasMultipleParts -> }
            holder.recyclerView.adapter = categoryAdapter
        }
        holder.rootLayout.setOnClickListener {
            //recyclerClickListener.setCellClicked(jsonObject, "");
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun getItem(position: Int): JSONObject {
        return jsonArray.optJSONObject(position)
    }
}