package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

// loads a list of earthquake metadata using a background AsyncTask
// to perform a network URL request
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    // simple tag for log messages
    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    // initialize state variable for url String
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {

        // inherit loader initialization configuration from superclass AsyncTaskLoader
        super(context);

        // this loader has only one designated url address
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

        // perform the HTTP request for article data and process the JSON response
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);

        return earthquakes;

    }

}
