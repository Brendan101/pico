package com.app.pico;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm implements Serializable{
    // prepTime is in minutes
    private int ID, prepTime;
    private String eventName;
    private boolean active, repeatable;
    private Calendar arrivalTime;
    private float startLat, startLon, endLat, endLon;

    public Alarm(int ID, String eventName, boolean active, boolean repeatable, int prepTime, Calendar arrivalTime, float startLat, float startLon,
                 float endLat, float endLon)
    {
        this.ID = ID;
        this.eventName = eventName;
        this.active = active;
        this.repeatable = repeatable;
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

    public boolean isActive() { return active; }

    public boolean isRepeatable() {
        return repeatable;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public String getPrepTimeForDisplay() {
        int min = prepTime % 60;
        int hours = prepTime/60;
        String prepTimeString;

        if(hours == 0) {
            prepTimeString = min + " min";
        }
        else {
            prepTimeString = hours + " hours " + min + " min";
        }

        return prepTimeString;
    }

    public Calendar getArrivalTime() { return arrivalTime; }

    public String getArrivalTimeAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setActive(int active) {
        if(active == 0) {
            this.active = false;
        }
        else {
            this.active = true;
        }
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setRepeatable(int repeatable) {
        if(repeatable == 0) {
            this.repeatable = false;
        }
        else {
            this.repeatable = true;
        }
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setArrivalTime(Calendar arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
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
