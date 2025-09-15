package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.os.Bundle
import android.view.WindowManager
import android.os.Build
import sambal.mydd.app.utils.PermissionUtil
import android.Manifest.permission
import android.R
import android.content.pm.PackageManager
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.widget.Toast
import sambal.mydd.app.utils.SharedPreferenceVariable
import android.content.Intent
import android.content.Context
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.StatusBarcolor
import android.media.MediaPlayer
import android.util.Log
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.google.zxing.Result
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

class ScanQr : AppCompatActivity(), ResultHandler {
    var resultHandler: ResultHandler? = null
    var uuID = ""
    var price: String? = ""
    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    private var context: Context? = null
    private val PERMISSION_REQUEST_CODE = 1111
    var check: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        context = this
        val bundle = intent.extras
        try {
            if (bundle != null) {
                if (bundle.getString("price") != null) {
                    price = bundle.getString("price")
                }
                if (bundle.getString("check") != null) {
                    check = bundle.getString("check")
                }
            }
        } catch (e: Exception) {
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.isCameraPermissionGranted(this)) {
                initCamera()
            } else {
                requestPermission()
            }
//        } else {
//            ErrorMessage.E("dddddddddddee")
//            initCamera()
//        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(permission.CAMERA, " "),
            PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> try {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("if is working", ">>")
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.e("if is not  working", ">>")
                    finish()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun initCamera() {
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera(mCameraId)
        mScannerView!!.flash = mFlash
        mScannerView!!.setAutoFocus(mAutoFocus)
    }

    private fun scanQRCode(ticketToken: String, price: String?) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val lat = MainActivity.userLat.toString() + ""
            val lng = MainActivity.userLang.toString() + ""
            val call = AppConfig.api_Interface().scanQRCode(uuID, lat, lng)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ReScan", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    Toast.makeText(context,
                                        resp.optString("message"),
                                        Toast.LENGTH_SHORT).show()
                                    SharedPreferenceVariable.savePreferences(context,
                                        KeyConstant.KEY_CODE,
                                        resp.optString("code"))
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
                                    finish() //finishing activity
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        Toast.makeText(context,
                                            resp.optString(KeyConstant.KEY_MESSAGE),
                                            Toast.LENGTH_SHORT).show()
                                        finish()
                                        dialogManager.stopProcessDialog()
                                        mScannerView!!.resumeCameraPreview(resultHandler)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            runOnUiThread {
                                Toast.makeText(context,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            runOnUiThread {
                                Toast.makeText(context,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        runOnUiThread {
                            ErrorMessage.T(context,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    runOnUiThread {
                        ErrorMessage.T(context,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                    }
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    private fun pairQRCode(ticketToken: String, price: String?) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val lat = MainActivity.userLat.toString() + ""
            val lng = MainActivity.userLang.toString() + ""
            val call = AppConfig.api_Interface().pairQRCode(uuID, lat, lng)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ReScan", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    Toast.makeText(context,
                                        resp.optString("message"),
                                        Toast.LENGTH_SHORT).show()
                                    SharedPreferenceVariable.savePreferences(context,
                                        KeyConstant.KEY_CODE,
                                        resp.optString("code"))
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
                                    finish() //finishing activity
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        Toast.makeText(context,
                                            resp.optString(KeyConstant.KEY_MESSAGE),
                                            Toast.LENGTH_SHORT).show()
                                        finish()
                                        dialogManager.stopProcessDialog()
                                        mScannerView!!.resumeCameraPreview(resultHandler)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            runOnUiThread {
                                Toast.makeText(context,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            runOnUiThread {
                                Toast.makeText(context,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        runOnUiThread {
                            ErrorMessage.T(context,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    runOnUiThread {
                        ErrorMessage.T(context,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                    }
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    fun getParams(ticketToken: String, price: String): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["userUUID"] = ticketToken
        params["price"] = price
        Log.e("Scan", params.toString())
        return params
    }

    public override fun onResume() {
        super.onResume()
        checkPermissions()
        try {
            StatusBarcolor.setStatusbarColor(this@ScanQr, "")
        } catch (e: Exception) {
        }
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
        uuID = rawResult.text
        mScannerView!!.stopCamera()
        try {
            if (check == "") {
                scanQRCode(rawResult.text, price)
            } else {
                pairQRCode(rawResult.text, price)
            }
        } catch (e: Exception) {
        }
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
            try {
                val mPlayer = MediaPlayer.create(this@ScanQr, sambal.mydd.app.R.raw.beep)
                mPlayer.start()
            } catch (e: Exception) {
            }
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