package sambal.mydd.app.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import sambal.mydd.app.fragment.History.HistoryPointsFragment
import sambal.mydd.app.fragment.History.HistoryVoucherFragment
import com.google.android.material.tabs.TabLayout
import sambal.mydd.app.adapter.TabsAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NewhistoryBinding
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception
import java.util.ArrayList

class NewhistoryDashboardDetials : BaseActivity() {
    var binding: NewhistoryBinding? = null
    var context: Context? = null
    var TotalVisitCount = ""
    var TotalPoints = ""
    var TotalVoucher = ""

    override val contentResId: Int
        get() = R.layout.newhistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.newhistory)
        setToolbarWithBackButton_colorprimary("History")
        context = this@NewhistoryDashboardDetials
        val fragmentList: MutableList<Fragment> = ArrayList()
        fragmentList.add(HistoryPointsFragment())
        fragmentList.add(HistoryVoucherFragment())
        val titleList: MutableList<String> = ArrayList()
        titleList.add("Points")
        titleList.add("Voucher")
        binding!!.tabLayout.tabMode = TabLayout.MODE_FIXED
        val adapter = TabsAdapter(supportFragmentManager, titleList, fragmentList)
        binding!!.pager.offscreenPageLimit = titleList.size
        binding!!.pager.adapter = adapter
        binding!!.tabLayout.setupWithViewPager(binding!!.pager)
        binding!!.pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                Log.e("SELECTED VIEWPAGER PAGE", ">>$position")
                if (position == 0) {
                    binding!!.llVisits.visibility = View.VISIBLE
                    binding!!.tvHeading.text = "Total Points"
                    binding!!.tvVisitCount.text = TotalVisitCount
                    binding!!.tvPoints.text = TotalPoints
                } else {
                    binding!!.llVisits.visibility = View.GONE
                    binding!!.tvHeading.text = "Total Voucher"
                    binding!!.tvPoints.text = TotalVoucher
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    fun setDataPoints(userTotalVisitCount: String, userTotalPoints: String) {
        binding!!.tvVisitCount.text = userTotalVisitCount
        binding!!.tvPoints.text = userTotalPoints
        TotalVisitCount = userTotalVisitCount
        TotalPoints = userTotalPoints
    }

    fun setDataVoucher(totalVoucher: String) {
        TotalVoucher = totalVoucher
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@NewhistoryDashboardDetials, "")
        } catch (e: Exception) {
        }
    }
}