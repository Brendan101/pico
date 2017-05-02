package com.app.pico;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private StyledTextHeader logo;
    private StyledButton btnAlarms;
    private StyledButton btnLocations;
    private StyledButton btnSettings;

    private Handler myHandler = new Handler();

    AlarmDBOperationsClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new AlarmDBOperationsClass(this);
        //this.deleteDatabase(AlarmDBHelperClass.DB_NAME);
        //createTestAlarm();

        logo = (StyledTextHeader) findViewById(R.id.txtLogo);
        btnAlarms = (StyledButton) findViewById(R.id.btnAlarm);
        btnLocations = (StyledButton) findViewById(R.id.btnLocations);
        btnSettings = (StyledButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

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
        alarm.setStartLat(10.34f);
        alarm.setStartLon(12.34f);
        alarm.setEndLat(5.35f);
        alarm.setEndLon(676.34f);
        db.addAlarm(alarm);
    }
}
