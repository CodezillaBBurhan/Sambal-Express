package sambal.mydd.app.constant

interface IntentConstant {
    companion object {
        const val INTENT_KEY_USER_ID = "userId"
        const val INTENT_KEY_USER_KEY = "userKey"
        const val INTENT_KEY_USER_MOBILE = "userMobile"
        const val INTENT_KEY_IS_FROM_FORGOT_PASSWORD = "isFromForgotPassword"
        const val INTENT_LIST = "list"
        const val PREF_USER_DETAIL = "user_detail"
        const val PREF_DEVICE_ID = "device_id"
        const val PREF_USER_PROFILE_IMAGE = "user_profile_image"
        const val PREF_USER_BANNER_IMAGE = "user_banner_image"
        const val PREF_FCM_TOKEN = "fcm_token"
        const val PREF_ACCESS_TOKEN = "auth_access_token"
        const val PREF_REFRESH_TOKEN = "auth_refresh_token"
        const val PREF_IS_LOGIN_STATUS = "isLoginStatus"
        const val PREF_IS_OPEN_FIRST_TIME = "isOpenFirstTime"
        const val PREF_ISDOWNLOADING_BEGIN = "downloading_begin"
        const val PREF_DOWNLOADING_VIDEO_CATCHUP_ID = "downloading_catchup_id"
        const val PREF_DOWNLOADING_VIDEO_PROGRESS = "downloading_catchup_progress"
        const val PREF_DOWNLOADING_ID = "downloading_video_id"
        const val PREF_OTHER_USER_ID = "other_user_id"
        const val PREF_TRACK = "track"
        const val PREF_SCREEN_NAME = "screen_name"
        const val PREF_LOGIN_SCREEN_NAME = "login_screen_name"
        const val INTENT_KEY_PRODUCT_ID = "product_id"
        const val INTENT_KEY_AGENT_ID = "agentId"
        const val PREF_LATITUDE = "latitude"
        const val PREF_LONGITUDE = "longitude"

        var countGiftVoucher=0
    }
}