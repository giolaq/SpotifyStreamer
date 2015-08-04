package com.laquysoft.spotifystreamer.modules;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by joaobiriba on 04/08/15.
 */
@Module
public class SpotifyServiceModule {

    @Provides
    SpotifyService provideSpotifyService() {
        return new SpotifyApi().getService();
    }
}
