package sambal.mydd.app.models.MyDeal_Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PromotionDeals implements Serializable {
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productAgentId")
    @Expose
    private Integer productAgentId;
    @SerializedName("productAgentName")
    @Expose
    private String productAgentName;
    @SerializedName("productAgentAddress")
    @Expose
    private String productAgentAddress;
    @SerializedName("productDistance")
    @Expose
    private String productDistance;
    @SerializedName("productOffer")
    @Expose
    private String productOffer;
    @SerializedName("productOfferColor")
    @Expose
    private String productOfferColor;
    @SerializedName("dealExpiredDate")
    @Expose
    private String dealExpiredDate;

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

    public String getProductAgentAddress() {
        return productAgentAddress;
    }

    public void setProductAgentAddress(String productAgentAddress) {
        this.productAgentAddress = productAgentAddress;
    }

    public String getProductDistance() {
        return productDistance;
    }

    public void setProductDistance(String productDistance) {
        this.productDistance = productDistance;
    }

    public String getProductOffer() {
        return productOffer;
    }

    public void setProductOffer(String productOffer) {
        this.productOffer = productOffer;
    }

    public String getProductOfferColor() {
        return productOfferColor;
    }

    public void setProductOfferColor(String productOfferColor) {
        this.productOfferColor = productOfferColor;
    }

    public String getDealExpiredDate() {
        return dealExpiredDate;
    }

    public void setDealExpiredDate(String dealExpiredDate) {
        this.dealExpiredDate = dealExpiredDate;
    }

}
