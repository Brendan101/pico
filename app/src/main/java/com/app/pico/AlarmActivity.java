package com.app.pico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final int START_LOCATION_REQUEST_CODE = 20;
    private final int END_LOCATION_REQUEST_CODE = 21;
    private final int timeIncrement = 5;
    private final int onColor = Color.parseColor("#FDBE69");
    private final int offColor = Color.parseColor("#95999a");
    private final String DEFAULT_SOUND = "Default";

    // TODO decide how to handle default start location
    // default end location should be loaded from global settings?
    private final int USE_CURRENT_LOC = -1;
    private final int USE_DEFAULT_LOC = -2;

    DBOperationsClass db;

    StyledTextHeader alarmHeader;

    String ampm[] = new String[]{"AM","PM"};
    int bm = Calendar.AM;
    int arrivalHour, arrivalMinute;

    EditText editAlarmName;
    Button saveBtn, btnLeftPrepTime, btnRightPrepTime, btnLeftSnoozeTime, btnRightSnoozeTime;
    StyledTextView setPrepTimeView, txtPrepTimePicker, txtRepeatDays, txtSnoozeTime, txtSnoozeTimePicker;
    StyledTextView txtArrivalTimePicker, setArrivalTimeView, setStartLocView, setEndLocView;
    StyledTextView txtHour, txtMin, txtAMPM;
    RelativeLayout startLocRow, arrivalTimeRow, endLocRow, prepTimeRow, repeatRow, snoozeRow;
    LinearLayout prepTimePicker, repeatPicker, snoozePicker;
    Spinner txtSoundName;
    ArrayAdapter<CharSequence> spinAdapter;

    StyledToggleButton suTog, moTog, tuTog, weTog, thTog, frTog, saTog;
    StyledToggleButton[] togArray;

    LinearLayout arrivePicker;
    ImageButton btnHourUp, btnHourDown, btnMinUp, btnMinDown, btnAMPMUp, btnAMPMDown;
    Calendar arrivalTimeCal = Calendar.getInstance();

    Alarm alarm;

    Location startLoc;
    Location endLoc;
    int prepTime, currPrepTimeVis, currRepeatVis, snoozeTime, currSnoozeTimeVis, currArrivalVis;
    boolean[] daysSelected;
    String[] daysOrdered;
    String sound;

    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        db = new DBOperationsClass(this);

        alarmHeader = (StyledTextHeader) findViewById(R.id.singleAlarmHeader);
        setArrivalTimeView = (StyledTextView) findViewById(R.id.setArrivalTimeView);
        //txtArrivalTimePicker = (StyledTextView) findViewById(R.id.txtArrivalTimePicker);
        txtHour = (StyledTextView) findViewById(R.id.txtHour);
        txtMin = (StyledTextView) findViewById(R.id.txtMinute);
        txtAMPM = (StyledTextView) findViewById(R.id.txtAMPM);

        editAlarmName = (EditText) findViewById(R.id.editAlarmName);
        saveBtn = (Button) findViewById(R.id.saveAlarm);
        startLocRow = (RelativeLayout) findViewById(R.id.startLocRow);
        endLocRow = (RelativeLayout) findViewById(R.id.endLocRow);
        setStartLocView = (StyledTextView) findViewById(R.id.setStartLocationView);
        setEndLocView = (StyledTextView) findViewById(R.id.setEndLocationView);

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

        txtSoundName = (Spinner) findViewById(R.id.txtSoundName);
        spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.sounds_array, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSoundName.setAdapter(spinAdapter);
        txtSoundName.setOnItemSelectedListener(this);

        sound = DEFAULT_SOUND;

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

        btnHourUp = (ImageButton) findViewById(R.id.btnHourUp);
        btnHourDown = (ImageButton) findViewById(R.id.btnHourDown);
        btnMinUp = (ImageButton) findViewById(R.id.btnMinuteUp);
        btnMinDown = (ImageButton) findViewById(R.id.btnMinuteDown);
        btnAMPMUp = (ImageButton) findViewById(R.id.btnAMPMUp);
        btnAMPMDown = (ImageButton) findViewById(R.id.btnAMPMDown);
        currArrivalVis = View.GONE;
        arrivalTimeCal = Calendar.getInstance();
        arrivalTimeRow = (RelativeLayout) findViewById(R.id.arrivalTimeRow);

        arrivePicker = (LinearLayout) findViewById(R.id.arrivePicker);
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
        arrivalTimeRow.setOnClickListener(this);

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
        myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, arrivePicker));


        Bundle intentData = getIntent().getExtras();

        // Todo: do this in a handler?
        if(intentData != null) {
            alarm = (Alarm) intentData.getSerializable("alarm");
            // Update values in the layout to the actual Alarm values
            alarmHeader.setText("Edit Alarm");

            prepTime = alarm.getPrepTime();
            myHandler.post(new AlarmActivity.PrepTimePickerRunnable(prepTime));
            myHandler.post(new AlarmActivity.PrepTimeTextRunnable(prepTime));

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

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            try {
                arrivalTimeCal.setTime(formatter.parse(alarm.getArrivalTimeAsString()));
                arrivalHour = arrivalTimeCal.get(Calendar.HOUR);
                arrivalMinute = arrivalTimeCal.get(Calendar.MINUTE);
                bm = arrivalTimeCal.get(Calendar.AM_PM);

                myHandler.post(new ArrivalPickerRunnable(arrivalHour, arrivalMinute, bm));
                myHandler.post(new ArrivalTextRunnable(arrivalHour, arrivalMinute, bm));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            snoozeTime = alarm.getSnoozeTime();
            myHandler.post(new AlarmActivity.SnoozeTimePickerRunnable(snoozeTime));
            myHandler.post(new AlarmActivity.SnoozeTextRunnable(snoozeTime));

            int spinnerPosition = spinAdapter.getPosition(alarm.getSound());
            txtSoundName.setSelection(spinnerPosition);
        }
        else {
            alarm = null;
            alarmHeader.setText("New Alarm");
            // Set default Alarm date to current date/time + a day
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

            Calendar currentDate = Calendar.getInstance();
            currentDate.add(Calendar.DATE, 1);
            myHandler.post(new ArrivalPickerRunnable(currentDate.get(Calendar.HOUR), currentDate.get(Calendar.MINUTE), currentDate.get(Calendar.AM_PM)));
            String defaultArrivalTime = formatter.format(currentDate.getTime());
            setArrivalTimeView.setText(defaultArrivalTime);

            arrivalHour = currentDate.get(Calendar.HOUR);
            arrivalMinute = currentDate.get(Calendar.MINUTE);
            bm = currentDate.get(Calendar.AM_PM);

        }


        // onclick listeners for time dropdown
        btnHourUp.setOnClickListener(this);
        btnHourDown.setOnClickListener(this);
        btnMinUp.setOnClickListener(this);
        btnMinDown.setOnClickListener(this);
        btnAMPMUp.setOnClickListener(this);
        btnAMPMDown.setOnClickListener(this);

        arrivalTimeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currArrivalVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, arrivePicker));
                    currArrivalVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, arrivePicker));
                    currArrivalVis = View.GONE;
                }

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.saveAlarm:
                String eventName = editAlarmName.getText().toString();
                String repeat = Arrays.toString(daysSelected);

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                String arrivalTime = formatter.format(arrivalTimeCal.getTime());

                // TODO: Pop up if there's no Alarm Name
                if(alarm != null) {
                    // Update Alarm values and store to db
                    // TODO: Location things
                    alarm.setEventName(eventName);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);

                    // TODO: fix these defaults
                    if (startLoc != null) {
                        alarm.setStartLocationID(startLoc.getID());
                    }

                    if (endLoc != null) {
                        alarm.setEndLocationID(endLoc.getID());
                    }

                    alarm.setRepeat(repeat);
                    alarm.setSnoozeTime(snoozeTime);
                    alarm.setSound(sound);

                    // Route back to Alarm List Activity if all values properly filled out
                    if (startLoc == null){
                        myHandler.post(new ToastRunnable("You must enter a start location!"));
                    } else if (endLoc == null){
                        myHandler.post(new ToastRunnable("You must enter an end location!"));
                    } else if (eventName.isEmpty()){
                        myHandler.post((new ToastRunnable("You must enter an alarm name!")));
                        editAlarmName.requestFocus();
                    } else {
                        if (alarm.isActive()){
                            // start service to enable alarm
                            Intent setAlarm = new Intent(getApplicationContext(), TrafficService.class);
                            setAlarm.putExtra("command", "initial");
                            setAlarm.putExtra("alarm", alarm);
                            getApplicationContext().startService(setAlarm);
                        }

                        db.updateAlarm(alarm);
                        this.finish();
                    }
                }
                else {
                    // Create a new alarm and store to db
                    // TODO: fix default values
                    alarm = new Alarm();
                    alarm.setEventName(eventName);
                    alarm.setActive(true);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);
                    // TODO: fix these defaults
                    if (startLoc != null) {
                        alarm.setStartLocationID(startLoc.getID());
                    }
                    if (endLoc != null) {
                        alarm.setEndLocationID(endLoc.getID());
                    }
                    alarm.setRepeat(repeat);
                    alarm.setSnoozeTime(snoozeTime);
                    alarm.setSound(sound);

                    // Route back to Alarm List Activity
                    if (startLoc == null){
                        myHandler.post(new ToastRunnable("You must enter a start location!"));
                        alarm = null;
                    } else if (endLoc == null){
                        myHandler.post(new ToastRunnable("You must enter an end location!"));
                        alarm = null;
                    } else if (eventName.isEmpty()){
                        myHandler.post((new ToastRunnable("You must enter an alarm name!")));
                        editAlarmName.requestFocus();
                        alarm = null;
                    } else {
                        // start service to enable alarm
                        Intent setAlarm = new Intent(getApplicationContext(), TrafficService.class);
                        setAlarm.putExtra("command", "initial");
                        setAlarm.putExtra("alarm", alarm);
                        getApplicationContext().startService(setAlarm);

                        db.addAlarm(alarm);
                        this.finish();
                    }

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
            case R.id.btnHourUp:
                arrivalTimeCal.add(Calendar.HOUR, 1);
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                break;
            case R.id.btnHourDown:
                arrivalTimeCal.add(Calendar.HOUR, -1);
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                break;
            case R.id.btnMinuteUp:
                arrivalTimeCal.add(Calendar.MINUTE, 1);
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                break;
            case R.id.btnMinuteDown:
                arrivalTimeCal.add(Calendar.MINUTE, -1);
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                break;
            case R.id.btnAMPMUp:
                if (arrivalTimeCal.get(Calendar.AM_PM) == Calendar.AM){
                    arrivalTimeCal.set(Calendar.AM_PM, Calendar.PM);
                } else {
                    arrivalTimeCal.set(Calendar.AM_PM, Calendar.AM);
                }
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                break;
            case R.id.btnAMPMDown:
                if (arrivalTimeCal.get(Calendar.AM_PM) == Calendar.AM){
                    arrivalTimeCal.set(Calendar.AM_PM, Calendar.PM);
                } else {
                    arrivalTimeCal.set(Calendar.AM_PM, Calendar.AM);
                }
                myHandler.post(new ArrivalPickerRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
                myHandler.post(new ArrivalTextRunnable(arrivalTimeCal.get(Calendar.HOUR), arrivalTimeCal.get(Calendar.MINUTE), arrivalTimeCal.get(Calendar.AM_PM)));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sound = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        sound = DEFAULT_SOUND;
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

    private class ArrivalPickerRunnable implements Runnable{
        private int _arrivalHour;
        private int _arrivalMinute;
        private int _bm;

        public ArrivalPickerRunnable(int arrivalHour, int arrivalMinute, int bm){

            this._arrivalHour = arrivalHour;
            this._arrivalMinute = arrivalMinute;
            this._bm = bm;
            if(this._arrivalHour == 0){this._arrivalHour  = 12;}
        }

        @Override
        public void run() {
            String hours = String.valueOf(this._arrivalHour );
            String minutes = String.valueOf(this._arrivalMinute );

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            txtHour.setText(hours);
            txtMin.setText(minutes);
            txtAMPM.setText(ampm[this._bm]);
        }
    }

    private class ArrivalTextRunnable implements Runnable{
        private int _arrivalHour;
        private int _arrivalMinute;
        private int _bm;


        public ArrivalTextRunnable(int arrivalHour, int arrivalMinute, int bm){
            this._arrivalHour = arrivalHour;
            this._arrivalMinute = arrivalMinute;
            this._bm = bm;
            if(this._arrivalHour == 0){this._arrivalHour  = 12;}

        }

        @Override
        public void run() {
            String hours = String.valueOf(this._arrivalHour );
            String minutes = String.valueOf(this._arrivalMinute );

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            setArrivalTimeView.setText(hours + ":"+ minutes +" "+ ampm[this._bm]);}
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

    private class ToastRunnable implements Runnable {
        private String _toast_text;

        public ToastRunnable(String toast_text){
            this._toast_text = toast_text;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), this._toast_text, Toast.LENGTH_SHORT).show();
        }
    }

}
