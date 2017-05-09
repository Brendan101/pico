package com.app.pico;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm implements Serializable{
    // prepTime and snoozeTime are in minutes
    private int ID, prepTime, snoozeTime, startLocationID, endLocationID;
    private String eventName, repeat, sound;
    private boolean active;
    private Calendar arrivalTime;

    public Alarm(int ID, String eventName, boolean active, int prepTime, Calendar arrivalTime,
                 int startLocationID, int endLocationID, String repeat, int snoozeTime, String sound)
    {
        this.ID = ID;
        this.eventName = eventName;
        this.active = active;
        this.prepTime = prepTime;
        this.arrivalTime = arrivalTime;
        this.startLocationID = startLocationID;
        this.endLocationID = endLocationID;
        this.repeat = repeat;
        this.snoozeTime = snoozeTime;
        this.sound = sound;
    }

    public Alarm() {}

    public int getID() { return ID; }

    public String getEventName() {
        return eventName;
    }

    public boolean isActive() { return active; }

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

    public String getArrivalTimeAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
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

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setArrivalTime(String arrivalTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
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

    public int getSnoozeTime() {
        return snoozeTime;
    }

    public void setSnoozeTime(int snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
