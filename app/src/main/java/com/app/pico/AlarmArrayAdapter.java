package com.app.pico;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wordNumber on 4/27/17.
 */

public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

    private static final int ROW_LAYOUT_ID = R.layout.listview_item_row;

    Context context;
    List<Alarm> alarmList;
    DBOperationsClass db;

    public AlarmArrayAdapter(Context context, List<Alarm> alarmList) {
        super(context, ROW_LAYOUT_ID, alarmList);
        this.context = context;
        this.alarmList = alarmList;
        this.db = new DBOperationsClass(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        AlarmHolder alarmholder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(ROW_LAYOUT_ID, parent, false);

        alarmholder = new AlarmHolder();
        alarmholder.alarm = alarmList.get(position);
        alarmholder.active = (StyledSwitch) row.findViewById(R.id.activeSwitch);
        alarmholder.active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the Alarm in the database to be active
                AlarmHolder holder = (AlarmHolder) view.getTag();
                Alarm alarm = holder.alarm;
                if(alarm.isActive()) {
                    //cancel alarm
                    alarm.setActive(false);
                    Intent cancelAlarm = new Intent(context, TrafficService.class);
                    cancelAlarm.putExtra("command", "cancel");
                    cancelAlarm.putExtra("alarm", alarmList.get(position));
                    context.startService(cancelAlarm);
                }
                else {
                    //set alarm
                    alarm.setActive(true);
                    Intent setAlarm = new Intent(context, TrafficService.class);
                    setAlarm.putExtra("command", "initial");
                    setAlarm.putExtra("alarm", alarmList.get(position));
                    context.startService(setAlarm);
                }
                db.updateAlarm(alarm);
            }
        });
        alarmholder.active.setTag(alarmholder);

        alarmholder.name = (TextView)row.findViewById(R.id.eventNameView);
        alarmholder.readyTime = (TextView)row.findViewById(R.id.timeReadyView);

        row.setTag(alarmholder);

        setupItem(alarmholder);
        return row;
    }

    private void setupItem(AlarmHolder alarmHolder)
    {
        alarmHolder.name.setText(alarmHolder.alarm.getEventName());
        alarmHolder.readyTime.setText(alarmHolder.alarm.getPrepTimeForDisplay());
        alarmHolder.active.setChecked(alarmHolder.alarm.isActive());
    }

    public static class AlarmHolder
    {
        Alarm alarm;
        TextView name;
        TextView readyTime;
        StyledSwitch active;
    }
}

