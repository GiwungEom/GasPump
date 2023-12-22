package com.gw.study.gaspump.gasstation.dashboard.preset

import com.gw.study.gaspump.gasstation.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.dashboard.preset.state.Gauge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.zip

private const val EMPTY = 0F
private const val SPARE = 0.3F
private const val FULL = 1F

class PresetGauge(
    private val presetFactor: Float = 0.7F
) {

    data class AmountInfo(val amount: Int = 0, val type: PresetType = PresetType.Payment)

    init {
        require(presetFactor in 0.5F..0.9F) { "PresetFactor Must Be Between 0.5F - 0.9F" }
    }

    private val _presetAmount: MutableStateFlow<AmountInfo> = MutableStateFlow(AmountInfo())
    val presetAmount: StateFlow<AmountInfo> = _presetAmount.asStateFlow()
    fun setPreset(presetAmount: Int, type: PresetType) {
        _presetAmount.value = AmountInfo(presetAmount, type)
    }

    private fun getReferencePayment(presetPayment: Int): Int = (presetPayment * presetFactor).toInt()

    private fun getRate(amount: Int): Float = amount / _presetAmount.value.amount.toFloat()

    private fun checkGauge(amount: Int): Gauge {
        val rate = getRate(amount)
        return when {
            rate == 0F -> Gauge.Empty
            rate < SPARE -> Gauge.Spare
            rate < presetFactor -> Gauge.Middle
            rate < FULL -> Gauge.Almost
            else -> Gauge.Full
        }
    }

    fun checkPresetFactor(presetGasAmount: Int, gasAmount: Int, action: (Boolean) -> Unit) {
        if (presetGasAmount > 0 && gasAmount > 0) {
            action(getReferencePayment(presetGasAmount) >= gasAmount)
        } else {
            action(true)
        }
    }

    fun getGauge(gasAmount: Flow<Int>, payment: Flow<Int>): Flow<Gauge> {
        return gasAmount.zip(payment) { fuel, fee ->
            if (presetAmount.value.amount == 0) {
                Gauge.Empty
            } else {
                checkGauge(if (presetAmount.value.type == PresetType.Payment) fee else fuel)
            }
        }
    }
}