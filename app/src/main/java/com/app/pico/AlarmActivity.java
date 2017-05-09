package com.app.pico;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private final int START_LOCATION_REQUEST_CODE = 20;
    private final int END_LOCATION_REQUEST_CODE = 21;
    private final int timeIncrement = 5;
    private final int onColor = Color.parseColor("#FDBE69");
    private final int offColor = Color.parseColor("#95999a");

    // TODO decide how to handle default start location
    // default end location should be loaded from global settings?
    private final int USE_CURRENT_LOC = -1;
    private final int USE_DEFAULT_LOC = -2;

    DBOperationsClass db;

    TextView alarmHeader, setArrivalTimeView, setStartLocView, setEndLocView;
    EditText editAlarmName;
    Button saveBtn, btnLeftPrepTime, btnRightPrepTime, btnLeftSnoozeTime, btnRightSnoozeTime;
    StyledTextView setPrepTimeView, txtPrepTimePicker, txtRepeatDays, txtSnoozeTime, txtSnoozeTimePicker, txtSoundName;
    RelativeLayout startLocRow, endLocRow, prepTimeRow, repeatRow, snoozeRow;
    LinearLayout prepTimePicker, repeatPicker, snoozePicker;

    StyledToggleButton suTog, moTog, tuTog, weTog, thTog, frTog, saTog;
    StyledToggleButton[] togArray;

    Alarm alarm;

    Location startLoc;
    Location endLoc;
    int prepTime, currPrepTimeVis, currRepeatVis, snoozeTime, currSnoozeTimeVis;
    boolean[] daysSelected;
    String[] daysOrdered;

    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        db = new DBOperationsClass(this);

        alarmHeader = (TextView) findViewById(R.id.singleAlarmHeader);
        setArrivalTimeView = (TextView) findViewById(R.id.setArrivalTimeView);
        editAlarmName = (EditText) findViewById(R.id.editAlarmName);
        saveBtn = (Button) findViewById(R.id.saveAlarm);
        startLocRow = (RelativeLayout) findViewById(R.id.startLocRow);
        endLocRow = (RelativeLayout) findViewById(R.id.endLocRow);
        setStartLocView = (TextView) findViewById(R.id.setStartLocationView);
        setEndLocView = (TextView) findViewById(R.id.setEndLocationView);
        txtSoundName = (StyledTextView) findViewById(R.id.txtSoundName);

        // Prep Time Row and picker
        prepTimeRow = (RelativeLayout) findViewById(R.id.prepTimeRow);
        prepTimePicker = (LinearLayout) findViewById(R.id.prepTimePicker);
        setPrepTimeView = (StyledTextView) findViewById(R.id.setPrepTimeView);
        txtPrepTimePicker = (StyledTextView) findViewById(R.id.txtPrepTimePicker);
        btnLeftPrepTime = (Button) findViewById(R.id.btnLeftPrepTime);
        btnRightPrepTime = (Button) findViewById(R.id.btnRightPrepTime);
        prepTime = 5;
        currPrepTimeVis = View.GONE;

        // Repeat Row and picker
        repeatRow = (RelativeLayout) findViewById(R.id.repeatRow);
        repeatPicker = (LinearLayout) findViewById(R.id.repeatPicker);
        txtRepeatDays = (StyledTextView) findViewById(R.id.txtRepeatDays);
        currRepeatVis = View.GONE;
        suTog = (StyledToggleButton) findViewById(R.id.sundayToggle);
        moTog = (StyledToggleButton) findViewById(R.id.mondayToggle);
        tuTog = (StyledToggleButton) findViewById(R.id.tuesdayToggle);
        weTog = (StyledToggleButton) findViewById(R.id.wednesdayToggle);
        thTog = (StyledToggleButton) findViewById(R.id.thursdayToggle);
        frTog = (StyledToggleButton) findViewById(R.id.fridayToggle);
        saTog = (StyledToggleButton) findViewById(R.id.saturdayToggle);

        // we do this to ensure our days remain in order in the UI
        daysSelected = new boolean[]{false, false, false, false, false, false, false};
        daysOrdered = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};

        togArray = new StyledToggleButton[]{suTog, moTog, tuTog, weTog, thTog, frTog, saTog};

        // Snooze Row and picker
        snoozeRow = (RelativeLayout) findViewById(R.id.snoozeRow);
        snoozePicker = (LinearLayout) findViewById(R.id.snoozePicker);
        txtSnoozeTime = (StyledTextView) findViewById(R.id.txtSnoozeTime);
        txtSnoozeTimePicker = (StyledTextView) findViewById(R.id.txtSnoozeTimePicker);
        btnLeftSnoozeTime = (Button) findViewById(R.id.btnLeftSnoozeTime);
        btnRightSnoozeTime = (Button) findViewById(R.id.btnRightSnoozeTime);
        currSnoozeTimeVis = View.GONE;
        snoozeTime = 5;

        saveBtn.setOnClickListener(this);
        startLocRow.setOnClickListener(this);
        endLocRow.setOnClickListener(this);
        prepTimeRow.setOnClickListener(this);
        repeatRow.setOnClickListener(this);
        btnLeftPrepTime.setOnClickListener(this);
        btnRightPrepTime.setOnClickListener(this);
        snoozeRow.setOnClickListener(this);
        btnLeftSnoozeTime.setOnClickListener(this);
        btnRightSnoozeTime.setOnClickListener(this);

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
                        myHandler.post(new AlarmActivity.RepeatColorRunnable(onColor, tog));
                    } else {
                        daysSelected[index] = false;
                        myHandler.post(new AlarmActivity.RepeatColorRunnable(offColor, tog));
                    }
                    myHandler.post(new AlarmActivity.RepeatRunnable(daysSelected));
                }
            });
        }

        myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, prepTimePicker));
        myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, repeatPicker));
        myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, snoozePicker));

        Bundle intentData = getIntent().getExtras();

        // Todo: do this in a handler?
        if(intentData != null) {
            alarm = (Alarm) intentData.getSerializable("alarm");
            // Update values in the layout to the actual Alarm values
            alarmHeader.setText("Edit Alarm");

            prepTime = alarm.getPrepTime();
            myHandler.post(new AlarmActivity.PrepTimePickerRunnable(prepTime));
            myHandler.post(new AlarmActivity.PrepTimeTextRunnable(prepTime));

            setArrivalTimeView.setText(alarm.getArrivalTimeAsString());
            editAlarmName.setText(alarm.getEventName());

            if (alarm.getStartLocationID() > 0){
                startLoc = db.getLocationByID(alarm.getStartLocationID());
                myHandler.post(new UpdateLocationRunnable(startLoc.getLocationName(), setStartLocView));
            }
            if (alarm.getEndLocationID() > 0){
                endLoc = db.getLocationByID(alarm.getEndLocationID());
                myHandler.post(new UpdateLocationRunnable(endLoc.getLocationName(), setEndLocView));
            }

            txtRepeatDays.setText(alarm.getRepeat());
            String repeat = alarm.getRepeat();

            if(!repeat.equals("No Repeat")) {
                repeat = repeat.replace("[","");
                repeat = repeat.replace("]","");
                String[] repeatArray = repeat.split(",");

                for(int i = 0; i < repeatArray.length; i++) {
                    boolean toggleOn = Boolean.parseBoolean(repeatArray[i].trim());
                    daysSelected[i] = toggleOn;
                    if(toggleOn) {
                        myHandler.post(new AlarmActivity.RepeatColorRunnable(onColor, togArray[i]));
                    }
                }
            }
            myHandler.post(new AlarmActivity.RepeatRunnable(daysSelected));

            snoozeTime = alarm.getSnoozeTime();
            myHandler.post(new AlarmActivity.SnoozeTimePickerRunnable(snoozeTime));
            myHandler.post(new AlarmActivity.SnoozeTextRunnable(snoozeTime));

            txtSoundName.setText(alarm.getSound());
        }
        else {
            alarm = null;
            alarmHeader.setText("New Alarm");
            // Set default Alarm date to current date/time + a day
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            Calendar currentDate = Calendar.getInstance();
            currentDate.add(Calendar.DATE, 1);
            String defaultArrivalTime = formatter.format(currentDate.getTime());
            setArrivalTimeView.setText(defaultArrivalTime);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.saveAlarm:
                String eventName = editAlarmName.getText().toString();
                String arrivalTime = setArrivalTimeView.getText().toString();
                String repeat = Arrays.toString(daysSelected);
                String sound = txtSoundName.getText().toString();

                // TODO: Pop up if there's no Alarm Name
                if(alarm != null) {
                    // Update Alarm values and store to db
                    // TODO: Location things
                    alarm.setEventName(eventName);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);

                    if (startLoc != null) {
                        alarm.setStartLocationID(startLoc.getID());
                    } else{
                        alarm.setStartLocationID(USE_CURRENT_LOC);
                    }
                    if (endLoc != null) {
                        alarm.setEndLocationID(endLoc.getID());
                    } else {
                        alarm.setEndLocationID(USE_DEFAULT_LOC);
                    }
                    alarm.setRepeat(repeat);
                    alarm.setSnoozeTime(snoozeTime);
                    alarm.setSound(sound);
                    db.updateAlarm(alarm);
                    // Route back to Alarm List Activity
                    this.finish();
                }
                else {
                    // Create a new alarm and store to db
                    // TODO: fix default values
                    alarm = new Alarm();
                    alarm.setEventName(eventName);
                    alarm.setActive(true);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);
                    if (startLoc != null) {
                        alarm.setStartLocationID(startLoc.getID());
                    } else{
                        alarm.setStartLocationID(USE_CURRENT_LOC);
                    }
                    if (endLoc != null) {
                        alarm.setEndLocationID(endLoc.getID());
                    } else {
                        alarm.setEndLocationID(USE_DEFAULT_LOC);
                    }
                    alarm.setRepeat(repeat);
                    alarm.setSnoozeTime(snoozeTime);
                    alarm.setSound(sound);
                    db.addAlarm(alarm);
                    // Route back to Alarm List Activity
                    this.finish();
                }
                break;
            case R.id.startLocRow:
                Intent startIntent = new Intent(AlarmActivity.this, LocationListActivity.class);
                startIntent.putExtra("parent", "alarm");
                startIntent.putExtra("type", "start");
                startActivityForResult(startIntent, START_LOCATION_REQUEST_CODE);
                break;
            case R.id.endLocRow:
                Intent endIntent = new Intent(AlarmActivity.this, LocationListActivity.class);
                endIntent.putExtra("parent", "alarm");
                endIntent.putExtra("type", "end");
                startActivityForResult(endIntent, END_LOCATION_REQUEST_CODE);
                break;
            case R.id.prepTimeRow:
                if (currPrepTimeVis == View.GONE) {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.VISIBLE, prepTimePicker));
                    currPrepTimeVis = View.VISIBLE;
                } else {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, prepTimePicker));
                    currPrepTimeVis = View.GONE;
                }
                break;
            case R.id.btnLeftPrepTime:
                if (prepTime >= timeIncrement) {
                    prepTime -= timeIncrement;
                    myHandler.post(new AlarmActivity.PrepTimePickerRunnable(prepTime));
                    myHandler.post(new AlarmActivity.PrepTimeTextRunnable(prepTime));
                }
                break;
            case R.id.btnRightPrepTime:
                prepTime += timeIncrement;
                myHandler.post(new AlarmActivity.PrepTimePickerRunnable(prepTime));
                myHandler.post(new AlarmActivity.PrepTimeTextRunnable(prepTime));
                break;
            case R.id.repeatRow:
                if (currRepeatVis == View.GONE) {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.VISIBLE, repeatPicker));
                    currRepeatVis = View.VISIBLE;
                } else {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, repeatPicker));
                    currRepeatVis = View.GONE;
                }
                break;
            case R.id.snoozeRow:
                if (currSnoozeTimeVis == View.GONE) {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.VISIBLE, snoozePicker));
                    currSnoozeTimeVis = View.VISIBLE;
                } else {
                    myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, snoozePicker));
                    currSnoozeTimeVis = View.GONE;
                    myHandler.post(new AlarmActivity.SnoozeTextRunnable(snoozeTime));
                }
                break;
            case R.id.btnLeftSnoozeTime:
                if (snoozeTime >= timeIncrement) {
                    snoozeTime -= timeIncrement;
                    myHandler.post(new AlarmActivity.SnoozeTimePickerRunnable(snoozeTime));
                    myHandler.post(new AlarmActivity.SnoozeTextRunnable(snoozeTime));
                }
                break;
            case R.id.btnRightSnoozeTime:
                snoozeTime += timeIncrement;
                myHandler.post(new AlarmActivity.SnoozeTimePickerRunnable(snoozeTime));
                myHandler.post(new AlarmActivity.SnoozeTextRunnable(snoozeTime));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Bundle locData = data.getExtras();
                if(locData != null) {
                    startLoc = (Location) locData.getSerializable("location");
                    myHandler.post(new UpdateLocationRunnable(startLoc.getLocationName(), setStartLocView));
                }
            }
        } else if(requestCode == END_LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Bundle locData = data.getExtras();
                if(locData != null) {
                    endLoc = (Location) locData.getSerializable("location");
                    myHandler.post(new UpdateLocationRunnable(endLoc.getLocationName(), setEndLocView));
                }

            }
        }
    }

    private class UpdateLocationRunnable implements Runnable{
        private String _newLoc;
        private TextView _view;
        
        public UpdateLocationRunnable(String newLoc, TextView view){
            this._newLoc = newLoc;
            this._view = view;
        }

        @Override
        public void run() {
            this._view.setText(this._newLoc);
        }
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

    private class PrepTimePickerRunnable implements Runnable{
        private int _prepTime;

        public PrepTimePickerRunnable(int prepTime){
            this._prepTime = prepTime;
        }

        @Override
        public void run() {
            String hours = String.valueOf(this._prepTime / 60);
            String minutes = String.valueOf(this._prepTime % 60);

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            txtPrepTimePicker.setText(hours+":"+minutes);
        }
    }

    private class PrepTimeTextRunnable implements Runnable {
        private int _prepTime;

        public PrepTimeTextRunnable(int prepTime){
            this._prepTime = prepTime;
        }

        @Override
        public void run() {
            setPrepTimeView.setText(String.valueOf(this._prepTime) + " min");
        }
    }

    private class RepeatColorRunnable implements Runnable {
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

    private class RepeatRunnable implements Runnable {
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
            // Display "No Repeat"
            else {
                resultString = "No Repeat";
            }

            // set textview
            txtRepeatDays.setText(resultString);
        }
    }

    private class SnoozeTextRunnable implements Runnable {
        private int _snoozeTime;

        public SnoozeTextRunnable(int snoozeTime){
            this._snoozeTime = snoozeTime;
        }

        @Override
        public void run() {
            txtSnoozeTime.setText(String.valueOf(this._snoozeTime) + " min");
        }
    }

    private class SnoozeTimePickerRunnable implements Runnable {
        private int _snoozeTime;

        public SnoozeTimePickerRunnable(int snoozeTime){
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

}
