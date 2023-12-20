package com.gw.study.gaspump.gas.pump.engine.type

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.Engine
import kotlinx.coroutines.flow.Flow

interface GasEngine {
    val gas: Gas
    val engine: Engine

    operator fun invoke(): Flow<Gas>
}