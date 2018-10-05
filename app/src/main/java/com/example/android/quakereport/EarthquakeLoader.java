package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    //
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        // TODO: Finish implementing this constructor
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {

        // TODO: Implement this method
        // check that the input parameter has at least one string
        if (mUrl == null) {
            return null;
        }

        // Perform the HTTP request for earthquake data and process the response.
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;

    }

}
