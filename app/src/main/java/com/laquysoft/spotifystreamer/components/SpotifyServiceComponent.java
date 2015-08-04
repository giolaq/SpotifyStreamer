package com.laquysoft.spotifystreamer.components;


import com.laquysoft.spotifystreamer.modules.SpotifyServiceModule;

import dagger.Component;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by joaobiriba on 04/08/15.
 */
@Component(modules = {SpotifyServiceModule.class})
public interface SpotifyServiceComponent {
    SpotifyService provideSpotifyService();
}
