package com.laquysoft.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by joaobiriba on 06/07/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_NEXT = "NEXT";
    private static final String ACTION_PREVIOUS = "PREVIOUS";


    private static final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    private static String mUrl;
    private static MediaPlayerService mInstance = null;

    private MediaPlayer mMediaPlayer = null;    // The Media Player
    private int mBufferPosition;
    private static String mSongTitle;
    private static String mSongPicUrl;

    NotificationManager mNotificationManager;
    Notification mNotification = null;
    final int NOTIFICATION_ID = 1;

    private final Handler handler = new Handler();
    private Intent intent;
    public static final String BROADCAST_ACTION = "com.laquysoft.spotifystreamer.DISPLAYSEEK";


    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped, // media player is stopped and not prepared to play
        Preparing, // media player is preparing...
        Playing, // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused
        // playback paused (media player ready!)
    }

    ;

    State mState = State.Retrieving;

    @Override
    public void onCreate() {
        mInstance = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        intent = new Intent(BROADCAST_ACTION);
    }

    private void handleIntent( Intent intent ) {
        if( intent != null && intent.getAction() != null ) {
            if( intent.getAction().equalsIgnoreCase( ACTION_PLAY ) ) {
                updateNotification("handle intent");
            } else if( intent.getAction().equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            } else if( intent.getAction().equalsIgnoreCase( ACTION_NEXT ) ) {
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer(); // initialize it here
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                initMediaPlayer();
            }

        }
        handler.removeCallbacks(sendUpdatesToUI);
        handler.post(sendUpdatesToUI);

        return START_STICKY;
    }


    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1 second
        }
    };


    private void DisplayLoggingInfo() {
        Log.d(LOG_TAG, "DisplayLoggingInfo " + getCurrentPosition());
        intent.putExtra("mPlayerTrackPosition", getCurrentPosition());
        sendBroadcast(intent);
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mUrl);
        } catch (IllegalArgumentException e) {
            // ...
        } catch (IllegalStateException e) {
            // ...
        } catch (IOException e) {
            // ...
        }

        try {
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        } catch (IllegalStateException e) {
            // ...
        }
        mState = State.Preparing;
    }

    public void restartMusic() {
        // Restart music
    }

    protected void setBufferPosition(int progress) {
        mBufferPosition = progress;
    }

    /**
     * Called when MediaPlayer is ready
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        // Begin playing music
        mState = State.Playing;
        startMusic();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.Retrieving;
        handler.removeCallbacks(sendUpdatesToUI);
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (mState.equals(State.Playing)) {
            mMediaPlayer.pause();
            mState = State.Paused;
            updateNotification(mSongTitle + "(paused)");
        }
    }

    public void startMusic() {
        if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
            mMediaPlayer.start();
            mState = State.Playing;
            updateNotification(mSongTitle + "(playing)");
        }
    }

    public boolean isPlaying() {
        if (mState.equals(State.Playing)) {
            return true;
        }
        return false;
    }

    public int getMusicDuration() {
        // Return current music duration
        return 0;
    }

    public int getCurrentPosition() {
        int currentPosition = 0;
        try {
            currentPosition = mMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException excp) {
            Log.d(LOG_TAG, "getCurrentPosition inconsistent state mplayer");
        }

        // Return current position
        return currentPosition;
    }

    public int getBufferPercentage() {
        return mBufferPosition;
    }

    public void seekMusicTo(int pos) {
        // Seek music to pos
        mMediaPlayer.seekTo(pos);
    }

    public static MediaPlayerService getInstance() {
        return mInstance;
    }

    public static void setSong(String url, String title, String songPicUrl) {
        mUrl = url;
        mSongTitle = title;
        mSongPicUrl = songPicUrl;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public String getSongPicUrl() {
        return mSongPicUrl;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getMusicDuration() / 100);
    }

    /**
     * Updates the notification.
     */
    void updateNotification(String text) {
        // Notify NotificationManager of new intent
        // NotificationCompatBuilder is a very convenient way to build backward-compatible
        // notifications.  Just throw in some data.
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Spotify Streamer")
                .setContentText(text);

        // Make something interesting happen when the user clicks on the notification.
        // In this case, opening the app is sufficient.
        Intent resultIntent = new Intent(getApplicationContext(), MediaPlayerService.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            notification.bigContentView = getExpandedView( true, notification );

        NotificationManager mNotificationManager =
                (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, notification);


    }

    /**
     * Configures service as a foreground service. A foreground service is a service that's doing something the user is
     * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
     * the notification here.
     */
    void setUpAsForeground(String text) {
        PendingIntent pi =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new Notification();
        mNotification.tickerText = text;
        mNotification.icon = R.mipmap.ic_launcher;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), text, pi);
        startForeground(NOTIFICATION_ID, mNotification);
    }

    private RemoteViews getExpandedView( boolean isPlaying, Notification notification ) {
        RemoteViews customView = new RemoteViews( getPackageName(), R.layout.view_notification );

        Picasso.with(getApplicationContext()).load(mSongPicUrl).into(customView, R.id.large_icon, NOTIFICATION_ID, notification);

        customView.setImageViewResource( R.id.large_icon, R.mipmap.ic_launcher );
        customView.setImageViewResource( R.id.ib_rewind, android.R.drawable.ic_media_previous );

        if( isPlaying )
            customView.setImageViewResource( R.id.ib_play_pause, android.R.drawable.ic_media_pause );
        else
            customView.setImageViewResource( R.id.ib_play_pause, android.R.drawable.ic_media_play );

        customView.setImageViewResource( R.id.ib_fast_forward, android.R.drawable.ic_media_next );

        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );

        intent.setAction( ACTION_PLAY );
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        //customView.setOnClickPendingIntent( R.id.ib_play_pause, pendingIntent );

        intent.setAction( ACTION_NEXT );
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        //customView.setOnClickPendingIntent( R.id.ib_fast_forward, pendingIntent );

        intent.setAction( ACTION_PREVIOUS );
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        //customView.setOnClickPendingIntent( R.id.ib_rewind, pendingIntent );

        return customView;
    }
}