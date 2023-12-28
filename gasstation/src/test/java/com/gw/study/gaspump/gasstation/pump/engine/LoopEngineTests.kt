package com.gw.study.gaspump.gasstation.pump.engine

import com.gw.study.gaspump.gasstation.exeption.LineReachedException
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.model.SpeedConfig
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
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

    private lateinit var testScope: TestScope
    private lateinit var engine: LoopEngine
    private val engineLifeCycleState: MutableStateFlow<EngineLifeCycle> = MutableStateFlow(EngineLifeCycle.Create)
    private val speedState: MutableStateFlow<Speed> = MutableStateFlow(Speed.Normal)

    @Mock
    private lateinit var receiveEngineState: ReceiveEngineState

    @Before
    fun setUp() {
        testScope = CoroutineTestScopeFactory.testScope()
        whenever(receiveEngineState.getLifeCycle()).thenReturn(engineLifeCycleState)
        whenever(receiveEngineState.getSpeed()).thenReturn(speedState)

        engine = LoopEngine(
            speedConfig = SpeedConfig(1L, 10L),
            receiveEngineState,
            testScope
        )
    }

    @Test(expected = LineReachedException::class)
    fun whenEngineStart_shouldFlowFuel() = runTest(testScope.testScheduler) {
        engineLifeCycleState.value = EngineLifeCycle.Start
        engine.invoke().collect {
            throw LineReachedException()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStop_shouldFlowStop() = runTest(testScope.testScheduler) {
        val expected = 10
        var actual = 0
        engineLifeCycleState.value = EngineLifeCycle.Start

        val job = engine.invoke()
            .onEach { actual++ }
            .launchIn(this)

        launch {
            delay(10L)
            engineLifeCycleState.value = EngineLifeCycle.Stop
            delay(10L)
            job.cancelAndJoin()
        }
        advanceTimeBy(20L)
        runCurrent()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun whenEngineSpeedNormal_shouldFlowNormalSpeed() = runTest(testScope.testScheduler) {
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
    fun whenEngineSpeedSlow_shouldFlowSlowSpeed() = runTest(testScope.testScheduler) {
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
            receiveEngineState,
            scope = testScope
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
    fun whenEnginePaused_shouldFlowPause() = runTest(testScope.testScheduler) {
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