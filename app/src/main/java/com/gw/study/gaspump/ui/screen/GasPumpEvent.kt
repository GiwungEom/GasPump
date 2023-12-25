package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.gasstation.dashboard.preset.model.PresetType

sealed class GasPumpEvent {

    data object PumpStart : GasPumpEvent()
    data object PumpStop : GasPumpEvent()
    data object PumpPause : GasPumpEvent()

    data class PresetInfo(val amount: Int, val type: PresetType) : GasPumpEvent()
}