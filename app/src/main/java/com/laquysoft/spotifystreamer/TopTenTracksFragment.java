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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksFragment extends Fragment {

    private static final String LOG_TAG = TopTenTracksFragment.class.getSimpleName();

    private TracksAdapter mTracksAdapter;

    private String mSpotifyId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10, container, false);

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mSpotifyId = intent.getStringExtra(Intent.EXTRA_TEXT);

        }

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mTracksAdapter = new TracksAdapter(getActivity(), R.layout.list_item_artist, new ArrayList<Track>());


        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);
        View empty = rootView.findViewById(R.id.empty);
        listView.setEmptyView(empty);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track selectedTrack = mTracksAdapter.getItem(position);
                String trackName = selectedTrack.name;
                String albumName = selectedTrack.album.name;
                String largeThumbnail = selectedTrack.album.images.get(0).url;
                String previewUrl = selectedTrack.preview_url;
                Log.i(LOG_TAG, "Selected Track " + trackName +
                    " from Album " + albumName +
                    " large Thumbnail url " + largeThumbnail +
                    " previewUrl " + previewUrl);

            }
        });
        return rootView;
    }

    private void updateTopTenTracks() {
        FetchTopTenTracksTask fetchTopTenTracksTask = new FetchTopTenTracksTask();
        fetchTopTenTracksTask.execute(mSpotifyId);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopTenTracks();
    }


    public class FetchTopTenTracksTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = FetchTopTenTracksTask.class.getSimpleName();


        @Override
        protected Tracks doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }


            SpotifyApi api = new SpotifyApi();


            SpotifyService spotify = api.getService();

            Map<String,Object> country = new HashMap<>();
            String locale = getActivity().getResources().getConfiguration().locale.getCountry();
            country.put("country", locale);


            Tracks tracks = spotify.getArtistTopTrack(params[0], country);
            return tracks;
        }

        @Override
        protected void onPostExecute(Tracks result) {
            if (result != null) {
                mTracksAdapter.clear();
                mTracksAdapter.addAll(result.tracks);
                // New data is back from the server.  Hooray!
            }
        }
    }
}
