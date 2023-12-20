package com.gw.study.gaspump.gas.pump.engine.type

import com.gw.study.gaspump.assistant.factory.TestFlow
import com.gw.study.gaspump.exeption.ReachedLineException
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.LoopEngine
import com.gw.study.gaspump.gas.state.EngineBreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PowerGasEngineTests {

    @Mock
    private lateinit var receiveState: EngineBreadBoard
    @Mock
    private lateinit var engine: LoopEngine

    private lateinit var gasEngine: PowerGasEngine

    @Before
    fun setUp() {
        whenever(engine.invoke()).thenReturn(TestFlow.testFlow(1, Unit))
        whenever(receiveState.getGasType()).thenReturn(MutableStateFlow(Gas.Gasoline))
        gasEngine = PowerGasEngine(Gas.Gasoline, engine, receiveState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollect_withSameGasType_shouldEmitGas() = runTest(UnconfinedTestDispatcher()) {
        var reached = false
        gasEngine().collect {
            reached = true
        }
        Assert.assertTrue(reached)
    }

    @Test
    fun whenCollect_withDifferentGasType_shouldNotEmitGas() = runTest {
        whenever(receiveState.getGasType()).thenReturn(MutableStateFlow(Gas.Diesel))
        gasEngine().collect {
            throw ReachedLineException()
        }
    }
}