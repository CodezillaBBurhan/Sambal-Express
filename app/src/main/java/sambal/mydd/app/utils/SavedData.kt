package sambal.mydd.app.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import sambal.mydd.app.DealDioApplication

object SavedData {
    private const val USERID = "userID"
    private const val AllData = "all_data"
    private const val Latitude = "latitude"
    private const val Longitude = "Longitude"
    private const val Address = "Address"
    private const val LocationPermission = "LocationPermission"

    public const val countNearByDevicePermission = "countNearByDevicePermission"

    var prefs: SharedPreferences? = null
    val instance: SharedPreferences?
        get() {
            if (prefs == null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(DealDioApplication.instance)
            }
            return prefs
        }

    fun getuserId(): String? {
        return instance!!.getString(USERID, "")
    }

    fun saveAllData(userid: String?) {
        val editor = instance!!.edit()
        editor.putString(AllData, userid)
        editor.apply()
    }

    fun getAllData(): String? {
        return instance!!.getString(AllData, "0")
    }

    fun getLatitude(): String? {
        return instance!!.getString(Latitude, "0")
    }

    fun saveLatitude(latitude: String?) {
        val editor = instance!!.edit()
        editor.putString(Latitude, latitude)
        editor.apply()
    }

    fun getLongitude(): String? {
        return instance!!.getString(Longitude, "0")
    }

    fun saveLongitude(mobile: String?) {
        val editor = instance!!.edit()
        editor.putString(Longitude, mobile)
        editor.apply()
    }

    fun getAddress(): String? {
        return instance!!.getString(Address, "")
    }

    fun saveAddress(mobile: String?) {
        val editor = instance!!.edit()
        editor.putString(Address, mobile)
        editor.apply()
    }

    fun getLocationPermission(): String? {
        return instance!!.getString(LocationPermission, "false")
    }

    fun saveLocationPermission(mobile: String?) {
        val editor = instance!!.edit()
        editor.putString(LocationPermission, mobile)
        editor.apply()
    }

    fun getCountNearByDevicePermission(): String? {
        return instance!!.getString(countNearByDevicePermission, "0")
    }

    fun saveCountNearByDevicePermission(count: String?) {
        val editor = instance!!.edit()
        editor.putString(countNearByDevicePermission, count)
        editor.apply()
    }
}