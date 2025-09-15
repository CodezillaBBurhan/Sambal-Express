package sambal.mydd.app.utils

import android.net.ConnectivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.Activity
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.RelativeLayout
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.TextView
import sambal.mydd.app.authentication.SignUpActivity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import sambal.mydd.app.R
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object AppUtil {
    //PubNub Key with Presonal Account
    //public static final String kPubNubPublishKey = "pub-c-27c1c8e5-2b49-4874-95f9-44f89b4dd304";
    //public static final String kPubNubsubscribeKey = "sub-c-baf07688-36d6-11e7-b310-0619f8945a4f";
    private var widthPixels = 0
    @JvmStatic
    fun isNetworkAvailable(context: Context?): Boolean {
        val isNetworkConnected = BooleanArray(1)
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun decodeBase64(input: String?): Bitmap? {
        return try {
            val decodedByte = Base64.decode(input, 0)
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        } catch (e: Exception) {
            null
        }
    }

    fun hideSoftKeyboard(context: Context?) {
        val activity = context as Activity
        if (activity.currentFocus != null) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

    /* ---NEW--- */
    fun hideKeyboard(view: View, context: Context?) {
        val inputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSoftKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1)
    }

    fun shareIntent(context: Context?, msg: String?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        //sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(sendIntent)
    }

    fun showMsgAlert(view: View?, msg: String?) {
        try {
            Snackbar.make(view!!, msg!!, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
        }
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(packageName,
                PackageManager.GET_SIGNATURES)
            Log.e("Package Name=", context.applicationContext.packageName)
            for (signature in packageInfo.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }

    fun setImageSizeForPopularVIew(
        img: View,
        context: Activity,
        widthRatio: Int,
        heightRatio: Int
    ): RelativeLayout.LayoutParams {
        Log.d("ratio", "width : $widthRatio height $heightRatio")
        val width = getDeviceWidth(context)
        Log.d("ratio", "screen width : $width")
        return if (widthRatio != 0) {
            val params =
                RelativeLayout.LayoutParams(width,
                    width * heightRatio / widthRatio * 100 / 100 + 85)
            img.layoutParams = params
            Log.d("ratio",
                "calculate width : " + width + " calculate height : " + width * heightRatio / widthRatio * 100 / 100)
            params
        } else {
            val params = RelativeLayout.LayoutParams(width, width * 1 / 1 * 100 / 100)
            img.layoutParams = params
            params
        }
    }

    fun setImageSizeForDescriptionView(
        img: View,
        context: Activity,
        widthRatio: Int,
        heightRatio: Int
    ): RelativeLayout.LayoutParams {
        Log.d("ratio", "width : $widthRatio height $heightRatio")
        val width = getDeviceWidth(context)
        Log.d("ratio", "screen width : $width")
        return if (widthRatio != 0) {
            val params =
                RelativeLayout.LayoutParams(width, width * heightRatio / widthRatio * 100 / 100)
            img.layoutParams = params
            Log.d("ratio",
                "calculate width : " + width + " calculate height : " + width * heightRatio / widthRatio * 100 / 100)
            params
        } else {
            val params = RelativeLayout.LayoutParams(width, width * 1 / 1 * 100 / 100)
            img.layoutParams = params
            params
        }
    }

    fun setImageSizeForHomeView(
        img: View,
        context: Activity,
        widthRatio: Int,
        heightRatio: Int
    ): LinearLayout.LayoutParams {
        Log.d("ratio", "width : $widthRatio height $heightRatio")
        val width = getDeviceWidth(context)
        Log.d("ratio", "screen width : $width")
        return if (widthRatio != 0) {
            val params =
                LinearLayout.LayoutParams(width, width * heightRatio / widthRatio * 100 / 100 + 85)
            img.layoutParams = params
            Log.d("ratio",
                "calculate width : " + width + " calculate height : " + width * heightRatio / widthRatio * 100 / 100)
            params
        } else {
            val params = LinearLayout.LayoutParams(width, width * 1 / 1 * 100 / 100)
            img.layoutParams = params
            params
        }
    }

    fun setCardViewSize(
        img: View,
        context: Activity,
        widthRatio: Int,
        heightRatio: Int
    ): FrameLayout.LayoutParams {
        Log.d("ratio", "width : $widthRatio height $heightRatio")
        val width = getDeviceWidth(context)
        Log.d("ratio", "screen width : $width")
        return if (widthRatio != 0) {
            val params =
                FrameLayout.LayoutParams(width - 40, width * heightRatio / widthRatio * 100 / 100)
            params.setMargins(20, 0, 20, 15)
            img.layoutParams = params
            Log.d("ratio",
                "calculate width : " + width + " calculate height : " + width * heightRatio / widthRatio * 100 / 100)
            params
        } else {
            val params = FrameLayout.LayoutParams(width, width * 1 / 1 * 100 / 100)
            img.layoutParams = params
            params
        }
    }

    fun getDeviceWidth(context: Activity): Int {
        if (widthPixels == 0) {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            widthPixels = displayMetrics.widthPixels
            Log.e("device width", widthPixels.toString() + "")
        }
        return widthPixels
    }

    fun signupPopup(msg: String?, context: Context) {
        val dialog1 = Dialog(context)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = msg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"

        //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                dialog1.dismiss()
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    @JvmStatic
    fun isForeground(context: Context): Boolean { //String PackageName
        // Get the Activity Manager
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        val task = manager.getRunningTasks(1)
        // Get the info we need for comparison.
        val componentInfo: ComponentName?
        componentInfo = try {
            task[0].topActivity
        } catch (e: Exception) {
            null
        }
        // Check if it matches our package name.
        return componentInfo != null && componentInfo.packageName == "sambal.mydd.app"
        // If not then our app is not on the foreground.
    }

    fun drawCircle(context: Context?, width: Int, height: Int, color: Int): ShapeDrawable {

        //////Drawing oval & Circle programmatically /////////////
        val oval = ShapeDrawable(OvalShape())
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }
}