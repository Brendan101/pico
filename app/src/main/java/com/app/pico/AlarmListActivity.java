package com.app.pico;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    final int BROADCAST = 100;

    ArrayList<Alarm> alarms;
    DBOperationsClass db;
    AlarmArrayAdapter adapter;
    ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        db = new DBOperationsClass(this);
        alarms = db.getAllAlarms();

        alarmListView = (ListView)findViewById(R.id.list_alarms);

        // Add "Add Alarm" footer to the list view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.listview_footer, alarmListView, false);
        alarmListView.addFooterView(footer, null, true);

        ImageButton newAlarmBtn = (ImageButton) footer.findViewById(R.id.addAlarmBtn);
        newAlarmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Route to the new alarm page
                Intent alarmInt = new Intent(AlarmListActivity.this, AlarmActivity.class);
                startActivity(alarmInt);
            }
        });

        alarmListView.setOnItemClickListener(this);
        alarmListView.setOnItemLongClickListener(this);

        populateAlarmList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the list
        populateAlarmList();
    }

    private void populateAlarmList() {
        alarms = db.getAllAlarms();
        adapter = new AlarmArrayAdapter(AlarmListActivity.this, alarms);
        alarmListView.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Alarm alarm = alarms.get(i);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmation!");
        alert.setMessage("Are you sure you want to delete " + alarm.getEventName() + " ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteAlarm(alarm);
                adapter.remove(alarm);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Alarm alarm = alarms.get(i);
        Intent alarmInt = new Intent(AlarmListActivity.this, AlarmActivity.class);
        alarmInt.putExtra("alarm", alarm);
        startActivity(alarmInt);
    }
}
