package sambal.mydd.app.adapter

import android.content.Context
import android.util.Log
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapadminreplycommentBinding
import java.lang.Exception

class AdapterAllReplyComments(private val context: Context, private val arr: JSONArray) :
    RecyclerView.Adapter<AdapterAllReplyComments.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AdapadminreplycommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = arr.optJSONObject(position)
        Log.e("Commects", obj.toString() + "")
        try {
            Picasso.with(context).load(obj.optString("agentImage"))
                .placeholder(context.resources.getDrawable(R.drawable.place_holder))
                .error(context.resources.getDrawable(R.drawable.place_holder))
                .into(holder.binding.ivCommentPhoto)
        } catch (e: Exception) {
            holder.binding.ivCommentPhoto.setImageResource(R.drawable.sponplaceholder)
        }
        try {
            // holder.tvRating.setText(obj.optString("userRating"));
            holder.binding.tvName.text = obj.optString("agentCompanyName")
            holder.binding.tvComment.text = obj.optString("userCommentsReply")
            holder.binding.tvTime.text = obj.optString("userCommentsReplyDate")
        } catch (e: Exception) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arr.length()
    }

    inner class MyViewHolder(var binding: AdapadminreplycommentBinding) : RecyclerView.ViewHolder(
        binding.root)
}