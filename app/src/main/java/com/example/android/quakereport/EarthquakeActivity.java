package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    // simple string tag for log messages
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    // query returns JSON object representing the 10 most recent earthquakes with a magnitude of at least 6
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=30";

    // initialize adapter for the ListView of Earthquake objects
    private EarthquakeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // super class constructor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // initialize an empty ArrayList to hold Earthquake objects
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // find a reference to the ListView
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // custom adapter populates ListView
        mAdapter = new EarthquakeAdapter(this, earthquakes);
        earthquakeListView.setAdapter(mAdapter);

        // set an item click listener on the ListView items
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // click opens up USGS page for more detailed information
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current Earthquake object
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Uri object to pass into web browser Intent
                String url = currentEarthquake.getUrl();
                Uri earthquakeUri = Uri.parse(url);

                // create Intent to open web browser
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // this conditional prevents the app from crashing by ensuring an email app
                // actually exists on the phone
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }

            }

        });

//        // start background thread to open url connection
//        // the conclusion of the background thread updates the UI
//        new EarthquakeAsyncTask().execute(USGS_REQUEST_URL);

        // initialize a loader manager to handle background thread
        // this automatically calls onCreateLoader()
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        // create and return a new EarthquakeLoader instance
        EarthquakeLoader loader = new EarthquakeLoader(this, USGS_REQUEST_URL);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        // clear the adapter of any previous query to USGS database
        mAdapter.clear();

        // check the input exists and is not empty
        if (earthquakes != null && !earthquakes.isEmpty()) {

            // calling addAll method on the adapter triggers the ListView to update
            mAdapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        // previously created loader is no longer needed and existing data can be discarded

    }
}
