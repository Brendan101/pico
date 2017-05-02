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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private final int START_LOCATION_REQUEST_CODE = 20;
    private final int END_LOCATION_REQUEST_CODE = 21;

    DBOperationsClass db;

    TextView alarmHeader, setPrepTimeView, setArrivalTimeView, setStartLocView, setEndLocView;
    EditText editAlarmName;
    Button saveBtn;

    RelativeLayout startLocRow;
    RelativeLayout endLocRow;

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
        editAlarmName = (EditText) findViewById(R.id.editAlarmName);
        saveBtn = (Button) findViewById(R.id.saveAlarm);
        startLocRow = (RelativeLayout) findViewById(R.id.startLocRow);
        endLocRow = (RelativeLayout) findViewById(R.id.endLocRow);
        setStartLocView = (TextView) findViewById(R.id.setStartLocationView);
        setEndLocView = (TextView) findViewById(R.id.setEndLocationView);

        saveBtn.setOnClickListener(this);
        startLocRow.setOnClickListener(this);
        endLocRow.setOnClickListener(this);

        //prepTimePicker.setOnClickListener(this);

        Bundle intentData = getIntent().getExtras();

        // Todo: do this in a handler?
        if(intentData != null) {
            alarm = (Alarm) intentData.getSerializable("alarm");
            // Update values in the layout to the actual Alarm values
            alarmHeader.setText("Edit Alarm");
            setPrepTimeView.setText(String.valueOf(alarm.getPrepTime()));
            setArrivalTimeView.setText(alarm.getArrivalTimeAsString());
            editAlarmName.setText(alarm.getEventName());
        }
        else {
            alarm = null;
            alarmHeader.setText("New Alarm");
            // Set default Alarm date to current date/time + a day
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
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
                int prepTime = Integer.valueOf(setPrepTimeView.getText().toString());
                String arrivalTime = setArrivalTimeView.getText().toString();

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
