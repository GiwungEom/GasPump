package com.gw.study.gaspump.gas.pump.engine.type

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.Engine
import com.gw.study.gaspump.gas.pump.engine.lifecycle.ReceiveEngineState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class GasEngine(
    val gas: Gas,
    private val engine: Engine,
    private val receiveEngineState: ReceiveEngineState
) {
    operator fun invoke(): Flow<Gas> = engine().map { gas }.filter { it == receiveEngineState.getGasType() }

}
