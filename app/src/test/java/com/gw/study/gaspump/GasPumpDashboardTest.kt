package com.gw.study.gaspump

import com.gw.study.gaspump.gas.Gas
import com.gw.study.gaspump.gas.Price
import com.gw.study.gaspump.gas.GasPumpDashboard
import com.gw.study.gaspump.gas.gValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GasPumpDashboardTest {

    @Test
    fun gasTypeSelectionTest() = runTest {
        val dashboard = GasPumpDashboard()
        dashboard.setGas(Gas.Gasoline)
        assertTrue(Gas.Gasoline == dashboard.fGas.gValue())
    }

    @Test
    fun gasPriceSelectionTest() = runTest {

        val dashboard = GasPumpDashboard(
            Price(Gas.Gasoline, 8),
            Price(Gas.Disel, 5),
            Price(Gas.Premium, 10)
        )
        assertEquals(8, dashboard.getPrice(Gas.Gasoline))
        assertEquals(5, dashboard.getPrice(Gas.Disel))
        assertEquals(10, dashboard.getPrice(Gas.Premium))
    }


}
