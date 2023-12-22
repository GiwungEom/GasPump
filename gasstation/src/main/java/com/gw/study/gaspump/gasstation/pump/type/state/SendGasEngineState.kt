package com.gw.study.gaspump.gasstation.gas.pump.type.state

import com.gw.study.gaspump.gasstation.gas.model.Gas

interface SendGasEngineState {
    suspend fun sendGasType(gas: Gas)
}