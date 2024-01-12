package com.gw.study.gaspump.ui.screen.data.screen

import com.gw.study.gaspump.R
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.ui.screen.model.GasName


object GasPumpScreenData {
    val GasNames = listOf(
        GasName(Gas.Gasoline, R.string.gasoline),
        GasName(Gas.Diesel, R.string.diesel),
        GasName(Gas.Premium, R.string.premium),
    )
}

