package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.gas.engine.Engine
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.model.PumpLifeCycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GasPumpTest {

    @Mock
    private lateinit var engine: Engine

    private lateinit var gasPump: GasPump

    @Before
    fun setUp() {
        val action = flow {
            emit(Unit)
        }
        whenever(engine.invoke()).thenReturn(action)
        gasPump = GasPump(Gas.Gasoline, engine = engine, fPumpLifeCycle = MutableStateFlow(Gas.Gasoline to PumpLifeCycle.Create))
    }

    @Test
    fun whenInitialize_shouldInitLifeCycleAndSpeedState() {
        verify(engine).lifeCycleState = any()
        verify(engine).speedState = any()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollect_shouldReturnGasType() = runTest {
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