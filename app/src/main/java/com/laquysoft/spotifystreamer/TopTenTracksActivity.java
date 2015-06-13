package com.laquysoft.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TopTenTracksActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TopTenTracksFragment())
                    .commit();
        }
    }

}
