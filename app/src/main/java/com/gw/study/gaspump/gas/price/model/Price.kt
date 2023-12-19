package com.gw.study.gaspump.gas.price.model

import com.gw.study.gaspump.gas.model.Gas

data class Price(val gasType: Gas, val pricePerLiter: Int)
