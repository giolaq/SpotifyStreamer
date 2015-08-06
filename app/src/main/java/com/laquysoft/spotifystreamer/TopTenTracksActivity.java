package com.laquysoft.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

import java.util.ArrayList;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksActivity extends AppCompatActivity implements PlayerFragment.PlayerCallback {

    private String mArtistName;
    private String mSpotifyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// The detail Activity called via intent.  Inspect the intent for  data.
        Intent intent = getIntent();
        if (intent != null) {
            mArtistName = intent.getStringExtra("artist");
            mSpotifyId = intent.getStringExtra("artistId");
            getSupportActionBar().setSubtitle(mArtistName);

        }
        setContentView(R.layout.activity_top10);

        TopTenTracksFragment topTenTracksFragment;

        if (savedInstanceState == null) {


            topTenTracksFragment = new TopTenTracksFragment();

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);
            arguments.putString("artistId", mSpotifyId);


            topTenTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_top10, topTenTracksFragment, "TopTenTracksFragment")
                    .commit();
        } else {
            topTenTracksFragment = (TopTenTracksFragment) getSupportFragmentManager().findFragmentByTag("TopTenTracksFragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(ArrayList<ParcelableSpotifyObject> selectedTrack, int idx) {

        MediaPlayerService.setTracks(this, selectedTrack);

        FragmentManager fragmentManager = getSupportFragmentManager();

        PlayerFragment playerFragment = PlayerFragment.newInstance(selectedTrack, idx);


        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, playerFragment, "PlayerFragment")
                .addToBackStack(null).commit();

    }

}
