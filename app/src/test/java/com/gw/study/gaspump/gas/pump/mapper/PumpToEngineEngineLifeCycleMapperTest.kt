package com.gw.study.gaspump.gas.pump.mapper

import com.gw.study.gaspump.gas.engine.model.EngineLifeCycle
import com.gw.study.gaspump.gas.pump.model.PumpLifeCycle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class PumpToEngineEngineLifeCycleMapperTest(
    private val pumpLifeCycle: PumpLifeCycle,
    private val expected: EngineLifeCycle
) {

    companion object {

        @JvmStatic
        @Parameters(name = "{index} given {0}, expected {1}")
        fun data(): Iterable<Any> {
            return listOf(
                arrayOf(PumpLifeCycle.Create, EngineLifeCycle.Create),
                arrayOf(PumpLifeCycle.Start, EngineLifeCycle.Start),
                arrayOf(PumpLifeCycle.Pause, EngineLifeCycle.Paused),
                arrayOf(PumpLifeCycle.Stop, EngineLifeCycle.Stop),
                arrayOf(PumpLifeCycle.Approach, EngineLifeCycle.Start),
                arrayOf(PumpLifeCycle.Destroy, EngineLifeCycle.Stop)
            )
        }
    }

    @Test
    fun whenProcessGiven_shouldMapToEngineLifeCycle() {
        val mapper = PumpToEngineLifeCycleMapper()
        Assert.assertEquals(expected, mapper.toEngineLifeCycle(pumpLifeCycle = pumpLifeCycle))
    }

}