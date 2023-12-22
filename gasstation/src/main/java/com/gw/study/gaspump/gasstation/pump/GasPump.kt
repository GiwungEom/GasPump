package com.gw.study.gaspump.gasstation.gas.pump

import com.gw.study.gaspump.gasstation.gas.model.Gas
import com.gw.study.gaspump.gasstation.gas.pump.type.GasEngine
import kotlinx.coroutines.flow.Flow

interface GasPump {
    val gasEngines: Map<Gas, GasEngine>
    operator fun invoke(): Flow<Gas>
}