package com.gw.study.gaspump.ui.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.gw.study.gaspump.DemoMainActivity
import com.gw.study.gaspump.R
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.idlingresource.CountingIdlingResource
import com.gw.study.gaspump.robot.startGasPump
import com.gw.study.gaspump.robot.startGasPumpWithPreset
import com.gw.study.gaspump.rule.HiltInjectionRule
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
        startGasPump {
            selectGasType(Gas.Gasoline)
            start()
            checkGasAmountChanged()
            checkPaymentChanged()
        }
    }

    @Test
    fun whenStartButtonClicked_withPresetAmount_shouldStopWhenPaymentIsLargerThanPresetAmount() {
        startGasPumpWithPreset {
            inputPresetPayment("3000")
            selectGasType(Gas.Gasoline)
            start()

            checkButton(R.string.start)
            checkLifeCycle(EngineLifeCycle.Stop)
        }
    }
}