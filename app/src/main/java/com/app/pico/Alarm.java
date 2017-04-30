package com.app.pico;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm {
    private int ID;
    private String eventName;
    private boolean repeat;
    private Calendar prepTime, arrivalTime;
    private float startLat, startLon, endLat, endLon;

    public Alarm(int ID, String eventName, boolean repeat, Calendar prepTime, Calendar arrivalTime, float startLat, float startLon,
                 float endLat, float endLon)
    {
        this.ID = ID;
        this.eventName = eventName;
        this.repeat = repeat;
        this.prepTime = prepTime;
        this.arrivalTime = arrivalTime;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
    }

    public Alarm() {}

    public int getID() { return ID; }

    public String getEventName() {
        return eventName;
    }

    public boolean getRepeat()
    {
        return repeat;
    }

    public Calendar getPrepTime() {
        return prepTime;
    }

    public String getPrepTimeAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        return formatter.format(prepTime.getTime());
    }

    public Calendar getArrivalTime() { return arrivalTime; }

    public String getArrivalTimeAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        return formatter.format(arrivalTime.getTime());
    }

    public float getStartLat() { return startLat; }

    public float getStartLon() { return startLon; }

    public float getEndLat() { return endLat; }

    public float getEndLon() { return endLon; }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setRepeat(int repeat) {
        if(repeat == 0) {
            this.repeat = false;
        }
        else {
            this.repeat = true;
        }
    }

    public void setPrepTime(Calendar prepTime) {
        this.prepTime = prepTime;
    }

    public void setPrepTime(String prepTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = formatter.parse(prepTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        this.prepTime = cal;
    }

    public void setArrivalTime(Calendar arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        Date date = null;
        try {
            date = formatter.parse(arrivalTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        this.arrivalTime = cal;
    }

    public void setStartLat(float startLat) {
        this.startLat = startLat;
    }

    public void setStartLon(float startLon) {
        this.startLon = startLon;
    }

    public void setEndLat(float endLat) {
        this.endLat = endLat;
    }

    public void setEndLon(float endLon) {
        this.endLon = endLon;
    }
}
