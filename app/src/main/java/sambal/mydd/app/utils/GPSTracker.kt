package sambal.mydd.app.utils

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.location.LocationManager
import android.location.Criteria
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.core.app.ActivityCompat

class GPSTracker : Service(), LocationListener {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        SavedData.saveLatitude(location.latitude.toString())
        SavedData.saveLongitude(location.longitude.toString())
        GPSCoordinates(location.latitude, location.longitude)
    }

    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates?)
    }

    // consider returning Location instead of this dummy wrapper class
    class GPSCoordinates {
        @JvmField
        var longitude = -1f
        @JvmField
        var latitude = -1f

        constructor(theLatitude: Float, theLongitude: Float) {
            longitude = theLongitude
            latitude = theLatitude
        }

        constructor(theLatitude: Double, theLongitude: Double) {
            longitude = theLongitude.toFloat()
            latitude = theLatitude.toFloat()
        }
    }

    companion object {
        // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
        // contents of the else and if. Also be sure to check gps permission/settings are allowed.
        // call usually takes <10ms
        @JvmStatic
        fun requestSingleUpdate(context: Context, callback: LocationCallback) {
            val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isNetworkEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        SavedData.saveLatitude(location.latitude.toString())
                        SavedData.saveLongitude(location.longitude.toString())
                        callback.onNewLocationAvailable(GPSCoordinates(location.latitude,
                            location.longitude))
                        locationManager.removeUpdates(this) // stop further updates
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, null)
            } else {
                val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGPSEnabled) {
                    val criteria = Criteria()
                    criteria.accuracy = Criteria.ACCURACY_FINE
                    locationManager.requestSingleUpdate(criteria, object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            SavedData.saveLatitude(location.latitude.toString())
                            SavedData.saveLongitude(location.longitude.toString())
                            callback.onNewLocationAvailable(GPSCoordinates(location.latitude,
                                location.longitude))
                            locationManager.removeUpdates(this) // stop further updates
                        }

                        override fun onStatusChanged(
                            provider: String,
                            status: Int,
                            extras: Bundle
                        ) {
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }, null)
                }
            }
        }
    }
}