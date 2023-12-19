package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.gas.engine.Engine
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.mapper.PumpToEngineLifeCycleMapper
import com.gw.study.gaspump.gas.pump.mapper.PumpToEngineSpeedMapper
import com.gw.study.gaspump.gas.pump.model.PumpLifeCycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class GasPump(
    private val gas: Gas,
    private val engine: Engine,
    fPumpLifeCycle: Flow<Pair<Gas, PumpLifeCycle>> = MutableStateFlow(Pair(Gas.Unknown, PumpLifeCycle.Create)),
    private val engineStateMapper: PumpToEngineLifeCycleMapper = PumpToEngineLifeCycleMapper(),
    private val engineSpeedMapper: PumpToEngineSpeedMapper = PumpToEngineSpeedMapper()
) {

    private val pumpLifeCycle = fPumpLifeCycle.filter { it.first == gas }
    init {
        engine.lifeCycleState = pumpLifeCycle.map { engineStateMapper.toEngineLifeCycle(it.second) }
        engine.speedState = pumpLifeCycle.map { engineSpeedMapper.toEngineSpeed(it.second) }
    }

    operator fun invoke(): Flow<Gas> = engine().map { gas }
}
