package com.gw.study.gaspump.robot

import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gw.study.gaspump.DemoMainActivity
import com.gw.study.gaspump.R
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.tag.TestTag
import com.gw.study.gaspump.ui.screen.GasPumpScreenEndToEndResetTests
import com.gw.study.gaspump.ui.screen.GasPumpScreenEndToEndTests

fun GasPumpScreenEndToEndTests.startGasPump(func: GasPumpRobot.() -> Unit) {
    GasPumpRobot(this.rule).func()
}

fun GasPumpScreenEndToEndTests.startGasPumpWithPreset(func: GasPumpRobot.() -> Unit) {
    rule.registerIdlingResource(idlingResource = countingIdlingResource)
    startGasPump(func)
    rule.unregisterIdlingResource(idlingResource = countingIdlingResource)
}

fun GasPumpScreenEndToEndResetTests.startGasPumpAndReset(func: GasPumpRobot.() -> Unit) {
    rule.registerIdlingResource(idlingResource = uriIdlingResource)
    GasPumpRobot(this.rule).func()
    rule.unregisterIdlingResource(idlingResource = uriIdlingResource)
}

class GasPumpRobot(
    composeRule: AndroidComposeTestRule<ActivityScenarioRule<DemoMainActivity>, DemoMainActivity>
) : BaseRobot<DemoMainActivity>(composeRule) {

    fun selectGasType(gasType: Gas) {
        clickButton(gasType.toString())
    }

    fun inputPresetPayment(text: String) {
        inputText(
SemanticsMatcher.keyIsDefined(SemanticsActions.PasteText)
            and
            hasText(getText(R.string.preset)),
            text
        )
        Espresso.closeSoftKeyboard()
    }

    fun checkButton(@StringRes text: Int) {
        assertExists(
            nodeMatcher = hasClickAction() and hasText(getText(text))
        )
    }

    fun checkLifeCycle(lifeCycle: EngineLifeCycle) {
        assert(
            nodeMatcher = hasTestTag(TestTag.PROGRESS),
            assertMatcher = hasText(lifeCycle.toString())
        )
    }

    fun start() {
        clickButtonWithStringId(R.string.start)
    }

    fun reset() {
        clickButtonWithStringId(R.string.reset)
    }

    fun checkPayment(text: String) = matchText(hasTestTag(TestTag.PAYMENT), text)

    fun checkGasAmountChanged() = checkValueChanged(TestTag.GAS_AMOUNT)

    fun checkPaymentChanged() = checkValueChanged(TestTag.PAYMENT)

    fun checkInitialGasAmount() = matchText(hasTestTag(TestTag.GAS_AMOUNT), "0")

    fun checkInitialPreset() = matchText(hasTestTag(TestTag.PRESET), getText(R.string.preset_placeholder))

    fun checkInitialPayment() = checkPayment("0")

    private fun checkValueChanged(tag: String) {
        matchNoText(hasTestTag(tag), "0")
    }
}
