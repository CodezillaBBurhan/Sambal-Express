package sambal.mydd.app.models.HomePageDeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LatestDeals implements Serializable {
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productDistance")
    @Expose
    private String productDistance;
    @SerializedName("productAgentId")
    @Expose
    private Integer productAgentId;
    @SerializedName("productAgentName")
    @Expose
    private String productAgentName;
    @SerializedName("productAgentImage")
    @Expose
    private String productAgentImage;
    @SerializedName("productLocation")
    @Expose
    private String productLocation;
    @SerializedName("productFavourite")
    @Expose
    private Integer productFavourite;
    @SerializedName("productImage")
    @Expose
    private String productImage;
    @SerializedName("productCurrency")
    @Expose
    private String productCurrency;
    @SerializedName("productDiscountPercentageEnabled")
    @Expose
    private Integer productDiscountPercentageEnabled;
    @SerializedName("productDiscountPercentage")
    @Expose
    private String productDiscountPercentage;
    @SerializedName("productPrice")
    @Expose
    private String productPrice;
    @SerializedName("productFinalPrice")
    @Expose
    private String productFinalPrice;
    @SerializedName("productDDLoyaltyPrice")
    @Expose
    private String productDDLoyaltyPrice;
    @SerializedName("priceEnabledId")
    @Expose
    private Integer priceEnabledId;
    @SerializedName("priceEnabledStatus")
    @Expose
    private String priceEnabledStatus;
    @SerializedName("discountPriceEnabledId")
    @Expose
    private Integer discountPriceEnabledId;
    @SerializedName("discountPriceEnabledStatus")
    @Expose
    private String discountPriceEnabledStatus;
    @SerializedName("offerType")
    @Expose
    private String offerType;
    @SerializedName("offerTypeId")
    @Expose
    private Integer offerTypeId;
    @SerializedName("dealExpiredDate")
    @Expose
    private String dealExpiredDate;
    @SerializedName("dealExclusiveStatus")
    @Expose
    private Integer dealExclusiveStatus;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDistance() {
        return productDistance;
    }

    public void setProductDistance(String productDistance) {
        this.productDistance = productDistance;
    }

    public Integer getProductAgentId() {
        return productAgentId;
    }

    public void setProductAgentId(Integer productAgentId) {
        this.productAgentId = productAgentId;
    }

    public String getProductAgentName() {
        return productAgentName;
    }

    public void setProductAgentName(String productAgentName) {
        this.productAgentName = productAgentName;
    }

    public String getProductAgentImage() {
        return productAgentImage;
    }

    public void setProductAgentImage(String productAgentImage) {
        this.productAgentImage = productAgentImage;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public void setProductLocation(String productLocation) {
        this.productLocation = productLocation;
    }

    public Integer getProductFavourite() {
        return productFavourite;
    }

    public void setProductFavourite(Integer productFavourite) {
        this.productFavourite = productFavourite;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductCurrency() {
        return productCurrency;
    }

    public void setProductCurrency(String productCurrency) {
        this.productCurrency = productCurrency;
    }

    public Integer getProductDiscountPercentageEnabled() {
        return productDiscountPercentageEnabled;
    }

    public void setProductDiscountPercentageEnabled(Integer productDiscountPercentageEnabled) {
        this.productDiscountPercentageEnabled = productDiscountPercentageEnabled;
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

    public String getProductDDLoyaltyPrice() {
        return productDDLoyaltyPrice;
    }

    public void setProductDDLoyaltyPrice(String productDDLoyaltyPrice) {
        this.productDDLoyaltyPrice = productDDLoyaltyPrice;
    }

    public Integer getPriceEnabledId() {
        return priceEnabledId;
    }

    public void setPriceEnabledId(Integer priceEnabledId) {
        this.priceEnabledId = priceEnabledId;
    }

    public String getPriceEnabledStatus() {
        return priceEnabledStatus;
    }

    public void setPriceEnabledStatus(String priceEnabledStatus) {
        this.priceEnabledStatus = priceEnabledStatus;
    }

    public Integer getDiscountPriceEnabledId() {
        return discountPriceEnabledId;
    }

    public void setDiscountPriceEnabledId(Integer discountPriceEnabledId) {
        this.discountPriceEnabledId = discountPriceEnabledId;
    }

    public String getDiscountPriceEnabledStatus() {
        return discountPriceEnabledStatus;
    }

    public void setDiscountPriceEnabledStatus(String discountPriceEnabledStatus) {
        this.discountPriceEnabledStatus = discountPriceEnabledStatus;
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

    public String getDealExpiredDate() {
        return dealExpiredDate;
    }

    public void setDealExpiredDate(String dealExpiredDate) {
        this.dealExpiredDate = dealExpiredDate;
    }

    public Integer getDealExclusiveStatus() {
        return dealExclusiveStatus;
    }

    public void setDealExclusiveStatus(Integer dealExclusiveStatus) {
        this.dealExclusiveStatus = dealExclusiveStatus;
    }
}
