package sambal.mydd.app.adapter

import android.content.Context
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
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import sambal.mydd.app.R
import sambal.mydd.app.activity.WebViewActivity
import sambal.mydd.app.activity.New_AgentDetails
import sambal.mydd.app.constant.IntentConstant
import java.util.*

class SearchSubAgentsAdapter(
    private val context: Context,
    private val searchKeyword: String,
    private val jsonArray: JSONArray,
    private val recyclerClickListener: RecyclerClickListener
) : RecyclerView.Adapter<SearchSubDealsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSubDealsViewHolder {
        return SearchSubDealsViewHolder(LayoutInflater.from(parent.context), parent, context)
    }

    override fun onBindViewHolder(holder: SearchSubDealsViewHolder, position: Int) {
        Log.e("SEeeee", "11")
        val jsonObject = getItem(position)
        val type = jsonObject.optInt("type")
        val name = jsonObject.optString("name")
        val agentId = jsonObject.optInt("agentId").toString() + ""
        val isCorporate = jsonObject.optInt("isCorporate")
        val agentExternalURL = jsonObject.optString("agentExternalURL")
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
        holder.rootLayout.setOnClickListener {
            if (isCorporate == 1 && agentExternalURL.length != 0) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("title", name)
                intent.putExtra("url", agentExternalURL)
                context.startActivity(intent)
            } else if (isCorporate == 0) {
                val intent = Intent(context, New_AgentDetails::class.java)
                intent.putExtra("direct", "false")
                intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun getItem(position: Int): JSONObject {
        return jsonArray.optJSONObject(position)
    }
}