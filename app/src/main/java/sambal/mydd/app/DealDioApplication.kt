package sambal.mydd.app

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.multidex.MultiDex
import com.splunk.mint.Mint
import sambal.mydd.app.check_internet.ConnectivityReceiver
import sambal.mydd.app.check_internet.ConnectivityReceiver.ConnectivityReceiverListener


class DealDioApplication : Application()/*,MultiDexApplication()*/ {
    private var launchComponent: Any? = null


    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = this
        appContext = applicationContext
        Mint.initAndStartSession(this, "021fb470")
        System.gc()


/*====================*/
      //  Log.e(TAG, "App just launched!")
      /*  try {
            val filters = ArrayList<ScanFilter>()
            // Scan for any Bluetooth device with a specific hardware address.
            val filter =
                ScanFilter.Builder().setDeviceAddress("AC:23:3F:F0:95:22")
                    .build()
            filters.add(filter)
            val settings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build()
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val scanPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, MyBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val bluetoothManager =
                this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bluetoothManager.adapter.bluetoothLeScanner.startScan(
                    filters,
                    settings,
                    scanPendingIntent
                )
            }
        } catch (e: Exception) {
            ErrorMessage.E("Exception>>123>${e.toString()}")
        }*/
    }

    fun onComponentStart(component: Any, intent: Intent?) {
        var componentStartType = "started after previous app launch"
        if (launchComponent == null) {
            componentStartType = "launched app"
            launchComponent = component
        }
        // Component launched app: MainActivity with intent Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10000000 cmp=org.altbeacon.beaconreference/.MainActivity }
        // Component started after previous app launch: MainActivity with intent Intent { flg=0x10000000 cmp=org.altbeacon.beaconreference/.MainActivity }
        Log.e(
            TAG,
            "Component $componentStartType: ${component.javaClass.simpleName} with intent: $intent "
        )
    }

    fun setConnectivityListener(listener: ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    companion object {
        // App ID: 1555486edec94a5
        //Region: us
        var appContext: Context? = null
            private set

        @get:Synchronized
        var instance: DealDioApplication? = null
            private set
    }
}