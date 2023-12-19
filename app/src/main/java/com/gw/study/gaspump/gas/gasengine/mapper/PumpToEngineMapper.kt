package com.gw.study.gaspump.gas.gasengine.mapper

import com.gw.study.gaspump.gas.engine.model.EngineLifeCycle
import com.gw.study.gaspump.gas.engine.model.Speed
import com.gw.study.gaspump.gas.gasengine.model.PumpLifeCycle

class PumpToEngineLifeCycleMapper {
    fun toEngineLifeCycle(pumpLifeCycle: PumpLifeCycle) =
        when (pumpLifeCycle) {
            PumpLifeCycle.Create -> EngineLifeCycle.Create
            PumpLifeCycle.Start -> EngineLifeCycle.Start
            PumpLifeCycle.Pause -> EngineLifeCycle.Paused
            PumpLifeCycle.Stop -> EngineLifeCycle.Stop
            PumpLifeCycle.Approach -> EngineLifeCycle.Start
            PumpLifeCycle.Destroy -> EngineLifeCycle.Stop
        }
}

class PumpToEngineSpeedMapper {
    fun toEngineSpeed(pumpLifeCycle: PumpLifeCycle) =
        when (pumpLifeCycle) {
            PumpLifeCycle.Approach -> Speed.Slow
            else -> Speed.Normal
        }
}