/**
 * Created by joaobiriba on 10/06/15.
 */
package com.laquysoft.spotifystreamer;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Encapsulates fetching the artists and displaying them in a list .
 */
public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    private ArtistAdapter mArtistsAdapter;
    private EditText artistEditText;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mArtistsAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, new ArrayList<Artist>());


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistEditText = (EditText) rootView.findViewById(R.id.input_artist);

        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String artistString = artistEditText.getText().toString();

                    updateArtists(artistString);

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
                String spotifyId = mArtistsAdapter.getItem(position).id;
                Log.i(LOG_TAG, "Click on Artist ID " + spotifyId);
                Intent intent = new Intent(getActivity(), TopTenTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, spotifyId);
                intent.putExtra("artist", mArtistsAdapter.getItem(position).name);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateArtists(String query) {
        FetchArtistsTask artistsTask = new FetchArtistsTask();
        String artistString = artistEditText.getText().toString();
        artistsTask.execute(query);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();
        private RetrofitError retrofitError;

        @Override
        protected List<Artist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }


            SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
            // api.setAccessToken("myAccessToken");

            SpotifyService spotify = api.getService();

            try {
                ArtistsPager artistsPager = spotify.searchArtists(params[0]);
                return artistsPager.artists.items;

            } catch (RetrofitError error) {
                retrofitError = error;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                mArtistsAdapter.clear();
                mArtistsAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            } else {
                Toast.makeText(getActivity(), "Ooops " + retrofitError.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Query", artistEditText.getText().toString());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("Query")) {
            updateArtists(savedInstanceState.getString("Query"));
        }
    }
}