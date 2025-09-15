package sambal.mydd.app.models.new_agent_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AgentDetail implements Serializable {
    @SerializedName("agentId")
    @Expose
    private Integer agentId;
    @SerializedName("agentCompanyName")
    @Expose
    private String agentCompanyName;
    @SerializedName("agentName")
    @Expose
    private String agentName;
    @SerializedName("agentAddress")
    @Expose
    private String agentAddress;
    @SerializedName("shareNotes")
    @Expose
    private String shareNotes;
    @SerializedName("shareMessage")
    @Expose
    private String shareMessage;
    @SerializedName("awardBanner")
    @Expose
    private String awardBanner;
    @SerializedName("categoryNameList")
    @Expose
    private List<CategoryName> categoryNameList = null;

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentCompanyName() {
        return agentCompanyName;
    }

    public void setAgentCompanyName(String agentCompanyName) {
        this.agentCompanyName = agentCompanyName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getShareNotes() {
        return shareNotes;
    }

    public void setShareNotes(String shareNotes) {
        this.shareNotes = shareNotes;
    }

    public String getShareMessage() {
        return shareMessage;
    }

    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
    }

    public String getAwardBanner() {
        return awardBanner;
    }

    public void setAwardBanner(String awardBanner) {
        this.awardBanner = awardBanner;
    }

    public List<CategoryName> getCategoryNameList() {
        return categoryNameList;
    }

    public void setCategoryNameList(List<CategoryName> categoryNameList) {
        this.categoryNameList = categoryNameList;
    }

}

