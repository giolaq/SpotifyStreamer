package com.laquysoft.spotifystreamer.events;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

/**
 * Created by joaobiriba on 01/08/15.
 */
public class TrackPlayingEvent {

    ParcelableSpotifyObject mTrack;
    int mProgress;

    public TrackPlayingEvent(ParcelableSpotifyObject track, int progress) {
        mTrack = track;
        mProgress = progress;
    }

    public ParcelableSpotifyObject getTrack() {
        return mTrack;
    }
    public int getProgress() {
        return mProgress;
    }

    public static TrackPlayingEvent newInstance(ParcelableSpotifyObject track, int progress) {
        return new TrackPlayingEvent(track, progress);
    }
}
