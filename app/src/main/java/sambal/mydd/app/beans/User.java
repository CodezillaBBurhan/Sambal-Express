package sambal.mydd.app.beans;

import android.graphics.Bitmap;

public class User {
    private Integer userId;
    private String name;
    private String userName;
    private Integer userCountryId;
    private String userCountry;
    private String userMobile;
    private String deviceID;
    private String mobileType;
    private String referralCode;
    private Integer userType;
    private String email;
    private String deviceToken;
    private String userKey;
    private String otp;
    private String userPhoto;
    private Integer userCountryCode;
    private Bitmap userProfileBitmap;
    private String userProfileName;
    private String userWalletBalance, userDefaultCurrency, userBannerImage;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserCountryId() {
        return userCountryId;
    }

    public void setUserCountryId(Integer userCountry) {
        this.userCountryId = userCountry;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMobileType() {
        return mobileType;
    }

    public void setMobileType(String mobileType) {
        this.mobileType = mobileType;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Integer getUserCountryCode() {
        return userCountryCode;
    }

    public void setUserCountryCode(Integer userCountryCode) {
        this.userCountryCode = userCountryCode;
    }

    public Bitmap getUserProfileBitmap() {
        return userProfileBitmap;
    }

    public void setUserProfileBitmap(Bitmap userProfileBitmap) {
        this.userProfileBitmap = userProfileBitmap;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public String getUserWalletBalance() {
        return userWalletBalance;
    }

    public void setUserWalletBalance(String userWalletBalance) {
        this.userWalletBalance = userWalletBalance;
    }

    public String getUserDefaultCurrency() {
        return userDefaultCurrency;
    }

    public void setUserDefaultCurrency(String userDefaultCurrency) {
        this.userDefaultCurrency = userDefaultCurrency;
    }

    public String getUserBannerImage() {
        return userBannerImage;
    }

    public void setUserBannerImage(String userBannerImage) {
        this.userBannerImage = userBannerImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", userCountryId=" + userCountryId +
                ", userCountry='" + userCountry + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", mobileType='" + mobileType + '\'' +
                ", referralCode='" + referralCode + '\'' +
                ", userType=" + userType +
                ", userEmail='" + email + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", userKey='" + userKey + '\'' +
                ", otp='" + otp + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", userCountryCode=" + userCountryCode +
                ", userProfileBitmap=" + userProfileBitmap +
                ", userProfileName='" + userProfileName + '\'' +
                ", userCurrency='" + userDefaultCurrency + '\'' +
                ", userBalanceMinutes='" + userWalletBalance + '\'' +
                ", userBannerImage='" + userBannerImage + '\'' +
                '}';
    }
}
