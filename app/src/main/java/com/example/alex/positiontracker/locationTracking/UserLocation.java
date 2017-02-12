package com.example.alex.positiontracker.locationTracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserLocation {
    private double mLatitude;
    private double mLongitude;
    long mTime;
    String mAddress;

    public String getAddress() {
        if (!mAddress.equals("null")) {
            return mAddress;
        } else {
            return "";
        }
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime() {
        Date date = new Date (mTime);
        Locale locale = Locale.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY h:mm a", locale);
        return formatter.format(date);
    }

    public void setTime(long time) {
        mTime = time;
    }

    public UserLocation (long time, double latitude, double longitude, String address) {
        mTime = time;
        mLatitude = latitude;
        mLongitude = longitude;
        mAddress = address;

    }

}
