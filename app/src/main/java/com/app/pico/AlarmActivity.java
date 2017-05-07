package com.app.pico;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private final int START_LOCATION_REQUEST_CODE = 20;
    private final int END_LOCATION_REQUEST_CODE = 21;

    DBOperationsClass db;
    String ampm[] = new String[]{"AM","PM"};
    int bm = Calendar.AM;
    int arrivalHour, arrivalMinute;
    TextView alarmHeader, setPrepTimeView, setArrivalTimeView, setStartLocView, setEndLocView, txtArrivalTimePicker;
    EditText editAlarmName;
    Button saveBtn;

    RelativeLayout startLocRow, arrivalTimeRow;
    RelativeLayout endLocRow;
    LinearLayout arrivePicker;
    int currArrivalVis;
    Button btnLeftTimeDec, btnRightTimeInc;
    Calendar currentDate;


    Alarm alarm;

    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        db = new DBOperationsClass(this);

        alarmHeader = (TextView) findViewById(R.id.singleAlarmHeader);
        setPrepTimeView = (TextView) findViewById(R.id.setPrepTimeView);
        setArrivalTimeView = (TextView) findViewById(R.id.setArrivalTimeView);
        txtArrivalTimePicker = (StyledTextView) findViewById(R.id.txtArrivalTimePicker);

        editAlarmName = (EditText) findViewById(R.id.editAlarmName);
        saveBtn = (Button) findViewById(R.id.saveAlarm);
        startLocRow = (RelativeLayout) findViewById(R.id.startLocRow);
        endLocRow = (RelativeLayout) findViewById(R.id.endLocRow);
        setStartLocView = (TextView) findViewById(R.id.setStartLocationView);
        setEndLocView = (TextView) findViewById(R.id.setEndLocationView);
        btnLeftTimeDec = (Button) findViewById(R.id.btnLeftTime);
        btnRightTimeInc = (Button) findViewById(R.id.btnRightTime);
        arrivalTimeRow = (RelativeLayout) findViewById(R.id.arrivalTimeRow);

        arrivePicker = (LinearLayout) findViewById(R.id.arrivePicker);
        saveBtn.setOnClickListener(this);
        startLocRow.setOnClickListener(this);
        endLocRow.setOnClickListener(this);
        arrivalTimeRow.setOnClickListener(this);

        myHandler.post(new AlarmActivity.VisibilityRunnable(View.GONE, arrivePicker));
        currArrivalVis = View.GONE;
        Bundle intentData = getIntent().getExtras();

        // Todo: do this in a handler?
        if(intentData != null) {
            alarm = (Alarm) intentData.getSerializable("alarm");
            // Update values in the layout to the actual Alarm values
            alarmHeader.setText("Edit Alarm");
            setPrepTimeView.setText(String.valueOf(alarm.getPrepTime()));
            currentDate = alarm.getArrivalTime();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

            String defaultArrivalTime = formatter.format(currentDate.getTime());
            setArrivalTimeView.setText(defaultArrivalTime);
            editAlarmName.setText(alarm.getEventName());
            currentDate = alarm.getArrivalTime();
        }
        else {
            alarm = null;
            alarmHeader.setText("New Alarm");
            // Set default Alarm date to current date/time + a day
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            currentDate = Calendar.getInstance();
            currentDate.add(Calendar.DATE, 1);
            String defaultArrivalTime = formatter.format(currentDate.getTime());
            setArrivalTimeView.setText(defaultArrivalTime);

        }
        String arrivalTime = setArrivalTimeView.getText().toString();

        arrivalHour = currentDate.get(Calendar.HOUR);
        arrivalMinute = currentDate.get(Calendar.MINUTE);
        bm = currentDate.get(Calendar.AM_PM);

        arrivalTimeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currArrivalVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, arrivePicker));
                    currArrivalVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, arrivePicker));
                    currArrivalVis = View.GONE;
                    myHandler.post(new ArrivalTextRunnable(arrivalHour, arrivalMinute));
                }

            }
        });

        btnLeftTimeDec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (arrivalMinute > 0) {
                    arrivalMinute -= 1;
                    }
                else {
                    if(arrivalHour > 0){arrivalHour -= 1;}
                    else {
                        arrivalHour = 11;
                        if (bm == Calendar.AM){bm = Calendar.PM;}
                        else {bm = Calendar.AM;}
                        }
                    arrivalMinute = 59;
                }
                myHandler.post(new ArrivalPickerRunnable(arrivalHour, arrivalMinute));

            }
        });

        btnRightTimeInc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (arrivalMinute < 59) {
                    arrivalMinute += 1;
                }
                else {
                    if(arrivalHour < 11){arrivalHour += 1;}
                    else {arrivalHour = 0;
                        if (bm == Calendar.AM){bm = Calendar.PM;}
                        else {bm = Calendar.AM;}
                    }
                    arrivalMinute = 0;
                }
                myHandler.post(new ArrivalPickerRunnable( arrivalHour, arrivalMinute));

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.saveAlarm:
                String eventName = editAlarmName.getText().toString();
                int prepTime = Integer.valueOf(setPrepTimeView.getText().toString());
                currentDate.set(Calendar.HOUR, arrivalHour);
                currentDate.set(Calendar.MINUTE, arrivalMinute);
                currentDate.set(Calendar.AM_PM, bm);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                String arrivalTime = formatter.format(currentDate.getTime());

                if(alarm != null) {
                    // Update Alarm values and store to db
                    // TODO: there is no repeatable field in the view
                    // TODO: Location things
                    alarm.setEventName(eventName);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);
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
                    alarm.setRepeatable(false);
                    alarm.setPrepTime(prepTime);
                    alarm.setArrivalTime(arrivalTime);
                    alarm.setStartLocation("Current Location");
                    alarm.setEndLocation("Default Location");
                    db.addAlarm(alarm);
                    // Route back to Alarm List Activity
                    this.finish();
                }
                break;
            case R.id.startLocRow:
                Intent startIntent = new Intent(AlarmActivity.this, LocationListActivity.class);
                startIntent.putExtra("parent", "alarm");
                startActivityForResult(startIntent, START_LOCATION_REQUEST_CODE);
                Log.e("onclick", "start");
                break;
            case R.id.endLocRow:
                Intent endIntent = new Intent(AlarmActivity.this, LocationListActivity.class);
                endIntent.putExtra("parent", "alarm");
                startActivityForResult(endIntent, END_LOCATION_REQUEST_CODE);
                break;
            /*case R.id.arrivalTimeRow:
                Intent timeIntent = new Intent(AlarmActivity.this, ArrivalActivity.class);
                timeIntent.putExtra("parent", alarm.getArrivalTime().getTimeInMillis());
                startActivityForResult(timeIntent, ARRIVAL_TIME_REQUEST_CODE);
                break;*/

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                String newLoc = data.getStringExtra("location");
                myHandler.post(new UpdateLocationRunnable(newLoc, setStartLocView));
            }
        } else if(requestCode == END_LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                String newLoc = data.getStringExtra("location");
                myHandler.post(new UpdateLocationRunnable(newLoc, setEndLocView));
            }
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

        public ArrivalPickerRunnable(int arrivalHour, int arrivalMinute){

            this._arrivalHour = arrivalHour;
            this._arrivalMinute = arrivalMinute;
            if(this._arrivalHour == 0){this._arrivalHour  = 12;}
        }

        @Override
        public void run() {
            String hours = String.valueOf(this._arrivalHour );
            String minutes = String.valueOf(this._arrivalMinute );

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            txtArrivalTimePicker.setText(hours + " : "+ minutes +" "+ ampm[bm]);
        }
    }

    private class ArrivalTextRunnable implements Runnable{
        private int _arrivalHour;
        private int _arrivalMinute;


        public ArrivalTextRunnable(int arrivalHour, int arrivalMinute){
            this._arrivalHour = arrivalHour;
            this._arrivalMinute = arrivalMinute;
            if(this._arrivalHour == 0){this._arrivalHour  = 12;}

        }

        @Override
        public void run() {
            String hours = String.valueOf(this._arrivalHour );
            String minutes = String.valueOf(this._arrivalMinute );

            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }

            setArrivalTimeView.setText(hours + " : "+ minutes +" "+ ampm[bm]);}
    }
    /*@Override
    public void onClick(View view) {
        String currentPrepTime = prepTimePicker.getText().toString();
        // Find the index of the 'h' and 's' in hours
        int hoursStartIndex = currentPrepTime.indexOf('h');
        int hoursEndIndex = currentPrepTime.indexOf('s');
        // Find the starting index of min
        int minIndex = currentPrepTime.indexOf("min");
        int hours;
        int min;

        if(hoursStartIndex == -1) {
            hours = 0;
            String minString = currentPrepTime.substring(0, minIndex-1);
            min = Integer.valueOf(minString);

        }
        else {
            String hoursString = currentPrepTime.substring(0, hoursStartIndex-1);
            hours = Integer.valueOf(hoursString);
            min = Integer.valueOf(currentPrepTime.substring(hoursEndIndex+2, minIndex-1));
        }

        final Dialog timePickerDialog = new Dialog(this);
        timePickerDialog.setContentView(R.layout.prep_timepicker);
        Button leftArrowBtn = (Button) timePickerDialog.findViewById(R.id.leftArrow);
        Button rightArrowBtn = (Button) timePickerDialog.findViewById(R.id.rightArrow);
        final NumberPicker hourPicker = (NumberPicker) timePickerDialog.findViewById(R.id.hourDisplay);
        final NumberPicker minPicker = (NumberPicker) timePickerDialog.findViewById(R.id.minDisplay);
        hourPicker.setMaxValue(10);
        hourPicker.setMinValue(0);
        hourPicker.setWrapSelectorWheel(false);
        //hourPicker.setOnValueChangedListener(this);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);
        minPicker.setWrapSelectorWheel(false);
        //minPicker.setOnValueChangedListener(this);

        leftArrowBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlarmActivity.this, "left arrow", Toast.LENGTH_LONG).show();
                //tv.setText(String.valueOf(np.getValue()));
                //d.dismiss();
            }
        });

        rightArrowBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //d.dismiss();
            }
        });
        timePickerDialog.show();

        /*Dialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String newPrepTime;
                if(selectedHour == 0) {
                    newPrepTime = selectedMinute + " min";
                }
                else {
                    newPrepTime = selectedHour + " hours " + selectedMinute + " min";
                }
                prepTimePicker.setText(newPrepTime);
            }
        }, hours, min, false);
        timePicker.setTitle("Select Preparation Time");
        timePicker.show();
    }*/
}
