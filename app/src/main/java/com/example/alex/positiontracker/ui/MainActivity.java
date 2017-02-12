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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.positiontracker.R;
import com.example.alex.positiontracker.database.LocationDataSource;
import com.example.alex.positiontracker.locationTracking.LocationProvider;
import com.example.alex.positiontracker.locationTracking.LocationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;




public class MainActivity extends AppCompatActivity {

    private static final String LOCATION_TRACKING_STATE = "location_tracking_state";
    public static final String USER_SELECTED_DATE = "selected_date";
    public static final String USER_SELECTED_FROM_DATE = "selected_from_date";
    public static final String USER_SELECTED_TO_DATE = "selected_to_date";
    public static final String LOCATION_NOTIFICATION_TIME = "location_notification_time";
    public static final int FROM_DATE_REQUEST = 101;
    public static final int TO_DATE_REQUEST = 102;



    private boolean mIsTrackingActivated;
    private long mSelectedFromDate;
    private long mSelectedToDate;

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



    @Override
    protected void onStop() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putBoolean(LOCATION_TRACKING_STATE, mIsTrackingActivated);
        edit.apply();
        super.onStop();
    }

    @BindView(R.id.locationSwitch) Switch mLocationSwitch;
    @BindView(R.id.showMapButton) Button mMapButton;
    @BindView(R.id.selectFromDateButton) Button mFromDateButton;
    @BindView(R.id.selectToDateButton) Button mToDateButton;
    @BindView(R.id.fromDateTextView) TextView mFromDateTextView;
    @BindView(R.id.toDateTextView) TextView mToDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mIsTrackingActivated = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(LOCATION_TRACKING_STATE, false);
        mLocationSwitch.setChecked(mIsTrackingActivated);
        long currentTime = System.currentTimeMillis();
        mSelectedFromDate = currentTime;
        mSelectedToDate = currentTime;
        mFromDateTextView.setText(getStringFromUnixTime(mSelectedFromDate));
        mToDateTextView.setText(getStringFromUnixTime(mSelectedToDate));
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
        mFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DatePickerActivity.class);
                intent.putExtra(USER_SELECTED_DATE, mSelectedFromDate);
                startActivityForResult(intent, FROM_DATE_REQUEST);

            }
        });

        mToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DatePickerActivity.class);
                intent.putExtra(USER_SELECTED_DATE, mSelectedToDate);
                startActivityForResult(intent, TO_DATE_REQUEST);
            }
        });

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFromDate > mSelectedToDate) {
                    Toast.makeText(MainActivity.this, "Please select correct date period", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(USER_SELECTED_FROM_DATE, mSelectedFromDate);
                intent.putExtra(USER_SELECTED_TO_DATE, mSelectedToDate);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == FROM_DATE_REQUEST) {
                mSelectedFromDate = data.getLongExtra(DatePickerActivity.RESULT, System.currentTimeMillis());
                mFromDateTextView.setText(getStringFromUnixTime(mSelectedFromDate));
            }
            if (requestCode == TO_DATE_REQUEST) {
                mSelectedToDate = data.getLongExtra(DatePickerActivity.RESULT, System.currentTimeMillis());
                mToDateTextView.setText(getStringFromUnixTime(mSelectedToDate));
            }
        }
    }

    private String getStringFromUnixTime(long unixTime) {
        Date date = new Date (unixTime);
        Locale locale = Locale.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY", locale);
        return formatter.format(date);
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
