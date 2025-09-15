package sambal.mydd.app.beans;


import java.io.Serializable;

public class AgentVoucherListBean implements Serializable {
    String voucherId, voucherSerialNumber, voucherNumber, voucherUUID, voucherPrice, currency, charityEnabled, voucherRedeemEnabled, voucherText,voucherRedeemedText,voucherRedeemedPrice,voucherColorCode;

    public AgentVoucherListBean() {
    }

    public AgentVoucherListBean(String voucherId, String voucherSerialNumber, String voucherNumber, String voucherUUID, String voucherPrice, String currency, String charityEnabled, String voucherRedeemEnabled, String voucherText, String voucherRedeemedText, String voucherRedeemedPrice, String voucherColorCode) {
        this.voucherId = voucherId;
        this.voucherSerialNumber = voucherSerialNumber;
        this.voucherNumber = voucherNumber;
        this.voucherUUID = voucherUUID;
        this.voucherPrice = voucherPrice;
        this.currency = currency;
        this.charityEnabled = charityEnabled;
        this.voucherRedeemEnabled = voucherRedeemEnabled;
        this.voucherText = voucherText;
        this.voucherRedeemedText = voucherRedeemedText;
        this.voucherRedeemedPrice = voucherRedeemedPrice;
        this.voucherColorCode = voucherColorCode;
    }

    public String getVoucherColorCode() {
        return voucherColorCode;
    }

    public void setVoucherColorCode(String voucherColorCode) {
        this.voucherColorCode = voucherColorCode;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
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

    public String getCharityEnabled() {
        return charityEnabled;
    }

    public void setCharityEnabled(String charityEnabled) {
        this.charityEnabled = charityEnabled;
    }

    public String getVoucherRedeemEnabled() {
        return voucherRedeemEnabled;
    }

    public void setVoucherRedeemEnabled(String voucherRedeemEnabled) {
        this.voucherRedeemEnabled = voucherRedeemEnabled;
    }

    public String getVoucherText() {
        return voucherText;
    }

    public void setVoucherText(String voucherText) {
        this.voucherText = voucherText;
    }

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
}