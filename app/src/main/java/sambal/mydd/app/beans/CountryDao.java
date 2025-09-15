package sambal.mydd.app.beans;

/**
 * Created by Husain on 06-04-2016.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 * "id":"1",
 * "country_code":"93",
 * "country_name":"Afghanistan",
 * "country_short_name":"AF",
 * "country_status":"1"
 * },
 */
public class CountryDao {
    public String mCountryId = "";
    public String mCountryCode = "";
    public String mCountryName = "";
    public String mCountryImage = "";
    public String mCountryShortName = "";
    private boolean mCountryStatuse = false;

    public CountryDao(String mCountryId, String mCountryCode, String mCountryName, String mCountryShortName, boolean mCountryStatuse) {
        this.mCountryId = mCountryId;
        this.mCountryCode = mCountryCode;
        this.mCountryName = mCountryName;
        this.mCountryShortName = mCountryShortName;
        this.mCountryStatuse = mCountryStatuse;
    }

    public CountryDao(JSONObject jObjects) {
        try {
            this.mCountryId = jObjects.getString("id");
            this.mCountryCode = jObjects.getString("country_code");
            this.mCountryName = jObjects.getString("country_name");
            this.mCountryShortName = jObjects.getString("country_short_name").toLowerCase();
            this.mCountryImage = "flag_" + mCountryShortName;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}