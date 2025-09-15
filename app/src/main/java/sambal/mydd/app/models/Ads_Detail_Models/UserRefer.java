package sambal.mydd.app.models.Ads_Detail_Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserRefer implements Serializable {
    @SerializedName("agentId")
    @Expose
    private Integer agentId;
    @SerializedName("agentCompanyName")
    @Expose
    private String agentCompanyName;
    @SerializedName("agentImage")
    @Expose
    private String agentImage;
    @SerializedName("adsTitle")
    @Expose
    private String adsTitle;
    @SerializedName("adsDescription")
    @Expose
    private String adsDescription;
    @SerializedName("adsProfileType")
    @Expose
    private Integer adsProfileType;
    @SerializedName("adsProfileTypeText")
    @Expose
    private String adsProfileTypeText;
    @SerializedName("adsExternalURL")
    @Expose
    private String adsExternalURL;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("typeText")
    @Expose
    private String typeText;
    @SerializedName("adsVideoURL")
    @Expose
    private String adsVideoURL;
    @SerializedName("adsImage")
    @Expose
    private String adsImage;

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentCompanyName() {
        return agentCompanyName;
    }

    public void setAgentCompanyName(String agentCompanyName) {
        this.agentCompanyName = agentCompanyName;
    }

    public String getAdsVideoURL() {
        return adsVideoURL;
    }

    public void setAdsVideoURL(String adsVideoURL) {
        this.adsVideoURL = adsVideoURL;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public String getAdsTitle() {
        return adsTitle;
    }

    public void setAdsTitle(String adsTitle) {
        this.adsTitle = adsTitle;
    }

    public String getAdsDescription() {
        return adsDescription;
    }

    public void setAdsDescription(String adsDescription) {
        this.adsDescription = adsDescription;
    }

    public Integer getAdsProfileType() {
        return adsProfileType;
    }

    public void setAdsProfileType(Integer adsProfileType) {
        this.adsProfileType = adsProfileType;
    }

    public String getAdsProfileTypeText() {
        return adsProfileTypeText;
    }

    public void setAdsProfileTypeText(String adsProfileTypeText) {
        this.adsProfileTypeText = adsProfileTypeText;
    }

    public String getAdsExternalURL() {
        return adsExternalURL;
    }

    public void setAdsExternalURL(String adsExternalURL) {
        this.adsExternalURL = adsExternalURL;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getAdsImage() {
        return adsImage;
    }

    public void setAdsImage(String adsImage) {
        this.adsImage = adsImage;
    }
}
