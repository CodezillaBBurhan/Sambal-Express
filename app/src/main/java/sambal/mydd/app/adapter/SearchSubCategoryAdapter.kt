package sambal.mydd.app.adapter

import org.json.JSONArray
import sambal.mydd.app.callback.RecyclerClickListener
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.viewHolder.SearchSubDealsViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import org.json.JSONObject
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import sambal.mydd.app.R
import java.util.*

class SearchSubCategoryAdapter(
    private val context: Context,
    private val searchKeyword: String,
    private val jsonArray: JSONArray,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<SearchSubDealsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSubDealsViewHolder {
        return SearchSubDealsViewHolder(LayoutInflater.from(parent.context), parent, context)
    }

    override fun onBindViewHolder(holder: SearchSubDealsViewHolder, position: Int) {
        val jsonObject = getItem(position)
        val type = jsonObject.optInt("type")
        val categoryId = jsonObject.optInt("categoryId").toString() + ""
        val name = jsonObject.optString("name")
        if (name.lowercase(Locale.getDefault())
                .indexOf(searchKeyword.lowercase(Locale.getDefault())) != -1
        ) {
            val i = name.lowercase(Locale.getDefault())
                .indexOf(searchKeyword.lowercase(Locale.getDefault()))
            val j = searchKeyword.length
            val endIndex = i + j
            val str = SpannableStringBuilder(name)
            str.setSpan(StyleSpan(Typeface.BOLD),
                i,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            str.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.black_normal_color)),
                i,
                endIndex,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            holder.tvCategoryName.text = str
        } else {
            holder.tvCategoryName.text = name
        }
        holder.rootLayout.setOnClickListener { //MainActivity.isCatSetFromSearch = 1;
            val resultIntent = Intent()
            resultIntent.putExtra("id", categoryId)
            (context as Activity).setResult(Activity.RESULT_OK, resultIntent)
            context.finish()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun getItem(position: Int): JSONObject {
        return jsonArray.optJSONObject(position)
    }
}