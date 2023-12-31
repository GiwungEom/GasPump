package com.gw.study.gaspump.di.coroutine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {

    @IoDispatcher
    @Provides
    fun providesIoDispatcher() = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher() = Dispatchers.Default

    @MainDispatcher
    @Provides
    fun providesMainDispatcher() = Dispatchers.Main.immediate

    @UnconfinedDispatcher
    @Provides
    fun providesUnconfinedDispatcher() = Dispatchers.Unconfined

}