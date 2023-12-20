package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.gas.dashboard.preset.PresetFactor
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gas.state.BreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
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

    @Before
    fun setUp() {
        whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline))
        whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, GAS_PRICE_EXPECTED))
        dashboard = GasPumpDashboard(gasPump, gasPrice, engineBreadBoard, presetFactor).apply { setPresetPayment(PRESET_AMOUNT) }
    }

    @Test
    fun whenSetPresetPayment_shouldCallPresetPaymentValue() {
        val expected = 10
        dashboard.setPresetPayment(expected)
        Assert.assertEquals(expected, dashboard.presetPayment.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenChangeSpeedCall_withTrue_shouldCallSendSpeedWithNormal() = runTest {
        setPresetFactorReturn(this, true)
        dashboard.gasAmount.launchIn(this)
        advanceUntilIdle()
        verify(engineBreadBoard).sendSpeed(eq(Speed.Normal))
    }

    // when slow then not change
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenChangeSpeedCall_withFalse_shouldCallSendSpeedWithSlow() = runTest {
        setPresetFactorReturn(this, false)
        dashboard.gasAmount.launchIn(this)
        advanceUntilIdle()
        verify(engineBreadBoard).sendSpeed(eq(Speed.Slow))
    }

    private suspend fun setPresetFactorReturn(testScope: TestScope, returnValue: Boolean) {
        doAnswer {
            val action = it.getArgument(2) as suspend (Boolean) -> Unit
            testScope.launch { action(returnValue) }
        }.whenever(presetFactor).checkPresetFactor(any(), any(), any())
    }
}