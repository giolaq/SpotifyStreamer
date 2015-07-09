package com.laquysoft.spotifystreamer;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by joaobiriba on 20/06/15.
 */
public class PlayerFragment extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = PlayerFragment.class.getSimpleName();

    public static final String TRACK_INFO_KEY = "selectedTrack";

    private ParcelableSpotifyObject trackToPlay;

    @InjectView(R.id.albumThumbIm)
    ImageView trackAlbumThumbnail;

    @InjectView(R.id.play_button)
    Button playButton;

    @InjectView(R.id.next_button)
    Button nextButton;

    @InjectView(R.id.previous_button)
    Button previousButton;

    @InjectView(R.id.artistTv)
    TextView artistTv;

    @InjectView(R.id.albumTv)
    TextView albumTv;

    @InjectView(R.id.trackNameTv)
    TextView trackNameTv;

    @InjectView(R.id.scrubbar)
    SeekBar scrubBar;


    private int trackProgress = 0;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface PlayerCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(ParcelableSpotifyObject selectedTrack);

        public void onNext();

        public void onPrevious();
    }


    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.PlayerDialogTheme);


        return dialog;
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.player_activity, container, false);

        ButterKnife.inject(this, rootView);


        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            trackToPlay = getArguments().getParcelable(TRACK_INFO_KEY);
        } else {
            trackToPlay = savedInstanceState.getParcelable(TRACK_INFO_KEY);
            trackProgress = savedInstanceState.getInt("Progress");
            scrubBar.setProgress(trackProgress);
        }

        if (!trackToPlay.largeThumbnailUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.largeThumbnailUrl).into(trackAlbumThumbnail);
        }

        if (!trackToPlay.mName.isEmpty()) {
            trackNameTv.setText(trackToPlay.mName);
        }

        if (!trackToPlay.mFatherName.isEmpty()) {
            albumTv.setText(trackToPlay.mFatherName);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistTv.setText(trackToPlay.mArtistName);
        }


        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mName, trackToPlay.largeThumbnailUrl);
        getActivity().startService(new Intent("PLAY"));


       /* if (mediaPlayer == null) {
            initializeMediaPlayer();
        } else {
            if (trackToPlay != null && !mediaPlayer.isPlaying())
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(trackToPlay.previewUrl);
                    linkScrubBarToMediaPlayer();
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (mediaPlayer.isPlaying()) {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            } else {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            }
            linkScrubBarToMediaPlayer();
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
*/
        return rootView;
    }

    public void play(View w) {
        playButton = (Button) w;
        if (MediaPlayerService.getInstance().isPlaying()) {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            MediaPlayerService.getInstance().pauseMusic();
        } else {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            MediaPlayerService.getInstance().startMusic();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRACK_INFO_KEY, trackToPlay);
        outState.putInt("Progress", scrubBar.getProgress());
    }


    private void initializeMediaPlayer() {
        if (!trackToPlay.previewUrl.isEmpty()) {
            String url = trackToPlay.previewUrl;
          /*  mediaPlayer = new MediaPlayer();
            mediaPlayer.seekTo(300 * trackProgress);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                linkScrubBarToMediaPlayer();
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

    }

    private void linkScrubBarToMediaPlayer() {
       /* mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
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
        });*/

    }


    public void onNext(ParcelableSpotifyObject selectedTrack) {


        trackToPlay = selectedTrack;


        if (!trackToPlay.largeThumbnailUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.largeThumbnailUrl).into(trackAlbumThumbnail);
        }

        if (!trackToPlay.mName.isEmpty()) {
            trackNameTv.setText(trackToPlay.mName);
        }

        if (!trackToPlay.mFatherName.isEmpty()) {
            albumTv.setText(trackToPlay.mFatherName);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistTv.setText(trackToPlay.mArtistName);
        }

        scrubBar.setProgress(0);

        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mName, trackToPlay.largeThumbnailUrl);
        getActivity().startService(new Intent("PLAY"));

       /* if (mediaPlayer == null) {
            initializeMediaPlayer();
        } else {
            if (trackToPlay != null)
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(trackToPlay.previewUrl);
                    linkScrubBarToMediaPlayer();
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (mediaPlayer.isPlaying()) {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            } else {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            }
            linkScrubBarToMediaPlayer();
        }*/


    }

    public void onPrevious(ParcelableSpotifyObject selectedTrack) {

        trackToPlay = selectedTrack;


        if (!trackToPlay.largeThumbnailUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.largeThumbnailUrl).into(trackAlbumThumbnail);
        }

        if (!trackToPlay.mName.isEmpty()) {
            trackNameTv.setText(trackToPlay.mName);
        }

        if (!trackToPlay.mFatherName.isEmpty()) {
            albumTv.setText(trackToPlay.mFatherName);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistTv.setText(trackToPlay.mArtistName);
        }

        scrubBar.setProgress(0);
        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mName, trackToPlay.largeThumbnailUrl);
        getActivity().startService(new Intent("PLAY"));

       /* if (mediaPlayer == null) {
            initializeMediaPlayer();
        } else {
            if (trackToPlay != null)
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(trackToPlay.previewUrl);
                    linkScrubBarToMediaPlayer();
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (mediaPlayer.isPlaying()) {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            } else {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            }
            linkScrubBarToMediaPlayer();
        }*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void stop() {
        //mediaPlayer.reset();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                play(v);
                break;
            case R.id.previous_button:
                ((PlayerCallback)getActivity()).onPrevious();
                break;
            case R.id.next_button:
                ((PlayerCallback)getActivity()).onNext();
                break;
            default:
                break;
        }
    }
}
