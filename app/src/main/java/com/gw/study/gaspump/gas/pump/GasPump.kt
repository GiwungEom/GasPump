package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.type.GasEngine
import kotlinx.coroutines.flow.Flow

interface GasPump {
    val gasEngines: Map<Gas, GasEngine>
    operator fun invoke(): Flow<Gas>
}