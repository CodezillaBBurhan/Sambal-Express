package sambal.mydd.app.models.HomePageDeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class Example implements Serializable {
    @SerializedName("latestDealsList")
    @Expose
    private List<LatestDeals> latestDealsList;

    public List<LatestDeals> getLatestDealsList() {
        return latestDealsList;
    }

    public void setLatestDealsList(List<LatestDeals> latestDealsList) {
        this.latestDealsList = latestDealsList;
    }
}
