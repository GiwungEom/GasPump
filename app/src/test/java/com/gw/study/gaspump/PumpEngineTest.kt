package com.gw.study.gaspump

import com.gw.study.gaspump.exeption.ReachedLineException
import com.gw.study.gaspump.gas.PumpEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
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

class PumpEngineTest {

    private lateinit var engine: PumpEngine

    @Before
    fun setUp() {
        engine = PumpEngine(
            speedConfig = PumpEngine.SpeedConfig(1L, 10L)
        )
    }

    @Test
    fun whenInitEngine_shouldBeCreateState() {
        val engine = PumpEngine()
        Assert.assertEquals(PumpEngine.LifeCycle.Create, engine.lifecycle.value)
    }

    @Test
    fun whenEngineStart_shouldBeStartState() {
        val engine = PumpEngine()
        engine.start()
        Assert.assertEquals(PumpEngine.LifeCycle.Start, engine.lifecycle.value)
    }

    @Test
    fun whenEnginePause_shouldBePausedState() {
        val engine = PumpEngine()
        engine.start()
        engine.pause()
        Assert.assertEquals(PumpEngine.LifeCycle.Paused, engine.lifecycle.value)
    }

    @Test
    fun whenEngineDestroy_shouldBeDestroyState() {
        val engine = PumpEngine()
        engine.start()
        engine.stop()
        Assert.assertEquals(PumpEngine.LifeCycle.Destroy, engine.lifecycle.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = ReachedLineException::class)
    fun whenEngineStart_shouldFlowFuel() = runTest(UnconfinedTestDispatcher()) {
        val engine = PumpEngine()
        engine.start()
        engine.invoke().collect {
            throw ReachedLineException()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStop_shouldFlowStop() = runTest(UnconfinedTestDispatcher()) {
        var actual = false
        val engine = PumpEngine()
        engine.start()

        val job = engine.invoke()
            .onCompletion { actual = true }
            .launchIn(this)

        job.cancelAndJoin()
        Assert.assertTrue(actual)
    }

    @Test
    fun whenEngineSpeedNormal_shouldFlowNormalSpeed() = runTest {
        val speedConfig = PumpEngine.SpeedConfig(1L, 10L)
        var actual = 0L
        val virtualTime = 100L
        val expected = 100L / speedConfig.normal
        startEngine(
            speedConfig = speedConfig,
            virtualTime = virtualTime,
            speedType = PumpEngine.Speed.Normal
        ) { actual++ }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun whenEngineSpeedSlow_shouldFlowSlowSpeed() = runTest {
        val speedConfig = PumpEngine.SpeedConfig(1L, 10L)
        var actual = 0L
        val virtualTime = 100L
        val expected = 100L / speedConfig.slow
        startEngine(
            speedConfig = speedConfig,
            virtualTime = virtualTime,
            speedType = PumpEngine.Speed.Slow
        ) { actual++ }
        Assert.assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startEngine(
        speedConfig: PumpEngine.SpeedConfig = PumpEngine.SpeedConfig(1L, 10L),
        speedType: PumpEngine.Speed,
        virtualTime: Long,
        action: () -> Unit
    ) {
        val engine = PumpEngine(speedConfig = speedConfig).apply { speed = speedType }
        engine.start()
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
        val engine = PumpEngine(speedConfig = PumpEngine.SpeedConfig(1L, 10L))
        engine.start()
        var count = 0
        val job = launch {
            engine().collect {
                count++
            }
        }
        advanceTimeBy(10L)
        engine.pause()
        advanceTimeBy(10L)
        job.cancelAndJoin()
        Assert.assertEquals(10, count)
    }
}