package com.gw.study.gaspump.gasstation.gas.pump.engine

import com.gw.study.gaspump.gasstation.exeption.ReachedLineException
import com.gw.study.gaspump.gasstation.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.gas.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gasstation.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.gas.pump.engine.model.SpeedConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class LoopEngineTests {

    private lateinit var engine: LoopEngine
    private val engineLifeCycleState: MutableStateFlow<EngineLifeCycle> = MutableStateFlow(EngineLifeCycle.Create)
    private val speedState: MutableStateFlow<Speed> = MutableStateFlow(Speed.Normal)

    @Mock
    private lateinit var receiveEngineState: ReceiveEngineState

    @Before
    fun setUp() {
        whenever(receiveEngineState.getLifeCycle()).thenReturn(engineLifeCycleState)
        whenever(receiveEngineState.getSpeed()).thenReturn(speedState)

        engine = LoopEngine(
            speedConfig = SpeedConfig(1L, 10L),
            receiveEngineState
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = ReachedLineException::class)
    fun whenEngineStart_shouldFlowFuel() = runTest(UnconfinedTestDispatcher()) {
        engineLifeCycleState.value = EngineLifeCycle.Start
        engine.invoke().collect {
            throw ReachedLineException()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStop_shouldFlowStop() = runTest(UnconfinedTestDispatcher()) {
        var actual = false
        engineLifeCycleState.value = EngineLifeCycle.Start

        val job = engine.invoke()
            .onCompletion { actual = true }
            .launchIn(this)

        job.cancelAndJoin()
        Assert.assertTrue(actual)
    }

    @Test
    fun whenEngineSpeedNormal_shouldFlowNormalSpeed() = runTest {
        val speedConfig = SpeedConfig(1L, 10L)
        var actual = 0L
        val virtualTime = 100L
        val expected = 100L / speedConfig.normal
        speedState.value = Speed.Normal
        startEngine(
            speedConfig = speedConfig,
            virtualTime = virtualTime
        ) { actual++ }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun whenEngineSpeedSlow_shouldFlowSlowSpeed() = runTest {
        val speedConfig = SpeedConfig(1L, 10L)
        var actual = 0L
        val virtualTime = 100L
        val expected = 100L / speedConfig.slow
        speedState.value = Speed.Slow
        startEngine(
            speedConfig = speedConfig,
            virtualTime = virtualTime
        ) { actual++ }

        Assert.assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startEngine(
        speedConfig: SpeedConfig = SpeedConfig(1L, 10L),
        virtualTime: Long,
        action: () -> Unit
    ) {
        val engine = LoopEngine(
            speedConfig = speedConfig,
            receiveEngineState
        )

        engineLifeCycleState.value = EngineLifeCycle.Start
        val job = launch {
            engine.invoke().collect {
                action()
            }
        }

        advanceTimeBy(virtualTime)
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEnginePaused_shouldFlowPause() = runTest {
        engineLifeCycleState.value = EngineLifeCycle.Start
        var count = 0
        val job = launch {
            engine().collect {
                count++
            }
        }
        advanceTimeBy(10L)
        engineLifeCycleState.value = EngineLifeCycle.Paused
        advanceTimeBy(10L)
        job.cancelAndJoin()
        Assert.assertEquals(10, count)
    }
}