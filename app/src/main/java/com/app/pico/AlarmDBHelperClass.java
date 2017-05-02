package com.app.pico;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rachel on 4/29/17.
 */

public class AlarmDBHelperClass extends SQLiteOpenHelper{
    static final String DB_NAME = "pico";
    static final int DB_VERSION = 1;

    static final String TABLE_NAME = "alarms";

    // Column names
    static final String ID = "id";
    static final String EVENT_NAME = "event_name";
    static final String ACTIVE = "active";
    static final String REPEATABLE = "repeatable";
    static final String PREP_TIME = "prep_time";
    static final String ARRIVAL_TIME = "arrival_time";
    static final String START_LAT = "start_lat";
    static final String START_LON = "start_lon";
    static final String END_LAT = "end_lat";
    static final String END_LON = "end_lon";

    public AlarmDBHelperClass(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table_string = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY," + EVENT_NAME + " TEXT," + ACTIVE + " INTEGER,"
                + REPEATABLE + " INTEGER," + PREP_TIME + "  INTEGER," + ARRIVAL_TIME + " TEXT,"
                + START_LAT + " REAL," + START_LON + " REAL," + END_LAT + " REAL,"
                + END_LON + " REAL" + ")";
        sqLiteDatabase.execSQL(create_table_string);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
