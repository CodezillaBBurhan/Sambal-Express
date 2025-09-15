package sambal.mydd.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class StorePointsDealsList implements Parcelable {
    String dealId, productId, dealName, agentId, dealFavourite, productCurrency, productDiscountPercentage, productPrice, productFinalPrice, dealExpiredDate, offerType, offerTypeId, priceEnabledId, discountPriceEnabledId, productDiscountPercentageEnabled, priceEnabledStatus, dealImage, agentName, viewCount;
    String type, dealExclusiveStatus;

    public StorePointsDealsList(String dealId, String productId, String dealName, String agentId, String dealFavourite, String productCurrency, String productDiscountPercentage, String productPrice, String productFinalPrice, String dealExpiredDate, String offerType, String offerTypeId, String priceEnabledId, String discountPriceEnabledId, String productDiscountPercentageEnabled, String priceEnabledStatus, String dealImage, String agentName, String viewCount, String type, String dealExclusiveStatus) {
        this.dealId = dealId;
        this.productId = productId;
        this.dealName = dealName;
        this.agentId = agentId;
        this.dealFavourite = dealFavourite;
        this.productCurrency = productCurrency;
        this.productDiscountPercentage = productDiscountPercentage;
        this.productPrice = productPrice;
        this.productFinalPrice = productFinalPrice;
        this.dealExpiredDate = dealExpiredDate;
        this.offerType = offerType;
        this.offerTypeId = offerTypeId;
        this.priceEnabledId = priceEnabledId;
        this.discountPriceEnabledId = discountPriceEnabledId;
        this.productDiscountPercentageEnabled = productDiscountPercentageEnabled;
        this.priceEnabledStatus = priceEnabledStatus;
        this.dealImage = dealImage;
        this.agentName = agentName;
        this.viewCount = viewCount;
        this.type = type;
        this.dealExclusiveStatus = dealExclusiveStatus;
    }

    public String getDealExclusiveStatus() {
        return dealExclusiveStatus;
    }

    public void setDealExclusiveStatus(String dealExclusiveStatus) {
        this.dealExclusiveStatus = dealExclusiveStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDealFavourite() {
        return dealFavourite;
    }

    public void setDealFavourite(String dealFavourite) {
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

    public String getDealExpiredDate() {
        return dealExpiredDate;
    }

    public void setDealExpiredDate(String dealExpiredDate) {
        this.dealExpiredDate = dealExpiredDate;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getOfferTypeId() {
        return offerTypeId;
    }

    public void setOfferTypeId(String offerTypeId) {
        this.offerTypeId = offerTypeId;
    }

    public String getPriceEnabledId() {
        return priceEnabledId;
    }

    public void setPriceEnabledId(String priceEnabledId) {
        this.priceEnabledId = priceEnabledId;
    }

    public String getDiscountPriceEnabledId() {
        return discountPriceEnabledId;
    }

    public void setDiscountPriceEnabledId(String discountPriceEnabledId) {
        this.discountPriceEnabledId = discountPriceEnabledId;
    }

    public String getProductDiscountPercentageEnabled() {
        return productDiscountPercentageEnabled;
    }

    public void setProductDiscountPercentageEnabled(String productDiscountPercentageEnabled) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
