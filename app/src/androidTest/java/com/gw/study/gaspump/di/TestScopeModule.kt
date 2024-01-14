package com.gw.study.gaspump.di

import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.di.coroutine.EngineScope
import com.gw.study.gaspump.di.coroutine.ScopeModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ScopeModule::class]
)
@Module
class TestScopeModule {

    @Singleton
    @DashboardScope
    @Provides
    fun providesDashboardTestScope(
        @StandardTestDispatcher testDispatcher: TestDispatcher
    ): CoroutineScope {
        return CoroutineScope(testDispatcher)
    }

    @Singleton
    @EngineScope
    @Provides
    fun providesEngineTestScope(
        @StandardTestDispatcher testDispatcher: TestDispatcher
    ): CoroutineScope {
        return CoroutineScope(testDispatcher)
    }

}