package com.laquysoft.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by joaobiriba on 20/06/15.
 */
public class PlayerActivity extends AppCompatActivity {

    public static final String TRACK_INFO_KEY = "selectedTrack";

    private ParcelableSpotifyObject trackToPlay;
    private MediaPlayer mediaPlayer;

    @InjectView(R.id.imageView)
    ImageView trackAlbumThumbnail;

    @InjectView(R.id.play_button)
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {

            trackToPlay = getIntent().getParcelableExtra(TRACK_INFO_KEY);
        }

        if (!trackToPlay.largeThumbnailUrl.isEmpty()) {
            Picasso.with(this).load(trackToPlay.largeThumbnailUrl).into(trackAlbumThumbnail);
        }


        if (!trackToPlay.previewUrl.isEmpty()) {
            String url = trackToPlay.previewUrl;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void play(View w) {
        mediaPlayer.start();
    }

}
