package com.gw.study.gaspump

import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPump
import com.gw.study.gaspump.gas.Process
import com.gw.study.gaspump.gas.PumpEngine
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GasPumpTest {

    @Test
    fun gasPumpTest() = runTest {
        val fProcess = MutableStateFlow(Process.Create)
        val pump = GasPump(
            gas = Gas.Gasoline,
            engine = PumpEngine(
                cScope = this
            ),
            fProcess = fProcess,
            cScope = this
        )
        var liters = 0
        launch {
            pump().collect {
                liters++
            }
        }

        fProcess.value = Process.Start
        delay(1000L)

        fProcess.value = Process.Pause
        delay(1000L)

        assertEquals(20, liters)

        fProcess.value = Process.Destroy
    }

}