package com.example.alex.positiontracker.locationTracking;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alex.positiontracker.ui.MainActivity;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    LocationThread mLocationThread;
    private IBinder mBinder = new LocalBinder();

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
        return Service.START_STICKY;
}

    @Override
    public void onDestroy() {
        Log.v (TAG, "Location service is stopped");
        mLocationThread.interrupt();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public LocationService getService () {
            return LocationService.this;
        }
    }

    public void setNotification (int notificationTime) {
        mLocationThread.setLocationNotificationTime(notificationTime);
    }

    public void stopNotifications () {
        mLocationThread.setLocationNotificationTime(0);
    }
}
