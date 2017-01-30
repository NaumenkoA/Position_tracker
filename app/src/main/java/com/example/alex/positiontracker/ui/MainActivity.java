package com.example.alex.positiontracker.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alex.positiontracker.R;
import com.example.alex.positiontracker.database.LocationDataSource;
import com.example.alex.positiontracker.locationTracking.LocationProvider;
import com.example.alex.positiontracker.locationTracking.LocationService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LOCATION_TRACKING_STATE = "location_tracking_state";
    public static final String LOCATION_NOTIFICATION_TIME = "location_notification_time";

    private boolean mIsTrackingActivated;
    // notification time in seconds
    private int mLocationNotificationTime = 60;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @BindView(R.id.locationSwitch) Switch mLocationSwitch;

    @Override
    protected void onStop() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putBoolean(LOCATION_TRACKING_STATE, mIsTrackingActivated);
        edit.apply();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mIsTrackingActivated = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(LOCATION_TRACKING_STATE, false);
        mLocationSwitch.setChecked(mIsTrackingActivated);
        //setNotificationTime();

        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, LocationProvider.PERMISSIONS_REQUEST_CODE);
                    }
                    mIsTrackingActivated = true;
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    startService(intent);
                } else {
                    mIsTrackingActivated = false;
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    stopService(intent);
                }
            }
        });

    }
//
//    private void setNotificationTime() {
//        Intent intent = new Intent();
//        intent.putExtra(LOCATION_NOTIFICATION_TIME, mLocationNotificationTime);
//        this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
//        this.unbindService(mServiceConnection);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // mLocationProvider.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
