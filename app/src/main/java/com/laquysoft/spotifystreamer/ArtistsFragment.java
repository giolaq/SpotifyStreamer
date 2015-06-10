/**
 * Created by joaobiriba on 10/06/15.
 */
package com.laquysoft.spotifystreamer;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Encapsulates fetching the artists and displaying them in a list .
 */
public class ArtistsFragment extends Fragment {

    private ArrayAdapter<String> mArtistsAdapter;
    private EditText artistEditText;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mArtistsAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_artist, // The name of the layout ID.
                        R.id.list_item_artist_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

         artistEditText = (EditText) rootView.findViewById(R.id.input_artist);

        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    updateArtists();

                    return true;
                }
                return false;
            }
        });
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String artist = mArtistsAdapter.getItem(position);
                //Intent intent = new Intent(getActivity(), DetailActivity.class)
                //      .putExtra(Intent.EXTRA_TEXT, forecast);
                //startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateArtists() {
        FetchArtistsTask artistsTask = new FetchArtistsTask();
        String artistString = artistEditText.getText().toString();
        artistsTask.execute(artistString);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateArtists();
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();


        /**
         * Take the String representing the complete output for the artist in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getArtistsDataFromJson(String artistsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_ARTISTS = "artists";
            final String OWM_ITEMS = "items";
            final String OWM_NAME = "name";

            JSONObject artistsJson = new JSONObject(artistsJsonStr);
            JSONObject artistsObj = artistsJson.getJSONObject(OWM_ARTISTS);
            JSONArray artistsArray = artistsObj.getJSONArray(OWM_ITEMS);

            String[] resultStrs = new String[artistsArray.length()];
            for (int i = 0; i < artistsArray.length(); i++) {

                String name;

                // Get the JSON object representing the day
                JSONObject artistObject = artistsArray.getJSONObject(i);
                name = artistObject.getString(OWM_NAME);

                resultStrs[i] = name;
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String artistsJsonStr = null;

            String type = "artist";

            try {

                final String SPOTIFY_BASE_URL =
                        "https://api.spotify.com/v1/search?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String TYPE_PARAM = "type";

                Uri builtUri = Uri.parse(SPOTIFY_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(TYPE_PARAM, type)
                        .build();


                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                artistsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getArtistsDataFromJson(artistsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mArtistsAdapter.clear();
                for (String dayForecastStr : result) {
                    mArtistsAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}