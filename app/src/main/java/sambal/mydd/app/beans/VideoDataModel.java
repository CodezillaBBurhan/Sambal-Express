package sambal.mydd.app.beans;

/**
 * Created by codezilla-11 on 6/4/18.
 */

public class VideoDataModel {

    String bannerImageUrl, bannerVideoUrl, bannerType, bannerVideoType, youtubeVideoId;

    public VideoDataModel() {
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getBannerVideoUrl() {
        return bannerVideoUrl;
    }

    public void setBannerVideoUrl(String bannerVideoUrl) {
        this.bannerVideoUrl = bannerVideoUrl;
    }

    public String getBannerType() {
        return bannerType;
    }

    public void setBannerType(String bannerType) {
        this.bannerType = bannerType;
    }

    public String getBannerVideoType() {
        return bannerVideoType;
    }

    public void setBannerVideoType(String bannerVideoType) {
        this.bannerVideoType = bannerVideoType;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }
}
