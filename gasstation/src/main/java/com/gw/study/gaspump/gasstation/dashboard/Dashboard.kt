package com.gw.study.gaspump.gasstation.dashboard

import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Dashboard {

    val gasAmount: Flow<Int>

    val payment: Flow<Int>

    val gasType: Flow<Gas>

    val gasPrices: Map<Gas, Price>

    val presetGasAmount: StateFlow<PresetGauge.AmountInfo>

    val lifeCycle: Flow<EngineLifeCycle>

    val speed: Flow<Speed>

    suspend fun setGasType(gas: Gas)

    suspend fun pumpStart()

    suspend fun pumpStop()

    suspend fun pumpPause()

    fun setPresetGasAmount(expected: Int)

    fun destroy()
}
