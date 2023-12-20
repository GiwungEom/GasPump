package com.gw.study.gaspump.gas.pump.engine.type

import com.gw.study.gaspump.gas.model.Gas

interface SendGasEngineState {
    suspend fun sendGasType(gas: Gas)
}