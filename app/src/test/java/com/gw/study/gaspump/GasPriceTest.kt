package com.gw.study.gaspump

import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.GasPrice
import com.gw.study.gaspump.gas.Price
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GasPriceTest {

    @Test
    fun setGasPriceTest() {
        val gasPrice = GasPrice()
        val price = Price(Gas.Gasoline, 8)
        gasPrice.addPrice(price)
        assertEquals(price, gasPrice.prices[Gas.Gasoline])
    }

    @Test
    fun gasPricePerLiterTest() = runTest {
        val gasPrice = GasPrice()
        val price = Price(Gas.Gasoline, 8)
        gasPrice.addPrice(price)

        launch {
            var count = 1
            gasPrice.calc(
                flow {
                    repeat(10) {
                        emit(it + 1)
                    }
                },
                MutableStateFlow(Gas.Gasoline).asStateFlow()
            ).collect {
                assertTrue(it / price.pricePerLiter == count++)
            }
        }.join()
    }
}