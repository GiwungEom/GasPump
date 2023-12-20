package com.gw.study.gaspump.gas.price

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.model.Price
import kotlinx.coroutines.flow.Flow

interface GasPrice {
    fun addPrice(price: Price)

    fun calc(gas: Flow<Gas>): Flow<Int>

}