package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.R
import com.gw.study.gaspump.di.CountingIdleResource
import com.gw.study.gaspump.di.GasEngineModule
import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.DecoratedEngine
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.idlingresource.base.CountingBaseIdlingResource
import com.gw.study.gaspump.robot.startGasPump
import com.gw.study.gaspump.robot.startGasPumpWithPreset
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
class GasPumpScreenEndToEndTests : BaseHiltTest() {

    @InstallIn(ViewModelComponent::class)
    @Module
    class TestGasEngineModule {
        @Provides
        fun providesEngine(
            @CountingIdleResource idlingResource: CountingBaseIdlingResource,
            engineState: ReceiveEngineState,
            @DashboardScope coroutineScope: CoroutineScope
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

    @CountingIdleResource
    @Inject
    lateinit var countingIdlingResource: CountingBaseIdlingResource

    @Test
    fun whenStartButtonClicked_shouldNotShowInitialGasAmountAndPayment() {
        startGasPump {
            selectGasType(Gas.Gasoline)
            start()
            checkGasAmountChanged()
            checkPaymentChanged()
        }
    }

    @Test
    fun whenStartButtonClicked_withPresetAmount_shouldStopWhenPaymentIsLargerThanPresetAmount() {
        startGasPumpWithPreset {
            inputPresetPayment("3000")
            selectGasType(Gas.Gasoline)
            start()

            checkButton(R.string.start)
            checkPayment("3000")
            checkLifeCycle(EngineLifeCycle.Stop)
        }
    }
}