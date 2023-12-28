package com.gw.study.gaspump.gasstation.pump.type

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.model.SpeedConfig
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
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

class GasEngineTests {

    private lateinit var testScope: TestScope
    private lateinit var gasEngine: GasEngine
    private lateinit var breadBoard: BreadBoard
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L)
    @Before
    fun setUp() {
        testScope = CoroutineTestScopeFactory.testScope()
        breadBoard = EngineBreadBoard()

        gasEngine = PowerGasEngine(
            Gas.Gasoline,
            LoopEngine(
                receiveState = breadBoard,
                speedConfig = speedConfig,
                scope = testScope
            ),
            breadBoard
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenInitialized_shouldEmitNothing() = runTest(testScope.testScheduler) {
        var actual = true
        backgroundScope.launch {
            gasEngine().collect {
                actual = false
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertTrue(actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStartedWithUnknownGasType_shouldEmitNothing() = runTest(testScope.testScheduler) {
        var actual = true
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        backgroundScope.launch {
            gasEngine().collect {
                actual = false
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertTrue(actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStartedWithGasolineGasType_shouldEmitGasoline() = runTest(testScope.testScheduler) {
        val expected = Gas.Gasoline
        var actual: Gas = Gas.Unknown
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        breadBoard.sendGasType(expected)
        backgroundScope.launch {
            gasEngine().collect {
                actual = it
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenEngineStop_shouldEmitNothing() = runTest(testScope.testScheduler) {
        val expected = 1
        val actual = mutableListOf<Gas>()
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        breadBoard.sendGasType(Gas.Gasoline)
        backgroundScope.launch {
            gasEngine().collect {
                actual.add(it)
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual.size)
        breadBoard.sendLifeCycle(EngineLifeCycle.Stop)
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual.size)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenChangeGasTypeWithPremium_shouldEmitNothing() = runTest(testScope.testScheduler) {
        val expected = 1
        val actual = mutableListOf<Gas>()
        breadBoard.sendLifeCycle(EngineLifeCycle.Start)
        breadBoard.sendGasType(Gas.Gasoline)
        backgroundScope.launch {
            gasEngine().collect {
                actual.add(it)
            }
        }
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual.size)
        breadBoard.sendGasType(Gas.Premium)
        advanceTimeBy(speedConfig.normal)
        Assert.assertEquals(expected, actual.size)
    }
}