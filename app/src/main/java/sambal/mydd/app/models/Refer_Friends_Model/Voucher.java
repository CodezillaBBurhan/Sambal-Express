package sambal.mydd.app.models.Refer_Friends_Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Voucher implements Serializable {
    @SerializedName("voucherId")
    @Expose
    private Integer voucherId;
    @SerializedName("voucherSerialNumber")
    @Expose
    private String voucherSerialNumber;
    @SerializedName("voucherNumber")
    @Expose
    private String voucherNumber;
    @SerializedName("voucherUUID")
    @Expose
    private String voucherUUID;
    @SerializedName("voucherPrice")
    @Expose
    private String voucherPrice;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("voucherRedeemEnabled")
    @Expose
    private Integer voucherRedeemEnabled;
    @SerializedName("voucherText")
    @Expose
    private String voucherText;
    @SerializedName("voucherCaimStatus")
    @Expose
    private Integer voucherCaimStatus;
    @SerializedName("voucherClainText")
    @Expose
    private String voucherClainText;
    @SerializedName("voucherRedeemedText")
    @Expose
    private String voucherRedeemedText;
    @SerializedName("voucherRedeemedPrice")
    @Expose
    private String voucherRedeemedPrice;

    public String getVoucherRedeemedText() {
        return voucherRedeemedText;
    }

    public void setVoucherRedeemedText(String voucherRedeemedText) {
        this.voucherRedeemedText = voucherRedeemedText;
    }

    public String getVoucherRedeemedPrice() {
        return voucherRedeemedPrice;
    }

    public void setVoucherRedeemedPrice(String voucherRedeemedPrice) {
        this.voucherRedeemedPrice = voucherRedeemedPrice;
    }

    public Integer getVoucherCaimStatus() {
        return voucherCaimStatus;
    }

    public void setVoucherCaimStatus(Integer voucherCaimStatus) {
        this.voucherCaimStatus = voucherCaimStatus;
    }

    public String getVoucherClainText() {
        return voucherClainText;
    }

    public void setVoucherClainText(String voucherClainText) {
        this.voucherClainText = voucherClainText;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherSerialNumber() {
        return voucherSerialNumber;
    }

    public void setVoucherSerialNumber(String voucherSerialNumber) {
        this.voucherSerialNumber = voucherSerialNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherUUID() {
        return voucherUUID;
    }

    public void setVoucherUUID(String voucherUUID) {
        this.voucherUUID = voucherUUID;
    }

    public String getVoucherPrice() {
        return voucherPrice;
    }

    public void setVoucherPrice(String voucherPrice) {
        this.voucherPrice = voucherPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getVoucherRedeemEnabled() {
        return voucherRedeemEnabled;
    }

    public void setVoucherRedeemEnabled(Integer voucherRedeemEnabled) {
        this.voucherRedeemEnabled = voucherRedeemEnabled;
    }

    public String getVoucherText() {
        return voucherText;
    }

    public void setVoucherText(String voucherText) {
        this.voucherText = voucherText;
    }
}
