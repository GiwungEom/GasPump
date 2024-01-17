package com.gw.study.gaspump.ui.screen.integration

import com.gw.study.gaspump.gasstation.dashboard.GasPumpDashboard
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.CumulateGasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.OnePassageGasPump
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.type.PowerGasEngine
import com.gw.study.gaspump.gasstation.state.EngineBreadBoard
import com.gw.study.gaspump.ui.screen.GasPumpEvent
import com.gw.study.gaspump.ui.screen.GasPumpUiState
import com.gw.study.gaspump.ui.screen.GasPumpViewModel
import com.gw.study.gaspump.ui.screen.builder.GasPumpViewModelBuilder
import com.gw.study.gaspump.ui.screen.rule.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GasPumpViewModelIntegrationTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: GasPumpViewModel
    private lateinit var dashboardScope: CoroutineScope
    @Before
    fun setUp() {
        dashboardScope = CoroutineScope(mainDispatcherRule.testDispatcher)

        val engineBreadBoard = EngineBreadBoard()
        val engine = LoopEngine(
            receiveState = engineBreadBoard,
            scope = dashboardScope
        )
        val gasPump = OnePassageGasPump(
            PowerGasEngine(gas = Gas.Gasoline, engine = engine, receiveState = engineBreadBoard),
            PowerGasEngine(gas = Gas.Premium, engine = engine, receiveState = engineBreadBoard),
            PowerGasEngine(gas = Gas.Diesel, engine = engine, receiveState = engineBreadBoard)
        )
        val gasPrice = getGasPrice()
        val presetGauge = PresetGauge()

        val dashboard = GasPumpDashboard(
            gasPump = gasPump,
            gasPrice = gasPrice,
            engineBreadBoard = engineBreadBoard,
            presetGauge = presetGauge,
            scope = dashboardScope
        )

        viewModel = GasPumpViewModelBuilder().apply {
            setDashboard(dashboard)
            setDispatcher(mainDispatcherRule.testDispatcher)
        }.build()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenTestFinished_shouldScopeFinish() = runTest {
        advanceTimeBy(50L)
        dashboardScope.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPumpStart_withGasolineGasType_shouldGasAmountAndPaymentAndLifeCycleAndGasTypeUiStateChanged() = runTest {
        lateinit var actual: GasPumpUiState
        val gasAmountExpected = 1
        val paymentExpected = 100
        val lifecycleExpected = EngineLifeCycle.Start
        val gasType = Gas.Gasoline
        val gasPrice = getGasPrice()
        val expected = GasPumpUiState(
            gasAmount = gasAmountExpected,
            payment = paymentExpected,
            lifeCycle = lifecycleExpected,
            gasType = gasType,
            gasPrices = gasPrice.prices
        )

        viewModel.sendEvent(GasPumpEvent.GasTypeSelect(expected.gasType))
        viewModel.sendEvent(GasPumpEvent.PumpStart)

        launch {
            viewModel.uiState
                .take(3)
                .onCompletion {
                    dashboardScope.cancel()
                }.collect {
                    actual = it
                }
        }
        advanceUntilIdle()
        Assert.assertEquals(expected, actual)
    }

    private fun getGasPrice() =
         CumulateGasPrice().apply {
            addPrice(Price(Gas.Gasoline, 100))
            addPrice(Price(Gas.Diesel, 50))
            addPrice(Price(Gas.Premium, 200))
        }
}