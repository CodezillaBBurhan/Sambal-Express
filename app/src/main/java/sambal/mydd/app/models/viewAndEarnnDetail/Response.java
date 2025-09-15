package sambal.mydd.app.models.viewAndEarnnDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("eventList")
    @Expose
    private List<EventDetail> eventList;

    public List<EventDetail> getEventList() {
        return eventList;
    }

    public void setEventList(List<EventDetail> eventList) {
        this.eventList = eventList;
    }
}
