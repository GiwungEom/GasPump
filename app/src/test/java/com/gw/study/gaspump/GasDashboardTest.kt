package com.gw.study.gaspump

import com.gw.study.gaspump.gas.BreadBoard
import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPrice
import com.gw.study.gaspump.gas.GasPump
import com.gw.study.gaspump.gas.GasPumpDashboard
import com.gw.study.gaspump.gas.Price
import com.gw.study.gaspump.gas.Process
import com.gw.study.gaspump.gas.PumpEngine
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
    private lateinit var dashboard: GasPumpDashboard
    @Before
    fun setUp() {
        testScope = CoroutineTestScopeFactory.testScope()
        with (testScope) {
            val breadBoard = BreadBoard()
            val process = breadBoard.fProcess.combine(breadBoard.fGasType) { process: Process, gas: Gas -> gas to process }
            val pump = GasPump(
                gas = Gas.Gasoline,
                engine = PumpEngine(),
                fProcess = process,
                cScope = this
            )
            val pump1 = GasPump(
                gas = Gas.Premium,
                engine = PumpEngine(),
                fProcess = process,
                cScope = this
            )
            val pump2 = GasPump(
                gas = Gas.Disel,
                engine = PumpEngine(),
                fProcess = process,
                cScope = this
            )

            val gasPrice = GasPrice()
            gasPrice.addPrice(Price(Gas.Gasoline, 5))

            dashboard = GasPumpDashboard(
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
        var result = Process.Create
        dashboard.preset = 600

        launch {
            dashboard.fProcess.collect {
                result = it
            }
        }

        dashboard.startGasPump(Gas.Gasoline)
        delay(10.seconds)
        assertEquals(Process.Stop, result)
    }

    @Test
    fun presetSlowFactorTest() = testScope.runTest {
        dashboard.preset = 600

        val channel = Channel<Process>()
        launch {
            dashboard.fPayment.collect {
                if (600 - (600 * 0.19) < it) {
                    with ((channel as SendChannel<Process>)) {
                        if (isActive) {
                            send(dashboard.fProcess.value)
                        }
                    }
                }
            }
        }

        launch {
            for (receive in channel) {
                assertEquals(Process.Approach, receive)
                (channel as ReceiveChannel<Process>).cancel()
                break
            }
            dashboard.stopGasPump()
        }
    }
}