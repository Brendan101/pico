package com.app.pico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ArrayList<Alarm> alarms;
    AlarmDBOperationsClass db;
    AlarmArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        db = new AlarmDBOperationsClass(this);
        alarms = db.getAllAlarms();

        adapter = new AlarmArrayAdapter(AlarmListActivity.this, alarms);
        ListView alarmListView = (ListView)findViewById(R.id.list_alarms);
        alarmListView.setAdapter(adapter);

        alarmListView.setOnItemLongClickListener(this);
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
                Toast.makeText(AlarmListActivity.this, "Deleted " + alarm.getEventName(), Toast.LENGTH_LONG).show();
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
}
