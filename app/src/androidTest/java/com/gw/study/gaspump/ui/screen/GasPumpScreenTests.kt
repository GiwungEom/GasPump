package com.gw.study.gaspump.ui.screen

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.gw.study.gaspump.R
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.tag.TestTag
import com.gw.study.gaspump.ui.screen.data.screen.GasPumpScreenData
import com.gw.study.gaspump.ui.screen.model.GasName
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GasPumpScreenTests {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Mock
    private lateinit var viewModel: GasPumpViewModel


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
                gasPrices = prices,
                gasType = Gas.Unknown,
                lifeCycle = EngineLifeCycle.Create,
                speed = Speed.Normal
            )
        }

        rule.onNodeWithTag(TestTag.GAS_AMOUNT).assert(hasText("0"))
        rule.onNodeWithTag(TestTag.PAYMENT).assert(hasText("0"))
        rule.onNodeWithText(prices[Gas.Gasoline]?.pricePerLiter.toString()).assertExists()
        rule.onNodeWithText(prices[Gas.Diesel]?.pricePerLiter.toString()).assertExists()
        rule.onNodeWithText(prices[Gas.Premium]?.pricePerLiter.toString()).assertExists()
        rule.onNodeWithText(Speed.Normal.name).assertExists()
    }

    @Test
    fun whenGasPumpControlIsVisible_shouldShowPresetAndStartAndFuelsButton() {
        rule.setContent {
//            GasPumpControl(
//                payment = "0",
//                presetInfo = PresetGauge.AmountInfo(),
//                gasNames = GasPumpScreenData.GasNames,
//                gasType = Gas.Unknown,
//                lifeCycle = EngineLifeCycle.Create,
//                onLifeCycleChanged = {},
//                onPresetValueChanged = {},
//                onGasTypeChanged = {},
//                onResetClicked = {}
//            )
            getDefaultGasPumpControlCompose().invoke()
        }
        val textGasoline = R.string.gasoline
        val textDiesel = R.string.diesel
        val textPremium = R.string.premium
        val textStart = R.string.start
        rule.onNodeWithTag(TestTag.PRESET).assert(hasText(R.string.preset_placeholder))
        rule.onNode(hasClickAction() and hasText(textGasoline)).assertExists()
        rule.onNode(hasClickAction() and hasText(textDiesel)).assertExists()
        rule.onNode(hasClickAction() and hasText(textPremium)).assertExists()
        rule.onNode(hasClickAction() and hasText(textStart)).assertExists()
    }

    @Test
    fun whenGasTypeUnknown_shouldDisableStartButton() {
        rule.setContent {
//            GasPumpControl(
//                payment = "0",
//                presetInfo = PresetGauge.AmountInfo(),
//                gasNames = GasPumpScreenData.GasNames,
//                gasType = Gas.Unknown,
//                lifeCycle = EngineLifeCycle.Create,
//                onLifeCycleChanged = {},
//                onPresetValueChanged = {},
//                onGasTypeChanged = {},
//                onResetClicked = {}
//            )
            getDefaultGasPumpControlCompose(
                gasType = Gas.Unknown
            ).invoke()
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).assert(isNotEnabled())
    }

    @Test
    fun whenGasTypeSelected_shouldEnableStartButton() {
        rule.setContent {
            var gasType: Gas by remember { mutableStateOf(Gas.Unknown) }
//            GasPumpControl(
//                payment = "0",
//                presetInfo = PresetGauge.AmountInfo(),
//                gasNames = GasPumpScreenData.GasNames,
//                gasType = gasType,
//                lifeCycle = EngineLifeCycle.Create,
//                onLifeCycleChanged = {},
//                onPresetValueChanged = {},
//                onGasTypeChanged = {
//                    gasType = it
//                },
//                onResetClicked = {}
//            )
            getDefaultGasPumpControlCompose(
                gasType = gasType,
                onGasTypeChanged = {
                    gasType = it
                }
            ).invoke()
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

//            GasPumpControl(
//                payment = "0",
//                presetInfo = PresetGauge.AmountInfo(),
//                gasNames = GasPumpScreenData.GasNames,
//                gasType = gasType,
//                lifeCycle = lifeCycle,
//                onLifeCycleChanged = {
//                    lifeCycle = it
//                },
//                onPresetValueChanged = {},
//                onGasTypeChanged = {
//                    gasType = it
//                },
//                onResetClicked = {}
//            )

            getDefaultGasPumpControlCompose(
                gasType = gasType,
                lifeCycle = lifeCycle,
                onLifeCycleChanged = {
                    lifeCycle = it
                },
                onGasTypeChanged = {
                    gasType = it
                }
            ).invoke()
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

            getDefaultGasPumpControlCompose(
                gasType = Gas.Gasoline,
                lifeCycle = lifeCycle,
                onLifeCycleChanged = {
                    lifeCycle = it
                }
            ).invoke()
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).performClick()
        rule.onNode(hasClickAction() and hasText(R.string.start)).assertDoesNotExist()
        rule.onNode(hasClickAction() and hasText(R.string.stop)).assertIsDisplayed()
    }

    @Test
    fun whenStopButtonClicked_shouldEnableGasTypeButtonsAndShowStartButton() {
        rule.setContent {
            var lifecycle by remember { mutableStateOf(EngineLifeCycle.Start) }
//            GasPumpControl(
//                payment = "0",
//                presetInfo = PresetGauge.AmountInfo(),
//                gasNames = GasPumpScreenData.GasNames,
//                gasType = Gas.Gasoline,
//                lifeCycle = lifecycle,
//                onLifeCycleChanged = {
//                    lifecycle = it
//                },
//                onPresetValueChanged = {},
//                onGasTypeChanged = {},
//                onResetClicked = {}
//            )

            getDefaultGasPumpControlCompose(
                gasType = Gas.Gasoline,
                lifeCycle = lifecycle,
                onLifeCycleChanged = {
                    lifecycle = it
                }
            ).invoke()
        }

        rule.onNode(hasClickAction() and hasText(R.string.stop)).performClick()
        GasPumpScreenData.GasNames.forEach { gasName ->
            rule.onNode(hasClickAction() and hasText(gasName.resID)).assert(isEnabled())
        }
        rule.onNode(hasClickAction() and hasText(R.string.start)).assertIsDisplayed()
    }

    @Test
    fun whenSelectGasType_shouldInvokeViewModelEvent_withGasType() {
        whenever(viewModel.uiState).thenReturn(MutableStateFlow(GasPumpUiState()))

        rule.setContent {
            GasPumpApp(
                viewModel = viewModel
            )
        }

        val expectedGas = Gas.Gasoline
        rule.onNode(hasClickAction() and hasText(expectedGas.toString())).performClick()
        verify(viewModel).sendEvent(GasPumpEvent.GasTypeSelect(expectedGas))
    }

    @Test
    fun whenStartGasPump_shouldInvokeEvent_withPumpStart() {
        whenever(viewModel.uiState).thenReturn(MutableStateFlow(GasPumpUiState(gasType = Gas.Gasoline)))

        rule.setContent {
            GasPumpApp(
                viewModel = viewModel
            )
        }

        val expectedEvent = GasPumpEvent.PumpStart
        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.start))).performClick()
        verify(viewModel).sendEvent(expectedEvent)
    }

    @Test
    fun whenInputPreset_shouldInvokeEvent_withPresetInfoSet() {
        whenever(viewModel.uiState).thenReturn(MutableStateFlow(GasPumpUiState()))

        rule.setContent {
            GasPumpApp(
                viewModel = viewModel
            )
        }

        val expectedValue = 50
        val expectedEvent = GasPumpEvent.PresetInfoSet(PresetGauge.AmountInfo(expectedValue))
        rule.onNode(hasClickAction() and hasText(rule.activity.getString(R.string.preset_placeholder))).performTextInput(expectedValue.toString())
        verify(viewModel).sendEvent(expectedEvent)
    }

    @Test
    fun whenGasPumpFinished_shouldShowResetButton() {
        rule.setContent {
            GasPumpControl(
                payment = "0",
                presetInfo = PresetGauge.AmountInfo(),
                gasNames = GasPumpScreenData.GasNames,
                gasType = Gas.Gasoline,
                speed = Speed.Normal,
                lifeCycle = EngineLifeCycle.Stop,
                onLifeCycleChanged = {},
                onPresetValueChanged = {},
                onGasTypeChanged = {},
                onResetClicked = {}
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.reset)).assertExists()
    }

    @Test
    fun whenResetButtonClicked_shouldInvokeViewModelEvent_withReset() {
        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(
                GasPumpUiState(
                    gasType = Gas.Gasoline,
                    lifeCycle = EngineLifeCycle.Stop,
                    speed = Speed.Slow
                )
            )
        )

        rule.setContent {
            GasPumpApp(
                viewModel = viewModel
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.reset)).performClick()
        verify(viewModel).sendEvent(eq(GasPumpEvent.Reset))
    }

    @Test
    fun whenPresetHigherThanZero_andPaymentHigherThanPreset_shouldSetStartButtonNotEnabled() {
        checkStartButtonWithResetAndPaymentCondition(1,2) { assertIsNotEnabled() }
    }

    @Test
    fun whenPresetHigherThanZero_andPaymentSameWithPreset_shouldSetStartButtonDisabled() {
        checkStartButtonWithResetAndPaymentCondition(1,1) { assertIsNotEnabled() }
    }

    @Test
    fun whenPresetIsZero_andPaymentSameWithPreset_shouldSetStartButtonEnabled() {
        checkStartButtonWithResetAndPaymentCondition(0,0) { assertIsEnabled() }
    }

    @Test
    fun whenPresetIsZero_andPaymentHigherThanPreset_shouldSetStartButtonEnabled() {
        checkStartButtonWithResetAndPaymentCondition(0,1) { assertIsEnabled() }
    }

    private fun checkStartButtonWithResetAndPaymentCondition(
        preset: Int,
        payment: Int,
        expectedSemanticsNodeInteraction: SemanticsNodeInteraction.() -> Unit
    ) {
        fun makeTestState(preset: Int, payment: Int) =
            GasPumpUiState(
                payment = payment,
                presetInfo = PresetGauge.AmountInfo(amount = preset),
                gasType = Gas.Gasoline
            )

        rule.setContent {
            GasPumpScreen(
                uiState = makeTestState(preset, payment),
                gasNames = listOf(GasName(Gas.Gasoline, R.string.gasoline)),
                onLifeCycleChanged = {},
                presetValueInput = {},
                onGasSelected = {},
                onResetClicked = {}
            )
        }

        rule.onNode(hasClickAction() and hasText(R.string.start)).expectedSemanticsNodeInteraction()
    }

    @Test
    fun whenSpeedChangedToSlow_shouldBePresetTextFieldDisabled() {
        rule.setContent {
            getDefaultGasPumpControlCompose(
                speed = Speed.Slow,
                lifeCycle = EngineLifeCycle.Start
            ).invoke()
        }

        rule.onNode(hasTestTag(TestTag.PRESET)).assertIsNotEnabled()
    }

    @Composable
    private fun getDefaultGasPumpControlCompose(
        payment: String = "0",
        presetInfo: PresetGauge.AmountInfo = PresetGauge.AmountInfo(),
        gasNames: List<GasName> = GasPumpScreenData.GasNames,
        gasType: Gas = Gas.Unknown,
        speed: Speed = Speed.Normal,
        lifeCycle: EngineLifeCycle = EngineLifeCycle.Create,
        onLifeCycleChanged: (EngineLifeCycle) -> Unit = {},
        onPresetValueChanged: (String) -> Unit = {},
        onGasTypeChanged: (Gas) -> Unit = {},
        onResetClicked: () -> Unit = {}
    ): @Composable () -> Unit {
        return {
            GasPumpControl(
                payment = payment,
                presetInfo = presetInfo,
                gasNames = gasNames,
                gasType = gasType,
                speed = speed,
                lifeCycle = lifeCycle,
                onLifeCycleChanged = onLifeCycleChanged,
                onPresetValueChanged = onPresetValueChanged,
                onGasTypeChanged = onGasTypeChanged,
                onResetClicked = onResetClicked
            )
        }
    }

    private fun hasText(@StringRes resId: Int) =
        hasText(rule.activity.getString(resId))
}