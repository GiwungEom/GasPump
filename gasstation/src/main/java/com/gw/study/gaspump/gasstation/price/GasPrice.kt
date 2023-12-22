package com.gw.study.gaspump.gasstation.gas.price

import com.gw.study.gaspump.gasstation.gas.model.Gas
import com.gw.study.gaspump.gasstation.gas.price.model.Price
import kotlinx.coroutines.flow.Flow

interface GasPrice {
    fun addPrice(price: Price)

    fun calc(gas: Flow<Gas>): Flow<Int>

}