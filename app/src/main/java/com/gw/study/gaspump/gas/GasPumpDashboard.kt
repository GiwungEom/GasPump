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
    gasPrice: GasPrice,
    cScope: CoroutineScope = CoroutineScope(CoroutineName("Dashboard")),
) {
    fun startGasPump(gas: Gas) {
        breadBoard.gasType = gas
        breadBoard.process = Process.Start
    }

    fun stopGasPump() {
        breadBoard.process = Process.Stop
    }

    // 주유량
    val fLiters: StateFlow<Int> = fFule.scan(0) { acc, _ -> acc + 1 }
        .stateIn(
            cScope,
            SharingStarted.WhileSubscribed(),
            0
        )
    // 주유 가격
    val fPayment: StateFlow<Int> = gasPrice.calc(fLiters, breadBoard.fGasType)
        .stateIn(
            cScope,
            SharingStarted.WhileSubscribed(),
            0
        )


}
