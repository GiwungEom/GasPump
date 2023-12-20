package com.gw.study.gaspump.gas.pump.engine.type

import com.gw.study.gaspump.gas.model.Gas
import kotlinx.coroutines.flow.StateFlow

interface ReceiveGasEngineState {
    fun getGasType(): StateFlow<Gas>
}