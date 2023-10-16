package com.gw.study.gaspump

import com.gw.study.gaspump.gas.PumpEngine
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GasPumpEngineTest {

    private lateinit var speedConfig: PumpEngine.SpeedConfig
    private val delayTime = 1000L
    @Before
    fun setUp() {
        speedConfig = PumpEngine.SpeedConfig(50, 100)
    }

    @Suppress("FunctionName")
    private fun ExpectedResult(
        delay: Long = delayTime,
        speed: PumpEngine.Speed = PumpEngine.Speed.Normal
    ): Int {
        return (delay / if (speed == PumpEngine.Speed.Normal) speedConfig.normal else speedConfig.slow).toInt()
    }

    @Test
    fun expectedResultTest() {
        assertEquals(20, ExpectedResult(delay = 1000, speed = PumpEngine.Speed.Normal))
        assertEquals(10, ExpectedResult(delay = 1000, speed = PumpEngine.Speed.Slow))
    }

    @Test
    fun gasPumpEngineTest() = runTest {
        val pumpEngine = PumpEngine(cScope = this)
        var frameCount = 0

        launch {
            pumpEngine()
                .collect {
                    frameCount++
                }
        }
        pumpEngine.start()
        delay(delayTime)
        pumpEngine.destroy()
        assertEquals(ExpectedResult(), frameCount)
    }

    @Test
    fun gasPumpEngineSpeedSlowTest() = runTest {
        val pumpEngine = PumpEngine(
            cScope = this
        )
        pumpEngine.speed = PumpEngine.Speed.Slow
        pumpEngine.start()

        var count = 0
        launch {
            pumpEngine().collect {
                count++
            }
        }

        launch {
            delay(delayTime)
            assertEquals(ExpectedResult(speed = PumpEngine.Speed.Slow), count)
        }.join()

        pumpEngine.destroy()
    }

    @Test
    fun gasPumpEngineCancelTest() = runTest {
        val pumpEngine = PumpEngine(cScope = this)
        var count = 0
        val delayTime = 200L
        launch {
            pumpEngine().collect {
                count++
            }
        }
        pumpEngine.start()
        launch {
            delay(delayTime)
            pumpEngine.destroy()
            delay(1000)
        }.join()
        assertEquals(ExpectedResult(delay = delayTime), count)
    }

    @Test
    fun gasPumpEngineLifeCycleTest() = runTest {
        val pumpEngine = PumpEngine(cScope = this)
        var count = 0
        launch {
            pumpEngine().collect {
                count++
            }
        }
        pumpEngine.start()
        delay(delayTime)
        assertEquals(ExpectedResult(), count)
        assertEquals(PumpEngine.LifeCycle.Start, pumpEngine.lifecycle.value)

        pumpEngine.pause()
        delay(delayTime)

        assertEquals(ExpectedResult(), count)
        pumpEngine.destroy()
    }
}