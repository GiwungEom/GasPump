package com.gw.study.gaspump.gasstation.pump

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.type.GasEngine
import kotlinx.coroutines.flow.Flow

interface GasPump {
    val gasEngines: Map<Gas, GasEngine>
    operator fun invoke(): Flow<Gas>
}