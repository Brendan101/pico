package com.app.pico;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rachel on 4/29/17.
 */

public class DBHelperClass extends SQLiteOpenHelper{
    static final String DB_NAME = "pico";
    static final int DB_VERSION = 1;

    // Alarm Table and Columns
    static final String ALARM_TABLE_NAME = "alarms";
    static final String ALARM_ID = "id";
    static final String ALARM_EVENT_NAME = "event_name";
    static final String ALARM_ACTIVE = "active";
    static final String ALARM_PREP_TIME = "prep_time";
    static final String ALARM_ARRIVAL_TIME = "arrival_time";
    static final String ALARM_START_LOCATION = "start_location";
    static final String ALARM_END_LOCATION = "end_location";
    static final String ALARM_REPEAT = "repeat";
    static final String ALARM_SNOOZE_TIME = "snooze_time";
    static final String ALARM_SOUND = "sound";

    // Location Table and Columns
    static final String LOCATION_TABLE_NAME = "locations";
    static final String LOCATION_ID = "id";
    static final String LOCATION_NAME = "location_name";
    static final String LOCATION_LATITUDE = "latitude";
    static final String LOCATION_LONGITUDE = "longitude";

    public DBHelperClass(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_alarm_table_string = "CREATE TABLE " + ALARM_TABLE_NAME + "("
                + ALARM_ID + " INTEGER PRIMARY KEY," + ALARM_EVENT_NAME + " TEXT," + ALARM_ACTIVE + " INTEGER,"
                + ALARM_PREP_TIME + "  INTEGER," + ALARM_ARRIVAL_TIME + " TEXT,"
                + ALARM_START_LOCATION + " INTEGER," + ALARM_END_LOCATION + " INTEGER,"
                + ALARM_REPEAT + " TEXT," + ALARM_SNOOZE_TIME + " INTEGER,"
                + ALARM_SOUND + " TEXT" + ")";

        String create_location_table_string = "CREATE TABLE " + LOCATION_TABLE_NAME + "("
                + LOCATION_ID + " INTEGER PRIMARY KEY," + LOCATION_NAME + " TEXT,"
                + LOCATION_LATITUDE + " REAL," + LOCATION_LONGITUDE + " REAL" + ")";

        sqLiteDatabase.execSQL(create_alarm_table_string);
        sqLiteDatabase.execSQL(create_location_table_string);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
