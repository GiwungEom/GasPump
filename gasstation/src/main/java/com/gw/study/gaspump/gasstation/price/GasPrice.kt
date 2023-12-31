package com.gw.study.gaspump.gasstation.price

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import kotlinx.coroutines.flow.Flow

interface GasPrice {

    val prices: Map<Gas, Price>

    fun addPrice(price: Price)

    fun calc(gas: Flow<Gas>): Flow<Int>

}