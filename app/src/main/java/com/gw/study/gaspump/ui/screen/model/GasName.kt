package com.gw.study.gaspump.ui.screen.model

import androidx.annotation.StringRes
import com.gw.study.gaspump.gasstation.model.Gas

data class GasName(val gas: Gas, @StringRes val resID: Int)