package sambal.mydd.app.utils

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface LoadInterface {
    @POST("secure/getGiftCardDetails")
    @FormUrlEncoded
    fun getGiftCardDetails(
        @Field(value = "giftId") giftId: Int,
        @Field(value = "giftVoucherId") giftVoucherId: Int
    ): Call<ResponseBody?>?

    @POST("secure/checkCustomerMobile")
    @FormUrlEncoded
    fun checkCustomerMobile(@Field(value = "userMobile") userMobile: String?): Call<ResponseBody?>?

    @POST("secure/sendGiftCard")
    @FormUrlEncoded
    fun sendGiftCard(
        @Field(value = "userId") userId: Int,
        @Field(value = "giftId") giftId: Int,
        @Field(value = "message") message: String?,
        @Field(value = "price") price: String?
    ): Call<ResponseBody?>?

    @POST("secure/sendGiftInvitation")
    @FormUrlEncoded
    fun sendGiftInvitation(
        @Field(value = "userMobile") userMobile: String?,
        @Field(value = "country") country: String?,
        @Field(value = "price") price: String?,
        @Field(value = "giftId") giftId: Int
    ): Call<ResponseBody?>?

    @POST("secure/giftCardPaymentProcess")
    @FormUrlEncoded
    fun giftCardPaymentProcess(
        @Field(value = "giftId") giftId: Int,
        @Field(value = "giftPrice") giftPrice: String?,
        @Field(value = "stripeToken") stripeToken: String?,
        @Field(value = "paymentId") paymentId: String?,
        @Field(value = "paymentStatus") paymentStatus: String?,
    ): Call<ResponseBody?>?

    @POST("getAgentAwardDetails")
    @FormUrlEncoded
    fun getAgentAwardDetails(
        @Field(value = "agentId") agentId: Int,
        @Field(value = "awardId") awardId: Int
    ): Call<ResponseBody?>?

    @POST("vote")
    @FormUrlEncoded
    fun vote(
        @Field(value = "agentId") agentId: Int,
        @Field(value = "voteSatus") voteSatus: Int,
        @Field(value = "categoryId") categoryId: Int,
        @Field(value = "email") email: String?,
        @Field(value = "awardId") awardId: Int
    ): Call<ResponseBody?>?

    @POST("secure/getNotifications")
    @FormUrlEncoded
    fun getNotifications(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "agentId") agentId: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @Headers("Accept: application/json; Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST
    fun callPostApi(@Url url: String?, @Body body: String?): Call<ResponseBody?>?

    @POST("secure/getAllTenDealsV1")
    @FormUrlEncoded
    fun getAllTenDealsV1(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("searchAllDeals")
    @FormUrlEncoded
    fun searchAllDeals(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "keyword") keyword: String?
    ): Call<ResponseBody?>?

    @POST("secure/logout")
    @FormUrlEncoded
    fun logout(@Field(value = "removeAccount") removeAccount: String?): Call<ResponseBody?>?

    @get:POST("secure/getReferFriend")
    @get:Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    val referFriend: Call<ResponseBody?>?

    @POST("secure/claimMyMoneyV1")
    @FormUrlEncoded
    fun claimMyMoneyV1(
        @Field(value = "voucherUUID") voucherUUID: String?,
        @Field(value = "account_name") account_name: String?,
        @Field(value = "accountNumber") accountNumber: String?,
        @Field(value = "sortcode") sortcode: String?,
        @Field(value = "email") email: String?
    ): Call<ResponseBody?>?

    @POST("secure/applyReferralPromocode")
    @FormUrlEncoded
    fun applyReferralPromocode(@Field(value = "promocode") promocode: String?): Call<ResponseBody?>?

    @POST("secure/removeReferralPromocode")
    @FormUrlEncoded
    fun removeReferralPromocode(@Field(value = "promocode") promocode: String?): Call<ResponseBody?>?

    @POST("secure/getAgentRecommendFriend")
    @FormUrlEncoded
    fun getAgentRecommendFriend(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @POST("secure/updateFavouriteDeal")
    @FormUrlEncoded
    fun updateFavouriteDeal(@Field(value = "dealId") dealId: String?): Call<ResponseBody?>?

    @POST("secure/logout")
    fun logout(): Call<ResponseBody?>?

    @POST("secure/getPromotionDealsList")
    @FormUrlEncoded
    fun getPromotionDealsList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("secure/getAdsDetails")
    @FormUrlEncoded
    fun getAdsDetails(
        @Field(value = "productId") productId: String?,
        @Field(value = "agentId") agentId: String?
    ): Call<ResponseBody?>?

    @POST("secure/getPopupPromotionDealsList")
    @FormUrlEncoded
    fun getPopupPromotionDealsList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/getPopupPromotionDealsListV1")
            /* @FormUrlEncoded*/
    fun getPopupPromotionDealsListV1(
        /* @Field(value = "latitude") latitude: String?,
         @Field(value = "long") longitude: String?*/
    ): Call<ResponseBody?>?

    /////////////////////OKHTTP TO RETROFIT///////////////////////
    @POST("getMyPointListV3")
    @FormUrlEncoded
    fun getMyPointListV3(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "categoryIds") categoryIds: String?,
        @Field(value = "type") type: String?
    ): Call<ResponseBody?>?

    @POST("secure/addMyCharity")
    @FormUrlEncoded
    fun addMyCharity(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "charityId") charityId: String?
    ): Call<ResponseBody?>?

    @POST("secure/getMyProfileV1")
    @FormUrlEncoded
    fun getMyProfileV1(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("updateBeaconToken")
    @FormUrlEncoded
    fun updateBeaconToken(
        @Field(value = "deviceUUID") deviceUUID: String?,
        @Field(value = "major") major: String?,
        @Field(value = "minor") minor: String?,
        @Field(value = "macId") macId: String?
    ): Call<ResponseBody?>?

    @POST("importAllMerchantsBranches")
    @FormUrlEncoded
    fun importAllMerchantsBranches(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "distance") distance: String?
    ): Call<ResponseBody?>?

    @POST("updateDeviceToken")
    @FormUrlEncoded
    fun updateDeviceToken(
        @Field(value = "deviceID") deviceID: String?,
        @Field(value = "mobileType") mobileType: String?,
        @Field(value = "device_token") device_token: String?,
        @Field(value = "device_mode") device_mode: String?,
        @Field(value = "count") count: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "fcm_token") fcm_token: String?
    ): Call<ResponseBody?>?

    @POST("getVisitDeals")
    @FormUrlEncoded
    fun getVisitDeals(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "agentId") agentId: String?
    ): Call<ResponseBody?>?

    @POST("getAgentUserReviews")
    @FormUrlEncoded
    fun getAgentUserReviews(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("secure/deleteProductRating")
    @FormUrlEncoded
    fun deleteProductRating(@Field(value = "commentsId") commentsId: String?): Call<ResponseBody?>?

    @POST("getCategoryDealsList")
    @FormUrlEncoded
    fun getCategoryDealsList(
        @Field(value = "categoryId") categoryId: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("refreshToken")
    @FormUrlEncoded
    fun refreshToken(
        @Field(value = "grant_type") grant_type: String?,
        @Field(value = "client_id") client_id: String?,
        @Field(value = "client_secret") client_secret: String?,
        @Field(value = "refresh_token") refresh_token: String?
    ): Call<ResponseBody?>?

    @POST("secure/updateProfileV1")
    @FormUrlEncoded
    fun updateProfileV1(
        @Field(value = "userMobile") userMobile: String?,
        @Field(value = "name") name: String?,
        @Field(value = "lastName") lastName: String?,
        @Field(value = "email") email: String?,
        @Field(value = "door_number") door_number: String?,
        @Field(value = "street_name") street_name: String?,
        @Field(value = "city") city: String?,
        @Field(value = "postal_code") postal_code: String?,
        @Field(value = "country") country: String?,
        @Field(value = "userfile") userfile: String?,
        @Field(value = "dob") dob: String?
    ): Call<ResponseBody?>?

    @POST("getFreeDeals")
    @FormUrlEncoded
    fun getFreeDeals(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "agentId") agentId: String?
    ): Call<ResponseBody?>?

    @POST("secure/getMyFavouriteDeals")
    @FormUrlEncoded
    fun getMyFavouriteDeals(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("getAgentDetailsV3")
    @FormUrlEncoded
    fun getAgentDetailsV3(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/updateFollowAgent")
    @FormUrlEncoded
    fun updateFollowAgent(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @POST("secure/deleteAgentRating")
    @FormUrlEncoded
    fun deleteAgentRating(@Field(value = "commentsId") commentsId: String?): Call<ResponseBody?>?

    @POST("secure/getCharityList")
    @FormUrlEncoded
    fun getCharityList(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @get:POST("secure/getMyFamilyMember")
    val myFamilyMember: Call<ResponseBody?>?

    @Multipart
    @POST("secure/updateProfileImage")
    fun updateProfileImage(@Part file: MultipartBody.Part?): Call<ResponseBody?>?

    @POST("secure/removeDDcard")
    @FormUrlEncoded
    fun removeDDcard(@Field(value = "userDDCardId") userDDCardId: String?): Call<ResponseBody?>?

    @POST("secure/updateAgentRating")
    @FormUrlEncoded
    fun updateAgentRating(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "rating") rating: String?,
        @Field(value = "comments") comments: String?,
        @Field(value = "commentsId") commentsId: String?
    ): Call<ResponseBody?>?

    @POST("secure/scanQRCode")
    @FormUrlEncoded
    fun scanQRCode(
        @Field(value = "QRCode") QRCode: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/pairQRCode")
    @FormUrlEncoded
    fun pairQRCode(
        @Field(value = "QRCode") QRCode: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/scanAgentUUID")
    @FormUrlEncoded
    fun scanAgentUUID(
        @Field(value = "dealUUID") dealUUID: String?,
        @Field(value = "agentUUID") agentUUID: String?
    ): Call<ResponseBody?>?

    @POST("searchAllV1")
    @FormUrlEncoded
    fun searchAllV1(
        @Field(value = "keyword") keyword: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "distance") distance: String?
    ): Call<ResponseBody?>?

    @POST("getMyGiftVouchersV2")
    @FormUrlEncoded
    fun getMyGiftVouchersV2(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "categoryIds") categoryIds: String?,
        @Field(value = "keyword") keyword: String?
    ): Call<ResponseBody?>?

    @POST("searchMerchantChatList")
    @FormUrlEncoded
    fun searchMerchantChatList(
        @Field(value = "keyword") keyword: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("searchMerchants")
    @FormUrlEncoded
    fun searchMerchants(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/getAgentStoreList")
    @FormUrlEncoded
    fun getAgentStoreList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "type") type: String?
    ): Call<ResponseBody?>?

    @get:POST("getCategoryList")
    val categoryList: Call<ResponseBody?>?

    @POST("getAllDealsV1")
    @FormUrlEncoded
    fun getAllDealsV1(
        @Field(value = "agentIds") agentIds: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "categoryIds") categoryIds: String?,
        @Field(value = "dealType") dealType: String?
    ): Call<ResponseBody?>?

    @POST("getProductDetailsV1")
    @FormUrlEncoded
    fun getProductDetailsV1(
        @Field(value = "productId") productId: String?,
        @Field(value = "agentId") agentId: String?,
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?
    ): Call<ResponseBody?>?

    @POST("secure/applyPromoCode")
    @FormUrlEncoded
    fun applyPromoCode(@Field(value = "promoCode") promoCode: String?): Call<ResponseBody?>?

    @POST("secure/acceptFamilyMember")
    @FormUrlEncoded
    fun acceptFamilyMember(@Field(value = "familyHeadId") familyHeadId: String?): Call<ResponseBody?>?

    @POST("secure/cancelFamilyMember")
    @FormUrlEncoded
    fun cancelFamilyMember(@Field(value = "familyHeadId") familyHeadId: String?): Call<ResponseBody?>?

    @POST("secure/checkFamilyMember")
    @FormUrlEncoded
    fun checkFamilyMember(@Field(value = "userMobile") userMobile: String?): Call<ResponseBody?>?

    @POST("secure/addFamilyMember")
    @FormUrlEncoded
    fun addFamilyMember(@Field(value = "userId") userId: String?): Call<ResponseBody?>?

    @POST("secure/removeFamilyMember")
    @FormUrlEncoded
    fun removeFamilyMember(@Field(value = "userId") userId: String?): Call<ResponseBody?>?

    @POST("secure/exitFamilyMember")
    fun exitFamilyMember(): Call<ResponseBody?>?

    @POST("secure/existAssignFamilyHead")
    @FormUrlEncoded
    fun existAssignFamilyHead(@Field(value = "userId") userId: String?): Call<ResponseBody?>?

    @POST("getAllMerchantsChatList")
    @FormUrlEncoded
    fun getAllMerchantsChatList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("getAllMerchantsFavoriteChatList")
    @FormUrlEncoded
    fun getAllMerchantsFavoriteChatList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("secure/reportBusinessChatList")
    @FormUrlEncoded
    fun reportBusinessChatList(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @POST("secure/reportBusinessChat")
    @FormUrlEncoded
    fun reportBusinessChat(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "message") message: String?,
        @Field(value = "imageURL") imageURL: String?,
        @Field(value = "timeToken") timeToken: String?,
        @Field(value = "reportUserId") reportUserId: String?,
        @Field(value = "comments") comments: String?,
        @Field(value = "endTimeToken") endTimeToken: String?
    ): Call<ResponseBody?>?

    @POST("secure/deleteBusinessChatReport")
    @FormUrlEncoded
    fun deleteBusinessChatReport(
        @Field(value = "reportId") reportId: String?,
        @Field(value = "timeToken") timeToken: String?,
        @Field(value = "agentId") agentId: String?
    ): Call<ResponseBody?>?

    @POST("secure/sendChatNotification")
    @FormUrlEncoded
    fun sendChatNotification(
        @Field(value = "channelId") channelId: String?,
        @Field(value = "agentId") agentId: String?,
        @Field(value = "message") message: String?,
        @Field(value = "notificationStatus") notificationStatus: String?
    ): Call<ResponseBody?>?


    @POST("secure/exitChatAgent")
    @FormUrlEncoded
    fun exitChatAgent(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "timeToken") timeToken: String?,
        @Field(value = "channelId") channelId: String?
    ): Call<ResponseBody?>?

    @POST("secure/getMyPointsHistory")
    @FormUrlEncoded
    fun getMyPointsHistory(
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("secure/getMyFollowMerchantsList")
    @FormUrlEncoded
    fun getMyFollowMerchantsList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "distance") distance: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "categoryId") categoryId: String?
    ): Call<ResponseBody?>?

    @POST("secure/updateUnFollowAgent")
    @FormUrlEncoded
    fun updateUnFollowAgent(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    /*@Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")*/
    @POST("getAllMerchantsList")
    @FormUrlEncoded
    fun getAllMerchantsList(
        @Field(value = "latitude") latitude: String?,
        @Field(value = "long") longitude: String?,
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "categoryId") categoryId: String?
    ): Call<ResponseBody?>?

    @POST("secure/getNotificationsDetails")
    @FormUrlEncoded
    fun getNotificationsDetails(@Field(value = "notificationId") notificationId: String?): Call<ResponseBody?>?

    @POST("secure/updateUserMobileV1")
    @FormUrlEncoded
    fun updateUserMobileV1(
        @Field(value = "userMobile") userMobile: String?,
        @Field(value = "referralCode") referralCode: String?,
        @Field(value = "country") country: String?
    ): Call<ResponseBody?>?

    @POST("secure/numberVerification")
    @FormUrlEncoded
    fun numberVerification(@Field(value = "verificationCode") verificationCode: String?): Call<ResponseBody>

    @POST("secure/reSendVerificationCode")
    fun reSendVerificationCode(): Call<ResponseBody?>?

    @POST("secure/getVoucherRedeem")
    @FormUrlEncoded
    fun getVoucherRedeem(
        @Field(value = "voucherIds") voucherIds: String?,
        @Field(value = "agentId") agentId: String?,
        @Field(value = "price") price: String?
    ): Call<ResponseBody?>?

    @POST("secure/updateFavouriteChatAgent")
    @FormUrlEncoded
    fun updateFavouriteChatAgent(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @POST("secure/updateDDPoints")
    @FormUrlEncoded
    fun updateDDPoints(@Field(value = "points") points: String?): Call<ResponseBody?>?

    @POST("secure/newStore")
    @FormUrlEncoded
    fun newStore(
        @Field(value = "businessName") businessName: String?,
        @Field(value = "businessType") businessType: String?,
        @Field(value = "doorNo") doorNo: String?,
        @Field(value = "streetName") streetName: String?,
        @Field(value = "city") city: String?,
        @Field(value = "postCode") postCode: String?,
        @Field(value = "contactName") contactName: String?,
        @Field(value = "contactNumber") contactNumber: String?,
        @Field(value = "comments") comments: String?
    ): Call<ResponseBody?>?

    @POST("secure/getAgentActivities")
    @FormUrlEncoded
    fun getAgentActivities(@Field(value = "agentId") agentId: String?): Call<ResponseBody?>?

    @POST("secure/getAgentActivitiesDetails")
    @FormUrlEncoded
    fun getAgentActivitiesDetails(@Field(value = "referId") referId: String?): Call<ResponseBody?>?

    @POST("secure/shareVoucher")
    @FormUrlEncoded
    fun shareVoucher(@Field(value = "voucherId") voucherId: String?): Call<ResponseBody?>?

    @POST("getBeaconNotification")
    @FormUrlEncoded
    fun getBeaconNotification(@Field(value = "beaconUUID") beaconUUID: String?): Call<ResponseBody?>?


    @POST("secure/getGiftCardRequest")
    fun getGiftCardRequest(): Call<ResponseBody?>?

    @POST("secure/acceptGiftCardRequest")
    @FormUrlEncoded
    fun acceptGiftCardRequest(@Field(value = "giftId") giftId: String?): Call<ResponseBody?>?

    @POST("secure/rejectGiftCardRequest")
    @FormUrlEncoded
    fun rejectGiftCardRequest(@Field(value = "giftId") giftId: String?): Call<ResponseBody?>?


    @POST("secure/getRewardClub")
    @FormUrlEncoded
    fun getRewardClub(@Field(value = "categoryId") categoryId: String?): Call<ResponseBody?>?

    @POST("secure/getEarnClubPointsEventList")
    @FormUrlEncoded
    fun getEarnClubPointsEventList(
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?
    ): Call<ResponseBody?>?

    @POST("secure/getEarnClubPointsEventDetails")
    @FormUrlEncoded
    fun getEarnClubPointsEventDetails(
        @Field(value = "offset") offset: String?,
        @Field(value = "count") count: String?,
        @Field(value = "eventId") eventId: String?
    ): Call<ResponseBody?>?


    @POST("secure/getNoticeBoardList")
    fun getNoticeBoardList(): Call<ResponseBody?>?

    @POST("secure/subscribeRewardClubMembership")
    fun getSubscribeRewardClubMembership(): Call<ResponseBody?>?

    @POST("secure/subscribePaymentProcess")
    @FormUrlEncoded
    fun subscribePaymentProcess(
        @Field(value = "paymentId") paymentId: String?,
        @Field(value = "paymentStatus") paymentStatus: String?
    ): Call<ResponseBody?>?

    @POST("secure/getRewardClubDealDetails")
    @FormUrlEncoded
    fun getRewardClubDealDetails(
        @Field(value = "agentId") agentId: String?,
        @Field(value = "productId") productId: String?
    ): Call<ResponseBody?>?

    @POST("secure/likeEvent")
    @FormUrlEncoded
    fun likeEvent(@Field(value = "eventId") eventId: String?): Call<ResponseBody?>?

    @POST("secure/getEarnClubPointsEventViewed")
    @FormUrlEncoded
    fun getEarnClubPointsEventViewed(@Field(value = "eventId") eventId: String?): Call<ResponseBody?>?

}