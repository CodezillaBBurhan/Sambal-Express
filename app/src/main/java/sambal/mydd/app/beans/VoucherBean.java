package sambal.mydd.app.beans;

import java.io.Serializable;

public class VoucherBean implements Serializable {
    String redeemVoucherId, redeemType, agentName, redeemTypeStatus, ticketCurrency, ticketPrice, redeemDate, colorCode;

    public VoucherBean() {
    }

    public VoucherBean(String redeemVoucherId, String redeemType, String agentName, String redeemTypeStatus, String ticketCurrency, String ticketPrice, String redeemDate, String colorCode) {
        this.redeemVoucherId = redeemVoucherId;
        this.redeemType = redeemType;
        this.agentName = agentName;
        this.redeemTypeStatus = redeemTypeStatus;
        this.ticketCurrency = ticketCurrency;
        this.ticketPrice = ticketPrice;
        this.redeemDate = redeemDate;
        this.colorCode = colorCode;
    }

    public String getRedeemVoucherId() {
        return redeemVoucherId;
    }

    public void setRedeemVoucherId(String redeemVoucherId) {
        this.redeemVoucherId = redeemVoucherId;
    }

    public String getRedeemType() {
        return redeemType;
    }

    public void setRedeemType(String redeemType) {
        this.redeemType = redeemType;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getRedeemTypeStatus() {
        return redeemTypeStatus;
    }

    public void setRedeemTypeStatus(String redeemTypeStatus) {
        this.redeemTypeStatus = redeemTypeStatus;
    }

    public String getTicketCurrency() {
        return ticketCurrency;
    }

    public void setTicketCurrency(String ticketCurrency) {
        this.ticketCurrency = ticketCurrency;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getRedeemDate() {
        return redeemDate;
    }

    public void setRedeemDate(String redeemDate) {
        this.redeemDate = redeemDate;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return "VoucherBean{" +
                "redeemVoucherId='" + redeemVoucherId + '\'' +
                ", redeemType='" + redeemType + '\'' +
                ", agentName='" + agentName + '\'' +
                ", redeemTypeStatus='" + redeemTypeStatus + '\'' +
                ", ticketCurrency='" + ticketCurrency + '\'' +
                ", ticketPrice='" + ticketPrice + '\'' +
                ", redeemDate='" + redeemDate + '\'' +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}
