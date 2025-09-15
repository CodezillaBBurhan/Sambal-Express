package sambal.mydd.app.beans;

import java.io.Serializable;

public class ChatBean implements Serializable {
    String agentId, agentCompanyName, agentAddress, agentImage, agentEmail, agentMobile, agentCountry, agentCountryCode, agentURL, agentExternalURLEnable, agentExternalURL;
    String agentDescription, agentLatitude, agentLongitude, agentDistance, agentFavourite, agentVoucherEnabled, agentEnableDescription, agentAdsEnable, agentRating, agentDealEnabled, dealButtonEnable, moreProduct, moreVouchers, productId, ddPointsEnabled, isAdmin,chatCount,followingStatus;
    String chatTime;
    public ChatBean() {
    }

    public ChatBean(String agentId, String agentCompanyName, String agentAddress, String agentImage, String agentEmail, String agentMobile, String agentCountry, String agentCountryCode, String agentURL, String agentExternalURLEnable, String agentExternalURL, String agentDescription, String agentLatitude, String agentLongitude, String agentDistance, String agentFavourite, String agentVoucherEnabled, String agentEnableDescription, String agentAdsEnable, String agentRating, String agentDealEnabled, String dealButtonEnable, String moreProduct, String moreVouchers, String productId, String ddPointsEnabled, String isAdmin, String chatCount, String chatTime, String followingStatus) {
        this.agentId = agentId;
        this.agentCompanyName = agentCompanyName;
        this.agentAddress = agentAddress;
        this.agentImage = agentImage;
        this.agentEmail = agentEmail;
        this.agentMobile = agentMobile;
        this.agentCountry = agentCountry;
        this.agentCountryCode = agentCountryCode;
        this.agentURL = agentURL;
        this.agentExternalURLEnable = agentExternalURLEnable;
        this.agentExternalURL = agentExternalURL;
        this.agentDescription = agentDescription;
        this.agentLatitude = agentLatitude;
        this.agentLongitude = agentLongitude;
        this.agentDistance = agentDistance;
        this.agentFavourite = agentFavourite;
        this.agentVoucherEnabled = agentVoucherEnabled;
        this.agentEnableDescription = agentEnableDescription;
        this.agentAdsEnable = agentAdsEnable;
        this.agentRating = agentRating;
        this.agentDealEnabled = agentDealEnabled;
        this.dealButtonEnable = dealButtonEnable;
        this.moreProduct = moreProduct;
        this.moreVouchers = moreVouchers;
        this.productId = productId;
        this.ddPointsEnabled = ddPointsEnabled;
        this.isAdmin = isAdmin;
        this.chatCount = chatCount;
        this.chatTime = chatTime;
        this.followingStatus = followingStatus;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentCompanyName() {
        return agentCompanyName;
    }

    public void setAgentCompanyName(String agentCompanyName) {
        this.agentCompanyName = agentCompanyName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }

    public String getAgentCountry() {
        return agentCountry;
    }

    public void setAgentCountry(String agentCountry) {
        this.agentCountry = agentCountry;
    }

    public String getAgentCountryCode() {
        return agentCountryCode;
    }

    public void setAgentCountryCode(String agentCountryCode) {
        this.agentCountryCode = agentCountryCode;
    }

    public String getAgentURL() {
        return agentURL;
    }

    public void setAgentURL(String agentURL) {
        this.agentURL = agentURL;
    }

    public String getAgentExternalURLEnable() {
        return agentExternalURLEnable;
    }

    public void setAgentExternalURLEnable(String agentExternalURLEnable) {
        this.agentExternalURLEnable = agentExternalURLEnable;
    }

    public String getAgentExternalURL() {
        return agentExternalURL;
    }

    public void setAgentExternalURL(String agentExternalURL) {
        this.agentExternalURL = agentExternalURL;
    }

    public String getAgentDescription() {
        return agentDescription;
    }

    public void setAgentDescription(String agentDescription) {
        this.agentDescription = agentDescription;
    }

    public String getAgentLatitude() {
        return agentLatitude;
    }

    public void setAgentLatitude(String agentLatitude) {
        this.agentLatitude = agentLatitude;
    }

    public String getAgentLongitude() {
        return agentLongitude;
    }

    public void setAgentLongitude(String agentLongitude) {
        this.agentLongitude = agentLongitude;
    }

    public String getAgentDistance() {
        return agentDistance;
    }

    public void setAgentDistance(String agentDistance) {
        this.agentDistance = agentDistance;
    }

    public String getAgentFavourite() {
        return agentFavourite;
    }

    public void setAgentFavourite(String agentFavourite) {
        this.agentFavourite = agentFavourite;
    }

    public String getAgentVoucherEnabled() {
        return agentVoucherEnabled;
    }

    public void setAgentVoucherEnabled(String agentVoucherEnabled) {
        this.agentVoucherEnabled = agentVoucherEnabled;
    }

    public String getAgentEnableDescription() {
        return agentEnableDescription;
    }

    public void setAgentEnableDescription(String agentEnableDescription) {
        this.agentEnableDescription = agentEnableDescription;
    }

    public String getAgentAdsEnable() {
        return agentAdsEnable;
    }

    public void setAgentAdsEnable(String agentAdsEnable) {
        this.agentAdsEnable = agentAdsEnable;
    }

    public String getAgentRating() {
        return agentRating;
    }

    public void setAgentRating(String agentRating) {
        this.agentRating = agentRating;
    }

    public String getAgentDealEnabled() {
        return agentDealEnabled;
    }

    public void setAgentDealEnabled(String agentDealEnabled) {
        this.agentDealEnabled = agentDealEnabled;
    }

    public String getDealButtonEnable() {
        return dealButtonEnable;
    }

    public void setDealButtonEnable(String dealButtonEnable) {
        this.dealButtonEnable = dealButtonEnable;
    }

    public String getMoreProduct() {
        return moreProduct;
    }

    public void setMoreProduct(String moreProduct) {
        this.moreProduct = moreProduct;
    }

    public String getMoreVouchers() {
        return moreVouchers;
    }

    public void setMoreVouchers(String moreVouchers) {
        this.moreVouchers = moreVouchers;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDdPointsEnabled() {
        return ddPointsEnabled;
    }

    public void setDdPointsEnabled(String ddPointsEnabled) {
        this.ddPointsEnabled = ddPointsEnabled;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getChatCount() {
        return chatCount;
    }

    public void setChatCount(String chatCount) {
        this.chatCount = chatCount;
    }

    public String getFollowingStatus() {
        return followingStatus;
    }

    public void setFollowingStatus(String followingStatus) {
        this.followingStatus = followingStatus;
    }
}
