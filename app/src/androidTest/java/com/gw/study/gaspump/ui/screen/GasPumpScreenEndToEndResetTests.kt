package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.di.GasEngineModule
import com.gw.study.gaspump.di.UriIdleResource
import com.gw.study.gaspump.di.coroutine.EngineScope
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.DecoratedEngine
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.idlingresource.base.CountingBaseIdlingResource
import com.gw.study.gaspump.robot.startGasPumpAndReset
import com.gw.study.gaspump.ui.screen.base.BaseHiltTest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CoroutineScope
import org.junit.Test
import javax.inject.Inject

@UninstallModules(GasEngineModule::class)
@HiltAndroidTest
class GasPumpScreenEndToEndResetTests : BaseHiltTest() {


    @UriIdleResource
    @Inject
    lateinit var uriIdlingResource: CountingBaseIdlingResource

    @InstallIn(ViewModelComponent::class)
    @Module
    class TestGasEngineModule {

        @Provides
        fun providesEngine(
            @UriIdleResource idlingResource: CountingBaseIdlingResource,
            engineState: ReceiveEngineState,
            @EngineScope coroutineScope: CoroutineScope
        ): Engine {
            return DecoratedEngine(
                idlingResource = idlingResource,
                engine = LoopEngine(
                    receiveState = engineState,
                    scope = coroutineScope
                ),
                receiveState = engineState,
                scope = coroutineScope
            )
        }
    }

    @Test
    fun whenResetButtonClicked_shouldShowInitialValue() {
        startGasPumpAndReset {
            inputPresetPayment("3000")
            selectGasType(Gas.Gasoline)
            start()

            reset()
            checkInitialGasAmount()
            checkInitialPreset()
            checkInitialPayment()
        }
    }
}