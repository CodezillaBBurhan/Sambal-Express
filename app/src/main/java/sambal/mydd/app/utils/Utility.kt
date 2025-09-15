package sambal.mydd.app.utils

import android.Manifest
import android.R
import android.annotation.TargetApi
import android.os.Build
import android.content.pm.PackageManager
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Utility {
//    READ_EXTERNAL_STORAGE
    const val MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 123
    const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion > Build.VERSION_CODES.S) {

            // READ_EXTERNAL_STORAGE replace by READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(context!!,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!,
                        Manifest.permission.READ_MEDIA_IMAGES)
                ) {
                    val alertBuilder = AlertDialog.Builder(
                        context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                        ActivityCompat.requestPermissions((context as Activity?)!!,
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES),
                            MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES)
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions((context as Activity?)!!,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES)
                }
                false
            }
            else {
                true
            }
        }



        else {



            // READ_EXTERNAL_STORAGE replace by READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    val alertBuilder = AlertDialog.Builder(
                        context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                        ActivityCompat.requestPermissions((context as Activity?)!!,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions((context as Activity?)!!,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                }
                false
            }
            else {
                true
            }



//            true
        }
    }
}