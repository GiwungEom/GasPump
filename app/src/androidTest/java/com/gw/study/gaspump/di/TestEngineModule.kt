package com.gw.study.gaspump.di

import com.gw.study.gaspump.engine.LimitEngine
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [GasEngineModule::class]
)
class TestEngineModule {

    @ViewModelScoped
    @Provides
    fun providesEngine(): Engine {
        return LimitEngine()
    }
}