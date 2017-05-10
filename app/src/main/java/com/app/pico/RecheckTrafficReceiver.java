package com.app.pico;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class RecheckTrafficReceiver extends WakefulBroadcastReceiver {

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
