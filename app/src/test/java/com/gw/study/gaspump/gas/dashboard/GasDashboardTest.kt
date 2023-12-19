package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.gas.BreadBoard
import com.gw.study.gaspump.gas.engine.Engine
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.price.model.Price
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.model.PumpLifeCycle
import com.gw.study.gaspump.scope.CoroutineTestScopeFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class GasDashboardTest {

    private lateinit var testScope: TestScope
    private lateinit var dashboard: Dashboard
    @Before
    fun setUp() {
        testScope = CoroutineTestScopeFactory.testScope()
        with (testScope) {
            val breadBoard = BreadBoard()
            val process = breadBoard.fPumpLifeCycle.combine(breadBoard.fGasType) { pumpLifeCycle: PumpLifeCycle, gas: Gas -> gas to pumpLifeCycle }
            val pump = GasPump(
                gas = Gas.Gasoline,
                engine = Engine(),
                fPumpLifeCycle = process
            )
            val pump1 = GasPump(
                gas = Gas.Premium,
                engine = Engine(),
                fPumpLifeCycle = process
            )
            val pump2 = GasPump(
                gas = Gas.Diesel,
                engine = Engine(),
                fPumpLifeCycle = process
            )

            val gasPrice = GasPrice()
            gasPrice.addPrice(Price(Gas.Gasoline, 5))

            dashboard = Dashboard(
                breadBoard = breadBoard,
                fFule = merge(pump(), pump1(), pump2()),
                gasPrice = gasPrice,
                cScope = this
            )
        }
    }

    @Test
    fun dashboardStartFuelTest() = testScope.runTest {
        var liters = 0
        launch {
            dashboard.fLiters.collect {
                liters = it
            }
        }
        dashboard.startGasPump(Gas.Gasoline)
        delay(1.seconds)
        dashboard.stopGasPump()
        assertEquals(20, liters)
    }

    @Test
    fun dashboardPaymentTest() = testScope.runTest {
        var payment = 0
        launch {
            dashboard.fPayment.collect {
                payment = it
            }
        }

        dashboard.startGasPump(Gas.Gasoline)
        delay(1.seconds)
        dashboard.stopGasPump()
        assertEquals(100, payment)
    }

    @Test
    fun dashboardPresetPaymentTest() = testScope.runTest {
        var result = PumpLifeCycle.Create
        dashboard.preset = 600

        launch {
            dashboard.fProcess.collect {
                result = it
            }
        }

        dashboard.startGasPump(Gas.Gasoline)
        delay(10.seconds)
        assertEquals(PumpLifeCycle.Stop, result)
    }

    @Test
    fun presetSlowFactorTest() = testScope.runTest {
        dashboard.preset = 600

        val channel = Channel<PumpLifeCycle>()
        launch {
            dashboard.fPayment.collect {
                if (600 - (600 * 0.19) < it) {
                    with ((channel as SendChannel<PumpLifeCycle>)) {
                        if (isActive) {
                            send(dashboard.fProcess.value)
                        }
                    }
                }
            }
        }

        launch {
            for (receive in channel) {
                assertEquals(PumpLifeCycle.Approach, receive)
                (channel as ReceiveChannel<PumpLifeCycle>).cancel()
                break
            }
            dashboard.stopGasPump()
        }
    }
}