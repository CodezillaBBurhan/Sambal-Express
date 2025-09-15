package sambal.mydd.app.models.Ads_Detail_Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("userRefer")
    @Expose
    private List<UserRefer> userRefer = null;

    public List<UserRefer> getUserRefer() {
        return userRefer;
    }

    public void setUserRefer(List<UserRefer> userRefer) {
        this.userRefer = userRefer;
    }
}
