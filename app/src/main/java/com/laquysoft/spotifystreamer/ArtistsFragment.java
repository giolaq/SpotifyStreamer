/**
 * Created by joaobiriba on 10/06/15.
 */
package com.laquysoft.spotifystreamer;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Encapsulates fetching the artists and displaying them in a list .
 */
public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    private TracksAdapter mArtistsAdapter;

    private ArrayList<ParcelableSpotifyObject> artistArrayList;

    @InjectView(R.id.search_artist)
    SearchView artistSearchView;


    @Inject
    SpotifyService spotifyService;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * ArtistFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String id, String name);

        public void onShowNowPlaying();
    }

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpotifyServiceComponent component = DaggerSpotifyServiceComponent.builder().spotifyServiceModule(new SpotifyServiceModule()).build();

        spotifyService = component.provideSpotifyService();
        Intent intent = getActivity().getIntent();

        if (savedInstanceState != null) {
            artistArrayList = savedInstanceState.getParcelableArrayList("Artists");
        } else {
            artistArrayList = new ArrayList<ParcelableSpotifyObject>();
        }

        mArtistsAdapter = new TracksAdapter(getActivity(), R.layout.list_item_artist, artistArrayList,
                TracksAdapter.VIEW_TYPE_ARTIST);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);


        artistSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        updateArtists(query);

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String spotifyId = mArtistsAdapter.getItem(position).mFatherName;
                String artistName = mArtistsAdapter.getItem(position).mName;
                Log.i(LOG_TAG, "Click on Artist ID " + spotifyId);
                ((Callback) getActivity())
                        .onItemSelected(spotifyId, artistName);
            }
        });

        return rootView;
    }

    private void updateArtists(String query) {
        FetchArtistsTask artistsTask = new FetchArtistsTask();
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

            try {
                ArtistsPager artistsPager = spotifyService.searchArtists(params[0]);
                return artistsPager.artists.items;

            } catch (RetrofitError error) {
                retrofitError = error;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                if (result
                        .isEmpty()) {
                    Toast.makeText(getActivity(), "Artist not found, please refine your search", Toast.LENGTH_LONG).show();
                } else {
                    mArtistsAdapter.clear();
                    String smallImageUrl = "";
                    String bigImageUrl = "";
                    for (Artist track : result) {
                        if (!track.images.isEmpty()) {
                            smallImageUrl = track.images.get(0).url;
                        }
                        if (track.images.size() > 1) {
                            bigImageUrl = track.images.get(1).url;
                        }
                        StringBuilder builder = new StringBuilder();

                        ParcelableSpotifyObject parcelableSpotifyObject = new ParcelableSpotifyObject(track.name,
                                track.id,
                                "",
                                smallImageUrl,
                                bigImageUrl,
                                track.uri);
                        mArtistsAdapter.add(parcelableSpotifyObject);

                    }
                    // New data is back from the server.  Hooray!
                }

            } else {
                if (retrofitError != null) {
                    Toast.makeText(getActivity(), "Ooops " + retrofitError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Artist not found, please refine your search", Toast.LENGTH_LONG).show();

                }

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("Artists", artistArrayList);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("Query")) {
            updateArtists(savedInstanceState.getString("Query"));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_now_playing) {
            openNowPlaying();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openNowPlaying() {

        ((Callback) getActivity())
                .onShowNowPlaying();

    }

}