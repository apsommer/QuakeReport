package com.example.android.quakereport;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    // simple tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // constructor is deliberately empty as no objects of this class should ever exist
    private QueryUtils() {}

    // query the USGS database and return an ArrayList of Earthquake objects
    public static ArrayList<Earthquake> fetchEarthquakeData(String requestUrl) {

        // transform url string to URL object
        URL url = createUrl(requestUrl);

        // initialize raw JSON string to null
        String jsonResponse = null;

        // perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Earthquake> earthquakes = extractEarthquakes(jsonResponse);

        // Return the {@link Event}
        return earthquakes;
    }

    // returns URL object from a given string URL
    private static URL createUrl(String stringUrl) {

        // initialize returned object to null
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return the response string
    private static String makeHttpRequest(URL url) throws IOException {

        // initialize raw JSON string to null
        String jsonResponse = "";

        // if the URL is null then return early
        if (url == null) {
            return jsonResponse;
        }

        // initialize objects for connection and stream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            // open connection, set timeouts, set request method, connect
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); // milliseconds
            urlConnection.setConnectTimeout(15000); // milliseconds
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // check response code of HTTP request
            // 200 means success
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {

                // log HTTP response code
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());

            }

        // IOException is thrown by getInputStream() if something goes wrong parsing the characters
        } catch (IOException e) {

            // log exception stack trace
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);

        // disconnect from url and close stream
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        // data type String
        return jsonResponse;
    }

    // Convert the InputStream into a String which contains the entire raw JSON response
    private static String readFromStream(InputStream inputStream) throws IOException {

        // StringBuilder is mutable, convenient way to construct Strings
        StringBuilder output = new StringBuilder();

        // check that inputStream exists
        if (inputStream != null) {

            // define character set as UTF-8
            // this is at the byte level where each byte defines a single character
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            // parsing a line of characters is much faster than one at a time
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            // append lines to StringBuilder while there are still lines left to read
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // convert mutable StringBuilder to immutable String
        return output.toString();
    }


    // Return a list of Earthquake objects built up from parsing a JSON response.
    private static ArrayList<Earthquake> extractEarthquakes(String jsonResponse) {

        // initialize an empty ArrayList
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the raw jsonResponse String
        try {

            // go down two levels of JSON payload
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray features = root.getJSONArray("features");

            // loop through all features (features = earthquake events)
            for (int i = 0; i < features.length(); i++) {

                // properties Object contains the desired attributes
                JSONObject currentFeature = features.getJSONObject(i);
                JSONObject properties = currentFeature.getJSONObject("properties");

                // get desired attributes from parsed JSON
                double magnitude = (float) properties.getDouble("mag"); // cast double to float
                String location = properties.getString("place");
                long timeEpoch = properties.getLong("time");
                String url = properties.getString("url");

                // add data to new Earthquake object and store in ArrayList
                earthquakes.add(new Earthquake(magnitude, location, timeEpoch, url));

            }

        } catch (JSONException e) {

            // log exception stack trace
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // return the list of earthquakes
        return earthquakes;
    }

}