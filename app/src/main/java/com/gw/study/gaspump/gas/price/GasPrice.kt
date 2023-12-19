package com.gw.study.gaspump.gas.price

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.model.Price
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

class GasPrice {

    private var _prices: MutableMap<Gas, Price> = LinkedHashMap()
    val prices: Map<Gas, Price> = _prices
    fun addPrice(price: Price) {
        _prices[price.gasType] = price
    }

    fun calc(fLiter: Flow<Int>, fGas: StateFlow<Gas>): Flow<Int> =
        fLiter.map { it * prices.getValue(fGas.value).pricePerLiter }

    fun calcAcc(fuelFlow: Flow<Gas>): Flow<Int> =
        fuelFlow.runningFold(0) { accumulator, gas ->
            accumulator + prices.getValue(gas).pricePerLiter
        }
}
