package sambal.mydd.app.utils

import android.content.Context
import android.content.SharedPreferences
import sambal.mydd.app.beans.User
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.constant.IntentConstant
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException

class PreferenceHelper private constructor() {
    var SETTING_PREFERENCES = "setting_pref"
    private val settingPreferences: SharedPreferences
    private val TAG = PreferenceHelper::class.java.simpleName

    init {
        settingPreferences =
            mContext!!.getSharedPreferences(SETTING_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun setUserDeviceId(deviceid: String?) {
        val editor = settingPreferences.edit()
        editor.putString(IntentConstant.PREF_DEVICE_ID, deviceid)
        editor.commit()
    }

    //user.setUserPhoto(userObj.optString(KeyConstant.KEY_USER_FILE));
    val userDetail: User?
        get() {
            val jsonObject = settingPreferences.getString(IntentConstant.PREF_USER_DETAIL, null)
            val deviceId = deviceId
            try {
                val user = User()
                return if (jsonObject != null) {
                    val userObj = JSONObject(jsonObject)
                    user.userId = userObj.optInt(KeyConstant.KEY_USER_ID)
                    user.name = userObj.optString(KeyConstant.KEY_USER_NAME)
                    user.userName = userObj.optString(KeyConstant.KEY_USER_NAME)
                    user.email = userObj.optString(KeyConstant.KEY_EMAIL)
                    user.userMobile = userObj.optString(KeyConstant.KEY_USER_MOBILE)
                    user.userCountry = userObj.optString(KeyConstant.KEY_USER_COUNTRY)
                    user.referralCode = userObj.optString(KeyConstant.KEY_REFERRAL_CODE)
                    user.userKey = userObj.optString(KeyConstant.KEY_USER_KEY)
                    user.userType = userObj.optInt(KeyConstant.KEY_USER_TYPE)
                    //user.setUserPhoto(userObj.optString(KeyConstant.KEY_USER_FILE));
                    user.userPhoto = userObj.optString(KeyConstant.KEY_USER_PHOTO)
                    user.userCountryId = userObj.optInt(KeyConstant.KEY_USER_COUNTRY_ID)
                    user.userCountryCode = userObj.optInt(KeyConstant.KEY_USER_COUNTRY_CODE)
                    user.userProfileName = userObj.optString(KeyConstant.KEY_USER_PROFILE_NAME)
                    user.userDefaultCurrency = userObj.optString(KeyConstant.KEY_USER_CURRENCY)
                    user.userWalletBalance =
                        userObj.optString(KeyConstant.KEY_WALLET_USER_BALANCE_MINUTES)
                    user.userBannerImage = userObj.optString(KeyConstant.KEY_USER_BANNER_IMAGE)
                    user.deviceID = deviceId
                    user
                } else {
                    null
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

    fun setUserDetail(userDetail: String?) {
        val editor = settingPreferences.edit()
        editor.putString(IntentConstant.PREF_USER_DETAIL, userDetail)
        editor.commit()
    }

    val deviceId: String?
        get() = settingPreferences.getString(IntentConstant.PREF_DEVICE_ID, null)
    var userProfileImage: String?
        get() = settingPreferences.getString(IntentConstant.PREF_USER_PROFILE_IMAGE, null)
        set(image) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_USER_PROFILE_IMAGE, image)
            editor.commit()
        }
    var userBannerImage: String?
        get() = settingPreferences.getString(IntentConstant.PREF_USER_BANNER_IMAGE, null)
        set(image) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_USER_BANNER_IMAGE, image)
            editor.commit()
        }
    var fcmToken: String?
        get() = settingPreferences.getString(IntentConstant.PREF_FCM_TOKEN, "")
        set(image) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_FCM_TOKEN, image)
            editor.commit()
        }
    var isWalkThrough: Boolean
        get() = settingPreferences.getBoolean(IntentConstant.PREF_IS_LOGIN_STATUS, false)
        set(isLogin) {
            val editor = settingPreferences.edit()
            editor.putBoolean(IntentConstant.PREF_IS_LOGIN_STATUS, isLogin)
            editor.commit()
        }
    var isLogin: Boolean
        get() = settingPreferences.getBoolean(IntentConstant.PREF_IS_LOGIN_STATUS, false)
        set(isLogin) {
            val editor = settingPreferences.edit()
            editor.putBoolean(IntentConstant.PREF_IS_LOGIN_STATUS, isLogin)
            editor.commit()
        }
    var openAppFirstTime: Boolean
        get() = settingPreferences.getBoolean(IntentConstant.PREF_IS_OPEN_FIRST_TIME, false)
        set(isOpenFirstTime) {
            val editor = settingPreferences.edit()
            editor.putBoolean(IntentConstant.PREF_IS_OPEN_FIRST_TIME, isOpenFirstTime)
            editor.commit()
        }
    val isCatchupDownloading: Boolean
        get() = settingPreferences.getBoolean(IntentConstant.PREF_ISDOWNLOADING_BEGIN, false)

    fun setisCatchupDownloading(b: Boolean) {
        val editor = settingPreferences.edit()
        editor.putBoolean(IntentConstant.PREF_ISDOWNLOADING_BEGIN, b)
        editor.commit()
    }

    var downloadingCatchupId: String?
        get() = settingPreferences.getString(IntentConstant.PREF_DOWNLOADING_VIDEO_CATCHUP_ID, null)
        set(catchId) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_DOWNLOADING_VIDEO_CATCHUP_ID, catchId)
            editor.commit()
        }
    var catchupDownloadingProgress: Int
        get() = settingPreferences.getInt(IntentConstant.PREF_DOWNLOADING_VIDEO_PROGRESS, 0)
        set(i) {
            val editor = settingPreferences.edit()
            editor.putInt(IntentConstant.PREF_DOWNLOADING_VIDEO_PROGRESS, i)
            editor.commit()
        }
    var downloadVideoId: Long
        get() = settingPreferences.getLong(IntentConstant.PREF_DOWNLOADING_ID, 0)
        set(i) {
            val editor = settingPreferences.edit()
            editor.putLong(IntentConstant.PREF_DOWNLOADING_ID, i)
            editor.commit()
        }
    val id: Int
        get() = settingPreferences.getInt(IntentConstant.PREF_OTHER_USER_ID, 0)

    fun setId(i: String?) {
        val editor = settingPreferences.edit()
        editor.putString(IntentConstant.PREF_OTHER_USER_ID, i)
        editor.commit()
    }

    var track: Int
        get() = settingPreferences.getInt(IntentConstant.PREF_TRACK, 0)
        set(i) {
            val editor = settingPreferences.edit()
            editor.putInt(IntentConstant.PREF_TRACK, i)
            editor.commit()
        }
    var screenName: String?
        get() = settingPreferences.getString(IntentConstant.PREF_SCREEN_NAME, null)
        set(str) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_SCREEN_NAME, str)
            editor.commit()
        }
    var accessToken: String?
        get() = settingPreferences.getString(IntentConstant.PREF_ACCESS_TOKEN, null)

        set(token) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_ACCESS_TOKEN, token)
            editor.commit()
        }
    var refreshToken: String?
        get() = settingPreferences.getString(IntentConstant.PREF_REFRESH_TOKEN, null)
        set(token) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_REFRESH_TOKEN, token)
            editor.commit()
        }
    var loginScreenName: String?
        get() = settingPreferences.getString(IntentConstant.PREF_LOGIN_SCREEN_NAME, null)
        set(str) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_LOGIN_SCREEN_NAME, str)
            editor.commit()
        }
    val logout: Boolean
        get() {
            val editor = settingPreferences.edit()
            editor.clear().commit()
            return true
        }
    var lat: String?
        get() = settingPreferences.getString(IntentConstant.PREF_LATITUDE, null)
        set(lat) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_LATITUDE, lat)
            editor.commit()
        }
    var lng: String?
        get() = settingPreferences.getString(IntentConstant.PREF_LONGITUDE, null)
        set(lng) {
            val editor = settingPreferences.edit()
            editor.putString(IntentConstant.PREF_LONGITUDE, lng)
            editor.commit()
        }

    companion object {
        private var mContext: Context? = null
        private var _instance: PreferenceHelper? = null
        @JvmStatic
        fun getInstance(context: Context?): PreferenceHelper? {
            mContext = context
            if (_instance == null) {
                _instance = PreferenceHelper()
            }
            return _instance
        }
    }
}