package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import sambal.mydd.app.callback.CountryCallback
import android.widget.BaseAdapter
import sambal.mydd.app.adapter.CountryDialogAdapter.CountryFilter
import org.json.JSONObject
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Filter
import android.widget.TextView
import android.widget.Filter.FilterResults
import android.widget.Filterable
import sambal.mydd.app.R
import java.lang.Exception
import java.util.*

class CountryDialogAdapter(
//context
    private val context: Context, private var items: JSONArray, callback: CountryCallback
) : BaseAdapter(), Filterable {
    private val allItems: JSONArray
    private var countryFilter: CountryFilter? = null
    private val countryCallback: CountryCallback

    //public constructor
    init {
        allItems = items
        countryCallback = callback
    }

    override fun getCount(): Int {
        return items.length() //returns total of items in the list
    }

    override fun getItem(position: Int): JSONObject {
        return items.optJSONObject(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.dialog_country_item, parent, false)
        //var convertView = convertView
        //convertView = LayoutInflater.from(context).inflate(R.layout.dialog_country_item, parent, false)
        val country = getItem(position)
        val countryName = convertView.findViewById<TextView>(R.id.country_name)
        countryName.text = country.optString("country_name")
        countryName.setOnClickListener {
            try {
                countryCallback.setItemList(position, items)
            } catch (e: Exception) {
            }
        }
        return convertView
    }

    override fun getFilter(): Filter {
        if (countryFilter == null) countryFilter = CountryFilter()
        return countryFilter!!
    }

    private inner class CountryFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            // We implement here the filter logic
            if (constraint == null || constraint.length == 0) {
                // No filter implemented we return all the list
                results.values = allItems
                results.count = allItems.length()
            } else {
                // We perform filtering operation
                val filterArray = JSONArray()
                val length = allItems.length()
                for (i in 0 until length) {
                    val `object` = allItems.optJSONObject(i)
                    if (`object`.optString("country_name").uppercase(Locale.getDefault())
                            .startsWith(constraint.toString().uppercase(
                                Locale.getDefault()))
                    ) {
                        filterArray.put(`object`)
                    }
                }
                results.values = filterArray
                results.count = filterArray.length()
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                items = JSONArray()
                notifyDataSetInvalidated()
            } else {
                items = results.values as JSONArray
                notifyDataSetChanged()
            }
        }
    }
}