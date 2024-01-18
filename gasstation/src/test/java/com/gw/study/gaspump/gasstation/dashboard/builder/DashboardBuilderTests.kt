package com.gw.study.gaspump.gasstation.dashboard.builder

import com.gw.study.gaspump.gasstation.assistant.factory.TestFlow
import com.gw.study.gaspump.gasstation.dashboard.GasPumpDashboard
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.EngineBreadBoard
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class DashboardBuilderTests {

    @Mock
    private lateinit var gasPump: GasPump
    @Mock
    private lateinit var gasPrice: GasPrice
    @Mock
    private lateinit var engineBreadBoard: EngineBreadBoard
    @Mock
    private lateinit var presetGauge: PresetGauge

    private lateinit var dashboardScope: TestScope

    @Before
    fun setUp() {
        dashboardScope = CoroutineTestScopeFactory.testScope()
    }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun whenBuildCalled_withoutDependencies_shouldThrowNotImplementedException() {
        DashboardBuilder().build()
    }

    @Test
    fun whenBuildCalled_shouldGetDashboardObject() {
        val dashboard = getPresetBuilder()
            .addStubs(
                GasPump::class to { whenever(gasPump.invoke()).thenReturn(emptyFlow()) },
                GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(emptyFlow()) },
            ).build()
        Assert.assertNotNull(dashboard)
    }

    @Test
    fun whenAddStubGasPriceWithData_shouldGetSameResult() = runTest(dashboardScope.testScheduler) {
        val gasPriceInitialData = 0
        val gasPriceData = 5

        val expected = listOf(gasPriceInitialData, gasPriceData)
        val builder = getPresetBuilder().setScope(dashboardScope)
        val dashboard: GasPumpDashboard = builder.addStubs(
            GasPump::class to { whenever(gasPump.invoke()).thenReturn(TestFlow.testFlow(1, Gas.Gasoline)) },
            GasPrice::class to { whenever(gasPrice.calc(any())).thenReturn(TestFlow.testFlow(1, gasPriceData)) },
            PresetGauge::class to { whenever(presetGauge.getGauge(any(), any())).thenReturn(TestFlow.testFlow(1, Gauge.Empty)) },
        ).build()
        Assert.assertEquals(expected.joinToString(), dashboard.payment.take(2).toList().joinToString())
        dashboardScope.cancel()
    }

    private fun getPresetBuilder(): DashboardBuilder =
        DashboardBuilder().apply {
            setGasPump(gasPump)
            setGasPrice(gasPrice)
            setEngineBreadBoard(engineBreadBoard)
            setPresetGauge(presetGauge)
            setScope(CoroutineTestScopeFactory.testScope())
        }
}
