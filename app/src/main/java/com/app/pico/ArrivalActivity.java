package com.app.pico;

import android.icu.util.Calendar;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ArrivalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    // Month time picker
    RelativeLayout MonthRow;
    LinearLayout MonthPicker;
    int currMonthVis;
    Button btnLeftMonthDec;
    Button btnRightMonthInc;
    StyledTextView txtMonth;
    StyledTextView txtMonthPicker;
    int Month;


    // Day time picker
    RelativeLayout DayRow;
    LinearLayout DayPicker;
    int currDayVis;
    Button btnLeftDayDec;
    Button btnRightDayInc;
    StyledTextView txtDay;
    StyledTextView txtDayPicker;
    int Day;


    // Arrival time picker
    RelativeLayout ArrivalTimeRow;
    LinearLayout ArrivalTimePicker;
    int currArrivalTimeVis;
    Button btnLeftArrivalTimeDec;
    Button btnRightArrivalTimeInc;
    StyledTextView txtArrivalTime;
    StyledTextView txtArrivalTimePicker;
    int arrivalTime;


    // AMPM time picker
    RelativeLayout AMPMRow;
    LinearLayout AMPMPicker;
    int currAMPMVis;
    Button btnLeftAMPMDec;
    Button btnRightAMPMInc;
    StyledTextView txtAMPM;
    StyledTextView txtAMPMPicker;
    int AMPM;

    java.util.Calendar AlarmDate;
    Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrival);

        AlarmDate.setTimeInMillis(this.getIntent().getExtras().getLong("parent"));

        Month = AlarmDate.MONTH;
        Day = AlarmDate.DAY_OF_MONTH;
        arrivalTime = AlarmDate.HOUR * 60 + AlarmDate.MINUTE;
        AMPM = AlarmDate.AM_PM;


        MonthRow = (RelativeLayout) findViewById(R.id.month_row);
        MonthPicker = (LinearLayout) findViewById(R.id.monthPicker);
        txtMonth= (StyledTextView) findViewById(R.id.txtMonth);
        txtMonthPicker = (StyledTextView) findViewById(R.id.txtMonthPicker);
        btnLeftMonthDec = (Button) findViewById(R.id.btnLeftMonth);
        btnRightMonthInc = (Button) findViewById(R.id.btnRightMonth);

        DayRow = (RelativeLayout) findViewById(R.id.day_row);
        DayPicker = (LinearLayout) findViewById(R.id.DayPicker);
        txtDay= (StyledTextView) findViewById(R.id.txtDay);
        txtDayPicker = (StyledTextView) findViewById(R.id.txtDayPicker);
        btnLeftDayDec = (Button) findViewById(R.id.btnLeftDay);
        btnRightDayInc= (Button) findViewById(R.id.btnRightDay);

        ArrivalTimeRow = (RelativeLayout) findViewById(R.id.arrival_row);
        ArrivalTimePicker = (LinearLayout) findViewById(R.id.arrivePicker);
        txtArrivalTime= (StyledTextView) findViewById(R.id.txtTime);
        txtArrivalTimePicker = (StyledTextView) findViewById(R.id.txtArrivalTimePicker);
        btnLeftArrivalTimeDec = (Button) findViewById(R.id.btnLeftTime);
        btnRightArrivalTimeInc= (Button) findViewById(R.id.btnRightTime);


        AMPMRow = (RelativeLayout) findViewById(R.id.ampm_row);
        AMPMPicker = (LinearLayout) findViewById(R.id.AMPMPicker);
        txtAMPM= (StyledTextView) findViewById(R.id.txtAMPM);
        txtAMPMPicker = (StyledTextView) findViewById(R.id.txtAMPMPicker);
        btnLeftAMPMDec = (Button) findViewById(R.id.btnLeftAMPM);
        btnRightAMPMInc = (Button) findViewById(R.id.btnRightAMPM);


        myHandler.post(new VisibilityRunnable(View.GONE, MonthPicker));
        myHandler.post(new VisibilityRunnable(View.GONE, DayPicker));
        myHandler.post(new VisibilityRunnable(View.GONE, ArrivalTimePicker));
        myHandler.post(new VisibilityRunnable(View.GONE, AMPMPicker));

        currMonthVis =View.GONE;
        MonthRow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (currMonthVis == View.GONE) {
                    myHandler.post(new VisibilityRunnable(View.VISIBLE, MonthPicker));
                    currMonthVis = View.VISIBLE;
                } else {
                    myHandler.post(new VisibilityRunnable(View.GONE, MonthPicker));
                    currMonthVis = View.GONE;
                    //myHandler.post(new SnoozeTextRunnable(snoozeTime));
                }

            }
        });
        currDayVis = View.GONE;

        currArrivalTimeVis = View.GONE;

        currAMPMVis = View.GONE;



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

            class VisibilityRunnable implements Runnable {
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
}
