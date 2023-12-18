package com.gw.study.gaspump

import com.gw.study.gaspump.gas.BreadBoard
import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPump
import com.gw.study.gaspump.gas.Process
import com.gw.study.gaspump.gas.PumpEngine
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GasPumpTest {

    @Test
    fun gasPumpTest() = runTest {
        val breadBoard = BreadBoard()

        val pump = GasPump(
            gas = Gas.Gasoline,
            engine = PumpEngine(),
            fProcess = breadBoard.fProcess.combine(breadBoard.fGasType) { process: Process, gas: Gas -> gas to process },
            cScope = this
        )
        var liters = 0
        launch {
            pump().collect {
                liters++
            }
        }

        breadBoard.process = Process.Start
        delay(1000L)

        breadBoard.process = Process.Pause
        delay(1000L)

        assertEquals(21, liters)
        breadBoard.process = Process.Destroy
    }
}