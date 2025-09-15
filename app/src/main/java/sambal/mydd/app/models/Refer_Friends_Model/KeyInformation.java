package sambal.mydd.app.models.Refer_Friends_Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KeyInformation implements Serializable {
    @SerializedName("keyTitle")
    @Expose
    private String keyTitle;
    @SerializedName("keyDescription")
    @Expose
    private String keyDescription;

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public String getKeyDescription() {
        return keyDescription;
    }

    public void setKeyDescription(String keyDescription) {
        this.keyDescription = keyDescription;
    }
}
