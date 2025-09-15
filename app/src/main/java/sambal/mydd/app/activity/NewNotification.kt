package sambal.mydd.app.activity


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.R
import sambal.mydd.app.fragment.NotificationFragment
import sambal.mydd.app.databinding.NewnotificationBinding
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception

class NewNotification : BaseActivity() {
    private var agentName: String? = "Notifications"
    private var Check: String? = ""
    var binding: NewnotificationBinding? = null

    override val contentResId: Int
        get() = R.layout.newnotification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.newnotification)
        setToolbarWithBackButton_colorprimary("Notifications")
        setFragment()
        intentData
        setToolbarWithBackButton_colorprimary(agentName!!)
    }

    private fun setFragment() {
        val fragment = NotificationFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.my_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private val intentData: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                agentId = intent.getStringExtra("agentId")
                Log.e("agentId", agentId!!)
                agentName = intent.getStringExtra("title")
                if (!agentName.equals("Notifications", ignoreCase = true)) {
                    binding!!.tvNoti.visibility = View.VISIBLE
                } else {
                    binding!!.tvNoti.visibility = View.GONE
                }
                try {
                    if (bundle.getString("check") != null) {
                        Check = bundle.getString("check")
                    }
                } catch (e: Exception) {
                }
            }
        }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@NewNotification, "")
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmField
        var agentId: String? = ""
    }
}