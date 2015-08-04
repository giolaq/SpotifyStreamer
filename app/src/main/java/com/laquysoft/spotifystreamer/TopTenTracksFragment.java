package com.laquysoft.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.laquysoft.spotifystreamer.components.DaggerSpotifyServiceComponent;
import com.laquysoft.spotifystreamer.components.SpotifyServiceComponent;
import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;
import com.laquysoft.spotifystreamer.modules.SpotifyServiceModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksFragment extends Fragment {

    private static final String LOG_TAG = TopTenTracksFragment.class.getSimpleName();

    private TracksAdapter mTracksAdapter;

    private ArrayList<ParcelableSpotifyObject> trackArrayList;

    private String mSpotifyId;

    private int mSelectedTrackIdx;

    @Inject
    SpotifyService spotifyService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10, container, false);


        if (savedInstanceState != null) {
            trackArrayList = savedInstanceState.getParcelableArrayList("TopTenTracks");
            mSelectedTrackIdx = savedInstanceState.getInt("selectedTrackId");

        } else {
            trackArrayList = new ArrayList<ParcelableSpotifyObject>();
        }

        // The TracksAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mTracksAdapter = new TracksAdapter(getActivity(), R.layout.list_item_artist, trackArrayList, TracksAdapter.VIEW_TYPE_TOP_TRACK);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mSelectedTrackIdx = position;
                ParcelableSpotifyObject selectedTrack = mTracksAdapter.getItem(mSelectedTrackIdx);
                String trackName = selectedTrack.mName;
                String albumName = selectedTrack.mFatherName;
                String largeThumbnail = selectedTrack.largeThumbnailUrl;
                String smallThumbnailUrl = selectedTrack.smallThumbnailUrl;
                String previewUrl = selectedTrack.previewUrl;
                Log.i(LOG_TAG, "Selected Track " + trackName +
                        " from Album " + albumName +
                        " large Thumbnail url " + largeThumbnail +
                        " small Thumbnail url " + smallThumbnailUrl +
                        " previewUrl " + previewUrl);
                ((PlayerFragment.PlayerCallback) getActivity())
                        .onItemSelected(trackArrayList,mSelectedTrackIdx);

            }
        });
        return rootView;
    }

    private void updateTopTenTracks() {
        FetchTopTenTracksTask fetchTopTenTracksTask = new FetchTopTenTracksTask();
        fetchTopTenTracksTask.execute(mSpotifyId);
    }


    public ParcelableSpotifyObject loadNext() {
        ParcelableSpotifyObject selectedTrack = null;
        if (mSelectedTrackIdx < mTracksAdapter.getCount() - 1) {
            mSelectedTrackIdx = mSelectedTrackIdx + 1;
            selectedTrack = mTracksAdapter.getItem(mSelectedTrackIdx);
        }
        return selectedTrack;
    }

    public ParcelableSpotifyObject loadPrevious() {
        ParcelableSpotifyObject selectedTrack = null;
        if (mSelectedTrackIdx != 0) {
            mSelectedTrackIdx = mSelectedTrackIdx - 1;
            selectedTrack = mTracksAdapter.getItem(mSelectedTrackIdx);
        }
        return selectedTrack;

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public class FetchTopTenTracksTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = FetchTopTenTracksTask.class.getSimpleName();
        private RetrofitError retrofitError;


        @Override
        protected Tracks doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyServiceComponent component = DaggerSpotifyServiceComponent.builder().spotifyServiceModule(new SpotifyServiceModule()).build();

            spotifyService = component.provideSpotifyService();

            Map<String, Object> country = new HashMap<String, Object>();
            String locale = getActivity().getResources().getConfiguration().locale.getCountry();
            country.put("country", locale);

            try {
                return spotifyService.getArtistTopTrack(params[0], country);

            } catch (RetrofitError error) {
                retrofitError = error;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tracks result) {
            if (result != null) {
                if (result.tracks.isEmpty()) {
                    Toast.makeText(getActivity(), "Track not found, please refine your search", Toast.LENGTH_LONG).show();
                } else {
                    mTracksAdapter.clear();
                    String smallImageUrl = "";
                    String bigImageUrl = "";
                    for (Track track : result.tracks) {
                        if (!track.album.images.isEmpty()) {
                            smallImageUrl = track.album.images.get(0).url;
                        }
                        if (track.album.images.size() > 1) {
                            bigImageUrl = track.album.images.get(1).url;
                        }
                        StringBuilder builder = new StringBuilder();
                        for (ArtistSimple artist : track.artists) {
                            if (builder.length() > 0) builder.append(", ");
                            builder.append(artist.name);
                        }
                        ParcelableSpotifyObject parcelableSpotifyObject = new ParcelableSpotifyObject(track.name,
                                track.album.name,
                                builder.toString(),
                                smallImageUrl,
                                bigImageUrl,
                                track.preview_url);
                        mTracksAdapter.add(parcelableSpotifyObject);
                        mTracksAdapter.notifyDataSetChanged();
                    }
                }
                // New data is back from the server.  Hooray!
            } else {
                Toast.makeText(getActivity(), "Ooops " + retrofitError.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle arguments = getArguments();
        if (arguments != null) {
            mSpotifyId = arguments.getString("artistId");

        }

        if (savedInstanceState != null) {
        } else {
            updateTopTenTracks();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("TopTenTracks", trackArrayList);
        savedInstanceState.putInt("selectedTrackId", mSelectedTrackIdx);
        super.onSaveInstanceState(savedInstanceState);

    }

}
