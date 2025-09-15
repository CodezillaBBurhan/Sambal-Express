package sambal.mydd.app.adapter

import android.content.Context
import org.json.JSONArray
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.R
import java.lang.Exception

class Groceryimage_Adapter(private val context: Context, private val IMAGES: JSONArray) :
    PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return IMAGES.length()
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.groceryimageadapter, view, false)!!
        val imageView = imageLayout
            .findViewById<ImageView>(R.id.iv)
        val obj = IMAGES.optJSONObject(position)
        Log.e("Viewwww", obj.optString("bannerImage"))
        if (obj.optString("bannerImage") != null || !TextUtils.isEmpty(obj.optString("bannerImage"))) {
            try {
                Picasso.with(context)
                    .load(obj.optString("bannerImage"))
                    .placeholder(R.drawable.ic_placeholder_21x9)
                    .error(R.drawable.ic_placeholder_21x9)
                    .into(imageView)
            } catch (e: Exception) {
                Log.e("exx", e.toString())
            }
        }
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