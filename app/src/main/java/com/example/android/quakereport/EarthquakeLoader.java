package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

// loads a list of earthquake metadata using a background AsyncTask
// to perform a network URL request
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    // initialize state variable for url String
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {

        // inherit loader initialization configuration from superclass AsyncTaskLoader
        super(context);

        // this loader will only one designated url address
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        // this method is required to trigger loadInBackground()
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {

        // check that the input parameter has at least one string
        if (mUrl == null) {
            return null;
        }

        // Perform the HTTP request for earthquake data and process the response.
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;

    }

}
