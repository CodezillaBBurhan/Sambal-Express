package sambal.mydd.app.beans;

import java.io.Serializable;

public class FamilyList implements Serializable {
    String userId, userName, userAcceptStatus, userAcceptText, userFamilytype, userRemoveAccess;

    public FamilyList() {
    }

    public FamilyList(String userId, String userName, String userAcceptStatus, String userAcceptText, String userFamilytype, String userRemoveAccess) {
        this.userId = userId;
        this.userName = userName;
        this.userAcceptStatus = userAcceptStatus;
        this.userAcceptText = userAcceptText;
        this.userFamilytype = userFamilytype;
        this.userRemoveAccess = userRemoveAccess;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAcceptStatus() {
        return userAcceptStatus;
    }

    public void setUserAcceptStatus(String userAcceptStatus) {
        this.userAcceptStatus = userAcceptStatus;
    }

    public String getUserAcceptText() {
        return userAcceptText;
    }

    public void setUserAcceptText(String userAcceptText) {
        this.userAcceptText = userAcceptText;
    }

    public String getUserFamilytype() {
        return userFamilytype;
    }

    public void setUserFamilytype(String userFamilytype) {
        this.userFamilytype = userFamilytype;
    }

    public String getUserRemoveAccess() {
        return userRemoveAccess;
    }

    public void setUserRemoveAccess(String userRemoveAccess) {
        this.userRemoveAccess = userRemoveAccess;
    }

    @Override
    public String toString() {
        return "FamilyList{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userAcceptStatus='" + userAcceptStatus + '\'' +
                ", userAcceptText='" + userAcceptText + '\'' +
                ", userFamilytype='" + userFamilytype + '\'' +
                ", userRemoveAccess='" + userRemoveAccess + '\'' +
                '}';
    }
}
