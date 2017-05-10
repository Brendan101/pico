package com.app.pico;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by brena on 5/10/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public AlarmReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        String command = intent.getStringExtra("command");

        if (command.equals("alarm")) {

            Intent alarmIntent = new Intent(context, AlertActivity.class);
            alarmIntent.putExtra("command", "check");
            alarmIntent.putExtra("alarm", intent.getExtras().getSerializable("alarm"));
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);

        }

    }

}
