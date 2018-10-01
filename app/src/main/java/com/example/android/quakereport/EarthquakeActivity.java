
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // super class constructor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // list of earthquakes
        final ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();

        // find a reference to the ListView
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // custom adapter populates ListView
        final EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);
        earthquakeListView.setAdapter(adapter);

        // set an item click listener on the ListView items
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // click opens up USGS page for more detailed information
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current Earthquake object
                Earthquake currentEarthquake = adapter.getItem(position);

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

    }
}
