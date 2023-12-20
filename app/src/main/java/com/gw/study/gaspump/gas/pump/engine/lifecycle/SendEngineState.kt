package com.gw.study.gaspump.gas.pump.engine.lifecycle

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.model.Speed

interface SendEngineState {
    suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle)
    suspend fun sendSpeed(speed: Speed)

    suspend fun sendGasType(gas: Gas)
}