package com.gw.study.gaspump.gasstation.pump.type.state

import com.gw.study.gaspump.gasstation.model.Gas
import kotlinx.coroutines.flow.StateFlow

interface ReceiveGasEngineState {
    fun getGasType(): StateFlow<Gas>
}