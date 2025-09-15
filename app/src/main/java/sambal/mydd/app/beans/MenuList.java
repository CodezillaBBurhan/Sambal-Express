package sambal.mydd.app.beans;

import java.io.Serializable;

public class MenuList implements Serializable {
    String menuId, menuName, leftmenuStatusId, leftmenuStatus, leftmenuIcon, webURLStatus, webURL;

    public MenuList() {
    }

    public MenuList(String menuId, String menuName, String leftmenuStatusId, String leftmenuStatus, String leftmenuIcon, String webURLStatus, String webURL) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.leftmenuStatusId = leftmenuStatusId;
        this.leftmenuStatus = leftmenuStatus;
        this.leftmenuIcon = leftmenuIcon;
        this.webURLStatus = webURLStatus;
        this.webURL = webURL;
    }

    public String getLeftmenuStatusId() {
        return leftmenuStatusId;
    }

    public void setLeftmenuStatusId(String leftmenuStatusId) {
        this.leftmenuStatusId = leftmenuStatusId;
    }

    public String getLeftmenuStatus() {
        return leftmenuStatus;
    }

    public void setLeftmenuStatus(String leftmenuStatus) {
        this.leftmenuStatus = leftmenuStatus;
    }

    public String getLeftmenuIcon() {
        return leftmenuIcon;
    }

    public void setLeftmenuIcon(String leftmenuIcon) {
        this.leftmenuIcon = leftmenuIcon;
    }

    public String getWebURLStatus() {
        return webURLStatus;
    }

    public void setWebURLStatus(String webURLStatus) {
        this.webURLStatus = webURLStatus;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
