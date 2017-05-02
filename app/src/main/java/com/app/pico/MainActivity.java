package com.app.pico;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private StyledButton btnAlarms;
    private StyledButton btnLocations;
    private StyledButton btnSettings;

    private Handler myHandler = new Handler();

    DBOperationsClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBOperationsClass(this);
        this.deleteDatabase(DBHelperClass.DB_NAME);
        //createTestLocation();
        //createTestAlarm();

        btnAlarms = (StyledButton) findViewById(R.id.btnAlarm);
        btnLocations = (StyledButton) findViewById(R.id.btnLocations);
        btnSettings = (StyledButton) findViewById(R.id.btnSettings);

    }

    @Override
    protected void onResume() {
        super.onResume();
        btnAlarms.setOnClickListener(this);
        btnLocations.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnAlarms.setOnClickListener(null);
        btnLocations.setOnClickListener(null);
        btnSettings.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnAlarm:
                Intent alarmListInt = new Intent(MainActivity.this, AlarmListActivity.class);
                startActivity(alarmListInt);
                break;

            case R.id.btnLocations:
                Intent locationListInt = new Intent(MainActivity.this, LocationListActivity.class);
                locationListInt.putExtra("parent", "main");
                startActivity(locationListInt);
                break;

            case R.id.btnSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }
    }

    private void createTestAlarm() {
        Alarm alarm = new Alarm();
        alarm.setEventName("Another One");
        alarm.setActive(false);
        alarm.setRepeatable(false);
        alarm.setPrepTime(10);
        alarm.setArrivalTime("05/10/2017 11:40 AM");
        alarm.setStartLocation("Current Location");
        alarm.setEndLocation("Default Location");
        db.addAlarm(alarm);
    }

    private void createTestLocation() {
        Location location = new Location();
        location.setLocationName("Chicago");
        location.setLatitude(0);
        location.setLongitude(0);
        db.addLocation(location);
    }
}
