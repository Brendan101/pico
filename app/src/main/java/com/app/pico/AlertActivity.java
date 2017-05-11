package com.app.pico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class AlertActivity extends AppCompatActivity implements View.OnClickListener{

    public static final long MIN_IN_MILLIS = 60 * 1000;
    public static final String BUZZER = "Buzzer";
    public static final String CHURCH_CHIME = "Church Chime";
    public static final String MEADOWLARK = "Meadowlark";
    public static final String POLICE_SIREN = "Police Siren";

    Button snoozeButton;
    Button offButton;

    ImageView logoView;

    Intent intent;
    Alarm alarm;

    MediaPlayer mPlayer;
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
        if(!(mPlayer == null) && mPlayer.isPlaying()){
            mPlayer.stop();
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

        String sound = alarm.getSound();

        //play default alarm if no sound was chosen
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mPlayer = MediaPlayer.create(getApplicationContext(), defaultSound);

        switch(sound){

            case BUZZER:
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.buzzer);
                break;

            case CHURCH_CHIME:
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.church_chime);
                break;

            case MEADOWLARK:
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.meadowlark);
                break;

            case POLICE_SIREN:
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.police_siren);
                break;
        }

        mPlayer.start();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnSnooze:

                if(!(mPlayer == null) && mPlayer.isPlaying()){
                    mPlayer.stop();
                }

                //Make intent to the broadcast receiver again after snooze time has passed
                Intent setAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
                setAlarm.putExtra("alarm", alarm);
                setAlarm.putExtra("command", "alarm");
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), setAlarm, 0);
                AlarmManager myAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                myAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarm.getSnoozeTime() * MIN_IN_MILLIS, alarmIntent);

                finish();
                break;

            case R.id.btnOff:

                if(!(mPlayer == null) && mPlayer.isPlaying()){
                    mPlayer.stop();
                }


                //TODO set up repeat. Just add a number of days here and call traffic service with new time
                //repeat is a array of bools Su-Sa (cast it from string)

                finish();
                break;

        }

    }
}
