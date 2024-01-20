package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.assistant.factory.TestFlow
import com.gw.study.gaspump.gasstation.dashboard.builder.DashboardBuilder
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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

    private lateinit var dashboardScope: TestScope

    @Before
    fun setUp() {
        dashboardScope = CoroutineTestScopeFactory.testScope()
        dashboardBuilder = getDashboardBuilder()
        dashboardBuilder.addStubs(
            GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline)) },
            GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED)) },
            BreadBoard::class to { whenever(engineBreadBoard.getLifeCycle()).thenReturn(MutableStateFlow(EngineLifeCycle.Create)) },
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
    fun whenInitialize_shouldCallGasPumpAndGasPriceCalcAndGasType() = runTest(dashboardScope.testScheduler) {
        dashboardBuilder.build()
        verify(gasPump).invoke()
        verify(gasPrice).calc(any())
        verify(engineBreadBoard).getGasType()
    }

    @Test
    fun whenCollectGasAmount_shouldEmitZero() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
        val expected = 0
        val actual = dashboard.gasAmount.first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun whenCollectPayment_shouldEmit() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
        val actual = dashboard.payment.first { it == GAS_PRICE_EXPECTED }
        Assert.assertEquals(GAS_PRICE_EXPECTED, actual)
    }

    @Test
    fun whenCallPumpStart_shouldCallSendLifeCycleWithStart() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
        dashboard.pumpStart()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Start))
    }

    @Test
    fun whenCallPumpStop_shouldCallSendLifeCycleWithStop() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
        dashboard.pumpStop()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Stop))
    }

    @Test
    fun whenCallPumpPause_shouldCallSendLifeCycleWithPause() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
        dashboard.pumpPause()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Paused))
    }

    @Test
    fun whenSetGasType_shouldCallGasType() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder.build()
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

    @Test
    fun whenGetGasPrices_shouldBeSizeLargerThanZero() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder
            .addStubs(
                Price::class to { whenever(gasPrice.prices).thenReturn(mutableMapOf(Gas.Gasoline to Price(Gas.Gasoline, 50))) },
            ).build()

        Assert.assertTrue(dashboard.gasPrices.isNotEmpty())
    }

    @Test
    fun whenResetCalled_shouldResetBreadboardState() = runTest(dashboardScope.testScheduler) {
        val dashboard = dashboardBuilder
            .addStubs(
                Speed::class to { whenever(engineBreadBoard.getSpeed()).thenReturn(MutableStateFlow(Speed.Normal)) }
            )
            .build()
        dashboard.reset()
        verify(engineBreadBoard).reset()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenResetCalled_shouldResetGasAmountAndPaymentAndPreset() = runTest(dashboardScope.testScheduler) {

        val dashboard = dashboardBuilder
            .addStubs(
                GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(3, Gas.Gasoline, 1)) },
                GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED)) },
                BreadBoard::class to { whenever(engineBreadBoard.getSpeed()).thenReturn(MutableStateFlow(Speed.Normal)) }
            )
            .build()

        val expectedInitialValue = 0
        val expectedGasAmount = listOf(0, 1, 2, 3, expectedInitialValue)
        val expectedPayment = listOf(0, GAS_PRICE_EXPECTED, expectedInitialValue)

        launch {
            val actual = dashboard.gasAmount
                .onStart { println("gasAmount started") }
                .onCompletion { println("gasAmount completion") }
                .take(5)
                .toList()
            Assert.assertEquals(expectedGasAmount.joinToString(), actual.joinToString())
        }

        launch {
            val actual = dashboard.payment
                .onStart { println("payment started") }
                .onCompletion { println("payment completion") }
                .take(3)
                .toList()
            Assert.assertEquals(expectedPayment.joinToString(), actual.joinToString())
        }

        launch {
            delay(3)
            dashboard.reset()

        }

        advanceUntilIdle()

        verify(presetGauge).setPreset(eq(0), eq(PresetType.Payment))
    }
}