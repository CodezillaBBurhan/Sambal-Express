package sambal.mydd.app.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.os.Bundle
import android.view.WindowManager
import android.os.Build
import sambal.mydd.app.utils.PermissionUtil
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.StatusBarcolor
import android.media.MediaPlayer
import android.util.Log
import android.view.MenuItem
import android.view.Window
import com.google.zxing.Result
import sambal.mydd.app.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class ScanQrProductDetails : AppCompatActivity(), ResultHandler {
    private val REQUEST_CODE_LIVE_STREAMING = 10
    var resultHandler: ResultHandler? = null
    var uuID = ""
    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    private var context: Context? = null
    private var dealUUID: String? = ""
    private var agentId: String? = ""
    private var product_id: String? = ""
    var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        context = this
        val bundle = intent.extras
        if (bundle != null) {
            dealUUID = bundle.getString("dealUUID")
            agentId = bundle.getString("agentId")
            product_id = bundle.getString("product_id")
            pos = bundle.getInt("pos")
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
                initCamera()
            } else {
                val perms = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(perms, REQUEST_CODE_LIVE_STREAMING)
            }
        } else {
            initCamera()
        }
    }

    private fun initCamera() {
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera(mCameraId)
        mScannerView!!.flash = mFlash
        mScannerView!!.setAutoFocus(mAutoFocus)
    }

    private fun sendToken(uuID: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().scanAgentUUID(dealUUID, uuID)
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
                                    val dialog1 = Dialog(context!!)
                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog1.setContentView(R.layout.popupwithok)
                                    val lp = WindowManager.LayoutParams()
                                    lp.copyFrom(dialog1.window!!.attributes)
                                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                                    dialog1.window!!.attributes = lp
                                    val contentText =
                                        dialog1.findViewById<TextView>(R.id.popup_content)
                                    contentText.text = resp.optString("message")
                                    val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
                                    btnOk.text = "OK"
                                    dialog1.setCancelable(false)
                                    dialog1.show()
                                    try {
                                        btnOk.setOnClickListener {
                                            dialog1.dismiss()
                                            startActivity(Intent(this@ScanQrProductDetails,
                                                LatestProductDetails::class.java)
                                                .putExtra("agentId", agentId)
                                                .putExtra("direct", "non_direct")
                                                .putExtra("pos", pos)
                                                .putExtra("product_id", product_id)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                        }
                                        //
                                    } catch (e: Exception) {
                                    }
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
                            Toast.makeText(context,
                                MessageConstant.MESSAGE_SOMETHING_WRONG,
                                Toast.LENGTH_SHORT).show()
                        }
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    runOnUiThread {
                        Toast.makeText(context,
                            MessageConstant.MESSAGE_SOMETHING_WRONG,
                            Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    public override fun onResume() {
        super.onResume()
        checkPermissions()
        try {
            StatusBarcolor.setStatusbarColor(this@ScanQrProductDetails, "")
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
        sendToken(uuID)
        resultHandler = this
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    protected fun beepSound() {
        try {
            val mPlayer = MediaPlayer.create(this@ScanQrProductDetails, R.raw.beep)
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