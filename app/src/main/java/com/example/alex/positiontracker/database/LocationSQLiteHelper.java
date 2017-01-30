package com.example.alex.positiontracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LocationSQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "position.db";
    public static final int DB_VERSION = 1;

    public static final String USER_POSITIONS_TABLE = "USER_POSITIONS";
    public static final String COLUMN_TIME = "POSITION_TIME";
    public static final String COLUMN_LATITUDE = "LATITUDE";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";
    public static final String COLUMN_ADDRESS = "ADDRESS";
    public static final String CREATE_USER_POSITIONS_TABLE =
            "CREATE TABLE " + USER_POSITIONS_TABLE + "(" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIME + " INTEGER, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL, " +
                    COLUMN_ADDRESS + " TEXT" + ")";

    public LocationSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_POSITIONS_TABLE);
       }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
