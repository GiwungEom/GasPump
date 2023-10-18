package com.gw.study.gaspump.gas

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GasPumpDashboard(
    vararg prices: Price
) {

    private val priceMap: Map<Gas, Price>
    init {
        priceMap = prices.associateBy { it.gasType }
    }

    // 선택된 Gas
    private val _fGas: MutableStateFlow<GState<Gas>> = MutableStateFlow(GState.Init)
    val fGas: StateFlow<GState<Gas>> = _fGas.asStateFlow()

    fun setGas(gas: Gas) {
        _fGas.value = GState.Value(gas)
    }

    fun getPrice(gasType: Gas): Int = priceMap.getValue(gasType).pricePerLiter
}
