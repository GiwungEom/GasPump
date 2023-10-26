package com.gw.study.gaspump.gas

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class GasPumpDashboard(
    private val breadBoard: BreadBoard,
    fFule: Flow<Gas>,
    cScope: CoroutineScope = CoroutineScope(CoroutineName("Dashboard"))
) {
    fun startGasPump(gas: Gas) {
        breadBoard.gasType = gas
        breadBoard.process = Process.Start
    }

    fun stopGasPump() {
        breadBoard.process = Process.Stop
    }

    val fLiters: StateFlow<Int> = fFule.scan(0) { acc, _ -> acc + 1 }
        .stateIn(
            cScope,
            SharingStarted.WhileSubscribed(),
            0
        )

}
