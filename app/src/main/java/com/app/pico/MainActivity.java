package com.app.pico;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private StyledTextHeader logo;
    private StyledButton btnAlarms;
    private StyledButton btnLocations;
    private StyledButton btnSettings;

    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = (StyledTextHeader) findViewById(R.id.txtLogo);
        btnAlarms = (StyledButton) findViewById(R.id.btnAlarm);
        btnLocations = (StyledButton) findViewById(R.id.btnLocations);
        btnSettings = (StyledButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        btnAlarms.setOnClickListener(this);
        btnLocations.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnAlarms.setOnClickListener(null);
        btnLocations.setOnClickListener(null);
        btnSettings.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnAlarm:
                break;

            case R.id.btnLocations:
                break;

            case R.id.btnSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }

    }
}
