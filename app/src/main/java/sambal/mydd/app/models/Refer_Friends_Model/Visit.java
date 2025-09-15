package sambal.mydd.app.models.Refer_Friends_Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Visit implements Serializable {
    @SerializedName("visitColor")
    @Expose
    private String visitColor;

    public String getVisitColor() {
        return visitColor;
    }

    public void setVisitColor(String visitColor) {
        this.visitColor = visitColor;
    }
}
