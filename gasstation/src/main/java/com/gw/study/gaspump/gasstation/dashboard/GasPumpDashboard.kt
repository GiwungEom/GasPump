package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import com.gw.study.gaspump.gasstation.flow.Trigger
import com.gw.study.gaspump.gasstation.flow.resetFlow
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.state.BreadBoard
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn

class GasPumpDashboard(
    gasPump: GasPump,
    gasPrice: GasPrice,
    private val engineBreadBoard: BreadBoard,
    private val presetGauge: PresetGauge = PresetGauge(),
    private val scope: CoroutineScope = CoroutineScope(CoroutineName("dashboard") + Dispatchers.Default + SupervisorJob())
) : Dashboard {


    private val reset = MutableStateFlow(Trigger.None)

    private val gasFlow = gasPump()

    override val gasAmount = resetFlow(
        flow = gasFlow.map { 1 }.runningReduce { acc, _ -> acc + 1 }.onEach { println("gasAmount : $it") },
        resetStateFlow = reset,
        initialValue = 0
    ).stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = 0
    )

    override val payment = resetFlow(
        flow = gasPrice.calc(gasFlow),
        resetStateFlow = reset,
        initialValue = 0
    ).stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = 0
    )

    init {
        presetGauge.getGauge(gasAmount, payment)
            .onEach { changeEngineState(it) }
            .launchIn(scope = scope)
    }

    override val gasType = engineBreadBoard.getGasType()

    override val gasPrices: Map<Gas, Price> = gasPrice.prices

    override val presetGasAmount: StateFlow<PresetGauge.AmountInfo> = presetGauge.presetAmount

    override val lifeCycle = engineBreadBoard.getLifeCycle()

    override val speed = engineBreadBoard.getSpeed()

    override suspend fun setGasType(gas: Gas) {
        if (lifeCycle.value != EngineLifeCycle.Start) {
            engineBreadBoard.sendGasType(gas)
        }
    }

    override suspend fun pumpStart() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Start)
        reset.value = Trigger.None
    }

    override suspend fun pumpStop() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Stop)
    }

    override suspend fun pumpPause() {
        engineBreadBoard.sendLifeCycle(EngineLifeCycle.Paused)
    }

    override fun setPresetGasAmount(expected: Int) {
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

    override suspend fun reset() {
        engineBreadBoard.reset()
        reset.value = Trigger.Reset
        setPresetGasAmount(0)
    }

    override fun destroy() {
        try {
            scope.cancel()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}
