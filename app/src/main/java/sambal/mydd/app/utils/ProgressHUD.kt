package sambal.mydd.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.widget.TextView
import android.content.DialogInterface
import sambal.mydd.app.utils.DialogManager.DialogInteractionListener
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import sambal.mydd.app.R
import java.lang.Exception

class ProgressHUD : Dialog {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, theme: Int) : super(context!!, theme) {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val imageView = findViewById<ImageView>(R.id.spinnerImageView)
        val spinner = imageView.background as AnimationDrawable
        spinner.start()
    }

    fun setMessage(message: CharSequence?) {
        if (message != null && message.length > 0) {
            findViewById<View>(R.id.message).visibility =
                View.VISIBLE
            val txt = findViewById<TextView>(R.id.message)
            txt.text = message
            txt.invalidate()
        }
    }

    companion object {
        fun show(
            context: Context?,
            message: CharSequence?,
            visible: Boolean,
            indeterminate: Boolean,
            cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener?,
            cancelButtonListener: DialogInteractionListener?
        ): ProgressHUD {
            val dialog = ProgressHUD(context, R.style.ProgressHUD)
            dialog.setTitle("")
            dialog.setContentView(R.layout.progress_hud)
            if (message == null || message.length == 0) {
                dialog.findViewById<View>(R.id.message).visibility = View.GONE
            } else {
                val txt = dialog.findViewById<TextView>(R.id.message)
                txt.visibility = View.VISIBLE
                txt.text = message
            }
            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.2f
            dialog.window!!.attributes = lp
            dialog.window!!.decorView.findViewById<View>(android.R.id.content)
            dialog.setCanceledOnTouchOutside(true)
            //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            try {
                dialog.show()
            } catch (e: Exception) {
                ErrorMessage.E("show>>>>>"+e.toString())
            }
            return dialog
        }
    }
}