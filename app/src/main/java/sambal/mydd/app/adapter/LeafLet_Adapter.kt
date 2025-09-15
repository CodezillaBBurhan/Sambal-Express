package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.squareup.picasso.Picasso
import android.content.Intent
import sambal.mydd.app.activity.ExclusiveDeals
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.R
import java.lang.Exception

class LeafLet_Adapter(private val context: Context, private val array: JSONArray) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return array.length()
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.fullscreendialog, view, false)!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.iv)
        val ll = imageLayout
            .findViewById<RelativeLayout>(R.id.rl1)
        val obj = array.optJSONObject(position)
        try {
            imageView.setOnTouchListener(ImageMatrixTouchHandler(imageLayout.context))
            Picasso.with(context)
                .load(obj.optString("leafLetImage"))
                .into(imageView)
        } catch (e: Exception) {
            Log.e("exx", e.toString())
        }
        ll.setOnClickListener { context.startActivity(Intent(context, ExclusiveDeals::class.java)) }
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }
}