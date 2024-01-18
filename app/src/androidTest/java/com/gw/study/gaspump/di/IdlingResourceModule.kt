package com.gw.study.gaspump.di

import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.idlingresource.CountingIdlingResource
import com.gw.study.gaspump.idlingresource.UriIdlingResource
import com.gw.study.gaspump.idlingresource.base.CountingBaseIdlingResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class IdlingResourceModule {

    @CountingIdleResource
    @Singleton
    @Provides
    fun providesCountingIdleResource(): CountingBaseIdlingResource = CountingIdlingResource()

    @UriIdleResource
    @Singleton
    @Provides
    fun providesUriIdleResource(
        @DashboardScope coroutineScope: CoroutineScope
    ): CountingBaseIdlingResource = UriIdlingResource(
        idleTime = 1000L,
        coroutineScope = coroutineScope
    )
}