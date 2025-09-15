package sambal.mydd.app.models.GiftCard_Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Example {

    @SerializedName("giftId")
    @Expose
    private Integer giftId;
    @SerializedName("giftAgentName")
    @Expose
    private String giftAgentName;
    @SerializedName("giftFromUserName")
    @Expose
    private String giftFromUserName;
    @SerializedName("giftCurrency")
    @Expose
    private String giftCurrency;
    @SerializedName("giftAmount")
    @Expose
    private String giftAmount;
    @SerializedName("giftMessage")
    @Expose
    private String giftMessage;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error_type")
    @Expose
    private String errorType;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public String getGiftAgentName() {
        return giftAgentName;
    }

    public void setGiftAgentName(String giftAgentName) {
        this.giftAgentName = giftAgentName;
    }

    public String getGiftFromUserName() {
        return giftFromUserName;
    }

    public void setGiftFromUserName(String giftFromUserName) {
        this.giftFromUserName = giftFromUserName;
    }

    public String getGiftCurrency() {
        return giftCurrency;
    }

    public void setGiftCurrency(String giftCurrency) {
        this.giftCurrency = giftCurrency;
    }

    public String getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(String giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getGiftMessage() {
        return giftMessage;
    }

    public void setGiftMessage(String giftMessage) {
        this.giftMessage = giftMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}