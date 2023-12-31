package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle

data class GasPumpUiState(
    val gasAmount: Int = 0,
    val payment: Int = 0,
    val lifeCycle: EngineLifeCycle = EngineLifeCycle.Create,
    val speed: Speed = Speed.Normal,
    val presetInfo: PresetGauge.AmountInfo = PresetGauge.AmountInfo(),
    val gasType: Gas = Gas.Unknown,
    val gasPrices: Map<Gas, Price> = emptyMap(),
) {

    fun onGasAmountAndPaymentChanged(
        amount: Int,
        payment: Int
    ) = this.copy(gasAmount = amount, payment = payment)

    fun onLifeCycleChanged(lifeCycle: EngineLifeCycle) = this.copy(lifeCycle = lifeCycle)

    fun onSpeedChanged(speed: Speed) = this.copy(speed = speed)
    fun onPresetInfoChanged(presetInfo: PresetGauge.AmountInfo) = this.copy(presetInfo = presetInfo)
    fun onGasTypeChanged(gasType: Gas) = this.copy(gasType = gasType)

    fun onGasPricesChanged(gasPrices: Map<Gas, Price>) = this.copy(gasPrices = gasPrices)
}
