package com.gw.study.gaspump.gas.pump.type.state

import com.gw.study.gaspump.gas.model.Gas

interface SendGasEngineState {
    suspend fun sendGasType(gas: Gas)
}