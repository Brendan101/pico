package com.app.pico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Rachel on 4/29/17.
 */

public class AlarmDBOperationsClass {
    AlarmDBHelperClass dbHelper;

    public AlarmDBOperationsClass(Context context) {
        dbHelper = new AlarmDBHelperClass(context);
    }

    public void addAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.EVENT_NAME, alarm.getEventName());
        values.put(dbHelper.REPEAT, alarm.getRepeat());
        values.put(dbHelper.PREP_TIME, alarm.getPrepTimeAsString());
        values.put(dbHelper.ARRIVAL_TIME, alarm.getArrivalTimeAsString());
        values.put(dbHelper.START_LAT, alarm.getStartLat());
        values.put(dbHelper.START_LON, alarm.getStartLon());
        values.put(dbHelper.END_LAT, alarm.getEndLat());
        values.put(dbHelper.END_LON, alarm.getEndLon());

        // Insert Alarm
        db.insert(dbHelper.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Iterate over query results and add to list
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setID(Integer.parseInt(cursor.getString(0)));
                alarm.setEventName(cursor.getString(1));
                alarm.setRepeat(cursor.getInt(2));
                alarm.setPrepTime(cursor.getString(3));
                alarm.setArrivalTime(cursor.getString(4));
                alarm.setStartLat(cursor.getFloat(5));
                alarm.setStartLon(cursor.getFloat(6));
                alarm.setEndLat(cursor.getFloat(7));
                alarm.setEndLon(cursor.getFloat(8));

                // Add Alarm to list
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        return alarmList;
    }

    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.TABLE_NAME, dbHelper.ID + " = ?",
                new String[] { String.valueOf(alarm.getID()) });
        db.close();
    }
}
