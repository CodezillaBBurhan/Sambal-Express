package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.Signupbanner
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.R
import java.lang.Exception
import java.util.ArrayList

class SignUpbanner_Adapter(
    private val context: Context,
    private val mList: ArrayList<Signupbanner>
) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.homebannerimage, view, false)!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.iv)
        val ll = imageLayout
            .findViewById<RelativeLayout>(R.id.rl1)
        val hbl = mList[position]
        if (hbl.bannerImage != null || !TextUtils.isEmpty(hbl.bannerImage)) {
            try {
                Picasso.with(context)
                    .load(hbl.bannerImage)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder)
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