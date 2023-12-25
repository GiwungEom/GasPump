package com.gw.study.gaspump.gasstation.price.model

import com.gw.study.gaspump.gasstation.model.Gas

data class Price(val gasType: Gas, val pricePerLiter: Int)
