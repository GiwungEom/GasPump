package com.gw.study.gaspump.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun GasPumpApp(
    modifier: Modifier = Modifier,
    viewModel: GasPumpViewModel = viewModel()
) {
    val gasPumpState by viewModel.uiState.collectAsStateWithLifecycle()
    GasPumpScreen(
        uiState = gasPumpState,
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
            uiState.gasType.toString(),
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
        Row {
            gasPrices.forEach { (_, price) ->
                GasPrice(price)
            }
        }
    }
}

@Composable
fun GasPrice(
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
    gasType: String,
    lifeCycle: EngineLifeCycle,
    lifeCycleButtonClick: (EngineLifeCycle) -> Unit,
    presetValueChanged: (String) -> Unit,
    gasTypeChanged: (Gas) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.testTag(TestTag.PRESET),
            value = presetInfo.amount.toString(),
            onValueChange = { }
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = R.string.gasoline))
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = R.string.diesel))
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = R.string.premium))
        }
        if (lifeCycle != EngineLifeCycle.Start) {
            Button(onClick = {
                lifeCycleButtonClick(EngineLifeCycle.Start)
            }) {
                Text(text = stringResource(id = R.string.start))
            }
        } else {
            Button(onClick = {
                lifeCycleButtonClick(EngineLifeCycle.Stop)
            }) {
                Text(text = stringResource(id = R.string.stop))
            }
        }
        Text(text = gasType)
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
    GasPumpScreen(
        uiState = GasPumpUiState(),
        onLifeCycleChanged = {},
        presetValueInput = {},
        onGasSelected = {}
    )
}
