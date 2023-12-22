package com.gw.study.gaspump.gasstation.gas.price.model

import com.gw.study.gaspump.gasstation.gas.model.Gas

data class Price(val gasType: Gas, val pricePerLiter: Int)
