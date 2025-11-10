package sambal.mydd.app.authentication

import `in`.aabhasjindal.otptextview.OTPListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.asyncTask.CheckRegistrationTask
import sambal.mydd.app.beans.User
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.OtpBinding
import sambal.mydd.app.utils.*
import sambal.mydd.app.utils.MySMSBroadcastReceiver.OTPReceiveListener
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Boolean
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Throwable
import kotlin.let
import kotlin.toString


class VerifyOTPActivity : AppCompatActivity(), View.OnClickListener, OTPReceiveListener {
    private var binding: OtpBinding? = null
    var context: Context? = null
    var isComment: String? = null
    var otp = ""
    private var password = ""
    var smsReceiver: MySMSBroadcastReceiver? = null
    private var userId: Int? = null
    private var userKey: String? = null
    private var userMobile: String? = null
    private var isFromForgotPassword = false
    val dialogManager = DialogManager()
    // variable for FirebaseAuth class
    private var mAuth: FirebaseAuth? = null
    var etReferralCode=""
    private var responseObj: JSONObject? = null
    private var userDeviceToken: String? = ""
    private var name = ""
    private val userName = ""
    private var userCountryCode: Int? = 44
    private var countryCodeKey = "228"
    private var selectedCountry = "United Kingdom"
    private var userDeviceID = ""
    private val userEmail = ""
    private var userPicture: String? = null

    private var googleName: String? = ""
    private var gooleEmail: String? = ""
    private var googleImage = ""


    // string for storing our verification ID
    private var verificationId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.otp)
        context = this@VerifyOTPActivity
        // Setup keyboard and scroll handling
        setupKeyboardAndScrollHandling()
        startSmsListener()
        val extra = intent.extras
        if (extra != null) {
            userId = extra.getInt(IntentConstant.INTENT_KEY_USER_ID)
            userKey = extra.getString(IntentConstant.INTENT_KEY_USER_KEY)
            userMobile = extra.getString(IntentConstant.INTENT_KEY_USER_MOBILE)

            isFromForgotPassword =
                Boolean.parseBoolean(extra.getString(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD))
            isComment = extra.getString("isComment")
            etReferralCode= extra.getString("etReferralCode").toString()

            countryCodeKey=extra.getString("countryCodeKey").toString()
//            userMobile?.let { sendVerificationCode(it) }

//            extra.getString("send_OTP")?.let { Log.d("send_OTP", it) }
//            Log.d("send_OTP", extra.getString("send_OTP").toString())

            verificationId=extra.getString("send_OTP").toString()

            name=extra.getString("userName").toString()

            ErrorMessage.E("countryCodeKey>>"+ countryCodeKey)
        }
        initView()
        try {
            val ot = MySMSBroadcastReceiver.otp.split(" ".toRegex()).toTypedArray()
            binding!!.exOTP.otp = ot[0]
        } catch (e: Exception) {
        }

        otpValidateTimer()

    }
    private fun setupKeyboardAndScrollHandling() {
        // Handle focus changes on OTP field
        binding!!.exOTP.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollToOTPField()
            }
        }

        // Handle clicks on OTP field
        binding!!.exOTP.setOnClickListener {
            scrollToOTPField()
        }

        // Monitor keyboard visibility and scroll when keyboard appears
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding!!.root.rootView.height - binding!!.root.height
            if (heightDiff > 200) { // If more than 200 pixels, its probably keyboard
                if (binding!!.exOTP.hasFocus()) {
                    scrollToOTPField()
                }
            }
        }
    }

    private fun scrollToOTPField() {
        binding!!.scrollView.postDelayed({
            // Get OTP field location
            val location = IntArray(2)
            binding!!.exOTP.getLocationOnScreen(location)
            val otpY = location[1]

            // Get scroll view location
            binding!!.scrollView.getLocationOnScreen(location)
            val scrollViewY = location[1]

            // Calculate the scroll amount needed
            val scrollTo = (otpY - scrollViewY - 100).coerceAtLeast(0)

            // Scroll to position
            binding!!.scrollView.smoothScrollTo(0, scrollTo)
        }, 100)
    }

    private fun startSmsListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver!!.initOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.registerReceiver(smsReceiver, intentFilter, RECEIVER_EXPORTED)
            } else {
                this.registerReceiver(smsReceiver, intentFilter)
            }
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener { Log.e("Tag", "onSuccess") }
        } catch (e: Exception) {
            Log.e("Es", e.toString())
            ErrorMessage.T(this,e.toString())
            e.printStackTrace()
        }
    }


    private fun initView() {
        binding!!.tvNumber.text = "+$userMobile"
        binding!!.tvResend.setOnClickListener(this)
        binding!!.tvVerify.setOnClickListener(this)
        binding!!.tvWronNumber.setOnClickListener(this)
        binding!!.exOTP.setOtpListener(object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
                Log.e("Tag", "onSuccess")
            }

            override fun onOTPComplete(otps: String) {
                otp = otps
//                verifyCode(otp)
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvResend -> callResendOtp()
            R.id.tvVerify -> checkValidation()
            R.id.tvWronNumber -> finish()
        }
    }

    private fun checkValidation() {
        if (otp.equals("", ignoreCase = true)) {
            AppUtil.hideSoftKeyboard(context)
            Toast.makeText(this@VerifyOTPActivity, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            return
        } else if (!AppUtil.isNetworkAvailable(this@VerifyOTPActivity)) {
            AppUtil.hideSoftKeyboard(context)
            Toast.makeText(this@VerifyOTPActivity, "No Internet", Toast.LENGTH_SHORT).show()
            return
        } else {
            AppUtil.hideSoftKeyboard(context)

            verifyCode(otp)
//            callVerificationCode()
        }
    }

    private fun callVerificationCode() {
        val user = User()
        user.userId = userId
        user.userKey = userKey
        user.otp = otp
        Log.e("ISsss", userId.toString() + "," + userKey + "," + otp)
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().numberVerification(user.otp)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("NumberVerification", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_201) {

                                //AppUtil.updateFavoriteAgent(agentIds, resendSMS, context);
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                SharedPreferenceVariable.savePreferences(context,
                                    KeyConstant.Shar_Name,
                                    responseObj.optString("userName"))
                                SharedPreferenceVariable.deletePreferenceData(context,
                                    KeyConstant.Shar_Email)
                                SharedPreferenceVariable.savePreferences(context,
                                    KeyConstant.Shar_Phone,
                                    responseObj.optString("userMobile"))
                                SharedPreferenceVariable.savePreferences(context,
                                    KeyConstant.Shar_Photo,
                                    responseObj.optString("userPhoto"))
                                SharedPreferenceVariable.savePreferences(context,
                                    KeyConstant.KEY_USER_ID,
                                    responseObj.optString("userId"))
                                PreferenceHelper.getInstance(context)?.setUserDetail(responseObj.toString())
                                PreferenceHelper.getInstance(context)?.isLogin = true
                                dialogManager.stopProcessDialog()
                                if (errorType == "201") {
                                    startActivity(Intent(context, MainActivity::class.java)
                                        .putExtra("deals", "4")
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                                    finish()
                                } else if (isComment.equals("true", ignoreCase = true)) {
                                    finish()
                                } else {
                                    startActivity(Intent(context, MainActivity::class.java)
                                        .putExtra("deals", "4")
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                                    finish()
                                }
                            } else if (errorType == KeyConstant.KEY_RESPONSE_CODE_202) {
                                dialogManager.stopProcessDialog()
                                AppUtil.showMsgAlert(binding!!.exOTP,
                                    resp.optString(KeyConstant.KEY_MESSAGE))
                            } else {
                                dialogManager.stopProcessDialog()
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    AppUtil.showMsgAlert(binding!!.exOTP,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            dialogManager.stopProcessDialog()
                            e.printStackTrace()
                            AppUtil.showMsgAlert(binding!!.exOTP,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            dialogManager.stopProcessDialog()
                            e.printStackTrace()
                            AppUtil.showMsgAlert(binding!!.exOTP,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.exOTP,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.exOTP, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.exOTP, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun callResendOtp() {
//        if (AppUtil.isNetworkAvailable(this)) {
//            val dialogManager = DialogManager()
//            dialogManager.showProcessDialog(this, "", false, null)
//            val call = AppConfig.api_Interface().reSendVerificationCode()
//            call!!.enqueue(object : Callback<ResponseBody?> {
//                override fun onResponse(
//                    call: Call<ResponseBody?>,
//                    response: Response<ResponseBody?>,
//                ) {
//                    if (response.isSuccessful) {
//                        try {
//                            val jsonObject = JSONObject(response.body()!!.string())
//                            Log.e("Resend_Verifi_Code", jsonObject.toString())
//                            val message = jsonObject.optString(KeyConstant.KEY_MESSAGE)
//                            Log.e("MEssa", message)
//                            if (message != null) {
//                                dialogManager.stopProcessDialog()
//                                AppUtil.showMsgAlert(binding!!.tvNumber, message)
//                            } else {
//                                dialogManager.stopProcessDialog()
//                                AppUtil.showMsgAlert(binding!!.tvNumber,
//                                    MessageConstant.MESSAGE_SOMETHING_WRONG)
//                            }
//                        } catch (e: JSONException) {
//                            dialogManager.stopProcessDialog()
//                            e.printStackTrace()
//                            AppUtil.showMsgAlert(binding!!.tvNumber,
//                                MessageConstant.MESSAGE_SOMETHING_WRONG)
//                        } catch (e: IOException) {
//                            dialogManager.stopProcessDialog()
//                            e.printStackTrace()
//                            AppUtil.showMsgAlert(binding!!.tvNumber,
//                                MessageConstant.MESSAGE_SOMETHING_WRONG)
//                        }
//                    } else {
//                        dialogManager.stopProcessDialog()
//                        AppUtil.showMsgAlert(binding!!.tvNumber,
//                            MessageConstant.MESSAGE_SOMETHING_WRONG)
//                        Log.e("sendToken", "else is working" + response.code().toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                    ErrorMessage.E("ON FAILURE > " + t.message)
//                    dialogManager.stopProcessDialog()
//                    AppUtil.showMsgAlert(binding!!.tvNumber, t.message)
//                }
//            })
//        } else {
//            AppUtil.showMsgAlert(binding!!.exOTP, MessageConstant.MESSAGE_INTERNET_CONNECTION)
//        }


        otpValidateTimer()

        val stringWithoutSpaces = "+${userMobile?.replace("\\s".toRegex(), "")}"

        ErrorMessage.E("gffgfggf    $stringWithoutSpaces")

//        stringWithoutSpaces?.let { (context as SignUpActivity).sendVerificationCode(it,true) }



        val options = mAuth?.let {
            PhoneAuthOptions.newBuilder(it)
                .setPhoneNumber(stringWithoutSpaces) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
                .build()
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)



                verificationId = s


//                send_OTP= verificationId.toString()


            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val code = phoneAuthCredential.smsCode

                // checking if the code
                // is null or not.
                if (code != null) {
                    verifyCode(code);
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.

                Log.d("FirebaseException","${e.toString()}     $e.message")
                Toast.makeText(this@VerifyOTPActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    override fun onOTPReceived(otp: String) {
        Log.e("OTP", otp)
        try {
            val ot = otp.split(" ".toRegex()).toTypedArray()
            binding!!.exOTP.otp = ot[0]
        } catch (e: Exception) {
        }
    }

    override fun onOTPTimeOut() {
        Log.e("Timeout", "Timeout")
    }

    override fun onStop() {
        super.onStop()
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver!!)
        }
    }


    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        Log.d("ottpppppp22","$credential     ${verificationId}")
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.
                    dialogManager.stopProcessDialog()

                    val user = User()
                    user.userId = userId
                    user.userKey = userKey
                    user.otp = otp

                    Toast.makeText(this@VerifyOTPActivity,
                        "Succesfull register",
                        Toast.LENGTH_LONG).show()
                    PreferenceHelper.getInstance(context)?.isLogin = true
                    val i = Intent(this@VerifyOTPActivity, MainActivity::class.java)
                        .putExtra("deals", "4")
                        .putExtra("userId", userId)
                        .putExtra("userKey", userKey)
                        .putExtra("userMobile", userMobile)


                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    finish()

//                    verificationId?.let {
//                        callUserRegisteration(KeyConstant.SIGN_UP_WITH_EMAIL, null, "",
//                            it)
//                    }


                }
                else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.

//                    ErrorMessage.E("dsdssd>>"+ task.exception.toString())
//                    Toast.makeText(this@VerifyOTPActivity,
//                        task.exception.toString(),
//                        Toast.LENGTH_LONG).show()

                    ErrorMessage.E("Exceeeeption<><>"+task.toString())
                    ErrorMessage.E("Exceeeeption<><>"+task.exception.toString())

                    dialogManager.stopProcessDialog()

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        infoPopup("You have entered an invalid OTP. Please try with a valid OTP.")

                    } else {

                        infoPopup(task.exception.toString())

                    }


                }
            })
    }

    private fun verifyCode(code: String) {
        // credentials from our verification id and code.
        Log.d("ottpppppp","$code")
//        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }

//        val dialogManager = DialogManager()
        dialogManager.showProcessDialog(this, "", false, null)
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }


        // after getting credential we are
        // calling sign in method.
        Log.d("ottpppppp1","$credential")

        if (credential != null) {
            Log.d("ottpppppp","$code")
            signInWithCredential(credential)
        }
    }


    private fun otpValidateTimer(){
        object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                binding?.tvResend?.isClickable=false
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding?.tvResend?.text = String.format("%d:%02d", minutes, seconds)

            }
            override fun onFinish() {
                binding?.tvResend?.isClickable=true
                binding?.tvResend?.text = String.format("RESEND CODE")
            }
        }.start()
    }



    private fun callUserRegisteration(userType: Int, userPicture: String?, socialIdToken: String, verificationId: String) {
//        val dialogManager = DialogManager()


        dialogManager.showProcessDialog(this, "", false, null)
        // Log.e("USeeeee", user.email + "")
        CheckRegistrationTask(context,
            userPicture,
            etReferralCode.toString().trim { it <= ' ' },
            getUserParam(userType),
            socialIdToken,
            verificationId,
            object : AsyncCallback {
                override fun setResponse(responseCode: Int?, responseStr: String?) {
                    if (responseStr != null) {
                        try {
                            Log.e("NormalLogi", responseStr)
                            val resp = JSONObject(responseStr)
                            ErrorMessage.E("resp >>>$resp   ")
                            if (resp.optString("status") == "true") {
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                //   Log.e("Error", errorType)
                                //String message = resp.optString(KeyConstant.KEY_MESSAGE);
                                responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                val accessToken =
                                    responseObj!!.optString(KeyConstant.KEY_ACCESS_TOKEN)
                                ErrorMessage.E("accessToken>>> ${accessToken}")
                                val refreshToken =
                                    responseObj!!.optString(KeyConstant.KEY_REFRESH_TOKEN)
                                PreferenceHelper.getInstance(context)?.accessToken = accessToken
                                PreferenceHelper.getInstance(context)?.refreshToken = refreshToken


                                /*if (isFromSocialMedia) {
                            dialogManager.stopProcessDialog();
                            PreferenceHelper.getInstance(context).setUserDetail(responseObj.toString());

                            if (errorType.equalsIgnoreCase(KeyConstant.KEY_RESPONSE_CODE_201)) {
                                SharedPreferenceVariable.savePreferences(SignUpActivity.this, KeyConstant.Shar_Name, responseObj.optString("userName"));
                                SharedPreferenceVariable.savePreferences(SignUpActivity.this, KeyConstant.KEY_USER_ID, responseObj.optString("userId"));

                                SharedPreferenceVariable.savePreferences(SignUpActivity.this, KeyConstant.Shar_Photo, responseObj.optString("userPhoto"));
                                SharedPreferenceVariable.savePreferences(SignUpActivity.this, KeyConstant.Shar_Email, responseObj.optString("userEmail"));
                                SharedPreferenceVariable.savePreferences(SignUpActivity.this, KeyConstant.KEY_CODE, responseObj.optString("userQRCode"));

                                intentToMainActivity();
                            } else if (errorType.equalsIgnoreCase(KeyConstant.KEY_RESPONSE_CODE_200)) {
                                try {
                                   //LoginManager.getInstance().logOut();
                                    AppUtil.showMsgAlert(binding.signUpPassword, resp.optString(KeyConstant.KEY_MESSAGE));
                                } catch (Exception e) {
                                }
                                setUserOnLoginBySocialMedia(resp);
                                return;
                            } else if (errorType.equalsIgnoreCase(KeyConstant.KEY_RESPONSE_CODE_202)) {
                                try {
                                    LoginManager.getInstance().logOut();
                                } catch (Exception e) {
                                }
                                startActivity(new Intent(SignUpActivity.this, VerifyOTPActivity.class)
                                        .putExtra("userId", "")
                                        .putExtra("userKey", "")
                                        .putExtra("isFromForgotPassword", "")
                                        .putExtra("userMobile", resp.optString("mobile"))
                                        .putExtra("isComment", "false"));
                                return;
                            } else if (errorType.equalsIgnoreCase("203")) {
                                AppUtil.showMsgAlert(binding.signUpPassword, resp.optString(KeyConstant.KEY_MESSAGE));
                                PreferenceHelper.getInstance(context).setAccessToken(responseObj.optString("access_token"));
                                PreferenceHelper.getInstance(context).setAccessToken(responseObj.optString("access_token"));

                                try {
                                    LoginManager.getInstance().logOut();
                                } catch (Exception e) {
                                }

                                startActivity(new Intent(context, SocialNumberVerification.class)
                                        .putExtra("comment", "false")
                                        .putExtra("userId", "")
                                        .putExtra("userMobile", "")
                                        .putExtra("isFromForgotPassword", "false")
                                        .putExtra("referralcode", etReferralCode.getText().toString().trim()));
                                return;
                            }

                        } else {*/
                                PreferenceHelper.getInstance(context)
                                    ?.setUserDetail(responseObj.toString())

                                /*if (errorType.equalsIgnoreCase(KeyConstant.KEY_RESPONSE_CODE_200)) {
                                intentToMainActivity();
                            } else */

                                if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202,
                                        ignoreCase = true)
                                ) {
                                    runOnUiThread {
                                        try {
                                            dialogManager.stopProcessDialog()
//                                            binding!!.confirmNumber.text =
//                                                "+$userCountryCode $userMobile"
                                            AppUtil.hideSoftKeyboard(context)
                                            dialogManager.stopProcessDialog()


                                            Toast.makeText(this@VerifyOTPActivity,
                                                "Succesfull register",
                                                Toast.LENGTH_LONG).show()
                                            PreferenceHelper.getInstance(context)?.isLogin = true
                                            val i = Intent(this@VerifyOTPActivity, MainActivity::class.java)
                                                .putExtra("deals", "4")
                                                .putExtra("userId", userId)
                                                .putExtra("userKey", userKey)
                                                .putExtra("userMobile", userMobile)

                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(i)
                                            finish()

//                                            sendVerificationCode("+$userCountryCode$userMobile", false)
//                                            val intent1 =
//                                                Intent(context, VerifyNumber::class.java)
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
//                                                responseObj!!.optInt(KeyConstant.KEY_USER_ID))
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
//                                                responseObj!!.optString(KeyConstant.KEY_USER_KEY))
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
//                                                userCountryCode.toString() + " " + userMobile)
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
//                                                isFromForgotPassword)
//                                            intent1.putExtra("isComment", isComment)
//                                            intent1.putExtra("send_OTP", send_OTP)
//                                            intent1.putExtra("checkfirstClickForVerification", 1)
//                                            startActivity(intent1)

                                        } catch (e: Exception) {
                                        }
                                        /*
                                                                binding.signUpFormLayout.setVisibility(View.GONE);
                                                                ivLogo.setVisibility(View.VISIBLE);
                                                                binding.ivBg.setVisibility(View.GONE);
                                                                binding.signUpConfirmLayout.setVisibility(View.VISIBLE);*/
                                    }
                                }
                                else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_201,
                                        ignoreCase = true)
                                ) {

                                    try {

                                        SharedPreferenceVariable.deletePreferenceData(this@VerifyOTPActivity,
                                            KeyConstant.Shar_Email)
                                        SharedPreferenceVariable.savePreferences(this@VerifyOTPActivity,
                                            KeyConstant.Shar_Name,
                                            responseObj!!.optString("userName"))
                                        SharedPreferenceVariable.savePreferences(this@VerifyOTPActivity,
                                            KeyConstant.KEY_USER_ID,
                                            responseObj!!.optString("userId"))
                                        SharedPreferenceVariable.savePreferences(this@VerifyOTPActivity,
                                            KeyConstant.Shar_Photo,
                                            responseObj!!.optString("userPhoto"))
                                        SharedPreferenceVariable.savePreferences(this@VerifyOTPActivity,
                                            KeyConstant.Shar_Email,
                                            responseObj!!.optString("userEmail"))
                                        SharedPreferenceVariable.savePreferences(this@VerifyOTPActivity,
                                            KeyConstant.KEY_CODE,
                                            responseObj!!.optString("userQRCode"))
                                        dialogManager.stopProcessDialog()
                                        PreferenceHelper.getInstance(context)?.isLogin = true
                                        //                                if (isComment.equalsIgnoreCase("false")) {
//                                    intentToMainActivity();
//                                } else {
//                                    intentToMainActivity();
//                                }
                                        intentToMainActivity()
                                    }
                                    catch(e: Exception){
                                        Toast.makeText(this@VerifyOTPActivity,
                                            "${e.message}",
                                            Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                            KeyConstant.KEY_RESULT), ignoreCase = true)
                                    ) {
                                        dialogManager.stopProcessDialog()
//                                        AppUtil.showMsgAlert(binding!!.signUpPassword,
//                                            resp.optString(KeyConstant.KEY_MESSAGE))
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    try {
                                        // ErrorMessage.T(SignUpActivity.this, resp.optString("message"));
                                        infoPopup(resp.optString("message"))
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            ErrorMessage.E("Exception >>>$e")
                            dialogManager.stopProcessDialog()
                            e.printStackTrace()
                        }
                        dialogManager.stopProcessDialog()
                    }
                }

                override fun setException(e: String?) {}
            }).execute()



    }


    private fun getUserParam(userType: Int): User? {
        val user = User()
        userDeviceToken = PreferenceHelper.getInstance(context)?.fcmToken
        if (userDeviceToken != null) {
            // Log.e("device token", userDeviceToken!!)
        }

        val usermobile = userMobile
        val parts = usermobile?.split(" ")
        val phoneNumber = if (parts!!.size > 1) {
            parts[1]
        } else {
            // If there's no space, use the entire string
            usermobile.filter { it.isDigit() }
        }


//        ErrorMessage.E("ddddsasdsd>>"+userMobile + "   "+ phoneNumber  + "   "+ userCountryCode.toString() + "  "  + usermobile.split(" ")[0])

        userCountryCode=usermobile.split(" ")[0].toInt()
        ErrorMessage.E("ddddsasdsd>>"+userMobile + "   "+ phoneNumber  + "   "+ userCountryCode.toString() + "  "  +userCountryCode   + "  "+ countryCodeKey+
                " userName"+ name + "    userName"+userName  )



        return if (userType != KeyConstant.SIGN_UP_WITH_GOOGLE) {
            ErrorMessage.E("asasasasasss")
            SharedPreferenceVariable.deletePreferenceData(this@VerifyOTPActivity,
                KeyConstant.Shar_Email)
            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            user.name = name
            user.userName = userName
            user.deviceToken = PreferenceHelper.getInstance(context)?.fcmToken
            user.password = password //countryCodeKey
            user.userCountryId = countryCodeKey.toInt()
//            user.userCountry = userCountryCode.toString() + ""
            user.deviceID = userDeviceID
            user.mobileType = KeyConstant.DEVICE_TYPE_VALUE
            user.userType = userType
//            user.userMobile = userMobile
            user.userMobile = phoneNumber
//            user.userCountryId = userCountryCode
            user.userCountry = userCountryCode.toString() + ""
            if (userType == 1) {
                user.email = ""
            } else {
                user.email = userEmail
            }
            user.deviceToken = userDeviceToken
            if (user.deviceToken != null) {
                //  Log.e("user device token1", user.deviceToken)
            }
            if (userPicture != null) {
                user.userPhoto = userPicture
                if (!userPicture.equals("", ignoreCase = true)) {
                    PreferenceHelper.getInstance(context)?.userProfileImage = userPicture
                }
            }
            if (user != null) {
                //  Log.e("Login Data", user.toString() + "")
//                Log.d(VerifyOTPActivity.TAG, user.toString())
            }
            user
        }
        else {

            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            user.name = googleName
            user.userName = googleName
            user.password = password
            user.userCountryId = userCountryCode
//            user.userMobile = userMobile
            user.deviceID = userDeviceID
            user.userCountry = countryCodeKey
            user.mobileType = KeyConstant.DEVICE_TYPE_VALUE
            user.userType = userType
            user.email = gooleEmail
            user.userPhoto = googleImage
            user.deviceToken = userDeviceToken

            user.userMobile = phoneNumber
            if (user.deviceToken != null) {
                //   Log.e("user device token2", user.deviceToken)
            }
            userPicture = googleImage
            if (userPicture != null) {
                user.userPhoto = userPicture
                if (!userPicture.equals("", ignoreCase = true)) {
                    PreferenceHelper.getInstance(context)?.userProfileImage = userPicture
                }
            }
            // Log.e("userpic", user.userPhoto)
            //  Log.e("Fb image", googleImage)
//            Log.d(VerifyOTPActivity.TAG, user.toString())
            user
        }
    }


    private fun infoPopup(responseMsg: String) {
        val dialog1 = Dialog(this)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_common)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog1.window!!.attributes = lp
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = responseMsg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.visibility = View.GONE
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "OK"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                binding!!.exOTP.setOTP("");
                dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun intentToMainActivity() {
        Log.e(":Main", "Main")
        //AppUtil.updateFavoriteAgent(agentIds, binding.signUpReadMore, context);
        PreferenceHelper.getInstance(context)?.loginScreenName = "Home"

        //finish();
        dialogManager.stopProcessDialog()
        PreferenceHelper.getInstance(context)?.isLogin = true
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("deals", "")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }


}