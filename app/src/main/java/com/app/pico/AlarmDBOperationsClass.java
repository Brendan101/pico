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
        values.put(dbHelper.ACTIVE, alarm.isActive());
        values.put(dbHelper.REPEATABLE, alarm.isRepeatable());
        values.put(dbHelper.PREP_TIME, alarm.getPrepTime());
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
                alarm.setActive(cursor.getInt(2));
                alarm.setRepeatable(cursor.getInt(3));
                alarm.setPrepTime(cursor.getInt(4));
                alarm.setArrivalTime(cursor.getString(5));
                alarm.setStartLat(cursor.getFloat(6));
                alarm.setStartLon(cursor.getFloat(7));
                alarm.setEndLat(cursor.getFloat(8));
                alarm.setEndLon(cursor.getFloat(9));

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
