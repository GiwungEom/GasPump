package com.gw.study.gaspump.gasstation.gas.model

sealed interface Gas {
    data object Unknown : Gas

    data object Gasoline : Gas
    data object Diesel : Gas
    data object Premium : Gas
}
