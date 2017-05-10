package com.app.pico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class AlertActivity extends AppCompatActivity implements View.OnClickListener{

    public static final long MIN_IN_MILLIS = 60000;

    Button snoozeButton;
    Button offButton;

    ImageView logoView;

    Intent intent;
    Alarm alarm;

    Ringtone ringtone;
    boolean played;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        snoozeButton = (Button) findViewById(R.id.btnSnooze);
        offButton = (Button) findViewById(R.id.btnOff);

        logoView = (ImageView) findViewById(R.id.wakeUpImage);

        intent = getIntent();
        alarm = (Alarm)intent.getExtras().getSerializable("alarm");

        played = false;

        //so we can wake the phone up
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "AlertActivity");
        //and ignore lock screen
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    }

    @Override
    protected void onResume() {
        super.onResume();
        snoozeButton.setOnClickListener(this);
        offButton.setOnClickListener(this);

        //wake up
        wakeLock.acquire();

        //only play alarm when this activity is created
        if(!played) {
            SoundTask makeSound = new SoundTask();
            makeSound.execute();
            played = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        snoozeButton.setOnClickListener(null);
        offButton.setOnClickListener(null);
        if(!(ringtone == null) && ringtone.isPlaying()){
            ringtone.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    private class SoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //wait for it to be active
            while (!window.isActive()) {}
            playSound();
            return null;
        }

    }

    private void playSound(){
        //TODO do something like this to get the user-chosen sound
        //Uri ringURI = alarm.getSound();
        //for now use default
        Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringURI);
        ringtone.play();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnSnooze:

                if(!(ringtone == null) && ringtone.isPlaying()){
                    ringtone.stop();
                }

                //Make intent to the broadcast receiver again after snooze time has passed
                Intent setAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
                setAlarm.putExtra("alarm", alarm);
                setAlarm.putExtra("command", "alarm");
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), setAlarm, 0);
                AlarmManager myAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                Log.d("Snooze", Integer.toString(alarm.getSnoozeTime()));
                myAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarm.getSnoozeTime() * MIN_IN_MILLIS, alarmIntent);

                finish();
                break;

            case R.id.btnOff:

                if(!(ringtone == null) && ringtone.isPlaying()){
                    ringtone.stop();
                }

                finish();
                break;

        }

    }
}
