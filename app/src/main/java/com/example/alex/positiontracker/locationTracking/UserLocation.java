package com.example.alex.positiontracker.locationTracking;

public class UserLocation {
    private double mLatitude;
    private double mLongitude;
    long mTime;
    String mAddress;

    public String getAddress() {
        return mAddress;
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
