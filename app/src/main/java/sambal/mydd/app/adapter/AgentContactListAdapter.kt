package sambal.mydd.app.adapter

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.viewHolder.AgentContactListViewHolder
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.*
import sambal.mydd.app.activity.New_AgentDetails
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import sambal.mydd.app.R
import java.lang.Exception
import java.util.ArrayList

class AgentContactListAdapter(context: Context, activity: Activity, list: List<String>) :
    RecyclerView.Adapter<AgentContactListViewHolder>() {
    private var list: List<String> = ArrayList()
    private val context: Context
    private val activity: Activity

    init {
        this.list = list
        this.activity = activity
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentContactListViewHolder {
        return AgentContactListViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: AgentContactListViewHolder, position: Int) {
        if (list.size - 1 == position) {
            holder.view.visibility = View.GONE
        }
        val contactNo = list[holder.adapterPosition]
        holder.tvContact.text = contactNo
        holder.tvCallNow.setOnClickListener { callFun(contactNo) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun callFun(contactNo: String) {
        val intent1 = Intent(Intent.ACTION_CALL)
        intent1.data = Uri.parse("tel:$contactNo")

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE),
                New_AgentDetails.MY_PERMISSIONS_REQUEST_CALL_PHONE)

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {
                context.startActivity(intent1)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}