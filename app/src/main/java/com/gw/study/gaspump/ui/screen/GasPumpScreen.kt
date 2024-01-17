package com.gw.study.gaspump.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.gw.study.gaspump.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasPumpApp(
    modifier: Modifier = Modifier,
    viewModel: GasPumpViewModel = viewModel()
) {
    val gasPumpState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
    ) {
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
            onResetClicked = {
                viewModel.sendEvent(GasPumpEvent.Reset)
            },
            modifier = modifier.padding(it)
        )
    }
}

@Composable
fun GasPumpScreen(
    uiState: GasPumpUiState,
    gasNames: List<GasName>,
    onLifeCycleChanged: (EngineLifeCycle) -> Unit,
    presetValueInput: (String) -> Unit,
    onGasSelected: (Gas) -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(10.dp))

        GasPumpInfo(
            gasAmount = uiState.gasAmount.toString(),
            payment = uiState.payment.toString(),
            gasPrices = uiState.gasPrices,
            modifier = Modifier.fillMaxWidth(),
            gasType = uiState.gasType,
            lifeCycle = uiState.lifeCycle,
            speed = uiState.speed
        )

        GasPumpControl(
            presetInfo = uiState.presetInfo,
            gasNames = gasNames,
            gasType = uiState.gasType,
            lifeCycle = uiState.lifeCycle,
            onLifeCycleChanged = onLifeCycleChanged,
            onPresetValueChanged = presetValueInput,
            onGasTypeChanged = onGasSelected,
            onResetClicked = onResetClicked,
            modifier = Modifier.weight(0.7f)
        )
    }
}

@Composable
fun GasPumpInfo(
    gasAmount: String,
    payment: String,
    gasPrices: Map<Gas, Price>,
    gasType: Gas,
    lifeCycle: EngineLifeCycle,
    modifier: Modifier = Modifier,
    speed: Speed
) {
    GasPumpBorderColumnsWithTitle(
        title = stringResource(R.string.info),
        modifier = modifier
    ) {
        Row {
            GasInfoView(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 7.dp),
                title = stringResource(R.string.litre),
                content = gasAmount,
                testTag = TestTag.GAS_AMOUNT
            )
            GasInfoView(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 7.dp, top = 15.dp, bottom = 15.dp, end = 15.dp),
                title = stringResource(R.string.amount),
                content = payment,
                testTag = TestTag.PAYMENT
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            GasInfoView(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 7.dp, end = 7.dp, bottom = 15.dp),
                title = stringResource(R.string.selected_gas_title),
                content = gasType.toString(),
                testTag = TestTag.GAS_TYPE
            )
            GasInfoView(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 7.dp, end = 7.dp, bottom = 15.dp),
                title = stringResource(R.string.progress_title),
                content = lifeCycle.name,
                testTag = TestTag.PROGRESS
            )
            GasInfoView(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 7.dp, end = 7.dp, bottom = 15.dp),
                title = stringResource(R.string.speed_title),
                content = speed.name
            )
        }

        GasPumpBorderColumnsWithTitle(
            title = {
                Text(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.card_normal_padding)),
                    text = stringResource(R.string.price_title),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                gasPrices.toList().forEachIndexed { index, pricePair ->
                    GasInfoView(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                end = if (index != gasPrices.size - 1) 7.dp else 0.dp,
                                bottom = 7.dp,
                                top = 7.dp
                            ),
                        title = pricePair.second.gasType.toString(),
                        content = pricePair.second.pricePerLiter.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun GasInfoView(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    title: String,
    content: String,
    testTag: String = ""
) {
    ElevatedCard(
        modifier = modifier,
        colors = cardColors
    ) {
        val centerGravity = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(vertical = 7.dp, horizontal = 10.dp)

        Text(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .then(centerGravity),
            style = Typography.titleMedium,
            text = title
        )

        Text(
            modifier = centerGravity
                .testTag(testTag),
            style = Typography.bodyLarge,
            text = content,
            overflow = TextOverflow.Ellipsis
        )
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
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    GasPumpBorderColumnsWithTitle(
        title = stringResource(R.string.input),
        modifier = modifier
    ) {

        OutlinedTextField(
            modifier = Modifier
                .padding(10.dp)
                .testTag(TestTag.PRESET),
            label = { Text(text = stringResource(R.string.preset)) },
            placeholder = { Text(text = stringResource(R.string.preset_placeholder)) },
            value = presetInfo.amount.run { if (this == 0) "" else this.toString() },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = stringResource(id = R.string.preset)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = { onPresetValueChanged(it) }
        )

        Row {
            GasPumpBorderColumnsWithTitle(
                modifier = Modifier.weight(1f),
                title = {
                    Text(
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.card_normal_padding)),
                        text = stringResource(R.string.gas_title),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            ) {
                val padding = dimensionResource(id = R.dimen.card_normal_padding)
                gasNames.forEachIndexed { index, gasName ->
                    Button(
                        modifier = Modifier.padding(
                            start = padding,
                            top = padding,
                            bottom = if (index == gasNames.lastIndex) padding else 0.dp
                        ),
                        onClick = { onGasTypeChanged(gasName.gas) },
                        enabled = run {
                            if (gasType == gasName.gas) true else lifeCycle != EngineLifeCycle.Start
                        }
                    ) {
                        Text(text = stringResource(id = gasName.resID))
                    }
                }
            }

            GasPumpBorderColumnsWithTitle(
                modifier = Modifier.weight(1f),
                title = {
                    Text(
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.card_normal_padding)),
                        text = stringResource(R.string.trigger_title),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            ) {
                if (lifeCycle == EngineLifeCycle.Start) {
                    Button(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.card_normal_padding)),
                        onClick = { onLifeCycleChanged(EngineLifeCycle.Stop) })
                    {
                        Text(text = stringResource(id = R.string.stop))
                    }
                } else {
                    Button(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.card_normal_padding)),
                        onClick = { onLifeCycleChanged(EngineLifeCycle.Start) },
                        enabled = gasType != Gas.Unknown
                    ) {
                        Text(text = stringResource(id = R.string.start))
                    }

                    if (lifeCycle == EngineLifeCycle.Stop) {
                        Button(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.card_normal_padding)),
                            onClick = { onResetClicked() }
                        ) {
                            Text(text = stringResource(id = R.string.reset))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GasPumpBorderColumnsWithTitle(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
    ) {

        title()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.card_normal_padding))
                .border(
                    dimensionResource(id = R.dimen.card_outline_width),
                    MaterialTheme.colorScheme.surfaceTint,
                    MaterialTheme.shapes.medium
                )
        ) {
            content()
        }
    }
}
@Composable
fun GasPumpBorderColumnsWithTitle(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    GasPumpBorderColumnsWithTitle(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        content = content
    )
}

@Preview(showSystemUi = true)
@Composable
fun GasPumpScreenPreview() {
    GasPumpTheme {
        GasPumpScreen(
            uiState = GasPumpUiState().onGasPricesChanged(
                mapOf(
                    Gas.Gasoline to Price(Gas.Gasoline, 50),
                    Gas.Premium to Price(Gas.Premium, 20),
                    Gas.Diesel to Price(Gas.Diesel, 100)
                )
            ),
            gasNames = GasNames,
            onLifeCycleChanged = {},
            presetValueInput = {},
            onGasSelected = {},
            onResetClicked = {}
        )
    }
}
