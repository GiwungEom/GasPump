package com.gw.study.gaspump

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.gw.study.gaspump.assistant.matcher.hasNoText
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.idlingresource.CountingIdlingResource
import com.gw.study.gaspump.rule.HiltInjectionRule
import com.gw.study.gaspump.tag.TestTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GasPumpScreenEndToEndTests {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val hiltInjectionRule = HiltInjectionRule(hiltRule)

    @get:Rule(order = 3)
    val rule = createAndroidComposeRule<DemoMainActivity>()

    @Inject
    lateinit var idlingResource: CountingIdlingResource

    @Test
    fun whenStartButtonClicked_shouldNotShowInitialGasAmountAndPayment() {
        rule.onNode(hasClickAction() and hasText(Gas.Gasoline.toString())).performClick()
        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.start))).performClick()

        rule.onNode(hasTestTag(TestTag.GAS_AMOUNT)).assert(hasNoText("0"))
        rule.onNode(hasTestTag(TestTag.PAYMENT)).assert(hasNoText("0"))
    }

    @Test
    fun whenStartButtonClicked_withPresetAmount_shouldStopWhenPaymentIsLargerThanPresetAmount() {
        rule.registerIdlingResource(idlingResource = idlingResource)

        rule.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsActions.PasteText)
                    and
                    hasText(rule.activity.getString(R.string.preset))
        ).performTextInput("3000")
        Espresso.closeSoftKeyboard()

        rule.onNode(hasClickAction() and hasText(Gas.Gasoline.toString())).performClick()
        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.start))).performClick()

        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.start))).assertExists()
        rule.onNode(hasTestTag(TestTag.PROGRESS)).assert(hasText(EngineLifeCycle.Stop.toString()))

        rule.unregisterIdlingResource(idlingResource = idlingResource)
    }
}