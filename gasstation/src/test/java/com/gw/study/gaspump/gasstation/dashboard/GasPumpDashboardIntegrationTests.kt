package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.CumulateGasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.OnePassageGasPump
import com.gw.study.gaspump.gasstation.pump.engine.LoopEngine
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.type.PowerGasEngine
import com.gw.study.gaspump.gasstation.scope.CoroutineTestScopeFactory
import com.gw.study.gaspump.gasstation.state.EngineBreadBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GasPumpDashboardIntegrationTests {

    private lateinit var dashboard: GasPumpDashboard
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = CoroutineTestScopeFactory.testScope(testDispatcher)

    @Before
    fun setUp() {

        val engineBreadBoard = EngineBreadBoard()
        val engine = LoopEngine(
            receiveState = engineBreadBoard,
            scope = testScope
        )
        val gasPump = OnePassageGasPump(
            PowerGasEngine(gas = Gas.Gasoline, engine = engine, receiveState = engineBreadBoard),
            PowerGasEngine(gas = Gas.Premium, engine = engine, receiveState = engineBreadBoard),
            PowerGasEngine(gas = Gas.Diesel, engine = engine, receiveState = engineBreadBoard)
        )
        val gasPrice = CumulateGasPrice().apply {
            addPrice(Price(Gas.Gasoline, 100))
            addPrice(Price(Gas.Diesel, 50))
            addPrice(Price(Gas.Premium, 200))
        }

        val presetGauge = PresetGauge()

        dashboard = GasPumpDashboard(
            gasPump = gasPump,
            gasPrice = gasPrice,
            engineBreadBoard = engineBreadBoard,
            presetGauge = presetGauge,
            scope = testScope
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenStopCalled_withPreset_shouldStopAndBeSamePaymentWithPreset() = runTest(testScope.testScheduler) {
        val expectedPayment = 3000
        dashboard.setPresetGasAmount(expectedPayment)
        dashboard.setGasType(Gas.Gasoline)

        var actual = 0
        val paymentJob = launch {
            dashboard.payment.collect {
                actual = it
            }
        }

        launch {
            dashboard.pumpStart()
        }

        launch {
            dashboard.lifeCycle.first { it == EngineLifeCycle.Stop}
            Assert.assertEquals(expectedPayment, actual)
        }.invokeOnCompletion {
            testScope.cancel()
            paymentJob.cancel()
        }

        advanceUntilIdle()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenStopCalled_withPreset_shouldHaveRightGasAmount() = runTest(testScope.testScheduler) {
        val presetPayment = 3000
        val gasType = Gas.Gasoline
        val gasPrice = requireNotNull(dashboard.gasPrices[gasType])
        val expectedGasAmount = presetPayment / gasPrice.pricePerLiter

        dashboard.setPresetGasAmount(presetPayment)
        dashboard.setGasType(gasType)

        var actual = 0
        val gasAmountJob = launch {
            dashboard.gasAmount.collect {
                actual = it
            }
        }

        launch {
            dashboard.pumpStart()
        }

        launch {
            dashboard.lifeCycle.first { it == EngineLifeCycle.Stop}
            Assert.assertEquals(expectedGasAmount, actual)
        }.invokeOnCompletion {
            testScope.cancel()
            gasAmountJob.cancel()
        }

        advanceUntilIdle()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenResetCalled_shouldSetPaymentValueAndGasAmountToZero() = runTest(testScope.testScheduler) {
        dashboard.setPresetGasAmount(3000)
        dashboard.setGasType(Gas.Gasoline)

        var actualPayment = 0
        val paymentJob = launch {
            dashboard.payment.collect {
                actualPayment = it
            }
        }

        var actualGasAmount = 0
        val gasAmountJob = launch {
            dashboard.payment.collect {
                actualGasAmount = it
            }
        }

        launch {
            dashboard.pumpStart()
        }

        launch {
            dashboard.lifeCycle.first { it == EngineLifeCycle.Stop}
            dashboard.reset()
            delay(1)
            Assert.assertEquals(0, actualPayment)
            Assert.assertEquals(0, actualGasAmount)
        }.invokeOnCompletion {
            testScope.cancel()
            paymentJob.cancel()
            gasAmountJob.cancel()
        }

        advanceUntilIdle()
    }
}