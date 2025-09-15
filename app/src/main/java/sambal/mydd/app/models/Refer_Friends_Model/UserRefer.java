package sambal.mydd.app.models.Refer_Friends_Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserRefer implements Serializable {
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("referTitle")
    @Expose
    private String referTitle;
    @SerializedName("referDescription")
    @Expose
    private String referDescription;
    @SerializedName("referEarnings")
    @Expose
    private String referEarnings;
    @SerializedName("target")
    @Expose
    private Integer target;

    public Integer getReferralType() {
        return referralType;
    }

    public void setReferralType(Integer referralType) {
        this.referralType = referralType;
    }

    @SerializedName("referralType")
    @Expose
    private Integer referralType;

    @SerializedName("referBalanceText")
    @Expose
    private String referBalanceText;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;
    @SerializedName("agentName")
    @Expose
    private String agentName;
    @SerializedName("referralQRCode")
    @Expose
    private String referralQRCode;
    @SerializedName("referralURL")
    @Expose
    private String referralURL;
    @SerializedName("earned")
    @Expose
    private String earned;
    @SerializedName("referralText")
    @Expose
    private String referralText;
    @SerializedName("referralImage")
    @Expose
    private String referralImage;
    @SerializedName("visitList")
    @Expose
    private List<Visit> visitList = null;
    @SerializedName("voucherList")
    @Expose
    private List<Voucher> voucherList = null;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @SerializedName("keyInformation")
    @Expose
    private List<KeyInformation> keyInformation = null;
    @SerializedName("promocode")
    @Expose
    private List<Promocode> promocode = null;

    public List<Promocode> getPromocode() {
        return promocode;
    }

    public String getReferralImage() {
        return referralImage;
    }

    public void setReferralImage(String referralImage) {
        this.referralImage = referralImage;
    }

    public void setPromocode(List<Promocode> promocode) {
        this.promocode = promocode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getReferTitle() {
        return referTitle;
    }

    public void setReferTitle(String referTitle) {
        this.referTitle = referTitle;
    }

    public String getReferDescription() {
        return referDescription;
    }

    public void setReferDescription(String referDescription) {
        this.referDescription = referDescription;
    }

    public String getReferEarnings() {
        return referEarnings;
    }

    public void setReferEarnings(String referEarnings) {
        this.referEarnings = referEarnings;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public String getReferBalanceText() {
        return referBalanceText;
    }

    public void setReferBalanceText(String referBalanceText) {
        this.referBalanceText = referBalanceText;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferralQRCode() {
        return referralQRCode;
    }

    public void setReferralQRCode(String referralQRCode) {
        this.referralQRCode = referralQRCode;
    }

    public String getReferralURL() {
        return referralURL;
    }

    public void setReferralURL(String referralURL) {
        this.referralURL = referralURL;
    }

    public String getEarned() {
        return earned;
    }

    public void setEarned(String earned) {
        this.earned = earned;
    }

    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public List<Voucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(List<Voucher> voucherList) {
        this.voucherList = voucherList;
    }

    public List<KeyInformation> getKeyInformation() {
        return keyInformation;
    }

    public void setKeyInformation(List<KeyInformation> keyInformation) {
        this.keyInformation = keyInformation;
    }

    public String getReferralText() {
        return referralText;
    }

    public void setReferralText(String referralText) {
        this.referralText = referralText;
    }
}
