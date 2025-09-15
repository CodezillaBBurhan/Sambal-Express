package sambal.mydd.app.models.notice_board;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NoticeBoard implements Serializable {

    @SerializedName("adsId")
    @Expose
    private Integer adsId;
    @SerializedName("adsTitle")
    @Expose
    private String adsTitle;
    @SerializedName("adsImage")
    @Expose
    private String adsImage;
    @SerializedName("adsDescription")
    @Expose
    private String adsDescription;

    public Integer getAdsId() {
        return adsId;
    }

    public void setAdsId(Integer adsId) {
        this.adsId = adsId;
    }

    public String getAdsTitle() {
        return adsTitle;
    }

    public void setAdsTitle(String adsTitle) {
        this.adsTitle = adsTitle;
    }

    public String getAdsImage() {
        return adsImage;
    }

    public void setAdsImage(String adsImage) {
        this.adsImage = adsImage;
    }

    public String getAdsDescription() {
        return adsDescription;
    }

    public void setAdsDescription(String adsDescription) {
        this.adsDescription = adsDescription;
    }
}
