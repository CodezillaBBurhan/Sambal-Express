package sambal.mydd.app.authentication

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.asyncTask.CheckRegistrationTask
import sambal.mydd.app.beans.User
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.databinding.ConfirmlayoutBinding
import sambal.mydd.app.utils.*
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.TimeUnit

class VerifyNumber : AppCompatActivity(), View.OnClickListener {
    var binding: ConfirmlayoutBinding? = null
    private var userId: Int? = null
    private var userKey: String? = null
    private var userMobile: String? = null
    private var isComment: String? = null
    private var isFromForgotPassword: String? = null
    private var send_Otp: String? = null
    var checkfirstClickForVerification = 0
    private var mAuth: FirebaseAuth? = null
    var loadingbarforfetchingdata: Dialog? = null
    var etReferralCode = ""
    var name = ""
    var permissionDialog: Dialog? = null
    var countryCodeKey = ""
    var smsType = "";
    private var context: Context? = null
    private var responseObj: JSONObject? = null

    private var userDeviceID = ""
    private val userEmail = ""
    private var userPicture: String? = null
    private var userDeviceToken: String? = ""
    private var userName = ""
    private var googleName: String? = ""
    private var gooleEmail: String? = ""
    private var googleIdToken: String? = ""
    private var googleImage = ""
    private var password = ""
    private var userCountryCode: Int? = 44
    private var user_mobile_without_country_code: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.confirmlayout)
        context = this@VerifyNumber



        binding!!.confirmEditBtn.setOnClickListener(this)
        bundle
        binding!!.confirmOkBtn.setOnClickListener(this)

        // below line is for getting instance
        // of our FirebaseAuth.

        mAuth = FirebaseAuth.getInstance();
    }

    private val bundle: Unit
        private get() {
            try {
                val bundle = intent.extras

                if (bundle != null) {
                    binding!!.confirmNumber.text = "+" + bundle.getString("userMobile")
                    userId = bundle.getInt("userId")
                    userKey = bundle.getString("userKey")
                    userMobile = bundle.getString("userMobile")
                    isComment = bundle.getString("isComment")
                    send_Otp = bundle.getString("send_OTP")
                    isFromForgotPassword = bundle.getBoolean("isFromForgotPassword").toString()
                    checkfirstClickForVerification = bundle.getInt("checkfirstClickForVerification")
                    etReferralCode = bundle.getString("etReferralCode").toString()
                    name = bundle.getString("userName").toString()
                    userName = bundle.getString("userName").toString()

                    countryCodeKey = bundle.getString("countryCodeKey").toString()
                    smsType = bundle.getString("smsType").toString()

                    userCountryCode = bundle.getString("user_country_code").toString().toIntOrNull()

                    ErrorMessage.E("name111>>" + etReferralCode + "   >>" + smsType)

                    val separate_countrycode_number =
                        userMobile!!.split(" ") // Split the string based on spaces
                    user_mobile_without_country_code = separate_countrycode_number[1]
                }

            } catch (e: Exception) {

            }

        }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.confirm_ok_btn ->


//                startActivity(Intent(this@VerifyNumber,
//                VerifyOTPActivity::class.java)
//                .putExtra("userId", userId)
//                .putExtra("userKey", userKey)
//                .putExtra("userMobile", userMobile)
//                .putExtra("isFromForgotPassword", "no")
//                .putExtra("isComment", "false")
//                .putExtra("send_OTP", send_Otp))


                /*             if (smsType != null && !smsType.equals("") && smsType.equals("2")) {
                                 userMobile?.let { sendVerificationCode(it, false) }
                             } else {
                                 startActivity(Intent(this@VerifyNumber,
                                     VerifyOTPForLocalSmsActivity::class.java)
                                     .putExtra("userId", userId)
                                     .putExtra("userKey", userKey)
                                     .putExtra("userMobile", userMobile)
                                     .putExtra("isFromForgotPassword", "no")
                                     .putExtra("isComment", "false")
                                     .putExtra("send_OTP", send_Otp)
                                     .putExtra("etReferralCode", etReferralCode)
                                     .putExtra("userName", name)
                                     .putExtra("countryCodeKey", countryCodeKey))
                             }*/


                callUserRegisteration(KeyConstant.SIGN_UP_WITH_EMAIL, null, "", "")

            R.id.confirm_edit_btn,
            -> finish()
        }
    }

    override fun onStop() {
        super.onStop()
    }


    private fun callUserRegisteration(
        userType: Int,
        userPicture: String?,
        socialIdToken: String,
        verificationId: String,
    ) {



        val materialDialog = ErrorMessage.initProgressDialog(this@VerifyNumber)

        // Log.e("USeeeee", user.email + "")
        CheckRegistrationTask(context,
            userPicture,
            etReferralCode,
            getUserParam(userType),
            socialIdToken,
            "",
            object : AsyncCallback {
                override fun setResponse(responseCode: Int?, responseStr: String?) {
                    ErrorMessage.E("ththththth<><>" + responseCode)

                    try {
                        if(materialDialog.isShowing)
                        materialDialog.dismiss()
                    } catch (e: Exception) {
                    }
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

//                                 smsType =
//                                    responseObj!!.getString("smsType")

                                smsType = responseObj?.getString("smsType") ?: ""

                                ErrorMessage.E("smsType>>> ${smsType}")


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

                                            binding!!.confirmNumber.text =
                                                "+ $userMobile"
                                            AppUtil.hideSoftKeyboard(context)


//                                            sendVerificationCode("+$userCountryCode$userMobile", false)

//                                            val intent1 =
//                                                Intent(context, VerifyNumber::class.java)
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
//                                                responseObj!!.optInt(KeyConstant.KEY_USER_ID))
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
//                                                responseObj!!.optString(KeyConstant.KEY_USER_KEY))
////                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
////                                                userCountryCode.toString() + " " + userMobile)
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
//                                                ""+userMobile)
//                                            intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
//                                                isFromForgotPassword)
//                                            intent1.putExtra("isComment", isComment)
//                                            intent1.putExtra("send_OTP", send_Otp)
//                                            intent1.putExtra("smsType",smsType)
//                                            intent1.putExtra("checkfirstClickForVerification", 1)
//
//                                            startActivity(intent1)


                                            if (smsType != null && !smsType.equals("") && smsType.equals(
                                                    "2")
                                            ) {
                                                userMobile?.let { sendVerificationCode(it, false) }
                                            }

                                            else if (smsType != null && !smsType.equals("") && smsType.equals(
                                                    "0")
                                            ) {
                                                SharedPreferenceVariable.deletePreferenceData(this@VerifyNumber,
                                                    KeyConstant.Shar_Email)
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Name,
                                                    responseObj!!.optString("userName"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.KEY_USER_ID,
                                                    responseObj!!.optString("userId"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Photo,
                                                    responseObj!!.optString("userPhoto"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Email,
                                                    responseObj!!.optString("userEmail"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.KEY_CODE,
                                                    responseObj!!.optString("userQRCode"))
                                                PreferenceHelper.getInstance(context)?.isLogin = true

                                                intentToMainActivity()

                                            }

                                            else {
                                                startActivity(Intent(this@VerifyNumber,
                                                    VerifyOTPForLocalSmsActivity::class.java)
                                                    .putExtra("userId", userId)
                                                    .putExtra("userKey", userKey)
                                                    .putExtra("userMobile", userMobile)
                                                    .putExtra("isFromForgotPassword", "no")
                                                    .putExtra("isComment", "false")
                                                    .putExtra("send_OTP", send_Otp)
                                                    .putExtra("etReferralCode", etReferralCode)
                                                    .putExtra("userName", name)
                                                    .putExtra("countryCodeKey", countryCodeKey))
                                            }


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
                           /*         SharedPreferenceVariable.deletePreferenceData(this@VerifyNumber,
                                        KeyConstant.Shar_Email)
                                    SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                        KeyConstant.Shar_Name,
                                        responseObj!!.optString("userName"))
                                    SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                        KeyConstant.KEY_USER_ID,
                                        responseObj!!.optString("userId"))
                                    SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                        KeyConstant.Shar_Photo,
                                        responseObj!!.optString("userPhoto"))
                                    SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                        KeyConstant.Shar_Email,
                                        responseObj!!.optString("userEmail"))
                                    SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                        KeyConstant.KEY_CODE,
                                        responseObj!!.optString("userQRCode"))
                                    dialogManager.stopProcessDialog()
                                    PreferenceHelper.getInstance(context)?.isLogin = true
                                    //                                if (isComment.equalsIgnoreCase("false")) {
//                                    intentToMainActivity();
//                                } else {
//                                    intentToMainActivity();
//                                }
                                    intentToMainActivity()*/


                                    runOnUiThread {
                                        try {
                                            binding!!.confirmNumber.text =
                                                "+ $userMobile"
                                            AppUtil.hideSoftKeyboard(context)


                                            if (smsType != null && !smsType.equals("") && smsType.equals(
                                                    "2")
                                            ) {
                                                userMobile?.let { sendVerificationCode(it, false) }
                                            }

                                            else if (smsType != null && !smsType.equals("") && smsType.equals(
                                                    "0")
                                            ) {
                                                SharedPreferenceVariable.deletePreferenceData(this@VerifyNumber,
                                                    KeyConstant.Shar_Email)
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Name,
                                                    responseObj!!.optString("userName"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.KEY_USER_ID,
                                                    responseObj!!.optString("userId"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Photo,
                                                    responseObj!!.optString("userPhoto"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.Shar_Email,
                                                    responseObj!!.optString("userEmail"))
                                                SharedPreferenceVariable.savePreferences(this@VerifyNumber,
                                                    KeyConstant.KEY_CODE,
                                                    responseObj!!.optString("userQRCode"))
                                                PreferenceHelper.getInstance(context)?.isLogin = true

                                                intentToMainActivity()

                                            }

                                            else {
                                                startActivity(Intent(this@VerifyNumber,
                                                    VerifyOTPForLocalSmsActivity::class.java)
                                                    .putExtra("userId", userId)
                                                    .putExtra("userKey", userKey)
                                                    .putExtra("userMobile", userMobile)
                                                    .putExtra("isFromForgotPassword", "no")
                                                    .putExtra("isComment", "false")
                                                    .putExtra("send_OTP", send_Otp)
                                                    .putExtra("etReferralCode", etReferralCode)
                                                    .putExtra("userName", name)
                                                    .putExtra("countryCodeKey", countryCodeKey))
                                            }


                                        } catch (e: Exception) {
                                        }
                                        /*
                                                                binding.signUpFormLayout.setVisibility(View.GONE);
                                                                ivLogo.setVisibility(View.VISIBLE);
                                                                binding.ivBg.setVisibility(View.GONE);
                                                                binding.signUpConfirmLayout.setVisibility(View.VISIBLE);*/
                                    }



                                }


                                else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                            KeyConstant.KEY_RESULT), ignoreCase = true)
                                    ) {
                                        try {
                                            if(materialDialog.isShowing)
                                                materialDialog.dismiss()
                                        } catch (e: Exception) {
                                        }
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
                        }
                        catch (e: Exception) {
                            ErrorMessage.E("Exception >>>$e")
                            e.printStackTrace()
                        }

                        //ProgressDialogUtils.hideProgressDialog();
                    }
                }

                override fun setException(e: String?) {
                    try {
                        if(materialDialog.isShowing)
                            materialDialog.dismiss()
                    } catch (e: Exception) {
                    }
                }
            }).execute()


    }


    private fun intentToMainActivity() {
        Log.e(":Main", "Main")
        //AppUtil.updateFavoriteAgent(agentIds, binding.signUpReadMore, context);
        PreferenceHelper.getInstance(context)?.loginScreenName = "Home"

        //finish();
        PreferenceHelper.getInstance(context)?.isLogin = true
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("deals", "4")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
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
            btnOk.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun getUserParam(userType: Int): User? {
        val user = User()
        userDeviceToken = PreferenceHelper.getInstance(context)?.fcmToken
        if (userDeviceToken != null) {
            // Log.e("device token", userDeviceToken!!)
        }

        ErrorMessage.E("ddddsasdsd>>" + userMobile)
        return if (userType != KeyConstant.SIGN_UP_WITH_GOOGLE) {
            SharedPreferenceVariable.deletePreferenceData(this@VerifyNumber,
                KeyConstant.Shar_Email)
            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            ErrorMessage.E("countryCodeKey1>>" + countryCodeKey)

            user.name = name
            user.userName = userName
            user.deviceToken = PreferenceHelper.getInstance(context)?.fcmToken
            user.password = password //countryCodeKey
            user.userCountryId = countryCodeKey.toInt()
            user.userCountry = userCountryCode.toString() + ""
            user.userMobile = user_mobile_without_country_code
            user.deviceID = userDeviceID
            user.mobileType = KeyConstant.DEVICE_TYPE_VALUE
            user.userType = userType
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
                Log.d("VerifyNumber", user.toString())
            }
            user
        } else {
            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            ErrorMessage.E("countryCodeKey2>>" + countryCodeKey)
            user.name = googleName
            user.userName = googleName
            user.password = password
            user.userCountryId = userCountryCode
            user.userMobile = user_mobile_without_country_code
            user.deviceID = userDeviceID
            user.userCountry = countryCodeKey
            user.mobileType = KeyConstant.DEVICE_TYPE_VALUE
            user.userType = userType
            user.email = gooleEmail
            user.userPhoto = googleImage
            user.deviceToken = userDeviceToken
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
            Log.d("VerifyNumber", user.toString())
            user
        }
    }

    fun sendVerificationCode(
        number: String,
        checkForResendvariable: Boolean,
    ) {


        if (loadingbarforfetchingdata == null) {
            loadingbarforfetchingdata = ErrorMessage.initProgressDialog(this)
            loadingbarforfetchingdata!!.show()
        }

        val stringWithoutSpaces = "+${number.replace("\\s".toRegex(), "")}"


        Log.d("send_OTP", "${stringWithoutSpaces}     ${number.toString() + " "}")


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
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken,
            ) {
                super.onCodeSent(s, forceResendingToken)


                if (loadingbarforfetchingdata != null) {
                    loadingbarforfetchingdata!!.dismiss()
                }

                startActivity(Intent(this@VerifyNumber,
                    VerifyOTPActivity::class.java)
                    .putExtra("userId", userId)
                    .putExtra("userKey", userKey)
                    .putExtra("userMobile", userMobile)
                    .putExtra("isFromForgotPassword", "no")
                    .putExtra("isComment", "false")
                    .putExtra("send_OTP", s)
                    .putExtra("etReferralCode", etReferralCode)
                    .putExtra("userName", name)
                    .putExtra("countryCodeKey", countryCodeKey)

                )
            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val code = phoneAuthCredential.smsCode

                // checking if the code

                Log.d("code",
                    "${code}     ${code.toString() + " "}")
                // is null or not.
                if (code != null) {

                    if (loadingbarforfetchingdata != null) {
                        loadingbarforfetchingdata!!.dismiss()
                    }

                }
            }


            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.

                ErrorMessage.E("FirebaseExceptionhhii        $e.message")


                if (loadingbarforfetchingdata != null) {
                    loadingbarforfetchingdata!!.dismiss()
                }

//                Toast.makeText(this@VerifyNumber, e.message, Toast.LENGTH_LONG).show()

                try {
                    if (permissionDialog != null) {
                        permissionDialog!!.dismiss()
                    }

                    permissionDialog = Dialog(this@VerifyNumber)
                    permissionDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    permissionDialog!!.setContentView(R.layout.popupwithok)

                    permissionDialog!!.setCanceledOnTouchOutside(false)
                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(permissionDialog!!.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                    permissionDialog!!.window!!.attributes = lp
                    val tvTitle = permissionDialog!!.findViewById<TextView>(R.id.popup_content)
                    tvTitle.visibility = View.VISIBLE


                    if (e is FirebaseAuthInvalidCredentialsException) {
                        tvTitle.text =
                            "Invalid mobile number. Please try with a valid mobile number"
                    } else {
                        tvTitle.text = "${e.message}"
                    }

                    val btnOk = permissionDialog!!.findViewById<TextView>(R.id.popup_yes_btn)
                    btnOk.text = "OK"

                    val view = permissionDialog!!.findViewById<View>(R.id.view_btw_btn)
//                    view.visibility = View.VISIBLE
//                    if (permissionDialog != null) {
//                        permissionDialog!!.setCancelable(false)
//                        permissionDialog!!.show()
//                    }
                    try {
                        btnOk.setOnClickListener {
                            try {
                                if (permissionDialog != null) {
                                    permissionDialog!!.dismiss()
                                }


                            } catch (e: Exception) {
                            }

                        }

                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }

                permissionDialog!!.show()
            }


        }


}