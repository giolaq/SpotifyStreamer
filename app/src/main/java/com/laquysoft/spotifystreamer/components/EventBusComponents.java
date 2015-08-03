package com.laquysoft.spotifystreamer.components;

import com.laquysoft.spotifystreamer.common.MainThreadBus;
import com.laquysoft.spotifystreamer.modules.EventBusModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by joaobiriba on 01/08/15.
 */
@Singleton
@Component(modules = {EventBusModule.class})
public interface EventBusComponents {
    MainThreadBus provideMainThreadBus();
}
