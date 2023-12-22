package com.gw.study.gaspump.gasstation.pump.type

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.type.state.ReceiveGasEngineState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class PowerGasEngine(
    override val gas: Gas,
    override val engine: Engine,
    private val receiveState: ReceiveGasEngineState
) : GasEngine {

    override operator fun invoke(): Flow<Gas> = engine().map { gas }.filter { it == receiveState.getGasType().value }

}
