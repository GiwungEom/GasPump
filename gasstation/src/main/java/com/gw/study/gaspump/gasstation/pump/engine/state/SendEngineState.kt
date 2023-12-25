package com.gw.study.gaspump.gasstation.pump.engine.state

import com.gw.study.gaspump.gasstation.pump.engine.model.Speed

interface SendEngineState {
    suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle)
    fun setSpeed(speed: Speed)
}