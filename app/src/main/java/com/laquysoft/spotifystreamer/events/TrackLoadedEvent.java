package com.laquysoft.spotifystreamer.events;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;

/**
 * Created by joaobiriba on 04/08/15.
 */
public class TrackLoadedEvent {

    ParcelableSpotifyObject mTrack;

    public TrackLoadedEvent(ParcelableSpotifyObject track) {
        mTrack = track;
    }

    public ParcelableSpotifyObject getTrack() {
        return mTrack;
    }

    public static TrackLoadedEvent newInstance(ParcelableSpotifyObject track) {
        return new TrackLoadedEvent(track);
    }
}
