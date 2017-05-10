package com.app.pico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TempAlarmReceiver extends BroadcastReceiver {
    public TempAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"wake up", Toast.LENGTH_SHORT).show();

        String command = intent.getStringExtra("command");

        if (command.equals("alarm")) {
            // TODO verify functionality
            Intent setAlarm = new Intent(context, TrafficService.class);
            setAlarm.putExtra("command", "check");
            setAlarm.putExtra("alarm", intent.getExtras().getSerializable("alarm"));
            context.startService(setAlarm);
        }
    }
}
