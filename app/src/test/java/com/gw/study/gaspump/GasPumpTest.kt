package com.gw.study.gaspump

import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPump
import com.gw.study.gaspump.gas.PumpEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GasPumpTest {

    @Mock
    private lateinit var engine: PumpEngine

    private lateinit var gasPump: GasPump

    @Before
    fun setUp() {
        val action = flow {
            emit(Unit)
        }
        whenever(engine.invoke()).thenReturn(action)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollect_shouldReturnGasType() = runTest {
        gasPump = GasPump(Gas.Gasoline, engine = engine, fProcess = emptyFlow(), this)
        var actual: Gas? = null
        val job = launch {
            gasPump.invoke().collect {
                actual = it
            }
        }
        advanceTimeBy(10)
        job.cancel()
        Assert.assertEquals(Gas.Gasoline, actual)
    }
}