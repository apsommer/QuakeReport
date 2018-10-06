package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    // simple string tag for log messages
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    // URL query returns JSON object representing the most recent earthquakes
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=50";

    // constant value for the ID of the single earthquake loader
    private static final int EARTHQUAKE_LOADER_ID = 0;

    // define state variables to be initialized in onCreate()
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;

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

        // define an empty view in the rare case no earthquakes exist for the URL query parameters
        mEmptyTextView = (TextView) findViewById(R.id.empty_list);
        earthquakeListView.setEmptyView(mEmptyTextView);

        // define and display ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

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

                // this conditional prevents the app from crashing by ensuring
                // a web browser actually exists on the phone
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }

            }

        });

        // get status of internet connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

        // check status for internet connectivity
        if (isConnected) {

            // initialize a loader manager to handle a background thread
            LoaderManager loaderManager = getLoaderManager();

            // automatically calls onCreateLoader()
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }
        else { // not connected to internet

            // hide the progress bar
            mProgressBar.setVisibility(View.GONE);

            // the earthquakes list is empty
            mEmptyTextView.setText(R.string.no_internet_connection);

        }

    }

    // automatically called when the loader manager determines that a loader with an id of
    // EARTHQUAKE_LOADER_ID does not exist
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        // create and return a new loader with the given URL
        EarthquakeLoader loader = new EarthquakeLoader(this, USGS_REQUEST_URL);

        return loader;

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        // clear the adapter of any previous query to USGS database
        mAdapter.clear();

        // hide the progress bar
        mProgressBar.setVisibility(View.GONE);

        // check the input exists and is not empty
        if (earthquakes != null && !earthquakes.isEmpty()) {

            // calling addAll method on the adapter automatically triggers the ListView to update
            mAdapter.addAll(earthquakes);
        }
        else {

            // the earthquakes list is empty
            mEmptyTextView.setText(R.string.no_earthquakes_found);
        }

    }

    // previously created loader is no longer needed and existing data should be discarded
    // for this app, this only happens when the device "Back" button is pressed
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        // removing all data from adapter automatically clears the UI listview
        mAdapter.clear();

    }
}
