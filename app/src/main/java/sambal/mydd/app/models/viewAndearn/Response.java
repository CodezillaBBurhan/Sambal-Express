package sambal.mydd.app.models.viewAndearn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("eventHeading1")
    @Expose
    private String eventHeading1;
    @SerializedName("eventHeading2")
    @Expose
    private String eventHeading2;
    @SerializedName("eventHeading3")
    @Expose
    private String eventHeading3;
    @SerializedName("eventHeading4")
    @Expose
    private String eventHeading4;
    @SerializedName("eventList")
    @Expose
    private List<Event> eventList;

    public String getEventHeading1() {
        return eventHeading1;
    }

    public void setEventHeading1(String eventHeading1) {
        this.eventHeading1 = eventHeading1;
    }

    public String getEventHeading2() {
        return eventHeading2;
    }

    public void setEventHeading2(String eventHeading2) {
        this.eventHeading2 = eventHeading2;
    }

    public String getEventHeading3() {
        return eventHeading3;
    }

    public void setEventHeading3(String eventHeading3) {
        this.eventHeading3 = eventHeading3;
    }

    public String getEventHeading4() {
        return eventHeading4;
    }

    public void setEventHeading4(String eventHeading4) {
        this.eventHeading4 = eventHeading4;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
