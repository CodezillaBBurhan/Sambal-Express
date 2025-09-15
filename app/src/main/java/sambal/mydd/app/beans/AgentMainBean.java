package sambal.mydd.app.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class AgentMainBean implements Serializable {
    String agentId, agentCompanyName, agentAddress, distance, agentStandardPointStatus, agentDoublePointStatus, agentBonusPointStatus, agentBonusPoint, donateStatus, charityDonatedText;
    String agentPointStartDate, agentPointEndDate, userEarnedPoints, targetPoints, redeemRemarks, pointRemarks, minspend, agentPointsFAQ;
    String termsAndConditions, charityEnabled, agentRecommendEnabled, agentNotificationCount, agentNotificationEnabled, agentWalletType, agentUserTargetVisitCount, agentUserVisitCount, agentUserTodayVisit, agentUserFreeDealID, agentUserFreeDealName, agentUserFreeDealImage, agentUserDealStatus, agentUserFreeDealStatus, agentUserFreeDealText;
    /*////////////////////////////////////////////////////////////////////////*/



    /*////////////////////////////////////////////////////////////////////////*/
    ArrayList<AgentVoucherListBean> mList = new ArrayList<>();
    ArrayList<StorePointsDealsList> mDealList = new ArrayList<>();
    ArrayList<VisitcolorBean> mVisitcolorList = new ArrayList<>();
    ArrayList<GiftCard> giftCardslist = new ArrayList<>();
    String charityName, charityMemberCount, agentRecommendText;
    String membershipImage, membershipStatus;

    public AgentMainBean(String agentId, String agentCompanyName, String agentAddress, String distance, String agentStandardPointStatus, String agentDoublePointStatus, String agentBonusPointStatus, String agentBonusPoint, String donateStatus, String charityDonatedText, String agentPointStartDate, String agentPointEndDate, String userEarnedPoints, String targetPoints, String redeemRemarks, String pointRemarks, String minspend, String agentPointsFAQ, String termsAndConditions, String charityEnabled, String agentRecommendEnabled, String agentNotificationEnabled, String agentNotificationCount, String agentWalletType, String agentUserTargetVisitCount, String agentUserVisitCount, String agentUserTodayVisit, String agentUserFreeDealID, String agentUserFreeDealName, String agentUserFreeDealImage, String agentUserDealStatus, String agentUserFreeDealStatus, String agentUserFreeDealText, ArrayList<AgentVoucherListBean> mList, ArrayList<StorePointsDealsList> mDealList, ArrayList<VisitcolorBean> mVisitcolorList, String charityName, String charityMemberCount, String membershipStatus, String membershipImage, String agentRecommendText, ArrayList<GiftCard> giftCardslist) {
        this.agentId = agentId;
        this.agentCompanyName = agentCompanyName;
        this.agentAddress = agentAddress;
        this.distance = distance;
        this.agentStandardPointStatus = agentStandardPointStatus;
        this.agentDoublePointStatus = agentDoublePointStatus;
        this.agentBonusPointStatus = agentBonusPointStatus;
        this.agentBonusPoint = agentBonusPoint;
        this.donateStatus = donateStatus;
        this.charityDonatedText = charityDonatedText;
        this.agentPointStartDate = agentPointStartDate;
        this.agentPointEndDate = agentPointEndDate;
        this.userEarnedPoints = userEarnedPoints;
        this.targetPoints = targetPoints;
        this.redeemRemarks = redeemRemarks;
        this.pointRemarks = pointRemarks;
        this.minspend = minspend;
        this.agentPointsFAQ = agentPointsFAQ;
        this.termsAndConditions = termsAndConditions;
        this.charityEnabled = charityEnabled;
        this.agentRecommendEnabled = agentRecommendEnabled;
        this.agentNotificationEnabled = agentNotificationEnabled;
        this.agentWalletType = agentWalletType;
        this.agentUserTargetVisitCount = agentUserTargetVisitCount;
        this.agentUserVisitCount = agentUserVisitCount;
        this.agentUserTodayVisit = agentUserTodayVisit;
        this.agentUserFreeDealID = agentUserFreeDealID;
        this.agentUserFreeDealName = agentUserFreeDealName;
        this.agentUserFreeDealImage = agentUserFreeDealImage;
        this.agentUserDealStatus = agentUserDealStatus;
        this.agentUserFreeDealStatus = agentUserFreeDealStatus;
        this.agentUserFreeDealText = agentUserFreeDealText;
        this.agentNotificationCount = agentNotificationCount;
        this.mList = mList;
        this.mDealList = mDealList;
        this.mVisitcolorList = mVisitcolorList;
        this.charityName = charityName;
        this.charityMemberCount = charityMemberCount;
        this.membershipStatus = membershipStatus;
        this.membershipImage = membershipImage;
        this.agentRecommendText = agentRecommendText;
        this.giftCardslist = giftCardslist;


    }

    public ArrayList<GiftCard> getGiftCardslist() {
        return giftCardslist;
    }

    public void setGiftCardslist(ArrayList<GiftCard> giftCardslist) {
        this.giftCardslist = giftCardslist;
    }

    public String getAgentRecommendText() {
        return agentRecommendText;
    }

    public void setAgentRecommendText(String agentRecommendText) {
        this.agentRecommendText = agentRecommendText;
    }

    public String getMembershipImage() {
        return membershipImage;
    }

    public void setMembershipImage(String membershipImage) {
        this.membershipImage = membershipImage;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public String getCharityMemberCount() {
        return charityMemberCount;
    }

    public void setCharityMemberCount(String charityMemberCount) {
        this.charityMemberCount = charityMemberCount;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentCompanyName() {
        return agentCompanyName;
    }

    public void setAgentCompanyName(String agentCompanyName) {
        this.agentCompanyName = agentCompanyName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAgentStandardPointStatus() {
        return agentStandardPointStatus;
    }

    public void setAgentStandardPointStatus(String agentStandardPointStatus) {
        this.agentStandardPointStatus = agentStandardPointStatus;
    }

    public String getAgentDoublePointStatus() {
        return agentDoublePointStatus;
    }

    public void setAgentDoublePointStatus(String agentDoublePointStatus) {
        this.agentDoublePointStatus = agentDoublePointStatus;
    }

    public String getAgentBonusPointStatus() {
        return agentBonusPointStatus;
    }

    public void setAgentBonusPointStatus(String agentBonusPointStatus) {
        this.agentBonusPointStatus = agentBonusPointStatus;
    }

    public String getAgentBonusPoint() {
        return agentBonusPoint;
    }

    public void setAgentBonusPoint(String agentBonusPoint) {
        this.agentBonusPoint = agentBonusPoint;
    }

    public String getAgentNotificationCount() {
        return agentNotificationCount;
    }

    public void setAgentNotificationCount(String agentNotificationCount) {
        this.agentNotificationCount = agentNotificationCount;
    }

    public String getDonateStatus() {
        return donateStatus;
    }

    public void setDonateStatus(String donateStatus) {
        this.donateStatus = donateStatus;
    }

    public String getCharityDonatedText() {
        return charityDonatedText;
    }

    public void setCharityDonatedText(String charityDonatedText) {
        this.charityDonatedText = charityDonatedText;
    }

    public String getAgentPointStartDate() {
        return agentPointStartDate;
    }

    public void setAgentPointStartDate(String agentPointStartDate) {
        this.agentPointStartDate = agentPointStartDate;
    }

    public String getAgentPointEndDate() {
        return agentPointEndDate;
    }

    public void setAgentPointEndDate(String agentPointEndDate) {
        this.agentPointEndDate = agentPointEndDate;
    }

    public String getUserEarnedPoints() {
        return userEarnedPoints;
    }

    public void setUserEarnedPoints(String userEarnedPoints) {
        this.userEarnedPoints = userEarnedPoints;
    }

    public String getTargetPoints() {
        return targetPoints;
    }

    public void setTargetPoints(String targetPoints) {
        this.targetPoints = targetPoints;
    }

    public String getRedeemRemarks() {
        return redeemRemarks;
    }

    public void setRedeemRemarks(String redeemRemarks) {
        this.redeemRemarks = redeemRemarks;
    }

    public String getPointRemarks() {
        return pointRemarks;
    }

    public void setPointRemarks(String pointRemarks) {
        this.pointRemarks = pointRemarks;
    }

    public String getMinspend() {
        return minspend;
    }

    public void setMinspend(String minspend) {
        this.minspend = minspend;
    }

    public String getAgentPointsFAQ() {
        return agentPointsFAQ;
    }

    public void setAgentPointsFAQ(String agentPointsFAQ) {
        this.agentPointsFAQ = agentPointsFAQ;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getCharityEnabled() {
        return charityEnabled;
    }

    public void setCharityEnabled(String charityEnabled) {
        this.charityEnabled = charityEnabled;
    }

    public String getAgentRecommendEnabled() {
        return agentRecommendEnabled;
    }

    public void setAgentRecommendEnabled(String agentRecommendEnabled) {
        this.agentRecommendEnabled = agentRecommendEnabled;
    }

    public String getAgentNotificationEnabled() {
        return agentNotificationEnabled;
    }

    public void setAgentNotificationEnabled(String agentNotificationEnabled) {
        this.agentNotificationEnabled = agentNotificationEnabled;
    }

    public String getAgentWalletType() {
        return agentWalletType;
    }

    public void setAgentWalletType(String agentWalletType) {
        this.agentWalletType = agentWalletType;
    }

    public String getAgentUserTargetVisitCount() {
        return agentUserTargetVisitCount;
    }

    public void setAgentUserTargetVisitCount(String agentUserTargetVisitCount) {
        this.agentUserTargetVisitCount = agentUserTargetVisitCount;
    }

    public String getAgentUserVisitCount() {
        return agentUserVisitCount;
    }

    public void setAgentUserVisitCount(String agentUserVisitCount) {
        this.agentUserVisitCount = agentUserVisitCount;
    }

    public String getAgentUserTodayVisit() {
        return agentUserTodayVisit;
    }

    public void setAgentUserTodayVisit(String agentUserTodayVisit) {
        this.agentUserTodayVisit = agentUserTodayVisit;
    }

    public String getAgentUserFreeDealID() {
        return agentUserFreeDealID;
    }

    public void setAgentUserFreeDealID(String agentUserFreeDealID) {
        this.agentUserFreeDealID = agentUserFreeDealID;
    }

    public String getAgentUserFreeDealName() {
        return agentUserFreeDealName;
    }

    public void setAgentUserFreeDealName(String agentUserFreeDealName) {
        this.agentUserFreeDealName = agentUserFreeDealName;
    }

    public String getAgentUserFreeDealImage() {
        return agentUserFreeDealImage;
    }

    public void setAgentUserFreeDealImage(String agentUserFreeDealImage) {
        this.agentUserFreeDealImage = agentUserFreeDealImage;
    }

    public String getAgentUserDealStatus() {
        return agentUserDealStatus;
    }

    public void setAgentUserDealStatus(String agentUserDealStatus) {
        this.agentUserDealStatus = agentUserDealStatus;
    }

    public String getAgentUserFreeDealStatus() {
        return agentUserFreeDealStatus;
    }

    public void setAgentUserFreeDealStatus(String agentUserFreeDealStatus) {
        this.agentUserFreeDealStatus = agentUserFreeDealStatus;
    }

    public String getAgentUserFreeDealText() {
        return agentUserFreeDealText;
    }

    public void setAgentUserFreeDealText(String agentUserFreeDealText) {
        this.agentUserFreeDealText = agentUserFreeDealText;
    }

    public ArrayList<AgentVoucherListBean> getmList() {
        return mList;
    }

    public void setmList(ArrayList<AgentVoucherListBean> mList) {
        this.mList = mList;
    }

    public ArrayList<StorePointsDealsList> getmDealList() {
        return mDealList;
    }

    public void setmDealList(ArrayList<StorePointsDealsList> mDealList) {
        this.mDealList = mDealList;
    }

    public ArrayList<VisitcolorBean> getmVisitcolorList() {
        return mVisitcolorList;
    }

    public void setmVisitcolorList(ArrayList<VisitcolorBean> mVisitcolorList) {
        this.mVisitcolorList = mVisitcolorList;
    }
}