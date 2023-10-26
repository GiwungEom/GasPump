package com.gw.study.gaspump.gas

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// dependencies between dashboard and other parts
class BreadBoard {

    var gasType: Gas
        get() = _fGasType.value
        set(value) {
            _fGasType.value = value
        }

    var process: Process
        get() = _fProcess.value
        set(value) {
            _fProcess.value = value
        }

    private val _fGasType = MutableStateFlow(Gas.Gasoline)
    private val _fProcess = MutableStateFlow(Process.Create)

    val fGasType: StateFlow<Gas> = _fGasType.asStateFlow()
    val fProcess: StateFlow<Process> = _fProcess.asStateFlow()

}
