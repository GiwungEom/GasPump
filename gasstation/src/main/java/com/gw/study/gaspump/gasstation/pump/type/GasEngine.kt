package com.gw.study.gaspump.gasstation.pump.type

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import kotlinx.coroutines.flow.Flow

interface GasEngine {
    val gas: Gas
    val engine: Engine

    operator fun invoke(): Flow<Gas>
}