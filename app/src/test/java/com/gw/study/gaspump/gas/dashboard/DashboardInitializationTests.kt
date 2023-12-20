package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gas.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DashboardInitializationTests {

    private lateinit var dashboard: GasPumpDashboard

    @Mock
    private lateinit var gasPump: GasPump

    @Mock
    private lateinit var gasPrice: GasPrice

    @Mock
    private lateinit var engineBreadBoard: BreadBoard

    @Before
    fun setUp() {
        whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline))
        whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED))
        dashboard = GasPumpDashboard(gasPump, gasPrice, engineBreadBoard)
    }

    @Test
    fun whenInitialize_shouldCallGasPumpAndGasPriceCalcAndGasType() {
        verify(gasPump).invoke()
        verify(gasPrice).calc(any())
        verify(engineBreadBoard).getGasType()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollectGasAmount_shouldEmitOne() = runTest {
        var actual = 1
        dashboard.gasAmount.collect {
            actual = it
        }
        advanceUntilIdle()
        Assert.assertEquals(1, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollectPayment_shouldEmit() = runTest {
        var actual = 1
        dashboard.payment.collect {
            actual = it
        }
        advanceUntilIdle()
        Assert.assertEquals(GAS_PRICE_EXPECTED, actual)
    }

    @Test
    fun whenSetGasType_shouldCallGasType() = runTest {
        dashboard.setGasType(Gas.Gasoline)
        verify(engineBreadBoard).sendGasType(eq(Gas.Gasoline))
    }

    @Test
    fun whenCallPumpStart_shouldCallSendLifeCycleWithStart() = runTest {
        dashboard.pumpStart()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Start))
    }
    @Test
    fun whenCallPumpStop_shouldCallSendLifeCycleWithStop() = runTest {
        dashboard.pumpStop()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Stop))
    }

    @Test
    fun whenCallPumpPause_shouldCallSendLifeCycleWithPause() = runTest {
        dashboard.pumpPause()
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Paused))
    }
}