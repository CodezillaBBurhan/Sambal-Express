package sambal.mydd.app.authentication

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import sambal.mydd.app.DealDioApplication
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.VolleySingleton.VolleyAppHelper
import sambal.mydd.app.VolleySingleton.VolleyMultipartRequest
import sambal.mydd.app.VolleySingleton.VolleySingleton
import sambal.mydd.app.activity.TermsAndConditionActivity
import sambal.mydd.app.adapter.CountryDialogAdapter
import sambal.mydd.app.asyncTask.CheckRegistrationTask
import sambal.mydd.app.beans.User
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.UrlConstant
import sambal.mydd.app.database.DatabaseHandler
import sambal.mydd.app.databinding.ActivitySignUp2Binding
import sambal.mydd.app.utils.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sambal.mydd.app.activity.Webview
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


class SignUpActivity : AppCompatActivity(), View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener {
    private val RC_USER_REGISTER = 101
    var socialMediaUserObj: JSONObject? = null
    var ivLogo: ImageView? = null
    private var etReferralCode: EditText? = null
    private var responseObj: JSONObject? = null
    private var context: Context? = null
    private var password = ""
    private var name = ""
    private val userName = ""
    private var userCountryCode: Int? = 44
    private var countryCodeKey = "228"
    private var selectedCountry = "United Kingdom"
    private var userMobile = ""
    private var userDeviceID = ""
    private val userEmail = ""
    private var userPicture: String? = null
    private var userDeviceToken: String? = ""
    private var googleName: String? = ""
    private var gooleEmail: String? = ""
    private var googleIdToken: String? = ""
    private var googleImage = ""
    private val user = User()
    private var isFromSocialMedia = false
    private var isFromForgotPassword = false
    private var db: DatabaseHandler? = null
    private var isComment: String? = "false"
    var isValid = true
    val dialogManager = DialogManager()
    var CheckForResendvariable = false;
    var loadingbarforfetchingdata: Dialog? = null

    var send_OTP = ""
    var smsType=""
    private var mAuth: FirebaseAuth? = null
    private var verificationId: String? = null

    private var binding: ActivitySignUp2Binding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this@SignUpActivity, R.layout.activity_sign_up_2)
        context = this@SignUpActivity
        ivLogo = findViewById(R.id.logo)
        etReferralCode = findViewById(R.id.etReferralCode)
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear()

        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();



        try {
            val userDeviceID =
                Settings.Secure.getString(context!!.getContentResolver(),
                    Settings.Secure.ANDROID_ID)
            SharedPreferenceVariable.savePreferences(context,
                KeyConstant.Shar_DeviceID,
                userDeviceID)
            PreferenceHelper.getInstance(context)?.setUserDeviceId(userDeviceID)
        } catch (e: Exception) {
        }
        db = DatabaseHandler(this)
        signUpActivity = this
        val bundles = intent.extras
        if (bundles != null) {
            try {
                if (bundles.getString("isComment") != null) {
                    isComment = bundles.getString("isComment")
                }
                if (bundles.getString("PromoCodeLink") != null) {
                    etReferralCode!!.setText("" + bundles.getString("PromoCodeLink"))
                }
                if (bundles.getString("true") != null && bundles.getString("true") == "yes") {
                    binding!!.signUpFormLayout.visibility = View.GONE
                    binding!!.signUpConfirmLayout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                //  Log.e("Exception", "" + e.toString())
            }
        } else {
            //   Log.e("Exception", "else is working")
        }
        PreferenceHelper.getInstance(context)?.isLogin = false
        PreferenceHelper.getInstance(context)?.loginScreenName = "SignUp"
        initializeView()

        (this.application as DealDioApplication).onComponentStart(this, intent)
        /* val serviceIntent = Intent(this, MyBackgroundService::class.java)
         startService(serviceIntent)

         val periodicWorkRequest = PeriodicWorkRequest.Builder(
             MyPeriodicWorker::class.java, 5, TimeUnit.MINUTES
         )
             .build()

         // Enqueue the work request

         // Enqueue the work request
         WorkManager.getInstance(this).enqueue(periodicWorkRequest)

         Log.e(TAG, "Periodic task scheduled")*/

    }

    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

    private fun initializeView() {
        binding!!.signUpReadMore.setOnClickListener(this)
        binding!!.signUpForgotPassword.setOnClickListener(this)
        binding!!.signUpTerms.setOnClickListener(this)
        binding!!.signUpCountry.isClickable = true
        binding!!.signUpCountry.setOnClickListener(this)
        binding!!.signUpContinue.setOnClickListener(this)
        binding!!.confirmEditBtn.setOnClickListener(this)
        binding!!.confirmOkBtn.setOnClickListener(this)
        binding!!.signUpConfirmLayout.visibility = View.GONE
        binding!!.ivBg.visibility = View.VISIBLE
        binding!!.signUpCountry.text = selectedCountry
        binding!!.signUpCountryCode.text = "+$userCountryCode"
        initGoogleLogin()
    }

    /**
     * Validating form
     */
    private fun submitForm(view: View) {
        password = binding!!.signUpPassword.text.toString()
        userCountryCode =
            Integer.valueOf(binding!!.signUpCountryCode.text.toString().replace("+", ""))
        userMobile = binding!!.signUpPhoneNumber.text.toString()
        name = binding!!.signUpName.text.toString().trim { it <= ' ' }

        /*if (!validatePassword(view)) {
            return;
        }*/if (!validateName(view)) {
            return
        }
        if (!validateCountry(view)) {
            return
        }
        if (!validateContactNo(view)) {
            return
        }
        if (!binding!!.checkTermsCondition.isChecked || binding!!.checkTermsCondition.isChecked) {
            AppUtil.hideSoftKeyboard(this@SignUpActivity)

//            callUserRegisteration(KeyConstant.SIGN_UP_WITH_EMAIL, null, "","")


            ErrorMessage.E("name>>"+ name +"  "+ etReferralCode!!.text.toString())

//            val intent1 =
//                Intent(context, VerifyNumber::class.java)
////            intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
////                responseObj!!.optInt(KeyConstant.KEY_USER_ID))
////            intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
////                responseObj!!.optString(KeyConstant.KEY_USER_KEY))
//            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
//                userCountryCode.toString() + " " + userMobile)
//            intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
//                isFromForgotPassword)
//            intent1.putExtra("isComment", isComment)
//            intent1.putExtra("send_OTP", send_OTP)
//            intent1.putExtra("checkfirstClickForVerification", 1)
//            intent1.putExtra("etReferralCode", etReferralCode!!.text.toString())
//            intent1.putExtra("userName", name)
//            intent1.putExtra("countryCodeKey", countryCodeKey)
//            startActivity(intent1)




//            sendVerificationCode("+$userCountryCode$userMobile", false)


            val intent1 =
                Intent(context, VerifyNumber::class.java)
//            intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
//                responseObj!!.optInt(KeyConstant.KEY_USER_ID))
//            intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
//                responseObj!!.optString(KeyConstant.KEY_USER_KEY))

            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
                userCountryCode.toString() + " " + userMobile)


            intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
                isFromForgotPassword)
            intent1.putExtra("isComment", isComment)
            intent1.putExtra("send_OTP", send_OTP)
            intent1.putExtra("smsType",smsType)
            intent1.putExtra("checkfirstClickForVerification", 1)

            intent1.putExtra("user_country_code",
                userCountryCode.toString())
            intent1.putExtra("userName",name)

            intent1.putExtra("countryCodeKey",
                countryCodeKey)


            startActivity(intent1)


        }
        else {
            messagePopup("Please agree to terms and conditions")
            //TODO here
        }
    }

    private fun validateContactNo(view: View): Boolean {
        return if (userMobile.length >= 8 && userMobile.length <= 11) {
            true
        } else {
            AppUtil.showMsgAlert(view,
                resources.getString(R.string.err_msg_phone_no))
            false
        }
    }

    private fun validateCountry(view: View): Boolean {
        if (userCountryCode.toString().length < 0) {
            AppUtil.showMsgAlert(view, resources.getString(R.string.err_msg_country))
            return false
        }
        return true
    }

    private fun validateName(view: View): Boolean {
        if (name.length <= 0) {
            AppUtil.showMsgAlert(view, resources.getString(R.string.err_msg_name))
            return false
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_up_country -> showDialog(this@SignUpActivity, loadCountryJSONFromAsset())
            R.id.sign_up_continue -> {
                isFromForgotPassword = false
                isFromSocialMedia = false
                AppUtil.hideSoftKeyboard(context)
                if (TextUtils.isEmpty(binding!!.signUpPhoneNumber.text.toString()
                        .trim { it <= ' ' })
                ) {
                    AppUtil.showMsgAlert(v, "Enter Phone Number")
                } else if (!binding!!.cb.isChecked) {
                    AppUtil.showMsgAlert(v, "Please accept Terms & Privacy Policy")
                } else if (userCountryCode != null) {
                    isValid = true
                    submitForm(v)
                } else {
                    AppUtil.showMsgAlert(v, resources.getString(R.string.err_msg_country))
                    //Toast.makeText(getApplicationContext(),"Please select Country",Toast.LENGTH_LONG).show();
                }
            }
            R.id.confirm_edit_btn -> {
                binding!!.signUpConfirmLayout.visibility = View.GONE
                binding!!.ivBg.visibility = View.VISIBLE
                binding!!.signUpPhoneNumber.requestFocus()
                ivLogo!!.visibility = View.VISIBLE
                binding!!.signUpFormLayout.visibility = View.VISIBLE
            }
            R.id.confirm_ok_btn -> {
                AppUtil.hideSoftKeyboard(context)
                dialogManager.stopProcessDialog()
                val intent1 = Intent(context, VerifyOTPActivity::class.java)
                intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
                    responseObj!!.optInt(KeyConstant.KEY_USER_ID))
                intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
                    responseObj!!.optString(KeyConstant.KEY_USER_KEY))
                intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
                    userCountryCode.toString() + " " + userMobile)
                intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
                    isFromForgotPassword)
                intent1.putExtra("isComment", isComment)
                startActivity(intent1)
                finish()
            }
            R.id.sign_up_read_more -> {
               /* dialogManager.stopProcessDialog()
                val intent = Intent(this, TermsAndConditionActivity::class.java)
                startActivity(intent)*/

               startActivity(
                    Intent(this@SignUpActivity, Webview::class.java)
                        .putExtra("url", "https://www.sambalrewards.com/page/privacy-policy")
                        .putExtra("title", "Terms & Privacy Policy")
                        .putExtra("type", "non_direct")
                )
            }
            R.id.sign_up_forgot_password -> {
                isFromForgotPassword = true
                userCountryCode =
                    Integer.valueOf(binding!!.signUpCountryCode.text.toString().replace("+", ""))
                userMobile = binding!!.signUpPhoneNumber.text.toString()
                if (!validateCountry(v)) {
                    return
                }
                if (!validateContactNo(v)) {
                    return
                }
            }
        }
    }

    private fun showDialog(activity: Activity, list: JSONArray?) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val searchText = dialog.findViewById<EditText>(R.id.country_dialog_search_text)
        val searchBtn = dialog.findViewById<ImageButton>(R.id.country_dialog_search_btn)
        searchBtn.setOnClickListener { requestFocus(searchText) }
        val listView = dialog.findViewById<ListView>(R.id.country_dialog_list_view)
        val adapter = CountryDialogAdapter(context!!, list!!) { position, jsonArray ->
            try {
                userCountryCode =
                    jsonArray.optJSONObject(position).optString("country_code").toInt()
                countryCodeKey =
                    jsonArray.optJSONObject(position).optString("id").replace("^\\s*".toRegex(), "")
                        .replace("[^a-zA-Z0-9]+".toRegex(), "").toInt().toString() + ""


                ErrorMessage.E("countryCodeKey>>"+ countryCodeKey)
                selectedCountry = jsonArray.optJSONObject(position).optString("country_name")
                binding!!.signUpCountryCode.text = "+$userCountryCode"
                binding!!.signUpCountry.text = selectedCountry
//                getUserParam(KeyConstant.SIGN_UP_WITH_GOOGLE)

                dialog.dismiss()
            } catch (e: Exception) {
            }
        }
        listView.adapter = adapter
        dialog.show()
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l -> }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun loadCountryJSONFromAsset(): JSONArray? {
        var json: String? = null
        var countryArray: JSONArray? = null
        json = try {
            val `is` = application.assets.open("country.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        if (json != null) {
            try {
                countryArray = JSONArray(json)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return countryArray
    }

    private fun getUserParam(userType: Int): User? {
        val user = User()
        userDeviceToken = PreferenceHelper.getInstance(context)?.fcmToken
        if (userDeviceToken != null) {
            // Log.e("device token", userDeviceToken!!)
        }

        ErrorMessage.E("ddddsasdsd>>"+userMobile)
        return if (userType != KeyConstant.SIGN_UP_WITH_GOOGLE) {
            SharedPreferenceVariable.deletePreferenceData(this@SignUpActivity,
                KeyConstant.Shar_Email)
            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            ErrorMessage.E("countryCodeKey1>>"+ countryCodeKey)

            user.name = name
            user.userName = userName
            user.deviceToken = PreferenceHelper.getInstance(context)?.fcmToken
            user.password = password //countryCodeKey
            user.userCountryId = countryCodeKey.toInt()
            user.userCountry = userCountryCode.toString() + ""
            user.userMobile = userMobile
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
                Log.d(TAG, user.toString())
            }
            user
        }
        else {
            userDeviceID = PreferenceHelper.getInstance(context)?.deviceId.toString()
            ErrorMessage.E("countryCodeKey2>>"+ countryCodeKey)
            user.name = googleName
            user.userName = googleName
            user.password = password
            user.userCountryId = userCountryCode
            user.userMobile = userMobile
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
            Log.d(TAG, user.toString())
            user
        }
    }

    private fun initGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result)
        } else if (requestCode == RC_USER_REGISTER) {
            intentToMainActivity()
        } else {
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient!!)
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    private fun handleSignInResult(result: GoogleSignInResult?) {
        //  Log.e("google_login", "handleSignInResult:" + result!!.isSuccess)
        if (result!!.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result!!.signInAccount
            if (acct!!.email != null && !acct.email.equals("null", ignoreCase = true)) {
                gooleEmail = acct.email
                //    Log.e("ema", gooleEmail!!)
            } else {
                gooleEmail = ""
            }
            googleIdToken = if (acct.id != null && !acct.id.equals("null", ignoreCase = true)) {
                acct.id
            } else {
                ""
            }
            //  Log.e("inputToken", googleIdToken!!)
            googleName =
                if (acct.displayName != null && !acct.displayName.equals("null",
                        ignoreCase = true)
                ) {
                    acct.displayName
                } else {
                    ""
                }
            val uri = acct.photoUrl
            //   Log.e("image uri", acct.photoUrl.toString() + "")
            if (acct.photoUrl != null) {
                googleImage = acct.photoUrl.toString() + ""
                if (googleImage.equals("null", ignoreCase = true)) {
                    googleImage = ""
                }
            } else {
                googleImage = ""
            }

            //callUserRegisteration(KeyConstant.SIGN_UP_WITH_GOOGLE, googleImage, googleIdToken);


            //getUserParam(KeyConstant.SIGN_UP_WITH_GOOGLE);
            doSocialLogin(googleIdToken, googleImage, getUserParam(KeyConstant.SIGN_UP_WITH_GOOGLE))
        } else {
        }
    }

    public override fun onStart() {
        super.onStart()
        Log.d("google", "on start")
    }

    override fun onResume() {
        super.onResume()
        //ProgressDialogUtils.hideProgressDialog();
    }

    private fun callUserRegisteration(userType: Int, userPicture: String?, socialIdToken: String, verificationId : String) {
//        val dialogManager = DialogManager()


        dialogManager.showProcessDialog(this, "", false, null)
        // Log.e("USeeeee", user.email + "")
        CheckRegistrationTask(context,
            userPicture,
            etReferralCode!!.text.toString().trim { it <= ' ' },
            getUserParam(userType),
            socialIdToken,
            "",
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
                                            dialogManager.stopProcessDialog()
                                            binding!!.confirmNumber.text =
                                                "+$userCountryCode $userMobile"
                                            AppUtil.hideSoftKeyboard(context)
                                            dialogManager.stopProcessDialog()

//                                            sendVerificationCode("+$userCountryCode$userMobile", false)

                                            val intent1 =
                                                Intent(context, VerifyNumber::class.java)
                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
                                                responseObj!!.optInt(KeyConstant.KEY_USER_ID))
                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
                                                responseObj!!.optString(KeyConstant.KEY_USER_KEY))
                                            intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
                                                userCountryCode.toString() + " " + userMobile)


                                            intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
                                                isFromForgotPassword)
                                            intent1.putExtra("isComment", isComment)
                                            intent1.putExtra("send_OTP", send_OTP)
                                            intent1.putExtra("smsType",smsType)
                                            intent1.putExtra("checkfirstClickForVerification", 1)
                                            intent1.putExtra("userName",name)

                                            intent1.putExtra("user_country_code",
                                                userCountryCode.toString())
                                            startActivity(intent1)

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
                                    SharedPreferenceVariable.deletePreferenceData(this@SignUpActivity,
                                        KeyConstant.Shar_Email)
                                    SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                        KeyConstant.Shar_Name,
                                        responseObj!!.optString("userName"))
                                    SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                        KeyConstant.KEY_USER_ID,
                                        responseObj!!.optString("userId"))
                                    SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                        KeyConstant.Shar_Photo,
                                        responseObj!!.optString("userPhoto"))
                                    SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                        KeyConstant.Shar_Email,
                                        responseObj!!.optString("userEmail"))
                                    SharedPreferenceVariable.savePreferences(this@SignUpActivity,
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
                                else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                            KeyConstant.KEY_RESULT), ignoreCase = true)
                                    ) {
                                        dialogManager.stopProcessDialog()
                                        AppUtil.showMsgAlert(binding!!.signUpPassword,
                                            resp.optString(KeyConstant.KEY_MESSAGE))
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
                        } catch (e: Exception) {
                            ErrorMessage.E("Exception >>>$e")
                            dialogManager.stopProcessDialog()
                            e.printStackTrace()
                        }
                        dialogManager.stopProcessDialog()
                        //ProgressDialogUtils.hideProgressDialog();
                    }
                }

                override fun setException(e: String?) {}
            }).execute()


    }

    @Throws(JSONException::class)
    private fun setUserOnLoginBySocialMedia(resp: JSONObject) {

        //AppUtil.updateFavoriteAgent(agentIds, binding.signUpReadMore, context);
        Log.e("TRressss", resp.toString())
        socialMediaUserObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
        Log.e("resssss", socialMediaUserObj!!.optString("userName"))
        user.userId = socialMediaUserObj!!.optInt(KeyConstant.KEY_USER_ID)
        user.userKey = socialMediaUserObj!!.optString(KeyConstant.KEY_USER_KEY)
        user.userType = socialMediaUserObj!!.optInt(KeyConstant.KEY_USER_TYPE)
        socialMediaUserObj!!.put(KeyConstant.KEY_NAME, user.name)

        //socialMediaUserObj.put(KeyConstant.KEY_EMAIL, user.getEmail());
        socialMediaUserObj!!.put(KeyConstant.KEY_POST_EMAIL, user.email)
        socialMediaUserObj!!.put(KeyConstant.KEY_USER_FILE, user.userPhoto)
        SharedPreferenceVariable.savePreferences(this@SignUpActivity,
            KeyConstant.Shar_Name,
            socialMediaUserObj!!.optString("userName"))
        SharedPreferenceVariable.savePreferences(this@SignUpActivity,
            KeyConstant.KEY_USER_ID,
            socialMediaUserObj!!.optString("userId"))
        SharedPreferenceVariable.savePreferences(this@SignUpActivity,
            KeyConstant.Shar_Photo,
            socialMediaUserObj!!.optString("userPhoto"))
        SharedPreferenceVariable.savePreferences(this@SignUpActivity,
            KeyConstant.Shar_Email,
            socialMediaUserObj!!.optString("userEmail"))
        SharedPreferenceVariable.savePreferences(this@SignUpActivity,
            KeyConstant.KEY_CODE,
            socialMediaUserObj!!.optString("userQRCode"))
        SharedPreferenceVariable.deletePreferenceData(this@SignUpActivity, KeyConstant.Shar_Phone)
        runOnUiThread {
            PreferenceHelper.getInstance(context)?.isLogin = true
            dialogManager.stopProcessDialog()
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("deals", "")
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed() // optional depending on your needs
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

    private fun messagePopup(msg: String) {

        val dialog1 = Dialog(this)
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
        btnNo.visibility = View.GONE
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Ok"

        dialog1.show()
        try {
            btnOk.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    private fun doSocialLogin(socialIdToken: String?, userPicture: String?, userParam: User?) {
//        val dialogManager = DialogManager()
        dialogManager.showProcessDialog(this, "", false, null)
        val multipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(Method.POST,
            UrlConstant.BASE_URL + UrlConstant.URL_CHECK_REGISTER,
            Response.Listener { responseStr ->
                try {
                    if (responseStr.statusCode == 200) {
                        val resultResponse = String(responseStr.data)
                        Log.e("SOcilLogin", resultResponse)
                        val resp = JSONObject(resultResponse)
                        val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                        Log.e("Error", errorType)
                        //String message = resp.optString(KeyConstant.KEY_MESSAGE);
                        responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                        val accessToken = responseObj!!.optString(KeyConstant.KEY_ACCESS_TOKEN)
                        val refreshToken = responseObj!!.optString(KeyConstant.KEY_REFRESH_TOKEN)
                        PreferenceHelper.getInstance(context)?.accessToken = accessToken
                        PreferenceHelper.getInstance(context)?.refreshToken = refreshToken
                        dialogManager.stopProcessDialog()
                        PreferenceHelper.getInstance(context)?.setUserDetail(responseObj.toString())
                        if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                ignoreCase = true)
                        ) {
                            SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                KeyConstant.Shar_Name,
                                responseObj!!.optString("userName"))
                            SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                KeyConstant.KEY_USER_ID,
                                responseObj!!.optString("userId"))
                            SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                KeyConstant.Shar_Photo,
                                responseObj!!.optString("userPhoto"))
                            SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                KeyConstant.Shar_Email,
                                responseObj!!.optString("userEmail"))
                            SharedPreferenceVariable.savePreferences(this@SignUpActivity,
                                KeyConstant.KEY_CODE,
                                responseObj!!.optString("userQRCode"))
                            intentToMainActivity()
                        } else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_201,
                                ignoreCase = true)
                        ) {
                            setUserOnLoginBySocialMedia(resp)
                            return@Listener
                        } else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202,
                                ignoreCase = true)
                        ) {
                            dialogManager.stopProcessDialog()
                            startActivity(Intent(this@SignUpActivity, VerifyOTPActivity::class.java)
                                .putExtra("userId", "")
                                .putExtra("userKey", "")
                                .putExtra("isFromForgotPassword", "")
                                .putExtra("userMobile", resp.optString("mobile"))
                                .putExtra("isComment", "false"))
                            return@Listener
                        } else if (errorType.equals("203", ignoreCase = true)) {
                            AppUtil.showMsgAlert(binding!!.signUpPassword,
                                resp.optString(KeyConstant.KEY_MESSAGE))
                            PreferenceHelper.getInstance(context)?.accessToken =
                                responseObj!!.optString("access_token")
                            dialogManager.stopProcessDialog()
                            startActivity(Intent(context, SocialNumberVerification::class.java)
                                .putExtra("comment", "false")
                                .putExtra("userId", "")
                                .putExtra("userMobile", "")
                                .putExtra("isFromForgotPassword", "false")
                                .putExtra("referralcode",
                                    etReferralCode!!.text.toString().trim { it <= ' ' }))
                            return@Listener
                        }
                    }
                } catch (e: JSONException) {
                    dialogManager.stopProcessDialog()
                    e.printStackTrace()
                }
                dialogManager.stopProcessDialog()
                //ProgressDialogUtils.hideProgressDialog();
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                try {
                    val networkResponse = error.networkResponse
                    Log.e("Ress", networkResponse!!.statusCode.toString() + "")
                    if (networkResponse != null && networkResponse.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val ob = JSONObject(jsonError)
                            AppUtil.showMsgAlert(binding!!.signUpPassword, ob.optString("message"))
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Reeeee", e.toString())
                }
            }) {
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params[KeyConstant.KEY_SOCIAL_LOGIN_INPUT_TOKEN] = socialIdToken!!
                params[KeyConstant.KEY_GRANT_TYPE] = KeyConstant.KEY_GRANT_TYPE_VALUE
                params[KeyConstant.KEY_CLIENT_ID] = KeyConstant.KEY_CLIENT_ID_VALUE
                params[KeyConstant.KEY_CLIENT_SEC] = KeyConstant.KEY_CLIENT_SEC_VALUE
                params[KeyConstant.KEY_USER_COUNTRY] = userParam!!.userCountryId.toString() + ""
                params[KeyConstant.KEY_USER_MOBILE] = userParam.userMobile
                params[KeyConstant.KEY_DEVICE_ID] = userParam.deviceID
                params[KeyConstant.KEY_MOBILE_TYPE] = userParam.mobileType
                params[KeyConstant.KEY_USER_TYPE] = userParam.userType.toString() + ""
                params[KeyConstant.KEY_DEVICE_TOKEN] = userParam.deviceToken
                params[KeyConstant.KEY_DEVICE_MODE] = UrlConstant.DEVICE_DEBUG_MODE.toString() + ""
                params[KeyConstant.KEY_PASSWORD] = userParam.password
                params[KeyConstant.KEY_NAME] = userParam.name
                params[KeyConstant.KEY_USER_NAME] = userParam.userName
                params[KeyConstant.KEY_EMAIL] = userParam.email
                params[KeyConstant.KEY_POST_EMAIL] = userParam.email
                params["referalCode"] = etReferralCode!!.text.toString().trim { it <= ' ' }
                Log.e("Params", params.toString() + "")
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }

            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                try {
                    if (!TextUtils.isEmpty(userPicture) || userPicture != null) {
                        val bitmap = arrayOfNulls<Bitmap>(1)
                        Glide.with(applicationContext)
                            .asBitmap().load(userParam!!.userPhoto).skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(object : SimpleTarget<Bitmap?>(100, 100) {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap?>?,
                                ) {
                                    bitmap[0] = resource
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                                override fun onLoadStarted(placeholder: Drawable?) {
                                    super.onLoadStarted(placeholder)
                                }
                            })
                        /*  Bitmap bitmap = Glide.
                                with(context).
                                asBitmap()
                                .load(userParam.getUserPhoto())
                                .into(100, 100)// Width and height
                                .get();*/params["userfile"] = DataPart("userfile.jpg",
                            VolleyAppHelper.getFileFromBitmap(this@SignUpActivity.baseContext,
                                bitmap[0]),
                            "image/jpg")
                    }
                } catch (e: Exception) {
                }
                return params
            }
        }
        multipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        VolleySingleton.getInstance(this@SignUpActivity.baseContext)
            .addToRequestQueue(multipartRequest)
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

    companion object {
        private const val TAG = "SignUpActivity"
        private const val RC_GOOGLE_SIGN_IN = 100
        private var mGoogleApiClient: GoogleApiClient? = null
        private var signUpActivity: SignUpActivity? = null
    }


    fun sendVerificationCode(
        number: String,
        checkForResendvariable: Boolean,
    ) {


        if(loadingbarforfetchingdata!=null){
            loadingbarforfetchingdata = ErrorMessage.initProgressDialog(context)
            loadingbarforfetchingdata!!.show()
        }

        val stringWithoutSpaces = "+${number.replace("\\s".toRegex(), "")}"


        Log.d("send_OTP", "${stringWithoutSpaces}     ${number.toString() + " "}")

        CheckForResendvariable = checkForResendvariable;

        val options = mAuth?.let {
            PhoneAuthOptions.newBuilder(it)
                .setPhoneNumber(number) // Phone number to verify
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

                Log.d("send_OTP",
                    "${CheckForResendvariable}     ${userCountryCode.toString() + " " + userMobile}")

                val intent1 =
                    Intent(context, VerifyNumber::class.java)
//                intent1.putExtra(IntentConstant.INTENT_KEY_USER_ID,
//                    responseObj!!.optInt(KeyConstant.KEY_USER_ID))
//                intent1.putExtra(IntentConstant.INTENT_KEY_USER_KEY,
//                    responseObj!!.optString(KeyConstant.KEY_USER_KEY))
                intent1.putExtra(IntentConstant.INTENT_KEY_USER_MOBILE,
                    userCountryCode.toString() + " " + userMobile)
                intent1.putExtra(IntentConstant.INTENT_KEY_IS_FROM_FORGOT_PASSWORD,
                    isFromForgotPassword)
                intent1.putExtra("isComment", isComment)
                intent1.putExtra("send_OTP", s)
                startActivity(intent1)

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

                ErrorMessage.E("FirebaseException   ${e.toString()}     $e.message")

                if (loadingbarforfetchingdata != null) {
                    loadingbarforfetchingdata!!.dismiss()
                }

                Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }


}