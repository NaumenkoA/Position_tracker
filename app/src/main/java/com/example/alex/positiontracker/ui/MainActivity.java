package com.example.alex.positiontracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.example.alex.positiontracker.locationTracking.LocationService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;




public class MainActivity extends AppCompatActivity {

    private static final String LOCATION_TRACKING_STATE = "location_tracking_state";
    private static final String NOTIFICATION_STATE = "location_notification_state";
    public static final String USER_SELECTED_FROM_DATE = "selected_from_date";
    public static final String USER_SELECTED_TO_DATE = "selected_to_date";
    public static final String LOCATION_NOTIFICATION_TIME = "location_notification_time";
    public static final int FROM_DATE_REQUEST = 101;
    public static final int TO_DATE_REQUEST = 102;
    private static final int NOTIFICATION_TIME_REQUEST = 103;
    public static final int PERMISSIONS_REQUEST_CODE = 9001;

    private boolean mIsTrackingActivated;
    private boolean mIsNotificationActivated;
    private long mSelectedFromDate;
    private long mSelectedToDate;

    //notification time in minutes
    private int mLocationNotificationTime = 60;

    @Override
    protected void onStop() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putBoolean(LOCATION_TRACKING_STATE, mIsTrackingActivated);
        edit.putBoolean(NOTIFICATION_STATE, mIsNotificationActivated);
        edit.putInt(LOCATION_NOTIFICATION_TIME, mLocationNotificationTime);
        edit.apply();
        super.onStop();
    }

    @BindView(R.id.locationSwitch) Switch mLocationSwitch;
    @BindView(R.id.showMapButton) Button mMapButton;
    @BindView(R.id.selectFromDateButton) Button mFromDateButton;
    @BindView(R.id.selectToDateButton) Button mToDateButton;
    @BindView(R.id.fromDateTextView) TextView mFromDateTextView;
    @BindView(R.id.toDateTextView) TextView mToDateTextView;
    @BindView(R.id.notificationSwitch) Switch mNotificationSwitch;
    @BindView(R.id.notificationPeriodTextView) TextView mNotificationPeriodTextView;
    @BindView(R.id.selectNotificationPeriodButton) Button mNotificationPeriodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mIsTrackingActivated = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(LOCATION_TRACKING_STATE, false);
        mIsNotificationActivated = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(NOTIFICATION_STATE, false);
        mLocationNotificationTime = PreferenceManager.getDefaultSharedPreferences(this).getInt(LOCATION_NOTIFICATION_TIME, 60);
        int hour = mLocationNotificationTime/60;
        mNotificationPeriodTextView.setText(hour + ":" + convertMinuteToString(mLocationNotificationTime - hour*60));
        mLocationSwitch.setChecked(mIsTrackingActivated);
        mNotificationSwitch.setChecked(mIsNotificationActivated);
        mNotificationSwitch.setEnabled(mIsTrackingActivated);
        mSelectedFromDate = getCurrentDate(false);
        mSelectedToDate = getCurrentDate(true);
        mFromDateTextView.setText(getStringFromUnixTime(mSelectedFromDate));
        mToDateTextView.setText(getStringFromUnixTime(mSelectedToDate));

        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                    } else {
                        startPositionTracking();
                    }
                    } else {
                    mIsTrackingActivated = false;
                    if (mNotificationSwitch.isChecked()) {
                        Toast.makeText(MainActivity.this, "Notifications are not available when position tracking disactivated", Toast.LENGTH_SHORT).show();
                        mNotificationSwitch.setChecked(false);
                        mIsNotificationActivated = false;
                    }
                    mNotificationSwitch.setEnabled(false);
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    stopService(intent);
                }
            }
        });

        mNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mIsNotificationActivated = true;
                    //setNotification(true);
                } else {
                    mIsNotificationActivated = false;
                }
            }
        });
        mFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DatePickerActivity.class);
                intent.putExtra(USER_SELECTED_FROM_DATE, mSelectedFromDate);
                startActivityForResult(intent, FROM_DATE_REQUEST);

            }
        });

        mToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DatePickerActivity.class);
                intent.putExtra(USER_SELECTED_TO_DATE, mSelectedToDate);
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

        mNotificationPeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TimePickerActivity.class);
                startActivityForResult(intent, NOTIFICATION_TIME_REQUEST);
            }
        });
    }

    private void startPositionTracking() {
        mIsTrackingActivated = true;
        mNotificationSwitch.setEnabled(true);
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
    }

    private long getCurrentDate(boolean endOfDay) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get (Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour;
        int minute;
        if (endOfDay) {
            hour = 23;
            minute = 59;
        } else {
            hour = 0;
            minute = 0;
        }
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == FROM_DATE_REQUEST) {
                mSelectedFromDate = data.getLongExtra(DatePickerActivity.RESULT, 0);
                mFromDateTextView.setText(getStringFromUnixTime(mSelectedFromDate));
            }
            if (requestCode == TO_DATE_REQUEST) {
                mSelectedToDate = data.getLongExtra(DatePickerActivity.RESULT, 0);
                mToDateTextView.setText(getStringFromUnixTime(mSelectedToDate));
            }
            if (requestCode == NOTIFICATION_TIME_REQUEST){
                int hour = data.getIntExtra(TimePickerActivity.RESULT_HOUR, 0);
                int minute = data.getIntExtra(TimePickerActivity.RESULT_MINUTE, 0);
                mLocationNotificationTime = hour*60 + minute;
                mNotificationPeriodTextView.setText(hour + ":" + convertMinuteToString(minute));
            }
        }
    }

    private String convertMinuteToString(int minute) {
        if (minute >= 10) {
            return Integer.toString(minute);
        } else {
            return "0" + Integer.toString(minute);
        }
    }

    protected static String getStringFromUnixTime(long unixTime) {
        Date date = new Date (unixTime);
        Locale locale = Locale.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY", locale);
        return formatter.format(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startPositionTracking();
            } else {
                mIsTrackingActivated = false;
                mLocationSwitch.setChecked(false );
                mNotificationSwitch.setChecked(false);
                mNotificationSwitch.setEnabled(false);
                mIsNotificationActivated = false;
                Toast.makeText(this, "This app is not functional without location permissions. Please enable location permissions", Toast.LENGTH_LONG).show();
            }
        }

}

    private void setNotification (boolean isNotificationActivated) {
        if (isNotificationActivated) {
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            intent.putExtra(LOCATION_NOTIFICATION_TIME, mLocationNotificationTime);
            startService(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            startService(intent);
        }
    }
 }
