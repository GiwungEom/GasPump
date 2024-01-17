package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.gasstation.dashboard.Dashboard
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.ui.screen.assistant.factory.TestFlow
import com.gw.study.gaspump.ui.screen.builder.GasPumpViewModelBuilder
import com.gw.study.gaspump.ui.screen.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@RunWith(MockitoJUnitRunner::class)
class GasPumpViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dashboard: Dashboard

    private lateinit var viewModelBuilder: GasPumpViewModelBuilder

    @Before
    fun setUp() {
        viewModelBuilder = GasPumpViewModelBuilder().apply {
            setDashboard(dashboard)
            setDispatcher(mainDispatcherRule.testDispatcher)
            addStub(
                GasPump::class to { whenever(dashboard.gasAmount).thenReturn(TestFlow.testFlow(data = 1)) },
                GasPrice::class to { whenever(dashboard.payment).thenReturn(TestFlow.testFlow(data = 1)) },
                Price::class to { whenever(dashboard.gasPrices).thenReturn(emptyMap()) },
                EngineLifeCycle::class to { whenever(dashboard.lifeCycle).thenReturn(TestFlow.testFlow(data = EngineLifeCycle.Create)) },
                Speed::class to { whenever(dashboard.speed).thenReturn(TestFlow.testFlow(data = Speed.Normal)) },
                PresetGauge::class to { whenever(dashboard.presetGasAmount).thenReturn(MutableStateFlow(PresetGauge.AmountInfo())) },
                Gas::class to { whenever(dashboard.gasType).thenReturn(TestFlow.testFlow(data = Gas.Gasoline)) },
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenInitialized_shouldCollectGasAmountAndPaymentAndLifeCycleAndSpeedAndPresetAndGasType() = runTest {
        viewModelBuilder.build()
        advanceUntilIdle()
        verify(dashboard).gasAmount
        verify(dashboard).payment
        verify(dashboard).lifeCycle
        verify(dashboard).speed
        verify(dashboard).presetGasAmount
        verify(dashboard).gasType
        verify(dashboard).gasPrices
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventWithGasPumpStart_shouldCallDashboardPumpStart() = runTest {
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.PumpStart)
        advanceUntilIdle()
        verify(dashboard).pumpStart()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventWithGasTypeSelect_withGasoline_shouldCallDashboardSetGasType() = runTest {
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.GasTypeSelect(Gas.Gasoline))
        advanceUntilIdle()
        verify(dashboard).setGasType(eq(Gas.Gasoline))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventWithGasPumpStop_shouldCallDashboardPumpStop() = runTest {
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.PumpStop)
        advanceUntilIdle()
        verify(dashboard).pumpStop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventWithGasPumpPause_shouldCallDashboardPumpPause() = runTest {
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.PumpPause)
        advanceUntilIdle()
        verify(dashboard).pumpPause()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventSetPreset_shouldCallDashboardSetPreset() = runTest {
        val expectedPreset = PresetGauge.AmountInfo(amount = 10, type = PresetType.Payment)
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.PresetInfoSet(expectedPreset))
        advanceUntilIdle()
        verify(dashboard).setPresetGasAmount(eq(expectedPreset.amount))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSendEventReset_shouldCallDashboardReset() = runTest {
        val viewModel = viewModelBuilder.build()
        viewModel.sendEvent(GasPumpEvent.Reset)
        advanceUntilIdle()
        verify(dashboard).reset()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenGasAmountAndPaymentChanged_sameTime_shouldUpdateGasPumpUiStateGasAmountAndPayment() = runTest {
        val expectedGasAmount = 10
        val expectedGasPayment = 11

        val viewModel = viewModelBuilder
            .addStub(
                GasPump::class to { whenever(dashboard.gasAmount).thenReturn(flow { emit(expectedGasAmount) }) },
                GasPrice::class to { whenever(dashboard.payment).thenReturn(flow { emit(expectedGasPayment) }) }
            ).build()
        var gasPumpUiState: GasPumpUiState? = null
        backgroundScope.launch {
            viewModel.uiState.collect {
                gasPumpUiState = it
            }
        }
        advanceUntilIdle()
        Assert.assertEquals(expectedGasAmount, gasPumpUiState?.gasAmount)
        Assert.assertEquals(expectedGasPayment, gasPumpUiState?.payment)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenLifeCycleChanged_shouldUpdateGasPumpUiStateLifeCycle() = runTest {
        val expected = EngineLifeCycle.Start
        val viewModel = viewModelBuilder
            .addStub(
                EngineLifeCycle::class to { whenever(dashboard.lifeCycle).thenReturn(flow { emit(expected) }) }
            ).build()

        var gasPumpUiState: GasPumpUiState? = null
        backgroundScope.launch {
            viewModel.uiState.collect {
                gasPumpUiState = it
            }
        }
        advanceUntilIdle()
        Assert.assertEquals(expected, gasPumpUiState?.lifeCycle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenSpeedChanged_shouldUpdateGasPumpUiStateSpeed() = runTest {
        val expected = Speed.Slow
        val viewModel = viewModelBuilder
            .addStub(
                Speed::class to { whenever(dashboard.speed).thenReturn(flow { emit(expected) }) }
            ).build()

        var gasPumpUiState: GasPumpUiState? = null
        backgroundScope.launch {
            viewModel.uiState.collect {
                gasPumpUiState = it
            }
        }
        advanceUntilIdle()
        Assert.assertEquals(expected, gasPumpUiState?.speed)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetGasAmountChanged_shouldUpdateGasPumpUiStatePresetGasAmount() = runTest {
        val expected = PresetGauge.AmountInfo(amount = 10)
        val viewModel = viewModelBuilder
            .addStub(
                PresetGauge::class to {
                    whenever(dashboard.presetGasAmount).thenReturn(
                        MutableStateFlow(expected)
                    )
                }
            ).build()

        var gasPumpUiState: GasPumpUiState? = null
        backgroundScope.launch {
            viewModel.uiState.collect {
                gasPumpUiState = it
            }
        }
        advanceUntilIdle()
        Assert.assertEquals(expected, gasPumpUiState?.presetInfo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenGasTypeChanged_shouldUpdateGasPumpUiStatePresetGasType() = runTest {
        val expected = Gas.Gasoline
        val viewModel = viewModelBuilder
            .addStub(
                Gas::class to { whenever(dashboard.gasType).thenReturn(flow { emit(expected) }) }
            ).build()

        var gasPumpUiState: GasPumpUiState? = null
        backgroundScope.launch {
            viewModel.uiState.collect {
                gasPumpUiState = it
            }
        }
        advanceUntilIdle()
        Assert.assertEquals(expected, gasPumpUiState?.gasType)
    }
}