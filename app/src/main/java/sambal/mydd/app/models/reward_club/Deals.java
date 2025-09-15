package sambal.mydd.app.models.reward_club;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Deals implements Serializable {
    @SerializedName("dealId")
    @Expose
    private Integer dealId;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("dealName")
    @Expose
    private String dealName;
    @SerializedName("agentCompanyName")
    @Expose
    private String agentCompanyName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("agentId")
    @Expose
    private Integer agentId;
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
    @SerializedName("dealImage")
    @Expose
    private String dealImage;
    @SerializedName("clubPointsValue")
    @Expose
    private String clubPointsValue;
    @SerializedName("dealSavings")
    @Expose
    private String dealSavings;
    @SerializedName("dealRating")
    @Expose
    private String dealRating;

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getAgentCompanyName() {
        return agentCompanyName;
    }

    public void setAgentCompanyName(String agentCompanyName) {
        this.agentCompanyName = agentCompanyName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
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

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
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

    public String getDealRating() {
        return dealRating;
    }

    public void setDealRating(String dealRating) {
        this.dealRating = dealRating;
    }
}
