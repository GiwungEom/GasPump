package com.gw.study.gaspump

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import com.gw.study.gaspump.assistant.matcher.hasNoText
import com.gw.study.gaspump.di.StandardTestDispatcher
import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.rule.HiltInjectionRule
import com.gw.study.gaspump.tag.TestTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

    @StandardTestDispatcher
    @Inject
    lateinit var testDispatcher: TestDispatcher

    @DashboardScope
    @Inject
    lateinit var dashboardScope: CoroutineScope

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenStartButtonClicked_shouldNotShowInitialGasAmountAndPayment() = runTest(testDispatcher) {
        rule.onNode(hasClickAction() and hasText(Gas.Gasoline.toString())).performClick()
        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.start))).performClick()
        launch {
            delay(1000)
            rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.stop))).performClick()
        }.invokeOnCompletion {
            dashboardScope.cancel()
        }

        advanceUntilIdle()
        rule.onNode(hasTestTag(TestTag.GAS_AMOUNT)).assert(hasNoText("0"))
        rule.onNode(hasTestTag(TestTag.PAYMENT)).assert(hasNoText("0"))
    }
}