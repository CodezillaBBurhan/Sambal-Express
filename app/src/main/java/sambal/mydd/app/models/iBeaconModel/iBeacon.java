package sambal.mydd.app.models.iBeaconModel;

public class iBeacon {
    String uuid,major,minor,mac_address;

    public iBeacon(String uuid, String major, String minor, String mac_address) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.mac_address = mac_address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }
}
