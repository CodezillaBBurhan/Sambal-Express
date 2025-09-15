package sambal.mydd.app.models.Refer_Friends_Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Promocode implements Serializable {
    @SerializedName("promocode")
    @Expose
    private String promocode;
    @SerializedName("promocodeExpireDate")
    @Expose
    private String promocodeExpireDate;
    @SerializedName("promocodeText")
    @Expose
    private String promocodeText;

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getPromocodeExpireDate() {
        return promocodeExpireDate;
    }

    public void setPromocodeExpireDate(String promocodeExpireDate) {
        this.promocodeExpireDate = promocodeExpireDate;
    }

    public String getPromocodeText() {
        return promocodeText;
    }

    public void setPromocodeText(String promocodeText) {
        this.promocodeText = promocodeText;
    }
}
