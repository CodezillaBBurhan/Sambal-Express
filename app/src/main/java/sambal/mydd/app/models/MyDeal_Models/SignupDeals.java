package sambal.mydd.app.models.MyDeal_Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignupDeals {
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
    @SerializedName("productImage")
    @Expose
    private String productImage;
    @SerializedName("dealImage")
    @Expose
    private String dealImage;
    @SerializedName("dealExpiredDate")
    @Expose
    private String dealExpiredDate;
    @SerializedName("productType")
    @Expose
    private String productType;
    @SerializedName("productTypeColor")
    @Expose
    private String productTypeColor;
    @SerializedName("redeemedText")
    @Expose
    private String redeemedText;
    @SerializedName("productFavourite")
    @Expose
    private Integer productFavourite;

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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getDealExpiredDate() {
        return dealExpiredDate;
    }

    public void setDealExpiredDate(String dealExpiredDate) {
        this.dealExpiredDate = dealExpiredDate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductTypeColor() {
        return productTypeColor;
    }

    public void setProductTypeColor(String productTypeColor) {
        this.productTypeColor = productTypeColor;
    }

    public String getRedeemedText() {
        return redeemedText;
    }

    public void setRedeemedText(String redeemedText) {
        this.redeemedText = redeemedText;
    }

    public Integer getProductFavourite() {
        return productFavourite;
    }

    public void setProductFavourite(Integer productFavourite) {
        this.productFavourite = productFavourite;
    }
}
