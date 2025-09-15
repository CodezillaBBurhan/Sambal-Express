package sambal.mydd.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {
    private const val REQUEST_CODE_LIVE_STREAMING = 10
    fun isRecordAudioGranted(context: Context?): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun isWritePermissionGranted(context: Context?): Boolean {
        return (ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun isCameraPermissionGranted(context: Context?): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }
}