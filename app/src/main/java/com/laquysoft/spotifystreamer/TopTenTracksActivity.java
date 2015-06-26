package com.laquysoft.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksActivity extends AppCompatActivity implements TopTenTracksFragment.Callback {

    String mArtistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// The detail Activity called via intent.  Inspect the intent for  data.
        Intent intent = getIntent();
        if (intent != null) {
            mArtistName = intent.getStringExtra("artist");
            getSupportActionBar().setSubtitle(mArtistName);

        }
        setContentView(R.layout.activity_top10);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);

            TopTenTracksFragment topTenTracksFragment = new TopTenTracksFragment();
            topTenTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.toptentracks_detail_container, topTenTracksFragment)
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
        Intent intent = new Intent(this, PlayerActivity.class)
                .putExtra(PlayerActivity.TRACK_INFO_KEY, selectedTrack);
        startActivity(intent);

    }
}
