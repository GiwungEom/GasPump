package com.gw.study.gaspump.gasstation.pump.type.state

import com.gw.study.gaspump.gasstation.model.Gas

interface SendGasEngineState {
    suspend fun sendGasType(gas: Gas)
}