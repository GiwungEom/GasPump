package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.type.GasEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GasPumpTests {

    @Mock
    private lateinit var gasolineEngine: GasEngine

    @Mock
    private lateinit var dieselEngine: GasEngine

    @Mock
    private lateinit var premiumEngine: GasEngine

    private lateinit var gasPump: GasPump

    @Before
    fun setUp() {
        whenever(gasolineEngine.gas).thenReturn(Gas.Gasoline)
        whenever(dieselEngine.gas).thenReturn(Gas.Diesel)
        whenever(premiumEngine.gas).thenReturn(Gas.Premium)

        gasPump = GasPump(gasolineEngine, dieselEngine, premiumEngine)
    }

    @Test
    fun whenInitialize_shouldInvokeEngine() {
        verify(gasolineEngine).invoke()
        verify(dieselEngine).invoke()
        verify(premiumEngine).invoke()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenGasEngineEmit_inThreeEngines_shouldEmitGas() = runTest(UnconfinedTestDispatcher()) {
        val expected = Gas.Diesel
        whenever(dieselEngine.invoke()).thenReturn(TestFlow.testFlow(1, expected))
        whenever(gasolineEngine.invoke()).thenReturn(emptyFlow())
        whenever(premiumEngine.invoke()).thenReturn(emptyFlow())
        val gasPump = GasPump(gasolineEngine, dieselEngine, premiumEngine)
        var gasType: Gas = Gas.Unknown
        gasPump().collect {
            gasType = it
        }
        Assert.assertEquals(expected, gasType)
    }
}