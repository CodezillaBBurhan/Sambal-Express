package sambal.mydd.app.beans;

import java.io.Serializable;

public class AgentDetailsOfferBean implements Serializable {
    String dealId, dealName, dealImage, dealDescription, agentId, agentName, agentAddress, agentDistance, agentImage;
    String productCategoryName, dealFavourite, corporateDeal, moreProductLink, moreProductText, agentExternalURLEnable, agentExternalURL;
    String productCurrency, productDiscountPercentage, productPrice, productFinalPrice, offerLimitedEnabled, productTotalReedom;
    String dealStatus, dealStatusId, dealUUID, dealBarCode, dealExpiredDate, dealScanStatus, dealScanStatusId, offerType, offerTypeId;
    String priceEnabledId, dealExclusiveStatus, discountPriceEnabledId, productDiscountPercentageEnabled, priceEnabledStatus, loyaltyEnabled, offerGiftEnabled, offerGiftDescription, dealType, dealBannerEnabled;
    String type;
    public AgentDetailsOfferBean() {
    }

    public AgentDetailsOfferBean(String dealId, String dealName, String dealImage, String dealDescription, String agentId, String agentName, String agentAddress, String agentDistance, String agentImage, String productCategoryName, String dealFavourite, String corporateDeal, String moreProductLink, String moreProductText, String agentExternalURLEnable, String agentExternalURL, String productCurrency, String productDiscountPercentage, String productPrice, String productFinalPrice, String offerLimitedEnabled, String productTotalReedom, String dealStatus, String dealStatusId, String dealUUID, String dealBarCode, String dealExpiredDate, String dealScanStatus, String dealScanStatusId, String offerType, String offerTypeId, String priceEnabledId, String dealExclusiveStatus, String discountPriceEnabledId, String productDiscountPercentageEnabled, String priceEnabledStatus, String loyaltyEnabled, String offerGiftEnabled, String offerGiftDescription, String dealType, String dealBannerEnabled, String type) {
        this.dealId = dealId;
        this.dealName = dealName;
        this.dealImage = dealImage;
        this.dealDescription = dealDescription;
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentAddress = agentAddress;
        this.agentDistance = agentDistance;
        this.agentImage = agentImage;
        this.productCategoryName = productCategoryName;
        this.dealFavourite = dealFavourite;
        this.corporateDeal = corporateDeal;
        this.moreProductLink = moreProductLink;
        this.moreProductText = moreProductText;
        this.agentExternalURLEnable = agentExternalURLEnable;
        this.agentExternalURL = agentExternalURL;
        this.productCurrency = productCurrency;
        this.productDiscountPercentage = productDiscountPercentage;
        this.productPrice = productPrice;
        this.productFinalPrice = productFinalPrice;
        this.offerLimitedEnabled = offerLimitedEnabled;
        this.productTotalReedom = productTotalReedom;
        this.dealStatus = dealStatus;
        this.dealStatusId = dealStatusId;
        this.dealUUID = dealUUID;
        this.dealBarCode = dealBarCode;
        this.dealExpiredDate = dealExpiredDate;
        this.dealScanStatus = dealScanStatus;
        this.dealScanStatusId = dealScanStatusId;
        this.offerType = offerType;
        this.offerTypeId = offerTypeId;
        this.priceEnabledId = priceEnabledId;
        this.dealExclusiveStatus = dealExclusiveStatus;
        this.discountPriceEnabledId = discountPriceEnabledId;
        this.productDiscountPercentageEnabled = productDiscountPercentageEnabled;
        this.priceEnabledStatus = priceEnabledStatus;
        this.loyaltyEnabled = loyaltyEnabled;
        this.offerGiftEnabled = offerGiftEnabled;
        this.offerGiftDescription = offerGiftDescription;
        this.dealType = dealType;
        this.dealBannerEnabled = dealBannerEnabled;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentDistance() {
        return agentDistance;
    }

    public void setAgentDistance(String agentDistance) {
        this.agentDistance = agentDistance;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getDealFavourite() {
        return dealFavourite;
    }

    public void setDealFavourite(String dealFavourite) {
        this.dealFavourite = dealFavourite;
    }

    public String getCorporateDeal() {
        return corporateDeal;
    }

    public void setCorporateDeal(String corporateDeal) {
        this.corporateDeal = corporateDeal;
    }

    public String getMoreProductLink() {
        return moreProductLink;
    }

    public void setMoreProductLink(String moreProductLink) {
        this.moreProductLink = moreProductLink;
    }

    public String getMoreProductText() {
        return moreProductText;
    }

    public void setMoreProductText(String moreProductText) {
        this.moreProductText = moreProductText;
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

    public String getOfferLimitedEnabled() {
        return offerLimitedEnabled;
    }

    public void setOfferLimitedEnabled(String offerLimitedEnabled) {
        this.offerLimitedEnabled = offerLimitedEnabled;
    }

    public String getProductTotalReedom() {
        return productTotalReedom;
    }

    public void setProductTotalReedom(String productTotalReedom) {
        this.productTotalReedom = productTotalReedom;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getDealStatusId() {
        return dealStatusId;
    }

    public void setDealStatusId(String dealStatusId) {
        this.dealStatusId = dealStatusId;
    }

    public String getDealUUID() {
        return dealUUID;
    }

    public void setDealUUID(String dealUUID) {
        this.dealUUID = dealUUID;
    }

    public String getDealBarCode() {
        return dealBarCode;
    }

    public void setDealBarCode(String dealBarCode) {
        this.dealBarCode = dealBarCode;
    }

    public String getDealExpiredDate() {
        return dealExpiredDate;
    }

    public void setDealExpiredDate(String dealExpiredDate) {
        this.dealExpiredDate = dealExpiredDate;
    }

    public String getDealScanStatus() {
        return dealScanStatus;
    }

    public void setDealScanStatus(String dealScanStatus) {
        this.dealScanStatus = dealScanStatus;
    }

    public String getDealScanStatusId() {
        return dealScanStatusId;
    }

    public void setDealScanStatusId(String dealScanStatusId) {
        this.dealScanStatusId = dealScanStatusId;
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

    public String getDealExclusiveStatus() {
        return dealExclusiveStatus;
    }

    public void setDealExclusiveStatus(String dealExclusiveStatus) {
        this.dealExclusiveStatus = dealExclusiveStatus;
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

    public String getLoyaltyEnabled() {
        return loyaltyEnabled;
    }

    public void setLoyaltyEnabled(String loyaltyEnabled) {
        this.loyaltyEnabled = loyaltyEnabled;
    }

    public String getOfferGiftEnabled() {
        return offerGiftEnabled;
    }

    public void setOfferGiftEnabled(String offerGiftEnabled) {
        this.offerGiftEnabled = offerGiftEnabled;
    }

    public String getOfferGiftDescription() {
        return offerGiftDescription;
    }

    public void setOfferGiftDescription(String offerGiftDescription) {
        this.offerGiftDescription = offerGiftDescription;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getDealBannerEnabled() {
        return dealBannerEnabled;
    }

    public void setDealBannerEnabled(String dealBannerEnabled) {
        this.dealBannerEnabled = dealBannerEnabled;
    }
}
