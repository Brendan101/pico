package com.app.pico;

import java.io.Serializable;

/**
 * Created by Rachel on 5/1/17.
 */

public class Location implements Serializable{
    private int ID;
    private String locationName;
    private float latitude, longitude;

    public Location(int ID, String locationName, float latitude, float longitude) {
        this.ID = ID;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
