package sambal.mydd.app.models.MyDeal_Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("topTitleImage")
    @Expose
    private String topTitleImage;
    @SerializedName("topTitle")
    @Expose
    private String topTitle;
    @SerializedName("totalVoucher")
    @Expose
    private String totalVoucher;
    @SerializedName("signupDealsList")
    @Expose
    private List<SignupDeals> signupDealsList = null;
    @SerializedName("dailyDealsList")
    @Expose
    private List<DailyDeals> dailyDealsList = null;
    @SerializedName("latestDealsList")
    @Expose
    private List<DailyDeals> latestDealsList = null;
    @SerializedName("favouriteDealsList")
    @Expose
    private List<FavouriteDeals> favouriteDealsList = null;
    @SerializedName("promotionDealsList")
    @Expose
    private List<PromotionDeals> promotionDealsList = null;

    public String getTopTitleImage() {
        return topTitleImage;
    }

    public void setTopTitleImage(String topTitleImage) {
        this.topTitleImage = topTitleImage;
    }

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public String getTotalVoucher() {
        return totalVoucher;
    }

    public void setTotalVoucher(String totalVoucher) {
        this.totalVoucher = totalVoucher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PromotionDeals> getPromotionDealsList() {
        return promotionDealsList;
    }

    public void setPromotionDealsList(List<PromotionDeals> promotionDealsList) {
        this.promotionDealsList = promotionDealsList;
    }

    public List<SignupDeals> getSignupDealsList() {
        return signupDealsList;
    }

    public void setSignupDealsList(List<SignupDeals> signupDealsList) {
        this.signupDealsList = signupDealsList;
    }

    public List<DailyDeals> getDailyDealsList() {
        return dailyDealsList;
    }

    public void setDailyDealsList(List<DailyDeals> dailyDealsList) {
        this.dailyDealsList = dailyDealsList;
    }

    public List<DailyDeals> getLatestDealsList() {
        return latestDealsList;
    }

    public void setLatestDealsList(List<DailyDeals> latestDealsList) {
        this.latestDealsList = latestDealsList;
    }

    public List<FavouriteDeals> getFavouriteDealsList() {
        return favouriteDealsList;
    }

    public void setFavouriteDealsList(List<FavouriteDeals> favouriteDealsList) {
        this.favouriteDealsList = favouriteDealsList;
    }
}
