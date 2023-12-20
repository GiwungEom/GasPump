package com.gw.study.gaspump.gas.pump.engine.state

import com.gw.study.gaspump.gas.pump.engine.model.Speed

interface SendEngineState {
    suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle)
    suspend fun sendSpeed(speed: Speed)
}