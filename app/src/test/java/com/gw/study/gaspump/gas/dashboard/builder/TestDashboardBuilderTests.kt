package com.gw.study.gaspump.gas.dashboard.builder

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.gas.dashboard.GasPumpDashboard
import com.gw.study.gaspump.gas.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gas.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.state.EngineBreadBoard
import com.gw.study.gaspump.scope.CoroutineTestScopeFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class TestDashboardBuilderTests {

    @Mock
    private lateinit var gasPump: GasPump
    @Mock
    private lateinit var gasPrice: GasPrice
    @Mock
    private lateinit var engineBreadBoard: EngineBreadBoard
    @Mock
    private lateinit var presetGauge: PresetGauge

    @Test(expected = UninitializedPropertyAccessException::class)
    fun whenBuildCalled_withoutDependencies_shouldThrowNotImplementedException() {
        TestDashboardBuilder().build()
    }

    @Test
    fun whenBuildCalled_shouldGetDashboardObject() {
        val dashboard = getPresetBuilder().build()
        Assert.assertNotNull(dashboard)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenAddStubGasPriceWithData_shouldGetSameResult() = runTest(UnconfinedTestDispatcher()) {
        val expected = 5
        val builder = getPresetBuilder().setScope(this)
        val dashboard: GasPumpDashboard = builder.addStubs(
            GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline)) },
            GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, expected)) },
            PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Empty)) },
        ).build()
        Assert.assertEquals(expected, dashboard.payment.first())
    }

    private fun getPresetBuilder(): TestDashboardBuilder =
        TestDashboardBuilder().apply {
            setGasPump(gasPump)
            setGasPrice(gasPrice)
            setEngineBreadBoard(engineBreadBoard)
            setPresetGauge(presetGauge)
            setScope(CoroutineTestScopeFactory.testScope())
        }
}
