package sambal.mydd.app.constant

interface UrlConstant {
    companion object {
        const val GET = 1
        const val POST = 2
        const val DELETE = 3
        const val PUT = 4
        const val CONNECTION_TIME_OUT = 30
        const val SOCKET_TIME_OUT = 35

        //TODO Live

        //        Live Url
//        const val BASE_URL = "https://www.dealdio.com/ddapp_v2/"

       /* const val BASE_URL = "https://www.dealdio.com/ddapp_v3/"*/
        const val BASE_URL = "https://staging-ddpoints.mydd.app/ddapp_sambal/"

        //Live Base URL
       // const val BASE_URL = "https://www.sambalrewards.com/ddapp_sambal/"



        // staging base url
//        const val BASE_URL = "https://staging-ddpoints.mydd.app/ddapp_v2/"

//        const val BASE_URL = "https://staging-ddpoints.mydd.app/ddapp_v3/"

//        const val BASE_URL = "https://staging-ddpoints.mydd.app/ddapp_v3/"
        

        const val DEVICE_DEBUG_MODE = 1
        const val TWITTER_CONSUMER_KEY = "c6CAMWy4SVIGosGHYJ9TXbkfa"
        const val TWITTER_SECRET_KEY = "CNkyThWynvLPLLbRRZZ5fHYNwPx8AuRV0JtwBK0jatihy5DdiW"
        const val kPubNubPublishKey = "pub-c-299e24d7-efde-4683-ab86-4124b46f31a7"
        const val kPubNubSubscribeKey = "sub-c-4c3eb7e6-8373-11e8-b253-a290d76d122e"
        const val URL_GET_REFRESH_AUTH_TOKEN = "refreshToken"
        const val URL_CHECK_REGISTER = "checkRegisterV1"
        const val URL_UPLOAD_IMAGE_CHAT = "secure/uploadChatImage"
        const val URL_PROFILE_IMAGE = "secure/updateProfileImage"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_X_WWW_VALUE = "application/x-www-form-urlencoded"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_DEVICE_ID = "deviceID"
    }
}