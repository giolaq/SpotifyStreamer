package com.laquysoft.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ArtistsFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

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

            TopTenTracksFragment fragment = new TopTenTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.toptentracks_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTenTracksActivity.class);
            intent.putExtra("artistId",spotifyId);
            intent.putExtra("artist",name);
            startActivity(intent);
        }
    }
}
