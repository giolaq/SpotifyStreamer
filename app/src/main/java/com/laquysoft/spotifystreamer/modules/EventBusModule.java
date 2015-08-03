package com.laquysoft.spotifystreamer.modules;

import com.laquysoft.spotifystreamer.MediaPlayerService;
import com.laquysoft.spotifystreamer.PlayerFragment;
import com.laquysoft.spotifystreamer.common.MainThreadBus;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by joaobiriba on 01/08/15.
 */
@Module
public class EventBusModule {

    @Provides @Singleton MainThreadBus provideBus() {
        return MainThreadBus.getInstance();
    }

}
