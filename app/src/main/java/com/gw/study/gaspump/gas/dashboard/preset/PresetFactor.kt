package com.gw.study.gaspump.gas.dashboard.preset

class PresetFactor(
    private val presetFactor: Float = 0.7F
) {
    suspend fun checkPresetFactor(presetPayment: Int, gasAmount: Int, action: suspend (Boolean) -> Unit) {
        if (presetPayment > 0 && gasAmount > 0) {
            action(presetPayment * presetFactor >= gasAmount)
        }
    }
}