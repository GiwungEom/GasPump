package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.assistant.factory.TestFlow
import com.gw.study.gaspump.gasstation.dashboard.builder.DashboardBuilder
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


private const val PRESET_GAS_AMOUNT = 4

@RunWith(MockitoJUnitRunner::class)
class GasPumpDashboardPresetTests {

    private lateinit var dashboardBuilder: DashboardBuilder

    @Mock
    private lateinit var gasPump: GasPump

    @Mock
    private lateinit var gasPrice: GasPrice

    @Mock
    private lateinit var engineBreadBoard: BreadBoard

    @Mock
    private lateinit var presetGauge: PresetGauge

    private lateinit var dashboardScope: TestScope

    @Before
    fun setUp() {
        dashboardScope = CoroutineTestScopeFactory.testScope()
        dashboardBuilder = getDashboardBuilder()
        dashboardBuilder.addStubs(
            GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline)) },
            GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED)) },
            BreadBoard::class to { whenever(engineBreadBoard.getSpeed()).thenReturn(MutableStateFlow(Speed.Normal)) },
            PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Empty)) }
        ).setScope(dashboardScope)
    }

    private fun getDashboardBuilder(): DashboardBuilder =
        DashboardBuilder().apply {
            setGasPump(gasPump)
            setGasPrice(gasPrice)
            setEngineBreadBoard(engineBreadBoard)
            setPresetGauge(presetGauge)
        }

    @Test
    fun whenSetPresetAmount_shouldCallPresetGaugeSetPreset() = runTest {
        dashboardBuilder
            .build()
            .setPresetGasAmount(PRESET_GAS_AMOUNT)
        verify(presetGauge).setPreset(eq(PRESET_GAS_AMOUNT), any())
    }

    @Test
    fun whenPresetGaugeStateIsEmpty_shouldNotCallSendSpeed() = runTest {
        dashboardBuilder.build()
        verify(engineBreadBoard, never()).setSpeed(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetGaugeStateIsSpare_shouldCallSendSpeedWithNormal() = runTest(dashboardScope.testScheduler) {
        dashboardBuilder
            .addStubs(
                PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Spare)) }
            ).build()
        advanceUntilIdle()
        verify(engineBreadBoard).setSpeed(eq(Speed.Normal))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetGaugeStateIsAlmost_shouldCallSendSpeedWithSlow() = runTest(dashboardScope.testScheduler) {
        dashboardBuilder
            .addStubs(
                PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Almost)) }
            ).build()
        advanceUntilIdle()
        verify(engineBreadBoard).setSpeed(eq(Speed.Slow))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetGaugeStateIsFull_shouldCallSendLifeCycleWithStop() = runTest(dashboardScope.testScheduler) {
        dashboardBuilder
            .addStubs(
                PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Full)) }
            ).build()
        advanceUntilIdle()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Stop))
    }

    @Test
    fun whenEngineSpeedSlow_shouldNotChangePresetAmount() = runTest {
        val expected = 10
        dashboardBuilder
            .addStubs(
                PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Almost)) },
                BreadBoard::class to { whenever(engineBreadBoard.getSpeed()).thenReturn(MutableStateFlow(Speed.Slow)) }
            ).build()
            .setPresetGasAmount(expected)
        verify(presetGauge, never()).setPreset(any(), any())
    }
}