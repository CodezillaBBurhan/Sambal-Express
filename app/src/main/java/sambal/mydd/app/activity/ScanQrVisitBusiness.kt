package sambal.mydd.app.activity

import android.Manifest
import android.R
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.os.Bundle
import android.view.WindowManager
import android.os.Build
import sambal.mydd.app.utils.PermissionUtil
import sambal.mydd.app.utils.GPSTracker
import sambal.mydd.app.utils.StatusBarcolor
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.MenuItem
import com.google.zxing.Result
import java.lang.Exception
import java.util.ArrayList

class ScanQrVisitBusiness : AppCompatActivity(), ResultHandler {
    private val REQUEST_CODE_LIVE_STREAMING = 10
    var resultHandler: ResultHandler? = null
    var voucherUUID: String? = ""
    var voucherAmount: String? = ""
    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    var isStoreVouchers = false
    var currency: String? = ""
    private var latitude = 0.0
    private var longitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val bundle = intent.extras
        if (bundle != null) {
            voucherAmount = bundle.getString("amount")
            voucherUUID = bundle.getString("UUID")
            isStoreVouchers = bundle.getBoolean("storevouchers")
            currency = bundle.getString("currency")
        }
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false)
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.isCameraPermissionGranted(this)) {
            } else {
                val perms = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(perms, REQUEST_CODE_LIVE_STREAMING)
            }
        } else {
        }
    }

    private fun initCamera() {
        try {
            GPSTracker.requestSingleUpdate(this@ScanQrVisitBusiness,
                object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        Log.e("Location on MainAct is ", location.toString())
                        latitude = location!!.latitude.toDouble()
                        longitude = location.longitude.toDouble()
                        Log.e("LatSca", latitude.toString() + "")
                    }
                })
        } catch (e: Exception) {
        }
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera(mCameraId)
        mScannerView!!.flash = mFlash
        mScannerView!!.setAutoFocus(mAutoFocus)
    }

    public override fun onResume() {
        super.onResume()
        checkPermissions()
        try {
            StatusBarcolor.setStatusbarColor(this@ScanQrVisitBusiness, "")
        } catch (e: Exception) {
        }
        mScannerView!!.stopCamera()
        initCamera()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, mFlash)
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices)
        outState.putInt(CAMERA_ID, mCameraId)
    }

    override fun handleResult(rawResult: Result) {
        Log.e("TAG", rawResult.text) // Prints scan results
        Log.v("TAG", rawResult.barcodeFormat.toString())
        try {
            beepSound()
        } catch (e: Exception) {
        }
        val agentUUID = rawResult.text
        mScannerView!!.stopCamera()
        Log.e("Lsatassa", "$latitude,$longitude")
        startActivity(Intent(this@ScanQrVisitBusiness, VoucherScreen::class.java)
            .putExtra("agentUUID", agentUUID)
            .putExtra("voucherUUID", voucherUUID)
            .putExtra("voucherPrice", voucherAmount)
            .putExtra("storevouchers", isStoreVouchers)
            .putExtra("currency", currency)
            .putExtra("lat", latitude.toString())
            .putExtra("lng", longitude.toString()))
        finish()
        resultHandler = this
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    protected fun beepSound() {
        try {
            val mPlayer =
                MediaPlayer.create(this@ScanQrVisitBusiness, sambal.mydd.app.R.raw.beep)
            mPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Play", e.toString())
        }
    }

    companion object {
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val SELECTED_FORMATS = "SELECTED_FORMATS"
        private const val CAMERA_ID = "CAMERA_ID"
    }
}