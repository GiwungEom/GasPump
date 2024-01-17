package com.gw.study.gaspump.gasstation.state

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class EngineBreadBoardTests {

    @Test
    fun whenResetCalled_shouldSetInitialValueIntoStates() = runTest {
        val expectedSpeed = Speed.Normal
        val expectedGasType = Gas.Unknown
        val expectedEngineLifeCycle = EngineLifeCycle.Create

        val breadBoard = EngineBreadBoard()
        breadBoard.setSpeed(Speed.Slow)
        breadBoard.sendGasType(Gas.Gasoline)
        breadBoard.sendLifeCycle(EngineLifeCycle.Stop)

        breadBoard.reset()

        val actualSpeed = breadBoard.getSpeed().value
        val actualGasType = breadBoard.getGasType().value
        val actualEngineLifeCycle = breadBoard.getLifeCycle().value

        Assert.assertEquals(expectedSpeed, actualSpeed)
        Assert.assertEquals(expectedGasType, actualGasType)
        Assert.assertEquals(expectedEngineLifeCycle, actualEngineLifeCycle)
    }
}