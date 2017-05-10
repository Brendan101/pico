package com.app.pico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RecheckTrafficReceiver extends BroadcastReceiver {

    public RecheckTrafficReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        String command = intent.getStringExtra("command");

        if (command.equals("alarm")) {
            Intent setAlarm = new Intent(context, TrafficService.class);
            setAlarm.putExtra("command", "check");
            setAlarm.putExtra("alarm", intent.getExtras().getSerializable("alarm"));
            context.startService(setAlarm);
        }
    }
}
