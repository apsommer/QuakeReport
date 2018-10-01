package com.example.android.quakereport;

import android.location.Location;

/**
 * Custom Earthquake object contains 1 float, 1 String, 1 long
 */
public class Earthquake {

    // magnitude
    private double mMagnitude;

    // location
    private String mLocation;

    // time in Unix epoch milliseconds
    private long mTime;

    // url for specific USGS event page
    private String mUrl;

    // constructor
    public Earthquake(double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTime = time;
        mUrl = url;
    }

    // gets magnitude
    public double getMagnitude() { return mMagnitude; }

    // gets location
    public String getLocation() {
        return mLocation;
    }

    // gets time
    public long getTime() {
        return mTime;
    }

    // gets url
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "mMagnitude ='" + mMagnitude + '\'' +
                ", mLocation ='" + mLocation + '\'' +
                ", mTime =" + mTime + '\'' +
                ", mUrl =" + mUrl +
                '}';
    }

}
