package sambal.mydd.app.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.internal.ViewUtils.dpToPx
import sambal.mydd.app.R
import sambal.mydd.app.activity.ExclusiveDeals
import sambal.mydd.app.activity.reward_club.NoticeBoardListActivity
import sambal.mydd.app.beans.HomeBannerLIst


class HomeBanner_Adapter(
    private val context: Context,
    private val mList: ArrayList<HomeBannerLIst>
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
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        imageLayout.getLayoutParams().width = screenWidth / 3
        val imageView = imageLayout.findViewById<ImageView>(R.id.iv)
        val ll = imageLayout
            .findViewById<CardView>(R.id.rl1)
        val hbl = mList[position]
        if (hbl.adsImage != null || !TextUtils.isEmpty(hbl.adsImage)) {
            try {

                /*Transformation transformation = new RoundedTransformationBuilder()
                        .oval(false)
                        .build();
*/
                Glide.with(context)
                    .load(hbl.adsImage) .transform(RoundedCorners(dpToPx(10))) /*   .transform(transformation)*/ /*   .placeholder(R.drawable.placeholder11_4)

                           .error(R.drawable.placeholder11_4)*/
                    .into(imageView)
            } catch (e: Exception) {
                Log.e("exx", e.toString())
            }
        }
        ll.setOnClickListener { context.startActivity(Intent(context, NoticeBoardListActivity::class.java)) }
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
    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}