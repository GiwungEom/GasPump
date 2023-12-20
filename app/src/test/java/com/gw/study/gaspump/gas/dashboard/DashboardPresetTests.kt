package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.gas.dashboard.preset.PresetFactor
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gas.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


private const val PRESET_AMOUNT = 4

@RunWith(MockitoJUnitRunner::class)
class DashboardPresetTests {

    private lateinit var dashboard: GasPumpDashboard

    @Mock
    private lateinit var gasPump: GasPump

    @Mock
    private lateinit var gasPrice: GasPrice

    @Mock
    private lateinit var engineBreadBoard: BreadBoard

    @Mock
    private lateinit var presetFactor: PresetFactor

    private val speedMutableState = MutableStateFlow(Speed.Normal)

    @Before
    fun setUp() {
        whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline))
        whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED))
        whenever(engineBreadBoard.getSpeed()).thenReturn(speedMutableState)
        dashboard = GasPumpDashboard(gasPump, gasPrice, engineBreadBoard, presetFactor).apply { setPresetPayment(PRESET_AMOUNT) }
    }

    @Test
    fun whenSetPresetPayment_shouldCallPresetPaymentValue() {
        speedMutableState.value = Speed.Normal
        Assert.assertEquals(PRESET_AMOUNT, dashboard.presetPayment.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenChangeSpeedCall_withTrue_shouldCallSendSpeedWithNormal() = runTest {
        setPresetFactorReturn(true)
        dashboard.gasAmount.launchIn(this)
        advanceUntilIdle()
        verify(engineBreadBoard).setSpeed(eq(Speed.Normal))
    }

    // when slow then not change
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenChangeSpeedCall_withFalse_shouldCallSendSpeedWithSlow() = runTest {
        setPresetFactorReturn(false)
        dashboard.gasAmount.launchIn(this)
        advanceUntilIdle()
        verify(engineBreadBoard).setSpeed(eq(Speed.Slow))
    }

    @Test
    fun whenEngineSpeedSlow_shouldNotChangePresetPayment() = runTest {
        speedMutableState.value = Speed.Slow
        val expected = 10
        dashboard.setPresetPayment(expected)
        Assert.assertNotEquals(expected, dashboard.presetPayment.value)
    }

    @Test
    fun whenReachedPresetPayment_shouldStopGasPump() = runTest {
        verify(engineBreadBoard).sendLifeCycle(eq(EngineLifeCycle.Stop))
    }

    // pause

    private fun setPresetFactorReturn(returnValue: Boolean) {
        doAnswer {
            val action = it.getArgument(2) as (Boolean) -> Unit
            action(returnValue)
        }.whenever(presetFactor).checkPresetFactor(any(), any(), any())
    }
}