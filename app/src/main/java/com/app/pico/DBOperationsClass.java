package com.app.pico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Rachel on 4/29/17.
 */

public class DBOperationsClass {
    DBHelperClass dbHelper;

    public DBOperationsClass(Context context) {
        dbHelper = new DBHelperClass(context);
    }

    public void addAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.ALARM_EVENT_NAME, alarm.getEventName());
        values.put(dbHelper.ALARM_ACTIVE, alarm.isActive());
        values.put(dbHelper.ALARM_PREP_TIME, alarm.getPrepTime());
        values.put(dbHelper.ALARM_ARRIVAL_TIME, alarm.getArrivalTimeAsString());
        values.put(dbHelper.ALARM_START_LOCATION, alarm.getStartLocation());
        values.put(dbHelper.ALARM_END_LOCATION, alarm.getEndLocation());
        values.put(dbHelper.ALARM_REPEAT, alarm.getRepeat());
        values.put(dbHelper.ALARM_SNOOZE_TIME, alarm.getSnoozeTime());
        values.put(dbHelper.ALARM_SOUND, alarm.getSound());

        // Insert Alarm
        db.insert(dbHelper.ALARM_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + dbHelper.ALARM_TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Iterate over query results and add to list
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setID(Integer.parseInt(cursor.getString(0)));
                alarm.setEventName(cursor.getString(1));
                alarm.setActive(cursor.getInt(2));
                alarm.setPrepTime(cursor.getInt(3));
                alarm.setArrivalTime(cursor.getString(4));
                alarm.setStartLocation(cursor.getString(5));
                alarm.setEndLocation(cursor.getString(6));
                alarm.setRepeat(cursor.getString(7));
                alarm.setSnoozeTime(cursor.getInt(8));
                alarm.setSound(cursor.getString(9));

                // Add Alarm to list
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        return alarmList;
    }

    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.ALARM_TABLE_NAME, dbHelper.ALARM_ID + " = ?",
                new String[] { String.valueOf(alarm.getID()) });
        db.close();
    }

    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.ALARM_EVENT_NAME, alarm.getEventName());
        values.put(dbHelper.ALARM_ACTIVE, alarm.isActive());
        values.put(dbHelper.ALARM_PREP_TIME, alarm.getPrepTime());
        values.put(dbHelper.ALARM_ARRIVAL_TIME, alarm.getArrivalTimeAsString());
        values.put(dbHelper.ALARM_START_LOCATION, alarm.getStartLocation());
        values.put(dbHelper.ALARM_END_LOCATION, alarm.getEndLocation());
        values.put(dbHelper.ALARM_REPEAT, alarm.getRepeat());
        values.put(dbHelper.ALARM_SNOOZE_TIME, alarm.getSnoozeTime());
        values.put(dbHelper.ALARM_SOUND, alarm.getSound());

        // Update Alarm
        db.update(dbHelper.ALARM_TABLE_NAME, values, dbHelper.ALARM_ID + "=" + alarm.getID(), null);
        db.close();
    }

    // Location Queries //

    public void addLocation(Location location) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.LOCATION_NAME, location.getLocationName());
        values.put(dbHelper.LOCATION_LATITUDE, location.getLatitude());
        values.put(dbHelper.LOCATION_LONGITUDE, location.getLongitude());

        // Insert Location
        db.insert(dbHelper.LOCATION_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locationList = new ArrayList<Location>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + dbHelper.LOCATION_TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Iterate over query results and add to list
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setID(Integer.parseInt(cursor.getString(0)));
                location.setLocationName(cursor.getString(1));
                location.setLatitude(cursor.getFloat(2));
                location.setLongitude(cursor.getFloat(3));

                // Add Location to list
                locationList.add(location);
            } while (cursor.moveToNext());
        }

        return locationList;
    }

    public void deleteLocation(Location location) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.LOCATION_TABLE_NAME, dbHelper.LOCATION_ID + " = ?",
                new String[] { String.valueOf(location.getID()) });
        db.close();
    }

    public void updateLocation(Location location) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.LOCATION_NAME, location.getLocationName());
        values.put(dbHelper.LOCATION_LATITUDE, location.getLatitude());
        values.put(dbHelper.LOCATION_LONGITUDE, location.getLongitude());

        // Update Alarm
        db.update(dbHelper.LOCATION_TABLE_NAME, values, dbHelper.LOCATION_ID + "=" + location.getID(), null);
        db.close();
    }
}
