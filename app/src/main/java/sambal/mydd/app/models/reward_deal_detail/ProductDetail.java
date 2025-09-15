package sambal.mydd.app.models.reward_deal_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductDetail implements Serializable {

    @SerializedName("dealId")
    @Expose
    private Integer dealId;
    @SerializedName("dealName")
    @Expose
    private String dealName;
    @SerializedName("dealDescription")
    @Expose
    private String dealDescription;
    @SerializedName("agentId")
    @Expose
    private Integer agentId;
    @SerializedName("agentName")
    @Expose
    private String agentName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("dealImage")
    @Expose
    private String dealImage;
    @SerializedName("dealFavourite")
    @Expose
    private Integer dealFavourite;
    @SerializedName("productCurrency")
    @Expose
    private String productCurrency;
    @SerializedName("productDiscountPercentage")
    @Expose
    private String productDiscountPercentage;
    @SerializedName("productPrice")
    @Expose
    private String productPrice;
    @SerializedName("productFinalPrice")
    @Expose
    private String productFinalPrice;
    @SerializedName("offerType")
    @Expose
    private String offerType;
    @SerializedName("offerTypeId")
    @Expose
    private Integer offerTypeId;
    @SerializedName("priceEnabledId")
    @Expose
    private Integer priceEnabledId;
    @SerializedName("discountPriceEnabledId")
    @Expose
    private Integer discountPriceEnabledId;
    @SerializedName("productDiscountPercentageEnabled")
    @Expose
    private Integer productDiscountPercentageEnabled;
    @SerializedName("priceEnabledStatus")
    @Expose
    private String priceEnabledStatus;
    @SerializedName("dealRating")
    @Expose
    private String dealRating;
    @SerializedName("followingStatus")
    @Expose
    private Integer followingStatus;
    @SerializedName("followingCount")
    @Expose
    private Integer followingCount;
    @SerializedName("scanQRButtonEnable")
    @Expose
    private Integer scanQRButtonEnable;
    @SerializedName("generateQRButtonEnable")
    @Expose
    private Integer generateQRButtonEnable;
    @SerializedName("dealRedeemEnable")
    @Expose
    private Integer dealRedeemEnable;
    @SerializedName("dealRedeemLockedAlert")
    @Expose
    private String dealRedeemLockedAlert;
    @SerializedName("dealRedeemAlert")
    @Expose
    private String dealRedeemAlert;
    @SerializedName("orderOnlineLink")
    @Expose
    private String orderOnlineLink;
    @SerializedName("clubPointsValue")
    @Expose
    private String clubPointsValue;
    @SerializedName("dealSavings")
    @Expose
    private String dealSavings;
    @SerializedName("redeemButtonStatus")
    @Expose
    private Integer redeemButtonStatus;
    @SerializedName("redeemButtonOffer1")
    @Expose
    private String redeemButtonOffer1;
    @SerializedName("redeemButtonOffer2")
    @Expose
    private String redeemButtonOffer2;
    @SerializedName("redeemButtonOffer1QRuuid")
    @Expose
    private String redeemButtonOffer1QRuuid;
    @SerializedName("redeemButtonOffer2QRuuid")
    @Expose
    private String redeemButtonOffer2QRuuid;

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public Integer getDealFavourite() {
        return dealFavourite;
    }

    public void setDealFavourite(Integer dealFavourite) {
        this.dealFavourite = dealFavourite;
    }

    public String getProductCurrency() {
        return productCurrency;
    }

    public void setProductCurrency(String productCurrency) {
        this.productCurrency = productCurrency;
    }

    public String getProductDiscountPercentage() {
        return productDiscountPercentage;
    }

    public void setProductDiscountPercentage(String productDiscountPercentage) {
        this.productDiscountPercentage = productDiscountPercentage;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductFinalPrice() {
        return productFinalPrice;
    }

    public void setProductFinalPrice(String productFinalPrice) {
        this.productFinalPrice = productFinalPrice;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Integer getOfferTypeId() {
        return offerTypeId;
    }

    public void setOfferTypeId(Integer offerTypeId) {
        this.offerTypeId = offerTypeId;
    }

    public Integer getPriceEnabledId() {
        return priceEnabledId;
    }

    public void setPriceEnabledId(Integer priceEnabledId) {
        this.priceEnabledId = priceEnabledId;
    }

    public Integer getDiscountPriceEnabledId() {
        return discountPriceEnabledId;
    }

    public void setDiscountPriceEnabledId(Integer discountPriceEnabledId) {
        this.discountPriceEnabledId = discountPriceEnabledId;
    }

    public Integer getProductDiscountPercentageEnabled() {
        return productDiscountPercentageEnabled;
    }

    public void setProductDiscountPercentageEnabled(Integer productDiscountPercentageEnabled) {
        this.productDiscountPercentageEnabled = productDiscountPercentageEnabled;
    }

    public String getPriceEnabledStatus() {
        return priceEnabledStatus;
    }

    public void setPriceEnabledStatus(String priceEnabledStatus) {
        this.priceEnabledStatus = priceEnabledStatus;
    }

    public String getDealRating() {
        return dealRating;
    }

    public void setDealRating(String dealRating) {
        this.dealRating = dealRating;
    }

    public Integer getFollowingStatus() {
        return followingStatus;
    }

    public void setFollowingStatus(Integer followingStatus) {
        this.followingStatus = followingStatus;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getScanQRButtonEnable() {
        return scanQRButtonEnable;
    }

    public void setScanQRButtonEnable(Integer scanQRButtonEnable) {
        this.scanQRButtonEnable = scanQRButtonEnable;
    }

    public Integer getGenerateQRButtonEnable() {
        return generateQRButtonEnable;
    }

    public void setGenerateQRButtonEnable(Integer generateQRButtonEnable) {
        this.generateQRButtonEnable = generateQRButtonEnable;
    }

    public Integer getDealRedeemEnable() {
        return dealRedeemEnable;
    }

    public void setDealRedeemEnable(Integer dealRedeemEnable) {
        this.dealRedeemEnable = dealRedeemEnable;
    }

    public String getDealRedeemLockedAlert() {
        return dealRedeemLockedAlert;
    }

    public void setDealRedeemLockedAlert(String dealRedeemLockedAlert) {
        this.dealRedeemLockedAlert = dealRedeemLockedAlert;
    }

    public String getDealRedeemAlert() {
        return dealRedeemAlert;
    }

    public void setDealRedeemAlert(String dealRedeemAlert) {
        this.dealRedeemAlert = dealRedeemAlert;
    }

    public String getOrderOnlineLink() {
        return orderOnlineLink;
    }

    public void setOrderOnlineLink(String orderOnlineLink) {
        this.orderOnlineLink = orderOnlineLink;
    }

    public String getClubPointsValue() {
        return clubPointsValue;
    }

    public void setClubPointsValue(String clubPointsValue) {
        this.clubPointsValue = clubPointsValue;
    }

    public String getDealSavings() {
        return dealSavings;
    }

    public void setDealSavings(String dealSavings) {
        this.dealSavings = dealSavings;
    }

    public Integer getRedeemButtonStatus() {
        return redeemButtonStatus;
    }

    public void setRedeemButtonStatus(Integer redeemButtonStatus) {
        this.redeemButtonStatus = redeemButtonStatus;
    }

    public String getRedeemButtonOffer1() {
        return redeemButtonOffer1;
    }

    public void setRedeemButtonOffer1(String redeemButtonOffer1) {
        this.redeemButtonOffer1 = redeemButtonOffer1;
    }

    public String getRedeemButtonOffer2() {
        return redeemButtonOffer2;
    }

    public void setRedeemButtonOffer2(String redeemButtonOffer2) {
        this.redeemButtonOffer2 = redeemButtonOffer2;
    }

    public String getRedeemButtonOffer1QRuuid() {
        return redeemButtonOffer1QRuuid;
    }

    public void setRedeemButtonOffer1QRuuid(String redeemButtonOffer1QRuuid) {
        this.redeemButtonOffer1QRuuid = redeemButtonOffer1QRuuid;
    }

    public String getRedeemButtonOffer2QRuuid() {
        return redeemButtonOffer2QRuuid;
    }

    public void setRedeemButtonOffer2QRuuid(String redeemButtonOffer2QRuuid) {
        this.redeemButtonOffer2QRuuid = redeemButtonOffer2QRuuid;
    }

}
