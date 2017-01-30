package com.example.alex.positiontracker.locationTracking;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alex.positiontracker.ui.MainActivity;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    LocationThread mLocationThread;

    @Override
    public void onCreate() {
        mLocationThread = new LocationThread(this);
        mLocationThread.setName("Location Thread");
         }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v (TAG, "Location service is started");
        mLocationThread.start();
        NotificationManager manager = (NotificationManager) getSystemService (NOTIFICATION_SERVICE);
        mLocationThread.setNotificationManager(manager);
        mLocationThread.setLocationNotificationTime(60*30);
        return Service.START_STICKY;
}

    @Override
    public void onDestroy() {
        Log.v (TAG, "Location service is stopped");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int notificationTime = intent.getIntExtra(MainActivity.LOCATION_NOTIFICATION_TIME, 0);
        mLocationThread.setLocationNotificationTime(notificationTime);
        return null;
        }
}
