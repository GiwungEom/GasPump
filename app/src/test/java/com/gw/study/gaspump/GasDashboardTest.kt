package com.gw.study.gaspump

import com.gw.study.gaspump.gas.BreadBoard
import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPrice
import com.gw.study.gaspump.gas.GasPump
import com.gw.study.gaspump.gas.GasPumpDashboard
import com.gw.study.gaspump.gas.Price
import com.gw.study.gaspump.gas.Process
import com.gw.study.gaspump.gas.PumpEngine
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GasDashboardTest {
    @Test
    fun dashboardStartFuelTest() = runTest {
        val breadBoard = BreadBoard()
        val process = breadBoard.fProcess.combine(breadBoard.fGasType) { process: Process, gas: Gas -> gas to process }
        val pump = GasPump(
            gas = Gas.Gasoline,
            engine = PumpEngine(cScope = this),
            fProcess = process,
            cScope = this
        )
        val pump1 = GasPump(
            gas = Gas.Premium,
            engine = PumpEngine(cScope = this),
            fProcess = process,
            cScope = this
        )
        val pump2 = GasPump(
            gas = Gas.Disel,
            engine = PumpEngine(cScope = this),
            fProcess = process,
            cScope = this
        )

        val dashboard = GasPumpDashboard(
            breadBoard = breadBoard,
            fFule = merge(pump(), pump1(), pump2()),
            gasPrice = GasPrice(),
            cScope = this
        )
        var liters = 0
        launch {
            dashboard.fLiters.collect {
                liters++
            }
        }

        dashboard.startGasPump(Gas.Gasoline)
        delay(1000)
        assertEquals(21, liters)
        dashboard.stopGasPump()
    }

    @Test
    fun dashboardPaymentTest() = runTest {
        val breadBoard = BreadBoard()
        val process = breadBoard.fProcess.combine(breadBoard.fGasType) { process: Process, gas: Gas -> gas to process }
        val pump = GasPump(
            gas = Gas.Gasoline,
            engine = PumpEngine(cScope = this),
            fProcess = process,
            cScope = this
        )

        val gasPrice = GasPrice()
        gasPrice.addPrice(Price(Gas.Gasoline, 3))

        val dashboard = GasPumpDashboard(
            breadBoard = breadBoard,
            fFule = pump(),
            gasPrice = gasPrice,
            cScope = this
        )

        var payment = 0
        launch {
            dashboard.fPayment.collect {
                payment = it
            }
        }

        dashboard.startGasPump(Gas.Gasoline)
        delay(1000L)
        dashboard.stopGasPump()
        assertEquals(60, payment)
    }



}