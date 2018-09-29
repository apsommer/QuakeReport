
package com.example.android.quakereport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // super class constructor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a fake list of earthquake locations.
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(4.4f,"San Diego", "May 12, 2001"));
        earthquakes.add(new Earthquake(5.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(6.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(2.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));
        earthquakes.add(new Earthquake(3.4f,"San Francisco", "May 12, 2001"));


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
    }
}
