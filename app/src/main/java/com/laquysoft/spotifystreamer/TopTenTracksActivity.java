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
    private PlayerFragment playerFragment;
    private TopTenTracksFragment topTenTracksFragment;

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


            //playerFragment = new PlayerFragment();
            topTenTracksFragment = new TopTenTracksFragment();

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);
            arguments.putString("artistId", mSpotifyId);


            topTenTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_top10, topTenTracksFragment, "TopTenTracksFragment")
                    .commit();
        } else {
            //playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
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
        FragmentManager fragmentManager = getSupportFragmentManager();

        stopService(new Intent(this, MediaPlayerService.class));

        playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PlayerFragment.TRACK_IDX_KEY, idx);
        bundle.putParcelableArrayList(PlayerFragment.TRACK_INFO_KEY, selectedTrack);

        playerFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, playerFragment, "PlayerFragment")
                .addToBackStack(null).commit();


    }

    @Override
    public void onNext() {
        ParcelableSpotifyObject selectedTrack = topTenTracksFragment.loadNext();
        playerFragment.onNext(selectedTrack);
    }

    @Override
    public void onPrevious() {
        ParcelableSpotifyObject selectedTrack = topTenTracksFragment.loadPrevious();
        playerFragment.onPrevious(selectedTrack);
    }

    public void play(View w) {
        playerFragment.play(w);
    }

    public void previous(View w) {
        topTenTracksFragment.loadPrevious();
    }

    public void next(View w) {
        topTenTracksFragment.loadNext();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playerFragment.stop();
    }
}
