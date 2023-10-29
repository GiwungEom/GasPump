package com.gw.study.gaspump.gas

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class GasPrice {

    private var _prices: MutableMap<Gas, Price> = LinkedHashMap()
    val prices: Map<Gas, Price> = _prices
    fun addPrice(price: Price) {
        _prices[price.gasType] = price
    }

    fun calc(fLiter: Flow<Int>, fGas: StateFlow<Gas>): Flow<Int> =
        fLiter.map { it * prices.getValue(fGas.value).pricePerLiter }


}
