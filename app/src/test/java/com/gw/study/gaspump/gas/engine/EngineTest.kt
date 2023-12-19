package com.gw.study.gaspump.gas.engine

import com.gw.study.gaspump.exeption.ReachedLineException
import com.gw.study.gaspump.gas.engine.model.EngineLifeCycle
import com.gw.study.gaspump.gas.engine.model.Speed
import com.gw.study.gaspump.gas.engine.model.SpeedConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
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

class EngineTest {

    private lateinit var engine: Engine
    private val engineLifeCycleState: MutableStateFlow<EngineLifeCycle> = MutableStateFlow(EngineLifeCycle.Create)
    private val speedState: MutableStateFlow<Speed> = MutableStateFlow(Speed.Normal)

    @Before
    fun setUp() {
        engine = Engine(
            speedConfig = SpeedConfig(1L, 10L)
        ).apply {
            lifeCycleState = this@EngineTest.engineLifeCycleState
            speedState = this@EngineTest.speedState
        }
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
            virtualTime = virtualTime,
            speedState = speedState
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
            virtualTime = virtualTime,
            speedState = speedState
        ) { actual++ }

        Assert.assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startEngine(
        speedConfig: SpeedConfig = SpeedConfig(1L, 10L),
        speedState: Flow<Speed>,
        virtualTime: Long,
        action: () -> Unit
    ) {
        val engine = Engine(
            speedConfig = speedConfig
        ).apply {
            this.speedState = speedState
            this.lifeCycleState = this@EngineTest.engineLifeCycleState
        }

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