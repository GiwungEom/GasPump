package com.gw.study.gaspump.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TestDispatcherModule {

    @Singleton
    @Provides
    fun providesTestCoroutineScheduler(): TestCoroutineScheduler {
        return TestCoroutineScheduler()
    }

    @Singleton
    @StandardTestDispatcher
    @Provides
    fun providesStandardTestDispatcher(
        testCoroutineScheduler: TestCoroutineScheduler
    ): TestDispatcher {
        return StandardTestDispatcher(testCoroutineScheduler)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Singleton
    @UnconfinedTestDispatcher
    @Provides
    fun providesUnconfinedTestDispatcher(
        testCoroutineScheduler: TestCoroutineScheduler
    ): TestDispatcher {
        return UnconfinedTestDispatcher(testCoroutineScheduler)
    }

}