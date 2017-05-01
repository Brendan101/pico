package com.app.pico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    AlarmDBOperationsClass db;

    TextView alarmHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        db = new AlarmDBOperationsClass(this);

        alarmHeader = (TextView) findViewById(R.id.singleAlarmHeader);

        Bundle intentData = getIntent().getExtras();

        // Todo: do this in a handler?
        if(intentData != null) {
            Alarm alarm = (Alarm) intentData.getSerializable("alarm");
            alarmHeader.setText(alarm.getEventName());
        }
        else {
            alarmHeader.setText("New Alarm");
        }
    }
}
