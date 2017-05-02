package com.app.pico;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // snooze time increments in minutes
    private final int snoozeTimeIncrement = 5;
    private final int onColor = Color.parseColor("#FDBE69");
    private final int offColor = Color.parseColor("#95999a");


    boolean[] daysSelected;
    String[] daysOrdered;

    int snoozeTime;

    String sound;

    // Repeat day picker
    RelativeLayout repeatRow;
    LinearLayout repeatPicker;
    int currRepeatVis;
    StyledToggleButton suTog, moTog, tuTog, weTog, thTog, frTog, saTog;
    StyledToggleButton[] togArray;
    StyledTextView txtRepeatDays;

    // Snooze time picker
    RelativeLayout snoozeRow;
    LinearLayout snoozePicker;
    int currSnoozeVis;
    Button btnLeftTimeDec;
    Button btnRightTimeInc;
    StyledTextView txtSnoozeTime;
    StyledTextView txtSnoozeTimePicker;

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
        repeatPicker = (LinearLayout) findViewById(R.id.repeatPicker);
        txtRepeatDays = (StyledTextView) findViewById(R.id.txtRepeatDays);

        snoozeRow = (RelativeLayout) findViewById(R.id.snooze_row);
        snoozePicker = (LinearLayout) findViewById(R.id.snoozePicker);
        txtSnoozeTime = (StyledTextView) findViewById(R.id.txtSnoozeTime);
        txtSnoozeTimePicker = (StyledTextView) findViewById(R.id.txtSnoozeTimePicker);
        btnLeftTimeDec = (Button) findViewById(R.id.btnLeftTime);
        btnRightTimeInc = (Button) findViewById(R.id.btnRightTime);

        suTog = (StyledToggleButton) findViewById(R.id.sundayToggle);
        moTog = (StyledToggleButton) findViewById(R.id.mondayToggle);
        tuTog = (StyledToggleButton) findViewById(R.id.tuesdayToggle);
        weTog = (StyledToggleButton) findViewById(R.id.wednesdayToggle);
        thTog = (StyledToggleButton) findViewById(R.id.thursdayToggle);
        frTog = (StyledToggleButton) findViewById(R.id.fridayToggle);
        saTog = (StyledToggleButton) findViewById(R.id.saturdayToggle);

        togArray = new StyledToggleButton[]{suTog, moTog, tuTog, weTog, thTog, frTog, saTog};

        myHandler.post(new VisibilityRunnable(View.GONE, repeatPicker));
        myHandler.post(new VisibilityRunnable(View.GONE, snoozePicker));

        // make repeat row toggle the repeat picker
        currRepeatVis = View.GONE;
        repeatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currRepeatVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, repeatPicker));
                    currRepeatVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, repeatPicker));
                    currRepeatVis = View.GONE;
                }

            }
        });

        // snooze row toggles the snooze picker and on hide displays newly picked snooze time
        currSnoozeVis = View.GONE;
        snoozeRow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (currSnoozeVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, snoozePicker));
                    currSnoozeVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, snoozePicker));
                    currSnoozeVis = View.GONE;
                    myHandler.post(new SnoozeTextRunnable(snoozeTime));
                }

            }
        });

        btnLeftTimeDec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (snoozeTime >= snoozeTimeIncrement) {
                    snoozeTime -= snoozeTimeIncrement;
                    myHandler.post(new SnoozePickerRunnable(snoozeTime));
                }
            }
        });

        btnRightTimeInc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                snoozeTime += snoozeTimeIncrement;
                myHandler.post(new SnoozePickerRunnable(snoozeTime));
            }
        });

        // bulk declare all toggle button listeners in a loop
        // TODO possibly need lock
        for (int i = 0; i < togArray.length; i++){
            final int index = i;
            final StyledToggleButton tog = togArray[i];
            togArray[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        daysSelected[index] = true;
                        myHandler.post(new RepeatColorRunnable(onColor, tog));
                    } else {
                        daysSelected[index] = false;
                        myHandler.post(new RepeatColorRunnable(offColor, tog));
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

    private class RepeatColorRunnable implements Runnable{
        private int _color;
        private StyledToggleButton _tog;

        public RepeatColorRunnable(int color, StyledToggleButton tog){
            this._color = color;
            this._tog = tog;
        }

        @Override
        public void run() {
            this._tog.setTextColor(this._color);
        }
    }

    private class SnoozePickerRunnable implements Runnable{
        private int _snoozeTime;

        public SnoozePickerRunnable(int snoozeTime){
            this._snoozeTime = snoozeTime;
        }

        @Override
        public void run() {
            String hours = String.valueOf(this._snoozeTime / 60);
            String minutes = String.valueOf(this._snoozeTime % 60);

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            txtSnoozeTimePicker.setText(hours+":"+minutes);
        }
    }

    private class SnoozeTextRunnable implements Runnable{
        private int _snoozeTime;

        public SnoozeTextRunnable(int snoozeTime){
            this._snoozeTime = snoozeTime;
        }

        @Override
        public void run() {
            txtSnoozeTime.setText(String.valueOf(this._snoozeTime) + " min");
        }
    }
}

