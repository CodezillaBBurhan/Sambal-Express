package sambal.mydd.app.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTFrameHandler;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.FrameType;
import com.minew.beaconplus.sdk.frames.IBeaconFrame;
import com.minew.beaconplus.sdk.frames.MinewFrame;
import com.minew.beaconplus.sdk.interfaces.MTCentralManagerListener;
import sambal.mydd.app.R;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.ErrorMessage;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    MTCentralManager mtCentralManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //  fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        mtCentralManager = MTCentralManager.getInstance(this);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled; you can prompt the user to enable it
            // Alternatively, you can request Bluetooth activation using an Intent
            mtCentralManager.startScan();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // requestLocationUpdates();
            ErrorMessage.E("LocationService" + "Location updates started");
            /* return START_STICKY;*/
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                ErrorMessage.E("Service>>>>>>" + "Service is running...");
                                try {
                                    mtCentralManager.setMTCentralManagerListener(new MTCentralManagerListener() {
                                        @Override
                                        public void onScanedPeripheral(final List<MTPeripheral> peripherals) {
                                            for (MTPeripheral mtPeripheral : peripherals) {
                                                // get FrameHandler of a device.
                                                MTFrameHandler mtFrameHandler = mtPeripheral.mMTFrameHandler;
                                                ArrayList<MinewFrame> advFrames = mtFrameHandler.getAdvFrames();
                                                for (MinewFrame minewFrame : advFrames) {
                                                    FrameType frameType = minewFrame.getFrameType();
                                                    switch (frameType) {
                                                        case FrameiBeacon://iBeacon
                                                            IBeaconFrame iBeaconFrame = (IBeaconFrame) minewFrame;
                                                            ErrorMessage.E(" Uuid[0]>>>>" + iBeaconFrame.getUuid());
                                                            updateLocation(iBeaconFrame.getUuid());
                                                            //Log.v("beaconplus", iBeaconFrame.getUuid() + iBeaconFrame.getMajor() + iBeaconFrame.getMinor());
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                // ErrorMessage.E("Service>>>>>>" + "Service is running.Uuid>>.."+ Uuid[0]);
                                                // all data frames of device（such as:iBeacon，UID，URL...）
                                                //ArrayList<MinewFrame> advFrames = mtFrameHandler.getAdvFrames();
                                            }
                                        }
                                    });
                                    Thread.sleep(100000);
                                } catch (Exception e) {
                                    ErrorMessage.E("Exception>>>" + e.toString());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            ).start();
            final String CHANNELID = "Foreground Service ID";
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel(
                        CHANNELID,
                        CHANNELID,
                        NotificationManager.IMPORTANCE_LOW
                );


                getSystemService(NotificationManager.class).createNotificationChannel(channel);

                Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                        .setContentText(getString(R.string.app_name))
                        /* .setContentTitle("Service enabled")*/
                        .setSmallIcon(R.drawable.appicon);
                startForeground(1001, notification.build());
            }

            return super.onStartCommand(intent, flags, startId);
        } catch (Exception e) {
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Stop location updates here
        ErrorMessage.E("LocationService" + "Location updates stopped");
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateLocation(String Uuid) {
        if (AppUtil.isNetworkAvailable(LocationService.this)) {
            Call<ResponseBody> call = AppConfig.api_Interface().getBeaconNotification(Uuid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("updateLocation>>" + response.code());
                    if (response.isSuccessful()) {
                        try {
                            ErrorMessage.E("updateLocation>>" + response.body().string());

                        } catch (Exception e) {
                            e.printStackTrace();
                            ErrorMessage.E("Exception" + e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // ErrorMessage.T(DashboardActivity.this()(), "Response Fail");

                    System.out.println("============update profile fail  :" + t.toString());
                    //materialDialog.dismiss();

                }
            });

        }

    }
}





