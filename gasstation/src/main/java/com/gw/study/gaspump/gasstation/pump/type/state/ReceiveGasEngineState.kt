package com.gw.study.gaspump.gasstation.gas.pump.type.state

import com.gw.study.gaspump.gasstation.gas.model.Gas
import kotlinx.coroutines.flow.StateFlow

interface ReceiveGasEngineState {
    fun getGasType(): StateFlow<Gas>
}