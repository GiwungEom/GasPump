package com.gw.study.gaspump.gas.dashboard.preset

class PresetFactor(
    private val presetFactor: Float = 0.7F
) {

    private fun getReferencePayment(presetPayment: Int): Int = (presetPayment * presetFactor).toInt()

    fun checkPresetFactor(presetGasAmount: Int, gasAmount: Int, action: (Boolean) -> Unit) {
        if (presetGasAmount > 0 && gasAmount > 0) {
            action(getReferencePayment(presetGasAmount) >= gasAmount)
        } else {
            action(true)
        }
    }
}