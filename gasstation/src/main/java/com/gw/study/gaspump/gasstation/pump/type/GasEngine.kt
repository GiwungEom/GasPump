package com.gw.study.gaspump.gasstation.gas.pump.type

import com.gw.study.gaspump.gasstation.gas.model.Gas
import com.gw.study.gaspump.gasstation.gas.pump.engine.Engine
import kotlinx.coroutines.flow.Flow

interface GasEngine {
    val gas: Gas
    val engine: Engine

    operator fun invoke(): Flow<Gas>
}