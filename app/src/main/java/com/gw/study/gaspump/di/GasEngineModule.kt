package com.gw.study.gaspump.di

import com.gw.study.gaspump.di.coroutine.EngineScope
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope

@InstallIn(ViewModelComponent::class)
@Module
class GasEngineModule {

    @ViewModelScoped
    @Provides
    fun providesEngine(
        engineState: ReceiveEngineState,
        @EngineScope coroutineScope: CoroutineScope
    ): Engine {
        return LoopEngine(
            receiveState = engineState,
            scope = coroutineScope
        )
    }
}