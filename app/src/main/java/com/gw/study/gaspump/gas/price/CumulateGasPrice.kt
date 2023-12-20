package com.gw.study.gaspump.gas.price

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.model.Price
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
            accumulator + prices.getValue(fuel).pricePerLiter
        }
}
