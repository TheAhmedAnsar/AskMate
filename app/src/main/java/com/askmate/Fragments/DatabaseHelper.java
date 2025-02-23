package com.askmate.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.askmate.Fragments.CoachingLocation;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "coachingNearby.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "coaching_locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_KEY + " TEXT UNIQUE, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_LATITUDE + " REAL)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void addCoachingLocation(String key, double longitude, double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the location already exists in the database
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_KEY + " = ?", new String[]{key}, null, null, null);
        if (cursor.getCount() > 0) {
            // Location already exists, update the existing record
            ContentValues values = new ContentValues();
            values.put(COLUMN_LONGITUDE, longitude);
            values.put(COLUMN_LATITUDE, latitude);
            db.update(TABLE_NAME, values, COLUMN_KEY + " = ?", new String[]{key});
        } else {
            // Location doesn't exist, insert a new record
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY, key);
            values.put(COLUMN_LONGITUDE, longitude);
            values.put(COLUMN_LATITUDE, latitude);
            db.insert(TABLE_NAME, null, values);
        }

        cursor.close();
        db.close();
    }
    public void updateCoachingLocation(String key, double longitude, double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LATITUDE, latitude);

        db.update(TABLE_NAME, values, COLUMN_KEY + " = ?", new String[]{key});
        db.close();
    }

    public void removeCoachingLocation(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_KEY + " = ?", new String[]{key});
        db.close();
    }

    @SuppressLint("Range")
    public List<CoachingLocation> getAllCoachingLocations() {
        List<CoachingLocation> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                CoachingLocation location = new CoachingLocation();
                location.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                location.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                locations.add(location);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locations;
    }
}
