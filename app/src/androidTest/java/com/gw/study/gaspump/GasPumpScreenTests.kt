package com.gw.study.gaspump

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.tag.TestTag
import com.gw.study.gaspump.ui.screen.GasPumpControl
import com.gw.study.gaspump.ui.screen.GasPumpInfo
import com.gw.study.gaspump.ui.screen.GasPumpSpeed
import org.junit.Rule
import org.junit.Test

class GasPumpScreenTests {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun whenGasPumpInfoIsVisible_shouldShowGasAmountAndPaymentAndGasPrice() {
        val prices = mutableMapOf(
            Gas.Gasoline to Price(Gas.Gasoline, 50),
            Gas.Diesel to Price(Gas.Diesel, 10),
            Gas.Premium to Price(Gas.Gasoline, 100),
        )
        rule.setContent {
            GasPumpInfo(
                gasAmount = "0",
                payment = "0",
                gasPrices = prices
            )
        }

        rule.onNodeWithTag(TestTag.GAS_AMOUNT).assert(hasText("0"))
        rule.onNodeWithTag(TestTag.PAYMENT).assert(hasText("0"))
        rule.onNodeWithText(prices[Gas.Gasoline]?.pricePerLiter.toString()).assertExists()
        rule.onNodeWithText(prices[Gas.Diesel]?.pricePerLiter.toString()).assertExists()
        rule.onNodeWithText(prices[Gas.Premium]?.pricePerLiter.toString()).assertExists()
    }

    @Test
    fun whenGasPumpControlIsVisible_shouldShowPresetAndStartAndFuelsButton() {
        rule.setContent {
            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasType = Gas.Unknown.toString(),
                lifeCycle = EngineLifeCycle.Create,
                lifeCycleButtonClick = {},
                presetValueChanged = {},
                gasTypeChanged = {}
            )
        }
        val textGasoline = R.string.gasoline
        val textDiesel =  R.string.diesel
        val textPremium = R.string.premium
        val textStart = R.string.start
        rule.onNodeWithTag(TestTag.PRESET).assert(hasText("0"))
        rule.onNode(hasClickAction() and hasText(textGasoline)).assertExists()
        rule.onNode(hasClickAction() and hasText(textDiesel)).assertExists()
        rule.onNode(hasClickAction() and hasText(textPremium)).assertExists()
        rule.onNode(hasClickAction() and hasText(textStart)).assertExists()
        rule.onNodeWithText(Gas.Unknown.toString()).assertExists()
    }

    @Test
    fun whenGasPumpSpeedIsVisible_shouldShowSpeed() {
        rule.setContent {
            GasPumpSpeed(speed = Speed.Normal)
        }
        rule.onNodeWithText(Speed.Normal.name).assertExists()
    }

    private fun hasText(@StringRes resId: Int) =
        hasText(rule.activity.getString(resId))
}