package com.gw.study.gaspump.gas.price

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.price.model.Price
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GasPriceTest {

    @Test
    fun whenInit_shouldBePriceMapEmpty() {
        val gasPrice = GasPrice()
        Assert.assertEquals(0, gasPrice.prices.size)
    }

    @Test
    fun whenAddPrice_shouldNotBeEmpty() {
        val gasPrice = GasPrice()
        val price = Price(Gas.Gasoline, 8)
        gasPrice.addPrice(price)
        Assert.assertTrue(gasPrice.prices.isNotEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenFuelFlowsIn_shouldCalcWithGasPriceCumulatively() = runTest(UnconfinedTestDispatcher()) {
        val gasPrice = GasPrice()
        val price = Price(Gas.Gasoline, 8)
        gasPrice.addPrice(price)

        val expected = listOf(0, 8, 16, 24)
        val actual = mutableListOf<Int>()

        val fuelFlow = flow {
            repeat(3) {
                emit(Gas.Gasoline)
            }
        }
        val priceFlow = gasPrice.calcAcc(
            fuelFlow
        )
        launch {
            priceFlow.toList(actual)
        }

        Assert.assertEquals(expected.joinToString(), actual.joinToString())
    }
}