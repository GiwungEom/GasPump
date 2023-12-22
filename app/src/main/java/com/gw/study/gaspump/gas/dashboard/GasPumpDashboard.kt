package com.gw.study.gaspump.gas.dashboard

import com.gw.study.gaspump.gas.dashboard.preset.PresetFactor
import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.GasPrice
import com.gw.study.gaspump.gas.pump.GasPump
import com.gw.study.gaspump.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gas.state.BreadBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce

class GasPumpDashboard(
    gasPump: GasPump,
    gasPrice: GasPrice,
    private val engineBreadBoard: BreadBoard,
    private val presetFactor: PresetFactor = PresetFactor()
) {

    private val _presetGasAmount = MutableStateFlow(0)
    val presetGasAmount: StateFlow<Int> = _presetGasAmount.asStateFlow()
    private val gasFlow = gasPump()

    val gasAmount = gasFlow.map { 1 }.runningReduce { acc, _ -> acc + 1 }.onEach { gasAmount ->
        presetFactor.checkPresetFactor(presetGasAmount = presetGasAmount.value, gasAmount = gasAmount) { changeSpeed(it) }
    }

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

    fun setPresetGasAmount(expected: Int) {
        if (engineBreadBoard.getSpeed().value == Speed.Normal) {
            _presetGasAmount.value = expected
        }
    }

    private fun changeSpeed(normalSpeed: Boolean) {
        if (normalSpeed) {
            engineBreadBoard.setSpeed(Speed.Normal)
        } else {
            engineBreadBoard.setSpeed(Speed.Slow)
        }
    }
}