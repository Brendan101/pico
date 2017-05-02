package com.app.pico;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class AlertActivity extends AppCompatActivity implements View.OnClickListener{

    Button snoozeButton;
    Button offButton;

    ImageView logoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        snoozeButton = (Button) findViewById(R.id.btnSnooze);
        offButton = (Button) findViewById(R.id.btnOff);

        logoView = (ImageView) findViewById(R.id.logoImage);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.pico_symbols_bird_sun);
        Bitmap logoScaled = Bitmap.createScaledBitmap(logo, 256, 256, true);
        logoView.setImageBitmap(logoScaled);

    }

    @Override
    protected void onResume() {
        super.onResume();
        snoozeButton.setOnClickListener(this);
        offButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        snoozeButton.setOnClickListener(null);
        offButton.setOnClickListener(null);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnSnooze:
                //TODO snooze alarm
                break;

            case R.id.btnOff:
                //TODO turn off alarm
                break;

        }

    }
}
