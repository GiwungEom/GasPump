package com.gw.study.gaspump.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gw.study.gaspump.R
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.tag.TestTag
import com.gw.study.gaspump.ui.screen.data.screen.GasPumpScreenData.GasNames
import com.gw.study.gaspump.ui.screen.model.GasName
import com.gw.study.gaspump.ui.theme.GasPumpTheme


@Composable
fun GasPumpApp(
    modifier: Modifier = Modifier,
    viewModel: GasPumpViewModel = viewModel()
) {
    val gasPumpState by viewModel.uiState.collectAsStateWithLifecycle()
    GasPumpScreen(
        uiState = gasPumpState,
        gasNames = GasNames,
        onLifeCycleChanged = { lifeCycle: EngineLifeCycle ->
            viewModel.sendEvent(
                when (lifeCycle) {
                    EngineLifeCycle.Start -> GasPumpEvent.PumpStart
                    EngineLifeCycle.Stop -> GasPumpEvent.PumpStop
                    EngineLifeCycle.Paused -> GasPumpEvent.PumpPause
                    else -> throw IllegalStateException()
                }
            )
        },
        presetValueInput = { value: String ->
            viewModel.sendEvent(
                GasPumpEvent.PresetInfoSet(
                    PresetGauge.AmountInfo(amount = value.toInt())
                )
            )
        },
        onGasSelected = { gas: Gas ->
            viewModel.sendEvent(GasPumpEvent.GasTypeSelect(gasType = gas))
        },
        modifier = modifier
    )
}

@Composable
fun GasPumpScreen(
    uiState: GasPumpUiState,
    gasNames: List<GasName>,
    onLifeCycleChanged: (EngineLifeCycle) -> Unit,
    presetValueInput: (String) -> Unit,
    onGasSelected: (Gas) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        GasPumpInfo(
            uiState.gasAmount.toString(),
            uiState.payment.toString(),
            uiState.gasPrices,
            modifier = modifier
        )

        GasPumpControl(
            uiState.presetInfo,
            gasNames,
            uiState.gasType,
            uiState.lifeCycle,
            onLifeCycleChanged,
            presetValueInput,
            onGasSelected,
            modifier = modifier
        )

        GasPumpSpeed(
            uiState.speed
        )
    }
}

@Composable
fun GasPumpInfo(
    gasAmount: String,
    payment: String,
    gasPrices: Map<Gas, Price>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.testTag(TestTag.GAS_AMOUNT),
            text = gasAmount
        )
        Text(
            modifier = Modifier.testTag(TestTag.PAYMENT),
            text = payment
        )

        gasPrices.forEach { (_, price) ->
            GasPriceView(price)
        }
    }
}

@Composable
fun GasPriceView(
    price: Price
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = price.gasType.toString())
        Text(text = price.pricePerLiter.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasPumpControl(
    presetInfo: PresetGauge.AmountInfo,
    gasNames: List<GasName>,
    gasType: Gas,
    lifeCycle: EngineLifeCycle,
    onLifeCycleChanged: (EngineLifeCycle) -> Unit,
    onPresetValueChanged: (String) -> Unit,
    onGasTypeChanged: (Gas) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.testTag(TestTag.PRESET),
            value = presetInfo.amount.toString(),
            onValueChange = { }
        )

        gasNames.forEach { gasName ->
            Button(
                onClick = { onGasTypeChanged(gasName.gas) },
                enabled = run {
                    if (gasType == gasName.gas) true else lifeCycle != EngineLifeCycle.Start
                }
            ) {
                Text(text = stringResource(id = gasName.resID))
            }
        }

        if (lifeCycle != EngineLifeCycle.Start) {
            Button(onClick = { onLifeCycleChanged(EngineLifeCycle.Start) }) {
                Text(text = stringResource(id = R.string.start))
            }
        } else {
            Button(onClick = { onLifeCycleChanged(EngineLifeCycle.Stop) }) {
                Text(text = stringResource(id = R.string.stop))
            }
        }
        Text(text = gasType.toString())
        Text(text = lifeCycle.name)
    }
}

@Composable
fun GasPumpSpeed(
    speed: Speed
) {
    Text(text = speed.name)
}

@Preview
@Composable
fun GasPumpScreenPreview() {
    GasPumpTheme {
        GasPumpScreen(
            uiState = GasPumpUiState(),
            emptyList(),
            onLifeCycleChanged = {},
            presetValueInput = {},
            onGasSelected = {}
        )
    }
}
