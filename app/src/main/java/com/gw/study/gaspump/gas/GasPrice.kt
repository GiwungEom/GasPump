package com.gw.study.gaspump.gas

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class GasPrice(
    fLiter: Flow<Int> = emptyFlow(),                        // 주유량
    fGas: StateFlow<Gas> = MutableStateFlow(Gas.Gasoline)   // 연료 타입
) {

    // 지불 금액
    val fPayment: Flow<Int>

    init {
        fPayment = calc(fLiter = fLiter, fGas = fGas)
    }

    private var _prices: MutableMap<Gas, Price> = LinkedHashMap()
    val prices: Map<Gas, Price> = _prices
    fun addPrice(price: Price) {
        _prices[price.gasType] = price
    }

    private fun calc(fLiter: Flow<Int> = emptyFlow(), fGas: StateFlow<Gas>): Flow<Int> =
        fLiter.map { it * prices.getValue(fGas.value).pricePerLiter }


}
