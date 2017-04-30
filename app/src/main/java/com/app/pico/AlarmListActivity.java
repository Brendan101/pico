package com.app.pico;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

    List<Alarm> alarms;
    AlarmDBOperationsClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        db = new AlarmDBOperationsClass(this);
        alarms = db.getAllAlarms();

        AlarmArrayAdaptor adaptor = new AlarmArrayAdaptor(AlarmListActivity.this, alarms);
        ListView alarmListView = (ListView)findViewById(R.id.list_alarms);
        alarmListView.setAdapter(adaptor);

    }
}
