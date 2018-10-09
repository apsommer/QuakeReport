package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    // constant value for the ID of the single earthquake loader
    private static final int EARTHQUAKE_LOADER_ID = 0;

    // define state variables to be initialized in onCreate()
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;

    // initialize options menu in Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // called when the settings menu is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get id of menu item
        int id = item.getItemId();

        // hamburger icon in top-right is pressed
        if (id == R.id.action_settings) {

            // explicit Intent to start new SettingsActivity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        // call through to base class to perform the default menu handling
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
Log.e(LOG_TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~ onCreate");
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

        // get the hardcoded default preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // retrieve user preference for minimum magnitude
        // a reference to the default preference is required by getString
        String minMagnitudeKey = getString(R.string.settings_min_magnitude_key);
        String minMagnitudeDefaultValue = getString(R.string.settings_min_magnitude_default);
        String minMagnitude = sharedPrefs.getString(minMagnitudeKey, minMagnitudeDefaultValue);

        // retrieve user preference for order-by
        // a reference to the default preference is required by getString
        String orderByKey = getString(R.string.settings_order_by_key);
        String orderByDefaultValue = getString(R.string.settings_order_by_default);
        String orderBy = sharedPrefs.getString(orderByKey , orderByDefaultValue);

        // split URL String into constituent parts
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        // prepare URI object for appending query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // append query parameters, for example "format=geojson"
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        // convert completed URI to String
        // for example "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=orderBy"
        String urlFromUri = uriBuilder.toString();

        // pass concatenated URL to new loader
        EarthquakeLoader loader = new EarthquakeLoader(this, urlFromUri);
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
