package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    // regular expression used to split location string
    private static final String LOCATION_SEPARATOR = " of ";

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

        // get TextView references from list_item
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);
        TextView locationOffsetTextView = (TextView) listItemView.findViewById(R.id.location_offset);
        TextView locationPrimaryTextView = (TextView) listItemView.findViewById(R.id.location_primary);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);

        // get magnitude from the current Earthquake object and set to textview
        double magnitude = currentEarthquake.getMagnitude();
        magnitudeTextView.setText(formatMagnitude(magnitude)); // helper method coverts to time

        // set the proper background color on the magnitude circle.
        // fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        // split the location string into two parts for UI formatting
        // ex: "88km N of Yelizovo, Russia", therefore split String on word " of "
        String location = currentEarthquake.getLocation();
        String[] locationSplit = location.split(LOCATION_SEPARATOR); // LOCATION_SEPARATOR = " of "

        // define the two parts of location string
        String locationOffset;
        String locationPrimary;

        // not all location strings contain the offset portion
        if (location.contains(LOCATION_SEPARATOR)) {
            locationOffset = locationSplit[0] + LOCATION_SEPARATOR; // super simple string concatenation with +
            locationPrimary = locationSplit[1];
        }
        else {
            locationOffset = getContext().getString(R.string.near_the);
            locationPrimary = locationSplit[0];
        }

        locationOffsetTextView.setText(locationOffset);
        locationPrimaryTextView.setText(locationPrimary);

        // convert Unix epoch time to standard date and time format
        Date dateObject = new Date(currentEarthquake.getTime()); // epoch time
        dateTextView.setText(formatDate(dateObject)); // helper method converts to date
        timeTextView.setText(formatTime(dateObject)); // helper method coverts to time

        // return the inflated view to fragment
        return listItemView;

    }

    // simple helper method coverts time epoch to standard date
    private String formatDate(Date dateObject) {


        SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormatter.format(dateObject);

    }

    // simple helper method coverts time epoch to standard time
    private String formatTime(Date dateObject) {

        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        return timeFormatter.format(dateObject);

    }

    // simple helper method coverts double to String of the form "0.0"
    private String formatMagnitude(double magnitude) {

        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        return decimalFormatter.format(magnitude);

    }

    // simple helper method applies 1 of 10 colors to magnitude circle background
    private int getMagnitudeColor(double magnitude) {

        int color;
        switch ((int) magnitude) { // casting double to int simply truncates the decimal off
            case 0:
            case 1:
                color = R.color.magnitude1;
                break;
            case 2:
                color = R.color.magnitude2;
                break;
            case 3:
                color = R.color.magnitude3;
                break;
            case 4:
                color = R.color.magnitude4;
                break;
            case 5:
                color = R.color.magnitude5;
                break;
            case 6:
                color = R.color.magnitude6;
                break;
            case 7:
                color = R.color.magnitude7;
                break;
            case 8:
                color = R.color.magnitude8;
                break;
            case 9:
                color = R.color.magnitude9;
                break;
            default:
                color = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), color);
    }

}
