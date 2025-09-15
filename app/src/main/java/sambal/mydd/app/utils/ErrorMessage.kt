package sambal.mydd.app.utils

import android.os.Bundle
import android.content.Intent
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import androidx.appcompat.app.AlertDialog
import sambal.mydd.app.R
import java.lang.Exception
import java.text.SimpleDateFormat

object ErrorMessage {
    fun I(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
        onAnim(cx as Activity)

    }


    fun I_clear(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
        onAnim(cx as Activity)
    }

    @JvmStatic
    fun E(msg: String?) {
        if (true) {
            Log.e("Log.E By Burhan", msg!!)
        }
    }


    fun initProgressDialog(c: Context?): Dialog {
        val dialog = Dialog(c!!)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_hud)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        return dialog
    }

    fun T(c: Context?, msg: String?) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show()
    }

    fun D(c: Context?, title: String?, msg: String?) {

        if(c!=null) {
            val alertDialog = AlertDialog.Builder(
                c).create()
            alertDialog.setTitle(title)
            alertDialog.setMessage(msg)
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }
    }

    fun onAnim(activity: Activity) {
        try {
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
        }
    }

    fun I_ActivityForResult(
        cx: Activity,
        startActivity: Class<*>?,
        data: Bundle?,
        requestCode: Int
    ) {
        val intent = Intent(cx, startActivity)
        if (data != null) intent.putExtras(data)
        cx.startActivityForResult(intent, requestCode)
        onAnim(cx)
    }

    fun ChangeDateFormate(date: String?): String? {
        var date = date
        try {
            var spf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("dd/MM/yyyy")
            date = spf.format(newDate)
            return date
        } catch (e: Exception) {
        }
        return null
    }

    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }
}