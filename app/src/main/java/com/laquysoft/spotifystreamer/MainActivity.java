package com.laquysoft.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

public class MainActivity extends AppCompatActivity implements ArtistsFragment.Callback,PlayerFragment.PlayerCallback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;
    private PlayerFragment newFragment;
    private TopTenTracksFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.toptentracks_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.toptentracks_detail_container, new TopTenTracksFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        ArtistsFragment artistsFragment =  ((ArtistsFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_artists));

    }


    @Override
    public void onItemSelected(String spotifyId, String name) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString("artistId", spotifyId);
            args.putString("artist", "bo");

            fragment = new TopTenTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.toptentracks_detail_container, fragment, "TopTenTracksFragment")
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTenTracksActivity.class);
            intent.putExtra("artistId",spotifyId);
            intent.putExtra("artist",name);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(ParcelableSpotifyObject selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        newFragment = new PlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerFragment.TRACK_INFO_KEY, selectedTrack);

        newFragment.setArguments(bundle);

        if (mTwoPane) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }

    }

    @Override
    public void onNext() {
        ParcelableSpotifyObject track = fragment.loadNext();
        newFragment.onNext(track);

    }

    @Override
    public void onPrevious() {
        ParcelableSpotifyObject track = fragment.loadPrevious();
        newFragment.onPrevious(track);


    }

    public void play(View w) {
        newFragment.play(w);
    }

}
