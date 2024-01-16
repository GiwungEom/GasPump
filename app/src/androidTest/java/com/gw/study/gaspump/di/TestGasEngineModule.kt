package com.gw.study.gaspump.di

import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.gasstation.pump.engine.DecoratedEngine
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.idlingresource.CountingIdlingResource
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope

@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [GasEngineModule::class]
)
@Module
class TestGasEngineModule {

    @Provides
    fun providesEngine(
        countingIdlingResource: CountingIdlingResource,
        engineState: ReceiveEngineState,
        @DashboardScope coroutineScope: CoroutineScope
    ): Engine {
        return DecoratedEngine(
            countingIdlingResource = countingIdlingResource,
            engine = LoopEngine(
                receiveState = engineState,
                scope = coroutineScope
            ),
            receiveState = engineState,
            scope = coroutineScope
        )
    }
}