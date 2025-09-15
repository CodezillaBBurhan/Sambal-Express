package sambal.mydd.app.utils

import android.app.ProgressDialog
import android.content.Context
import java.lang.Exception
import kotlin.Throws

object ProgressDialogUtils {
    var dialog: ProgressDialog? = null
    fun showProgressDialog(pContext: Context?, msg: String?) {
        try {
            dialog = ProgressDialog(pContext)
            dialog!!.setCancelable(false)
            dialog!!.setMessage(msg)
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun hideProgressDialog() {
        try {
            if (ifDialogIsNotNullAndIsShowing()) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissDialog() {
        try {
            dialog!!.dismiss()
        } catch (e: Exception) {
        }
    }

    @Throws(Exception::class)
    private fun ifDialogIsNotNullAndIsShowing(): Boolean {
        return dialog != null && dialog!!.isShowing
    }
}