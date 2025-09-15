package sambal.mydd.app.beans;

import java.io.Serializable;

public class CharityListBean implements Serializable {
    String charityId, charityName, charitySubName, charityStatus, charityJoinedText, charityWebURL, charityDescription, charityImage;
    boolean isChecked;

    public CharityListBean() {
    }

    public CharityListBean(String charityId, String charityName, String charitySubName, String charityStatus, String charityJoinedText, String charityWebURL, String charityImage, String charityDescription, boolean isChecked) {
        this.charityId = charityId;
        this.charityName = charityName;
        this.charitySubName = charitySubName;
        this.charityStatus = charityStatus;
        this.charityJoinedText = charityJoinedText;
        this.charityWebURL = charityWebURL;
        this.charityImage = charityImage;
        this.charityDescription = charityDescription;
        this.isChecked = isChecked;
    }

    public String getCharityId() {
        return charityId;
    }

    public void setCharityId(String charityId) {
        this.charityId = charityId;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getCharitySubName() {
        return charitySubName;
    }

    public void setCharitySubName(String charitySubName) {
        this.charitySubName = charitySubName;
    }

    public String getCharityStatus() {
        return charityStatus;
    }

    public void setCharityStatus(String charityStatus) {
        this.charityStatus = charityStatus;
    }

    public String getCharityJoinedText() {
        return charityJoinedText;
    }

    public void setCharityJoinedText(String charityJoinedText) {
        this.charityJoinedText = charityJoinedText;
    }

    public String getCharityWebURL() {
        return charityWebURL;
    }

    public void setCharityWebURL(String charityWebURL) {
        this.charityWebURL = charityWebURL;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCharityDescription() {
        return charityDescription;
    }

    public void setCharityDescription(String charityDescription) {
        this.charityDescription = charityDescription;
    }

    public String getCharityImage() {
        return charityImage;
    }

    public void setCharityImage(String charityImage) {
        this.charityImage = charityImage;
    }
}
