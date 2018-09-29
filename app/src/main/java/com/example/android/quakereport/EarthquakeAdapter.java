package com.example.android.quakereport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    // constructor
    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes) {

        // call superclass ArrayAdapter constructor
        // second argument for populating a single TextView (the default for ArrayAdapter)
        // since a custom layout is inflated in getView() this second argument is arbitrary, for initialization only
        super(context, 0, earthquakes);

    }

    // must override to inflate anything other than the default single TextView expected by ArrayAdapter
    // parent is the ListView in earthquake_activity
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // rename input argument for clarity
        View listItemView = convertView;

        // if the passed view does not exist (therefore it is not being recycled) then inflate it from list_item
        if(listItemView  == null) {
            listItemView  = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // get the Earthquake object at this position in the ArrayList
        Earthquake currentEarthquake = getItem(position);

        // get TextView references from grid_item
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.list_magnitude);
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.list_location);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.list_date);

        // get title and description strings from the current Location object and set to textviews
        magnitudeTextView.setText(String.format("%1.1f", currentEarthquake.getMagnitude()));
        locationTextView.setText(currentEarthquake.getLocation());
        dateTextView.setText(currentEarthquake.getDate());

        // return the inflated view to fragment
        return listItemView;

    }



}
