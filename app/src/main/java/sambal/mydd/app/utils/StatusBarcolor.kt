package sambal.mydd.app.utils

import android.os.Build
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import sambal.mydd.app.R

object StatusBarcolor {
    /*fun setStatusbarColor(context: Context, check: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (check == "white") {
                (context as Activity).window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
                Log.e("setStatusbarColor", "" + check)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor =
                    context.getResources().getColor(R.color.colorPrimaryDark)
            } else if (check == "colorPrimary") {
                (context as Activity).window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE //  set status text dark
                Log.e("setStatusbarColor", "" + check)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor =
                    context.getResources().getColor(R.color.colorPrimaryDark)
            } else if (check == "black") {
                (context as Activity).window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
                Log.e("setStatusbarColor", "" + check)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor =
                    context.getResources().getColor(R.color.black)
            } else {
                (context as Activity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
                Log.e("setStatusbarColor", "" + check)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor = context.getResources().getColor(R.color.colorPrimaryDark)
                context.window.decorView.systemUiVisibility = 0
            }
            // ((Activity) context).getWindow().setStatusBarTextColor(context.getResources().getColor(R.color.black));
        }
    }*/
    fun setStatusbarColor(activity: Activity, check: String) {
        // Edge-to-edge (notch) support
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        // Apply padding for system bars (safe area)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Enable drawing system bar backgrounds
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // Default status bar color and light/dark icons
        val decorView = activity.window.decorView
        var statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimary)
        var lightStatusBar = false
        var drawableRes: Int? = null

        when (check) {
            "gradiant" -> drawableRes = R.drawable.main_gradiant_layout
            "green_gradient" -> drawableRes = R.drawable.main_gradiant_layout
            "colorPrimary" -> drawableRes = R.drawable.main_gradiant_layout
            "yellow_gradient" -> drawableRes = R.drawable.main_gradiant_yellow_layout
            "yellow" -> drawableRes = R.drawable.main_gradiant_yellow_layout
            "white" -> drawableRes = R.drawable.main_gradiant_layout
            "purple" -> drawableRes = R.drawable.main_gradiant_layout
            "red" -> drawableRes = R.drawable.main_gradiant_layout
            "black" -> drawableRes = R.drawable.main_gradiant_layout
            "" -> drawableRes = R.drawable.main_gradiant_layout
        }

        // Apply color
        if (drawableRes == null) {
            activity.window.statusBarColor = statusBarColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = if (lightStatusBar) {
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        } else {
            // Transparent for gradient backgrounds
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.setBackgroundDrawable(ContextCompat.getDrawable(activity, drawableRes))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}