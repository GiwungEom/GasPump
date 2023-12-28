package com.gw.study.gaspump.gasstation.pump

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.model.SpeedConfig
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.type.PowerGasEngine
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.BreadBoard
import com.gw.study.gaspump.gasstation.state.EngineBreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PumpTests {

    private lateinit var testScope: TestScope
    private lateinit var breadBoard: BreadBoard
    private lateinit var gasPump: GasPump
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L)

    @Before
    fun setUp() {
        testScope = CoroutineTestScopeFactory.testScope()
        breadBoard = EngineBreadBoard()
        val loopEngine = LoopEngine(
            receiveState = breadBoard,
            speedConfig = speedConfig,
            scope = testScope
        )
        gasPump = OnePassageGasPump(
            PowerGasEngine(
                Gas.Gasoline,
                loopEngine,
                breadBoard
            ),
            PowerGasEngine(
                Gas.Diesel,
                loopEngine,
                breadBoard
            ),
            PowerGasEngine(
                Gas.Premium,
                loopEngine,
                breadBoard
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenUnknownGasType_shouldNotEmit() = runTest(testScope.testScheduler) {
        var actual = true
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        backgroundScope.launch {
            gasPump().collect {
                actual = false
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertTrue(actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenGasTypeDiesel_shouldEmitDiesel() = runTest(testScope.testScheduler) {
        val expected = Gas.Diesel
        var actual: Gas = Gas.Unknown
        breadBoard.sendGasType(Gas.Diesel)
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        backgroundScope.launch {
            gasPump().collect {
                actual = it
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual)
        // 에러를 고치는 unit test를 작성해보자
    }
}