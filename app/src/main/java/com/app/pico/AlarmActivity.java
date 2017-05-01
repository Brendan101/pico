package com.app.pico;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    AlarmDBOperationsClass db;

    TextView alarmHeader, setPrepTimeView, setArrivalTimeView;
    EditText editAlarmName;
    Button saveBtn;

    Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        db = new AlarmDBOperationsClass(this);

        alarmHeader = (TextView) findViewById(R.id.singleAlarmHeader);
        setPrepTimeView = (TextView) findViewById(R.id.setPrepTimeView);
        setArrivalTimeView = (TextView) findViewById(R.id.setArrivalTimeView);
        editAlarmName = (EditText) findViewById(R.id.editAlarmName);
        saveBtn = (Button) findViewById(R.id.saveAlarm);

        saveBtn.setOnClickListener(this);

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
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.saveAlarm:
                Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
                if(alarm != null) {
                    // Update Alarm values and store to db
                    // TODO: there is no repeatable field in the view
                    // TODO: Location things
                    alarm.setEventName(editAlarmName.getText().toString());
                    alarm.setPrepTime(Integer.valueOf(setPrepTimeView.getText().toString()));
                    alarm.setArrivalTime(setArrivalTimeView.getText().toString());
                    db.updateAlarm(alarm);
                    Log.e("blah", db.getAllAlarms().get(1).getPrepTimeForDisplay());
                    // Route back to Alarm List Activity
                    this.finish();
                }
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
