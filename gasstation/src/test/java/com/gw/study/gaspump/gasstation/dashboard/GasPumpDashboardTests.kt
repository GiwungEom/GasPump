package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.assistant.factory.TestFlow
import com.gw.study.gaspump.gasstation.dashboard.builder.DashboardBuilder
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal const val GAS_PRICE_EXPECTED = 2

@RunWith(MockitoJUnitRunner::class)
class GasPumpDashboardTests {

    @Mock
    private lateinit var gasPump: GasPump

    @Mock
    private lateinit var gasPrice: GasPrice

    @Mock
    private lateinit var engineBreadBoard: BreadBoard

    private lateinit var dashboardBuilder: DashboardBuilder

    @Mock
    private lateinit var presetGauge: PresetGauge

    @Before
    fun setUp() {
        dashboardBuilder = getDashboardBuilder()
        dashboardBuilder.addStubs(
            GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline)) },
            GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED)) },
            BreadBoard::class to { whenever(engineBreadBoard.getLifeCycle()).thenReturn(MutableStateFlow(EngineLifeCycle.Create)) },
            PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Empty)) }
        )
    }

    private fun getDashboardBuilder(): DashboardBuilder =
        DashboardBuilder().apply {
            setGasPump(gasPump)
            setGasPrice(gasPrice)
            setEngineBreadBoard(engineBreadBoard)
            setPresetGauge(presetGauge)
        }

    @Test
    fun whenInitialize_shouldCallGasPumpAndGasPriceCalcAndGasType() = runTest {
        dashboardBuilder.setScope(this).build()
        verify(gasPump).invoke()
        verify(gasPrice).calc(any())
        verify(engineBreadBoard).getGasType()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollectGasAmount_shouldEmitZero() = runTest {
        var actual = 0
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.gasAmount.collect {
            actual = it
        }
        advanceUntilIdle()
        Assert.assertEquals(0, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollectPayment_shouldEmit() = runTest {
        var actual = 1
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.payment.collect {
            actual = it
        }
        advanceUntilIdle()
        Assert.assertEquals(GAS_PRICE_EXPECTED, actual)
    }

    @Test
    fun whenCallPumpStart_shouldCallSendLifeCycleWithStart() = runTest {
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.pumpStart()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Start))
    }
    @Test
    fun whenCallPumpStop_shouldCallSendLifeCycleWithStop() = runTest {
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.pumpStop()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Stop))
    }

    @Test
    fun whenCallPumpPause_shouldCallSendLifeCycleWithPause() = runTest {
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.pumpPause()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Paused))
    }

    @Test
    fun whenSetGasType_shouldCallGasType() = runTest {
        val dashboard = dashboardBuilder.setScope(this).build()
        dashboard.setGasType(Gas.Gasoline)
        verify(engineBreadBoard).sendGasType(eq(Gas.Gasoline))
    }

    @Test
    fun whenDestroyCalled_shouldBeCoroutineScopeActiveStateFalse() = runTest {
        val scope = CoroutineTestScopeFactory.testScope()
        dashboardBuilder
            .setScope(scope)
            .build()
            .destroy()
        Assert.assertFalse(scope.isActive)
    }
}