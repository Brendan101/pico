package com.app.pico;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
            displayNotification(context, alarm);
            context.startActivity(alarmIntent);
        }
    }

    private void displayNotification(Context context, Alarm alarm) {
        // TODO: Get ringtone type
        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.pico_symbols_bird_sun_notification)
                        .setContentTitle(alarm.getEventName())
                        .setContentText("Time to leave for your destination!")
                        .setAutoCancel(true)
                        //.setSound(soundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, AlertActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AlertActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(alarm.getID(), mBuilder.build());

        // Wake up the screen
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if(!isScreenOn)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "WakeLock");
            wl.acquire(10000);
        }
    }

}
