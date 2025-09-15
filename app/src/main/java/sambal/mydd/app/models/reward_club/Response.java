package sambal.mydd.app.models.reward_club;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("clubUserEarnedPoints")
    @Expose
    private String clubUserEarnedPoints;
    @SerializedName("clubTargetPoints")
    @Expose
    private Integer clubTargetPoints;
    @SerializedName("clubStartPrice")
    @Expose
    private String clubStartPrice;
    @SerializedName("clubEndPrice")
    @Expose
    private String clubEndPrice;
    @SerializedName("clubPriceSplit")
    @Expose
    private Integer clubPriceSplit;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("clubPointsRemarks")
    @Expose
    private String clubPointsRemarks;
    @SerializedName("clubPointsEarnedRemarks")
    @Expose
    private String clubPointsEarnedRemarks;
    @SerializedName("clubPointsPaymentRemarks")
    @Expose
    private String clubPointsPaymentRemarks;
    @SerializedName("clubMembershipStatus")
    @Expose
    private Integer clubMembershipStatus;
    @SerializedName("clubMembershipStatusText")
    @Expose
    private String clubMembershipStatusText;
    @SerializedName("clubMembershipRemainingDays")
    @Expose
    private Integer clubMembershipRemainingDays;
    @SerializedName("dealsList")
    @Expose
    private List<Deals> dealsList;
    @SerializedName("categoryList")
    @Expose
    private List<Category> categoryList;

    public String getClubUserEarnedPoints() {
        return clubUserEarnedPoints;
    }

    public void setClubUserEarnedPoints(String clubUserEarnedPoints) {
        this.clubUserEarnedPoints = clubUserEarnedPoints;
    }

    public Integer getClubTargetPoints() {
        return clubTargetPoints;
    }

    public void setClubTargetPoints(Integer clubTargetPoints) {
        this.clubTargetPoints = clubTargetPoints;
    }

    public String getClubStartPrice() {
        return clubStartPrice;
    }

    public void setClubStartPrice(String clubStartPrice) {
        this.clubStartPrice = clubStartPrice;
    }

    public String getClubEndPrice() {
        return clubEndPrice;
    }

    public void setClubEndPrice(String clubEndPrice) {
        this.clubEndPrice = clubEndPrice;
    }

    public Integer getClubPriceSplit() {
        return clubPriceSplit;
    }

    public void setClubPriceSplit(Integer clubPriceSplit) {
        this.clubPriceSplit = clubPriceSplit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getClubPointsRemarks() {
        return clubPointsRemarks;
    }

    public void setClubPointsRemarks(String clubPointsRemarks) {
        this.clubPointsRemarks = clubPointsRemarks;
    }

    public String getClubPointsEarnedRemarks() {
        return clubPointsEarnedRemarks;
    }

    public void setClubPointsEarnedRemarks(String clubPointsEarnedRemarks) {
        this.clubPointsEarnedRemarks = clubPointsEarnedRemarks;
    }

    public String getClubPointsPaymentRemarks() {
        return clubPointsPaymentRemarks;
    }

    public void setClubPointsPaymentRemarks(String clubPointsPaymentRemarks) {
        this.clubPointsPaymentRemarks = clubPointsPaymentRemarks;
    }

    public Integer getClubMembershipStatus() {
        return clubMembershipStatus;
    }

    public void setClubMembershipStatus(Integer clubMembershipStatus) {
        this.clubMembershipStatus = clubMembershipStatus;
    }

    public String getClubMembershipStatusText() {
        return clubMembershipStatusText;
    }

    public void setClubMembershipStatusText(String clubMembershipStatusText) {
        this.clubMembershipStatusText = clubMembershipStatusText;
    }

    public Integer getClubMembershipRemainingDays() {
        return clubMembershipRemainingDays;
    }

    public void setClubMembershipRemainingDays(Integer clubMembershipRemainingDays) {
        this.clubMembershipRemainingDays = clubMembershipRemainingDays;
    }

    public List<Deals> getDealsList() {
        return dealsList;
    }

    public void setDealsList(List<Deals> dealsList) {
        this.dealsList = dealsList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
