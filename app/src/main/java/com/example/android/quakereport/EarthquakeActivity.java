package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

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

        // start background thread to open url connection
        // the conclusion of the background thread updates the UI
        new EarthquakeAsyncTask().execute(USGS_REQUEST_URL);

    }

    // inner class for asynchronous background thread
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... url) {

            // check that the input parameter has at least one string
            if (url.length < 1 || url[0] == null) {
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(url[0]);
            return earthquakes;

        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {

            // clear the adapter of any previous query to USGS database
            mAdapter.clear();

            // check the input exists and is not empty
            if (earthquakes != null && !earthquakes.isEmpty()) {

                // calling addAll method on the adapter triggers the ListView to update
                mAdapter.addAll(earthquakes);
            }

        }
    }

}
