package com.gw.study.gaspump

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.tag.TestTag
import com.gw.study.gaspump.ui.screen.GasPumpControl
import com.gw.study.gaspump.ui.screen.GasPumpInfo
import com.gw.study.gaspump.ui.screen.GasPumpSpeed
import com.gw.study.gaspump.ui.screen.data.screen.GasPumpScreenData
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
                gasNames = GasPumpScreenData.GasNames,
                gasType = Gas.Unknown,
                lifeCycle = EngineLifeCycle.Create,
                onLifeCycleChanged = {},
                onPresetValueChanged = {},
                onGasTypeChanged = {}
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

    @Test
    fun whenGasTypeUnknown_shouldDisableStartButton() {
        rule.setContent {
            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = Gas.Unknown,
                lifeCycle = EngineLifeCycle.Create,
                onLifeCycleChanged = {},
                onPresetValueChanged = {},
                onGasTypeChanged = {}
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).assert(isNotEnabled())
    }

    @Test
    fun whenGasTypeSelected_shouldEnableStartButton() {
        rule.setContent {
            var gasType: Gas by remember { mutableStateOf(Gas.Unknown) }
            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = gasType,
                lifeCycle = EngineLifeCycle.Create,
                onLifeCycleChanged = {},
                onPresetValueChanged = {},
                onGasTypeChanged = {
                    gasType = it
                }
            )
        }
        rule.onNode(hasClickAction() and hasText(R.string.gasoline)).performClick()
        rule.onNode(hasClickAction() and hasText(R.string.start)).assert(isEnabled())
    }

    @Test
    fun whenStartButtonClicked_shouldDisableUnselectedGasTypeButtons() {
        val gas = Gas.Gasoline
        rule.setContent {
            var gasType: Gas by remember { mutableStateOf(gas) }
            var lifeCycle: EngineLifeCycle by remember { mutableStateOf(EngineLifeCycle.Create) }

            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = gasType,
                lifeCycle = lifeCycle,
                onLifeCycleChanged = {
                    lifeCycle = it
                },
                onPresetValueChanged = {},
                onGasTypeChanged = {
                    gasType = it
                }
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).performClick()

        GasPumpScreenData.GasNames.find { it.gas == gas }?.let { clickGasName ->
            GasPumpScreenData.GasNames.forEach { gasName ->
                rule.onNode(hasClickAction() and hasText(gasName.resID)).apply {
                    if (clickGasName.resID == gasName.resID) {
                        assert(isEnabled())
                    } else {
                        assert(isNotEnabled())
                    }
                }
            }
        }
    }

    @Test
    fun whenStartButtonClicked_shouldShowStopButtonAndHideStartButton() {
        rule.setContent {
            var lifeCycle: EngineLifeCycle by remember { mutableStateOf(EngineLifeCycle.Create) }

            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = Gas.Gasoline,
                lifeCycle = lifeCycle,
                onLifeCycleChanged = {
                    lifeCycle = it
                },
                onPresetValueChanged = {},
                onGasTypeChanged = {}
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).performClick()
        rule.onNode(hasClickAction() and hasText(R.string.start)).assertDoesNotExist()
        rule.onNode(hasClickAction() and hasText(R.string.stop)).assertIsDisplayed()
    }

    @Test
    fun whenStopButtonClicked_shouldEnableGasTypeButtonsAndShowStartButton() {
        rule.setContent {
            var lifecycle by remember { mutableStateOf(EngineLifeCycle.Start) }
            GasPumpControl(
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = Gas.Gasoline,
                lifeCycle = lifecycle,
                onLifeCycleChanged = {
                    lifecycle = it
                },
                onPresetValueChanged = {},
                onGasTypeChanged = {}
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.stop)).performClick()
        GasPumpScreenData.GasNames.forEach { gasName ->
            rule.onNode(hasClickAction() and hasText(gasName.resID)).assert(isEnabled())
        }
        rule.onNode(hasClickAction() and hasText(R.string.start)).assertIsDisplayed()
    }

    private fun hasText(@StringRes resId: Int) =
        hasText(rule.activity.getString(resId))
}