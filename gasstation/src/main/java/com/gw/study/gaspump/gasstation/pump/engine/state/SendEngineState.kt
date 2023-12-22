package com.gw.study.gaspump.gasstation.gas.pump.engine.state

import com.gw.study.gaspump.gasstation.gas.pump.engine.model.Speed

interface SendEngineState {
    suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle)
    fun setSpeed(speed: Speed)
}