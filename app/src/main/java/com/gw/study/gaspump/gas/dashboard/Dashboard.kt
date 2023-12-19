package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.gas.BreadBoard
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.gasengine.model.PumpLifeCycle
import com.gw.study.gaspump.gas.price.GasPrice
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Dashboard(
    private val slowFactor: Float = 0.2f,
    private val breadBoard: BreadBoard,
    fFule: Flow<Gas>,
    gasPrice: GasPrice,
    cScope: CoroutineScope = CoroutineScope(CoroutineName("Dashboard")),
) {
    fun startGasPump(gas: Gas) {
        breadBoard.gasType = gas
        breadBoard.pumpLifeCycle = PumpLifeCycle.Start
    }

    fun stopGasPump() {
        breadBoard.pumpLifeCycle = PumpLifeCycle.Stop
    }

    // 지정 가격
    var preset: Int
        get() = _fPreset.value
        set(value) { _fPreset.value = value }

    private val _fPreset = MutableStateFlow(0)

    // 주유 상태
    val fProcess = breadBoard.fPumpLifeCycle

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

    init {
        cScope.launch {
            limitGasPump(_fPreset, fPayment).collect()
        }
    }

    // 지정 가격에 따른 주유 상태 변경 (정지, 주유 속도 변경)
    private fun limitGasPump(
        preset: StateFlow<Int>,
        payment: StateFlow<Int>
    ): Flow<Unit> {
        return preset.filterNot { it == 0 }.combine(payment) { pre, pay ->
            if (pre <= pay) {
                stopGasPump()
            } else if (pre - (pre * slowFactor) < pay) {
                breadBoard.pumpLifeCycle = PumpLifeCycle.Approach
            } else {
                breadBoard.pumpLifeCycle = PumpLifeCycle.Start
            }
        }
    }
}
