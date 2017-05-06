package com.app.pico;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm implements Serializable{
    // prepTime is in minutes
    private int ID, prepTime, startLocationID, endLocationID;
    private String eventName;
    private boolean active, repeatable;
    private Calendar arrivalTime;

    public Alarm(int ID, String eventName, boolean active, boolean repeatable, int prepTime, Calendar arrivalTime,
                 int startLocationID, int endLocationID)
    {
        this.ID = ID;
        this.eventName = eventName;
        this.active = active;
        this.repeatable = repeatable;
        this.prepTime = prepTime;
        this.arrivalTime = arrivalTime;
        this.startLocationID = startLocationID;
        this.endLocationID = endLocationID;
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

    public int getStartLocationID() { return startLocationID; }

    public int getEndLocationID() { return endLocationID; }

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

    public void setStartLocationID(int startLocationID) {
        this.startLocationID = startLocationID;
    }

    public void setEndLocationID(int endLocationID) {
        this.endLocationID = endLocationID;
    }
}
