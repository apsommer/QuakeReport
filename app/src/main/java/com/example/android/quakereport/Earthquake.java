package com.example.android.quakereport;

import android.location.Location;

/**
 * Custom Earthquake object contains 1 float, 2 strings
 */
public class Earthquake {

    // magnitude
    private float mMagnitude;

    // location
    private String mLocation;

    // date
    private String mDate;

    // constructor
    public Earthquake(float magnitude, String location, String date) {
        mMagnitude = magnitude;
        mLocation = location;
        mDate = date;
    }

    // gets magnitude
    public float getMagnitude() { return mMagnitude; }

    // gets location
    public String getLocation() {
        return mLocation;
    }

    // gets date
    public String getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "mMagnitude ='" + mMagnitude + '\'' +
                ", mLocation ='" + mLocation + '\'' +
                ", mDate =" + mDate +
                '}';
    }

}
