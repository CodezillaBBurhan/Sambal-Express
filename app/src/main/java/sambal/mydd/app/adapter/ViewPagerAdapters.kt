package sambal.mydd.app.adapter

import android.content.Context
import sambal.mydd.app.beans.VideoDataModel
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import android.content.Intent
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.activity.PlayYouTubeVideoActivity
import sambal.mydd.app.activity.PlayVideoActivity
import androidx.viewpager.widget.ViewPager
import sambal.mydd.app.R
import java.lang.Exception
import java.util.ArrayList

class ViewPagerAdapters<I>(
    private val context: Context,
    var imagesList: ArrayList<VideoDataModel>,
    check: Boolean
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    var check = true

    //  private Integer [] images = {R.drawable.splashh,R.drawable.splashh,R.drawable.splashh,R.drawable.splashh};
    init {
        this.check = check
    }

    override fun getCount(): Int {
        return if (check) {
            imagesList.size
        } else {
            if (imagesList.size > 1) {
                1
            } else {
                imagesList.size
            }
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.adapter_image_viewpager, container, false)
        val model = imagesList[position]
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val ivPlayIcon = view.findViewById<ImageView>(R.id.play_icon)
        val bannerVideoType = model.bannerVideoType
        val bannerVideoUrl = model.bannerVideoUrl
        val youtubeVideoId = model.youtubeVideoId
        try {
            Picasso.with(context).load(model.bannerImageUrl) //.networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(context.resources.getDrawable(R.drawable.place_holder))
                .error(context.resources.getDrawable(R.drawable.place_holder))
                .into(imageView)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.place_holder)
        }
        ivPlayIcon.setOnClickListener {
            if (bannerVideoType.equals("youtube", ignoreCase = true)) {
                val intent = Intent(context, PlayYouTubeVideoActivity::class.java)
                intent.putExtra("videoUrl", bannerVideoUrl)
                intent.putExtra("videoId", youtubeVideoId)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, PlayVideoActivity::class.java)
                intent.putExtra("videoUrl", bannerVideoUrl)
                context.startActivity(intent)
            }
        }
        if (model.bannerType.equals("1", ignoreCase = true)) {
            //only image show
            ivPlayIcon.visibility = View.GONE
            imageView.isClickable = false
        } else if (model.bannerType.equals("2", ignoreCase = true)) {
            //show video thumbnail
            ivPlayIcon.visibility = View.VISIBLE
            imageView.isClickable = true
        }
        val vp = container as ViewPager
        vp.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}