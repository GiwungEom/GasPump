package com.gw.study.gaspump.gas.gasengine.mapper

import com.gw.study.gaspump.gas.engine.model.Speed
import com.gw.study.gaspump.gas.gasengine.model.PumpLifeCycle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class GasEngineToEngineSpeedMapperTest(
    private val pumpLifeCycle: PumpLifeCycle,
    private val expected: Speed
) {

    companion object {

        @JvmStatic
        @Parameters(name = "{index} give {0}, receive {1}")
        fun data(): Iterable<Any> {
            return listOf(
                arrayOf(PumpLifeCycle.Approach, Speed.Slow),
                arrayOf(PumpLifeCycle.Create, Speed.Normal),
                arrayOf(PumpLifeCycle.Start, Speed.Normal),
                arrayOf(PumpLifeCycle.Stop, Speed.Normal),
                arrayOf(PumpLifeCycle.Pause, Speed.Normal),
                arrayOf(PumpLifeCycle.Destroy, Speed.Normal)
            )
        }
    }

    @Test
    fun whenCallToEngineSpeed_shouldReturnEngineSpeed() {
        val mapper = PumpToEngineSpeedMapper()
        Assert.assertEquals(expected, mapper.toEngineSpeed(pumpLifeCycle = pumpLifeCycle))
    }
}