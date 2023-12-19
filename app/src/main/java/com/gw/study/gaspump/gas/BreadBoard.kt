package com.gw.study.gaspump.gas

import com.gw.study.gaspump.gas.engine.Engine
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.model.PumpLifeCycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// dependencies between dashboard and other parts
class BreadBoard(
    val gasMap: Map<Gas, Engine> = emptyMap()
) {

    // 주유 유형 변경
    var gasType: Gas
        get() = _fGasType.value
        set(value) {
            _fGasType.value = value
        }

    // 주유 처리 상태 변경
    var pumpLifeCycle: PumpLifeCycle
        get() = _fPumpLifeCycle.value
        set(value) {
            _fPumpLifeCycle.value = value
        }

    private val _fGasType: MutableStateFlow<Gas> = MutableStateFlow(Gas.Unknown)
    private val _fPumpLifeCycle = MutableStateFlow(PumpLifeCycle.Create)

    // 주유 유형
    val fGasType: StateFlow<Gas> = _fGasType.asStateFlow()
    // 주유 처리 상태
    val fPumpLifeCycle: StateFlow<PumpLifeCycle> = _fPumpLifeCycle.asStateFlow()

}
