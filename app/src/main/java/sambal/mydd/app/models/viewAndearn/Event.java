package sambal.mydd.app.models.viewAndearn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {
    @SerializedName("eventId")
    @Expose
    private Integer eventId;
    @SerializedName("agentId")
    @Expose
    private Integer agentId;
    @SerializedName("agentName")
    @Expose
    private String agentName;
    @SerializedName("agentImage")
    @Expose
    private String agentImage;
    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("eventTypeText")
    @Expose
    private String eventTypeText;
    @SerializedName("eventImage")
    @Expose
    private String eventImage;
    @SerializedName("eventYoutubeVideoId")
    @Expose
    private String eventYoutubeVideoId;
    @SerializedName("eventVideo")
    @Expose
    private String eventVideo;
    @SerializedName("eventDesc")
    @Expose
    private String eventDesc;
    @SerializedName("eventLikesStatus")
    @Expose
    private Integer eventLikesStatus;
    @SerializedName("eventLikesCount")
    @Expose
    private Integer eventLikesCount;
    @SerializedName("eventShareText")
    @Expose
    private String eventShareText;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getEventTypeText() {
        return eventTypeText;
    }

    public void setEventTypeText(String eventTypeText) {
        this.eventTypeText = eventTypeText;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventYoutubeVideoId() {
        return eventYoutubeVideoId;
    }

    public void setEventYoutubeVideoId(String eventYoutubeVideoId) {
        this.eventYoutubeVideoId = eventYoutubeVideoId;
    }

    public String getEventVideo() {
        return eventVideo;
    }

    public void setEventVideo(String eventVideo) {
        this.eventVideo = eventVideo;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Integer getEventLikesStatus() {
        return eventLikesStatus;
    }

    public void setEventLikesStatus(Integer eventLikesStatus) {
        this.eventLikesStatus = eventLikesStatus;
    }

    public Integer getEventLikesCount() {
        return eventLikesCount;
    }

    public void setEventLikesCount(Integer eventLikesCount) {
        this.eventLikesCount = eventLikesCount;
    }

    public String getEventShareText() {
        return eventShareText;
    }

    public void setEventShareText(String eventShareText) {
        this.eventShareText = eventShareText;
    }
}
