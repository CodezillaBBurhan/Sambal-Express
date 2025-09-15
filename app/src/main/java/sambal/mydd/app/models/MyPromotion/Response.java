package sambal.mydd.app.models.MyPromotion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("promotionDealsList")
    @Expose
    private List<PromotionDeals> promotionDealsList = null;

    public List<PromotionDeals> getPromotionDealsList() {
        return promotionDealsList;
    }

    public void setPromotionDealsList(List<PromotionDeals> promotionDealsList) {
        this.promotionDealsList = promotionDealsList;
    }
}
