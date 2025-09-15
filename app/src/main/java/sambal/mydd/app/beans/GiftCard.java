package sambal.mydd.app.beans;

import java.io.Serializable;

public class GiftCard  implements Serializable {
    int giftId,giftVoucherId,giftStatus;
    String giftBackgroundImage ,agentImage, giftPrice, giftSellingPrice, currency, discountValue, giftExpireDate, giftText,giftTextRemark,borderColor;

    public GiftCard() {
    }

    public GiftCard(int giftId, int giftVoucherId, int giftStatus, String giftBackgroundImage, String agentImage, String giftPrice, String giftSellingPrice, String currency, String discountValue, String giftExpireDate, String giftText,String giftTextRemark, String borderColor) {
        this.giftId = giftId;
        this.giftVoucherId = giftVoucherId;
        this.giftStatus = giftStatus;
        this.giftBackgroundImage = giftBackgroundImage;
        this.agentImage = agentImage;
        this.giftPrice = giftPrice;
        this.giftSellingPrice = giftSellingPrice;
        this.currency = currency;
        this.discountValue = discountValue;
        this.giftExpireDate = giftExpireDate;
        this.giftText = giftText;
        this.giftTextRemark=giftTextRemark;
        this.borderColor=borderColor;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public int getGiftVoucherId() {
        return giftVoucherId;
    }

    public void setGiftVoucherId(int giftVoucherId) {
        this.giftVoucherId = giftVoucherId;
    }

    public String getGiftBackgroundImage() {
        return giftBackgroundImage;
    }

    public void setGiftBackgroundImage(String giftBackgroundImage) {
        this.giftBackgroundImage = giftBackgroundImage;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public String getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(String giftPrice) {
        this.giftPrice = giftPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGiftText() {
        return giftText;
    }

    public void setGiftText(String giftText) {
        this.giftText = giftText;
    }

    public int getGiftStatus() {
        return giftStatus;
    }

    public void setGiftStatus(int giftStatus) {
        this.giftStatus = giftStatus;
    }

    public String getGiftSellingPrice() {
        return giftSellingPrice;
    }

    public void setGiftSellingPrice(String giftSellingPrice) {
        this.giftSellingPrice = giftSellingPrice;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getGiftExpireDate() {
        return giftExpireDate;
    }

    public void setGiftExpireDate(String giftExpireDate) {
        this.giftExpireDate = giftExpireDate;
    }

    public String getGiftTextRemark() {
        return giftTextRemark;
    }

    public void setGiftTextRemark(String giftTextRemark) {
        this.giftTextRemark = giftTextRemark;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }
}
