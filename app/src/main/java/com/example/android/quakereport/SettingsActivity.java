package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity with superclass constructor
        super.onCreate(savedInstanceState);

        // layout has a single <fragment> tag
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            // initialize fragment with superclass constructor
            super.onCreate(savedInstanceState);

            // layout has a single EditText preference
            addPreferencesFromResource(R.xml.settings_main);

            // get preference key Strings
            String minMagnitudeKey = getString(R.string.settings_min_magnitude_key);
            String orderByKey = getString(R.string.settings_order_by_key);

            // get the preference Objects for these keys
            Preference minMagnitude = findPreference(minMagnitudeKey);
            Preference orderBy = findPreference(orderByKey);

            // set listeners on these preferences
            bindPreferenceSummaryToValue(minMagnitude);
            bindPreferenceSummaryToValue(orderBy);

        }

        private void bindPreferenceSummaryToValue(Preference preference) {

            // set listener on the preference
            preference.setOnPreferenceChangeListener(this);

            // get reference to the devices's shared preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // get the String key for the preference
            String preferenceKey = sharedPreferences.getString(preference.getKey(), "");

            // update the preference TODO comments
            onPreferenceChange(preference, preferenceKey);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            // preference value is of type String
            String stringValue = value.toString();

            // the order-by preference is a list
            if (preference instanceof ListPreference) {

                // cast preference to the proper subclass
                ListPreference listPreference = (ListPreference) preference;

                // get index
                int prefIndex = listPreference.findIndexOfValue(stringValue);

                if (prefIndex >= 0) {

                    //
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }

            }

            else {
                //
                preference.setSummary(stringValue);
            }

            return true;

        }


    }



}
