package sambal.mydd.app.models.new_agent_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @SerializedName("agentDetails")
    @Expose
    private List<AgentDetail> agentDetails = null;

    public List<AgentDetail> getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(List<AgentDetail> agentDetails) {
        this.agentDetails = agentDetails;
    }

}
