package sambal.mydd.app.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.widget.TextView
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.ImageView
import sambal.mydd.app.R

@SuppressLint("StaticFieldLeak")
object DialogQr {
    var ivTick: ImageView? = null
    var tvSuccess: TextView? = null
    var dialog1: Dialog? = null
    fun showDialog(context: Context?, bitmap: Bitmap?, UUID: String?) {
        dialog1 = Dialog(context!!, R.style.NewDialog)
        dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1!!.setContentView(R.layout.homeqr)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1!!.window!!.attributes = lp
        val ivClose = dialog1!!.findViewById<ImageView>(R.id.ivClose)
        ivTick = dialog1!!.findViewById(R.id.ivTick)
        tvSuccess = dialog1!!.findViewById(R.id.tvSuccess)
        val ivBarcode = dialog1!!.findViewById<ImageView>(R.id.ivBarcode)
        ivTick!!.setVisibility(View.GONE)
        tvSuccess!!.setVisibility(View.GONE)
        ivClose.setOnClickListener { dialog1!!.dismiss() }
        val tvTitle = dialog1!!.findViewById<TextView>(R.id.ticket_title)
        tvTitle.text = "Collect MyDD Points"
        val ivQrCode = dialog1!!.findViewById<ImageView>(R.id.iv_qr_code)
        ivQrCode.setImageBitmap(bitmap)
        try {
            dialog1!!.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    private fun guessAppropriateEncoding(contents: CharSequence): String? {
        // Very crude at the moment
        for (i in 0 until contents.length) {
            if (contents[i].code > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }
}