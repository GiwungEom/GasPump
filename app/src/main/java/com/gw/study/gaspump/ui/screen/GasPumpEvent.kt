package com.gw.study.gaspump.ui.screen

import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge

sealed class GasPumpEvent {

    data object PumpStart : GasPumpEvent()
    data object PumpStop : GasPumpEvent()
    data object PumpPause : GasPumpEvent()

    data class PresetInfoSet(val preset: PresetGauge.AmountInfo) : GasPumpEvent()
}