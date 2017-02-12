package com.example.alex.positiontracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.alex.positiontracker.locationTracking.UserLocation;

import java.util.ArrayList;

public class LocationDataSource {
    private LocationSQLiteHelper mLocationSQLiteHelper;

    public LocationDataSource (Context context) {
        mLocationSQLiteHelper = new LocationSQLiteHelper(context);
    }


    public SQLiteDatabase open () {return mLocationSQLiteHelper.getWritableDatabase();
    }

    private void close (SQLiteDatabase database) {
        database.close();
    }


    public void addItem (UserLocation location) {
        double latitude = 0;
        double longitude = 0;
        SQLiteDatabase database = open();
        database.beginTransaction();

        // check that previous position is not the same
        Cursor cursor = database.query(LocationSQLiteHelper.USER_POSITIONS_TABLE,
                new String[] {BaseColumns._ID, LocationSQLiteHelper.COLUMN_LONGITUDE, LocationSQLiteHelper.COLUMN_LATITUDE},
                null, null, null, null,
                BaseColumns._ID +" DESC", "1");
        if (cursor.moveToLast()) {
        latitude = getDoubleFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LATITUDE);
        longitude = getDoubleFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LONGITUDE);
         }

        if (latitude != location.getLatitude() && longitude != location.getLongitude()) {

            ContentValues todoItemValue = new ContentValues();
            todoItemValue.put(LocationSQLiteHelper.COLUMN_TIME, location.getTime());
            todoItemValue.put(LocationSQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
            todoItemValue.put(LocationSQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
            todoItemValue.put(LocationSQLiteHelper.COLUMN_ADDRESS, location.getAddress());

            database.insert(LocationSQLiteHelper.USER_POSITIONS_TABLE, null, todoItemValue);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        }

    private double getDoubleFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(columnIndex);
    }

    private long getLongFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getLong(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }


    public long getLastLocationTime() {
        long time = 0;
        SQLiteDatabase database = open();
        database.beginTransaction();
        Cursor cursor = database.query(LocationSQLiteHelper.USER_POSITIONS_TABLE,
                new String[] {LocationSQLiteHelper.COLUMN_TIME},
                null, null, null, null,
                BaseColumns._ID +" DESC", "1");
        if (cursor.moveToLast()) {
            time = getLongFromColumnName(cursor, LocationSQLiteHelper.COLUMN_TIME);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return time;
    }

    public ArrayList<UserLocation> getLocationsForTimePeriod(long fromDate, long toDate) {
        ArrayList <UserLocation> locations = new ArrayList<>();
        String fromDateAsString = Long.toString(fromDate);
        String toDateAsString = Long.toString(toDate);
        double latitude;
        double longitude;
        long time;
        String address;

        SQLiteDatabase database = open();
        database.beginTransaction();
        Cursor cursor = database.query(LocationSQLiteHelper.USER_POSITIONS_TABLE,
                new String[] {BaseColumns._ID, LocationSQLiteHelper.COLUMN_LONGITUDE, LocationSQLiteHelper.COLUMN_LATITUDE, LocationSQLiteHelper.COLUMN_ADDRESS, LocationSQLiteHelper.COLUMN_TIME},
                LocationSQLiteHelper.COLUMN_TIME + "> ? AND " + LocationSQLiteHelper.COLUMN_TIME + "< ?",
                new String[] {fromDateAsString, toDateAsString},
                null,
                null,
                BaseColumns._ID +" ASC");
        if (cursor.moveToFirst()) {
            do {
                latitude = getDoubleFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LATITUDE);
                longitude = getDoubleFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LONGITUDE);
                time = getLongFromColumnName(cursor, LocationSQLiteHelper.COLUMN_TIME);
                address = getStringFromColumnName(cursor, LocationSQLiteHelper.COLUMN_ADDRESS);
                UserLocation location = new UserLocation(time, latitude, longitude, address);
                locations.add(location);
            } while (cursor.moveToNext());
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return locations;
    }
}
