package com.gw.study.gaspump.gas.pump.engine.state

import com.gw.study.gaspump.gas.pump.engine.model.Speed
import kotlinx.coroutines.flow.StateFlow


interface ReceiveEngineState {
    fun getLifeCycle(): StateFlow<EngineLifeCycle>
    fun getSpeed(): StateFlow<Speed>
}