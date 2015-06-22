package com.laquysoft.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by joaobiriba on 20/06/15.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final String LOG_TAG = PlayerActivity.class.getSimpleName();

    public static final String TRACK_INFO_KEY = "selectedTrack";

    private ParcelableSpotifyObject trackToPlay;
    private static MediaPlayer mediaPlayer;

    @InjectView(R.id.albumThumbIm)
    ImageView trackAlbumThumbnail;

    @InjectView(R.id.play_button)
    Button playButton;

    @InjectView(R.id.scrubbar)
    SeekBar scrubBar;


    private int trackProgress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            trackToPlay = getIntent().getParcelableExtra(TRACK_INFO_KEY);
        } else {
            trackToPlay = savedInstanceState.getParcelable(TRACK_INFO_KEY);
            trackProgress = savedInstanceState.getInt("Progress");
            scrubBar.setProgress(trackProgress);
        }

        if (!trackToPlay.largeThumbnailUrl.isEmpty()) {
            Picasso.with(this).load(trackToPlay.largeThumbnailUrl).into(trackAlbumThumbnail);
        }

        if (mediaPlayer == null) {
            initializeMediaPlayer();
        }

        scrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(LOG_TAG, "Progress " + progress);
                if (fromUser) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(300 * progress);
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.seekTo(300 * progress);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void play(View w) {
        if (mediaPlayer.isPlaying()) {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            mediaPlayer.pause();
        } else {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRACK_INFO_KEY, trackToPlay);
        outState.putInt("Progress", scrubBar.getProgress());
    }

    private void initializeMediaPlayer() {
        if (!trackToPlay.previewUrl.isEmpty()) {
            String url = trackToPlay.previewUrl;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.seekTo(300 * trackProgress);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if (mp.isPlaying() && scrubBar != null) {
                            scrubBar.setProgress(mp.getCurrentPosition() / 300);
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
                        scrubBar.setProgress(0);
                    }
                });
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
