package sambal.mydd.app.beans;

import java.io.Serializable;

public class PointsBean implements Serializable {
    String agentId, agentName, pointId, totalPoints,visitCount, pointCreatedDate, pointType, colorCode;

    public PointsBean() {
    }

    public PointsBean(String agentId, String agentName, String pointId, String totalPoints, String visitCount, String pointCreatedDate, String pointType, String colorCode) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.pointId = pointId;
        this.totalPoints = totalPoints;
        this.visitCount = visitCount;
        this.pointCreatedDate = pointCreatedDate;
        this.pointType = pointType;
        this.colorCode = colorCode;
    }


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    public String getPointCreatedDate() {
        return pointCreatedDate;
    }

    public void setPointCreatedDate(String pointCreatedDate) {
        this.pointCreatedDate = pointCreatedDate;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
