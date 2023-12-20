package com.gw.study.gaspump.gas.pump.engine.lifecycle

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.model.Speed
import kotlinx.coroutines.flow.StateFlow


interface ReceiveEngineState {
    fun getLifeCycle(): StateFlow<EngineLifeCycle>
    fun getSpeed(): StateFlow<Speed>

    fun getGasType(): StateFlow<Gas>

}