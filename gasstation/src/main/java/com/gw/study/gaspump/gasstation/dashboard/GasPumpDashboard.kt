package com.gw.study.gaspump.gasstation.gas.dashboard

import com.gw.study.gaspump.gasstation.gas.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.gas.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.gas.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.gas.model.Gas
import com.gw.study.gaspump.gasstation.gas.price.GasPrice
import com.gw.study.gaspump.gasstation.gas.pump.GasPump
import com.gw.study.gaspump.gasstation.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.gas.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.gas.state.BreadBoard
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce

class GasPumpDashboard(
    gasPump: GasPump,
    gasPrice: GasPrice,
    private val engineBreadBoard: BreadBoard,
    private val presetGauge: PresetGauge = PresetGauge(),
    private val scope: CoroutineScope = CoroutineScope(CoroutineName("dashboard") + Dispatchers.Default + SupervisorJob())
) {

    private val gasFlow = gasPump()

    val gasAmount = gasFlow.map { 1 }.runningReduce { acc, _ -> acc + 1 }

    val payment = gasPrice.calc(gasFlow)

    val gasType = engineBreadBoard.getGasType()

    val presetGasAmount: StateFlow<PresetGauge.AmountInfo> = presetGauge.presetAmount

    val lifeCycle = engineBreadBoard.getLifeCycle()

    val speed = engineBreadBoard.getSpeed()

    init {
        presetGauge.getGauge(gasAmount, payment)
            .onEach {
                changeEngineState(it)
            }.launchIn(scope = scope)
    }

    suspend fun setGasType(gas: Gas) {
        if (lifeCycle.value != EngineLifeCycle.Start) {
            engineBreadBoard.sendGasType(gas)
        }
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
            presetGauge.setPreset(expected, PresetType.Payment)
        }
    }

    private suspend fun changeEngineState(gauge: Gauge) {
        when (gauge) {
            Gauge.Spare, Gauge.Middle -> engineBreadBoard.setSpeed(Speed.Normal)
            Gauge.Almost -> engineBreadBoard.setSpeed(Speed.Slow)
            Gauge.Full -> engineBreadBoard.sendLifeCycle(EngineLifeCycle.Stop)
            else -> Unit
        }
    }

    fun destroy() {
        try {
            scope.cancel()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}
