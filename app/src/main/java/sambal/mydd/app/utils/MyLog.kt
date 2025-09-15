package sambal.mydd.app.utils

import android.R
import android.app.Activity
import android.util.Log
import java.lang.Exception

class MyLog {
    var showLogD = false
    var showLogE = false
    fun logD(tag: String?, msg: String?) {
        if (showLogD) {
            Log.d(tag, msg!!)
        }
    }

    fun logE(tag: String?, msg: String?) {
        if (showLogE) {
            Log.e(tag, msg!!)
        }
    }

    companion object {
        fun onAnim(activity: Activity) {
            try {
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } catch (e: Exception) {
            }
        }
    }
}