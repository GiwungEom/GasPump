package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gas.state.BreadBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

class GasPumpDashboard(
    gasPump: GasPump,
    gasPrice: GasPrice,
    private val engineBreadBoard: BreadBoard,
    private val presetFactor: Float = 0.7F
) {

    private val _presetPayment = MutableStateFlow(0)
    val presetPayment: StateFlow<Int> = _presetPayment.asStateFlow()
    private val gasFlow = gasPump()

    val gasAmount = gasFlow.scan(0) { acc, _ -> acc + 1 }.onEach(::checkPresetFactor)

    val payment = gasPrice.calc(gasFlow)

    val gasType = engineBreadBoard.getGasType()

    suspend fun setGasType(gas: Gas) {
        engineBreadBoard.sendGasType(gas)
    }

    suspend fun pumpStart() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Start)
    }

    suspend fun pumpStop() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Stop)
    }

    suspend fun pumpPause() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Paused)
    }

    fun setPresetPayment(expected: Int) {
        _presetPayment.value = expected
    }

    private suspend fun checkPresetFactor(gasAmount: Int) {
        if (_presetPayment.value > 0 && gasAmount > 0) {
            if (_presetPayment.value * presetFactor >= gasAmount) {
                engineBreadBoard.sendSpeed(Speed.Normal)
            } else {
                engineBreadBoard.sendSpeed(Speed.Slow)
            }
        }
    }
}
