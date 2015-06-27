package com.laquysoft.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksActivity extends AppCompatActivity implements TopTenTracksFragment.PlayerCallback {

    private String mArtistName;
    private String mSpotifyId;
    private PlayerFragment newFragment;

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


        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);
            arguments.putString("artistId", mSpotifyId);

            TopTenTracksFragment topTenTracksFragment = new TopTenTracksFragment();
            topTenTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_top10, topTenTracksFragment)
                    .commit();
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
    public void onItemSelected(ParcelableSpotifyObject selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        newFragment = new PlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerFragment.TRACK_INFO_KEY, selectedTrack);

        newFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();


    }

    public void play(View w) {
       newFragment.play(w);
    }

}
