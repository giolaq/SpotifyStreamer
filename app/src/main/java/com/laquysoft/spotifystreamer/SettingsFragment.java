package com.laquysoft.spotifystreamer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by joaobiriba on 05/08/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = SettingsFragment.class.getSimpleName();

    //Constants
    public static String PREF_COUNTRY_FOR_RESULTS;

    //Variables
    SharedPreferences mSharedPreferences;

    public SettingsFragment() {}

    public static Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Lifecycle methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        PREF_COUNTRY_FOR_RESULTS = getResources().getString(R.string.pref_country_code_key);

        mSharedPreferences = getPreferenceScreen().getSharedPreferences();
        updateCountryForResultsSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    /**
     * Custom methods
     */
    public void updateCountryForResultsSummary() {
        String currentValue = mSharedPreferences.getString(PREF_COUNTRY_FOR_RESULTS, "");
        List<String> countryEntryValues = Arrays.asList(getResources().getStringArray(R.array.pref_country_entryValues));
        List<String> countriesEntries = Arrays.asList(getResources().getStringArray(R.array.pref_country_entries));

        String summary = "";
        for(String entryValue : countryEntryValues) {
            if(entryValue.equals(currentValue)) {
                summary = countriesEntries.get(countryEntryValues.indexOf(entryValue));
                break;
            }
        }

        findPreference(PREF_COUNTRY_FOR_RESULTS).setSummary(summary);
    }


    /**
     * Listener for SharedPreferences update
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PREF_COUNTRY_FOR_RESULTS))
            updateCountryForResultsSummary();
    }

}
