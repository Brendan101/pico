package com.app.pico;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    boolean suRep, moRep, tuRep, weRep, thRep, frRep, saRep;
    Integer snoozeTime;
    String sound;

    ToggleButton suTog, moTog, tuTog, weTog, thTog, frTog, saTog;
    NumberPicker snoozePicker;
    Spinner soundSpinner;

    ImageView logoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        suRep = false;
        moRep = false;
        tuRep = false;
        weRep = false;
        thRep = false;
        frRep = false;
        saRep = false;
        snoozeTime = 5;
        sound = "";

        suTog = (ToggleButton) findViewById(R.id.sundayToggle);
        moTog = (ToggleButton) findViewById(R.id.mondayToggle);
        tuTog = (ToggleButton) findViewById(R.id.tuesdayToggle);
        weTog = (ToggleButton) findViewById(R.id.wednesdayToggle);
        thTog = (ToggleButton) findViewById(R.id.thursdayToggle);
        frTog = (ToggleButton) findViewById(R.id.fridayToggle);
        saTog = (ToggleButton) findViewById(R.id.saturdayToggle);

        snoozePicker = (NumberPicker) findViewById(R.id.numberPicker);
        snoozePicker.setMinValue(1);
        snoozePicker.setMaxValue(60);
        snoozePicker.setValue(5);
        snoozePicker.setWrapSelectorWheel(true);

        soundSpinner = (Spinner) findViewById(R.id.soundSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sounds_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundSpinner.setAdapter(adapter);

        logoView = (ImageView) findViewById(R.id.logoImage);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.pico_symbols_bird);
        Bitmap logoScaled = Bitmap.createScaledBitmap(logo, 81, 96, true);
        logoView.setImageBitmap(logoScaled);
    }

    @Override
    protected void onResume() {
        super.onResume();

        suTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    suRep = true;
                } else {
                    suRep = false;
                }
            }
        });
        moTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    moRep = true;
                } else {
                    moRep = false;
                }
            }
        });
        tuTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tuRep = true;
                } else {
                    tuRep = false;
                }
            }
        });
        weTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weRep = true;
                } else {
                    weRep = false;
                }
            }
        });
        thTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    thRep = true;
                } else {
                    thRep = false;
                }
            }
        });
        frTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    frRep = true;
                } else {
                    frRep = false;
                }
            }
        });
        saTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saRep = true;
                } else {
                    saRep = false;
                }
            }
        });

        snoozePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                snoozeTime = newVal;
            }
        });

        soundSpinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        suTog.setOnCheckedChangeListener(null);
        moTog.setOnCheckedChangeListener(null);
        tuTog.setOnCheckedChangeListener(null);
        weTog.setOnCheckedChangeListener(null);
        thTog.setOnCheckedChangeListener(null);
        frTog.setOnCheckedChangeListener(null);
        saTog.setOnCheckedChangeListener(null);

        snoozePicker.setOnValueChangedListener(null);

        soundSpinner.setOnItemSelectedListener(null);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sound = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        sound = "";
    }
}

