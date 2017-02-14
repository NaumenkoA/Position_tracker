package com.example.alex.positiontracker.locationTracking;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.alex.positiontracker.R;
import com.example.alex.positiontracker.database.LocationDataSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class LocationThread extends Thread implements LocationProvider.LocationCallback {
    private static final String TAG = LocationThread.class.getSimpleName();
    private static final int NOTIFICATION_CODE = 1;
    private Context mContext;
    private LocationDataSource mLocationDataSource;
    private int mLocationNotificationTime = 60;
    private NotificationManager mNotificationManager;
    private ScheduledExecutorService mScheduledExecutorService;

    void setLocationNotificationTime(int locationNotificationTime) {
        mLocationNotificationTime = locationNotificationTime;
        setNewNotification();
    }

    private void setNewNotification() {
        if (mLocationNotificationTime >0) {
            mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long lastLocationUpdateTime = mLocationDataSource.getLastLocationTime();
                    Log.v(TAG, "Notification task is running");
                    if ((System.currentTimeMillis() - lastLocationUpdateTime - mLocationNotificationTime * 60 * 1000) >= 0) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).
                                setSmallIcon(R.drawable.ic_schedule_black_24dp).
                                setContentTitle("Too long in same place").
                                setContentText("You are too long in the same place! Let's move on!");
                        mNotificationManager.notify(NOTIFICATION_CODE, builder.build());
                        mScheduledExecutorService.shutdownNow();
                    }
                }
            };
            mScheduledExecutorService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.MINUTES);
        } else {
            mScheduledExecutorService.shutdown();
        }
    }

    LocationThread(Context context) {
        mContext = context;
        mLocationDataSource = new LocationDataSource(mContext);
    }

    @Override
    public void run() {
        LocationProvider locationProvider = new LocationProvider(mContext, this);
        locationProvider.connect();
        Log.v (TAG, "Location thread is running");
          }

    @Override
    public void interrupt() {
        Log.v (TAG, "Location thread is stopped" + "");
        super.interrupt();
    }

    @Override
    public void handleNewLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.v(TAG, "New location logged");
        setNewNotification();

            Geocoder geocoder;

            List<Address> addresses = null;
            geocoder = new Geocoder(mContext, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String userAddress = "";
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                userAddress = city + ", " + address;
                Log.v(TAG, "New address: " + userAddress);
            }
            long time = System.currentTimeMillis();
            UserLocation newLocation = new UserLocation(time, latitude, longitude, userAddress);
            mLocationDataSource.addItem(newLocation);
        }

    void setNotificationManager(NotificationManager notificationManager) {
        mNotificationManager = notificationManager;
    }
}
