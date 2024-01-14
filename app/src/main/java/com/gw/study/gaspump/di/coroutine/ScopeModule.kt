package com.gw.study.gaspump.di.coroutine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class ScopeModule {

    @DashboardScope
    @Provides
    fun providesDashboardCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(CoroutineName("dashboard") + SupervisorJob() + dispatcher)
    }

    @EngineScope
    @Provides
    fun providesEngineCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(CoroutineName("engine") + SupervisorJob() + dispatcher)
    }
}