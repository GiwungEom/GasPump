package com.gw.study.gaspump.gasstation.price

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.runningFold

class CumulateGasPrice : GasPrice {

    private var _prices: MutableMap<Gas, Price> = LinkedHashMap()
    val prices: Map<Gas, Price> = _prices
    override fun addPrice(price: Price) {
        _prices[price.gasType] = price
    }

    override fun calc(gas: Flow<Gas>): Flow<Int> =
        gas.runningFold(0) { accumulator, fuel ->
            accumulator + (prices[fuel]?.pricePerLiter ?: 0)
        }
}
