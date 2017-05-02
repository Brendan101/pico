package com.app.pico;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    boolean suRep, moRep, tuRep, weRep, thRep, frRep, saRep;
    //List<String> daysSelected;
    boolean[] daysSelected;
    String[] daysOrdered;

    Integer snoozeTime;
    String sound;

    // Repeat day picker
    RelativeLayout repeatRow;
    int currRepeatVis;
    ToggleButton suTog, moTog, tuTog, weTog, thTog, frTog, saTog;
    ToggleButton[] togArray;
    StyledTextView txtRepeatDays;

    // Snooze time picker
    LinearLayout snoozePicker;
    Button btnLeftTimeInc;
    Button btnRightTimeInc;
    StyledTextView txtSnoozeTime;

    Spinner soundSpinner;

    ImageView logoView;

    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // we do this to ensure our days remain in order in the UI
        daysSelected = new boolean[]{false, false, false, false, false, false, false};
        daysOrdered = new String[]{"S","M", "T", "W", "T", "F", "S"};

        snoozeTime = 5;
        sound = "";

        repeatRow = (RelativeLayout) findViewById(R.id.repeat_row);
        snoozePicker = (LinearLayout) findViewById(R.id.repeatPicker);
        txtRepeatDays = (StyledTextView) findViewById(R.id.txtRepeatDays);
        suTog = (ToggleButton) findViewById(R.id.sundayToggle);
        moTog = (ToggleButton) findViewById(R.id.mondayToggle);
        tuTog = (ToggleButton) findViewById(R.id.tuesdayToggle);
        weTog = (ToggleButton) findViewById(R.id.wednesdayToggle);
        thTog = (ToggleButton) findViewById(R.id.thursdayToggle);
        frTog = (ToggleButton) findViewById(R.id.fridayToggle);
        saTog = (ToggleButton) findViewById(R.id.saturdayToggle);

        togArray = new ToggleButton[]{suTog, moTog, tuTog, weTog, thTog, frTog, saTog};
        myHandler.post(new VisibilityRunnable(View.GONE, snoozePicker));

        // make repeat row toggle the repeat picker
        currRepeatVis = View.GONE;
        repeatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currRepeatVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, snoozePicker));
                    currRepeatVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, snoozePicker));
                    currRepeatVis = View.GONE;
                }

            }
        });

        // bulk declare all toggle button listeners in a loop
        // TODO possibly need lock
        for (int i = 0; i < togArray.length; i++){
            final int index = i;
            togArray[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        daysSelected[index] = true;
                    } else {
                        daysSelected[index] = false;
                    }
                    myHandler.post(new RepeatRunnable(daysSelected));
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sound = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        sound = "";
    }

    private class VisibilityRunnable implements Runnable {
        private int _visibility;
        private View _view;

        public VisibilityRunnable(int visibility, View view){
            this._visibility = visibility;
            this._view = view;
        }

        @Override
        public void run() {
            this._view.setVisibility(this._visibility);
        }
    }

    private class RepeatRunnable implements Runnable{
        private boolean[] _daysSelected;
        private String resultString = "";

        public RepeatRunnable (boolean[] daysSelected){
            this._daysSelected = daysSelected;
        }

        @Override
        public void run() {
            // build string of selected days
            for (int i = 0; i < this._daysSelected.length; i++){
                if (this._daysSelected[i]){
                    resultString += daysOrdered[i] + ", ";
                }
            }

            // chop trailing punctuation
            if (!resultString.isEmpty()) {
                resultString = resultString.substring(0, resultString.length() - 2);
            }

            // set textview
            txtRepeatDays.setText(resultString);
        }
    }
}

