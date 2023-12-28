package com.gw.study.gaspump.ui.screen

import androidx.lifecycle.viewModelScope
import com.gw.study.gaspump.gasstation.dashboard.Dashboard
import com.gw.study.gaspump.ui.architecture.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip

class GasPumpViewModel(
    private val dashboard: Dashboard,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseViewModel<GasPumpEvent, GasPumpUiState>(dispatcher) {
    init {
        collectDashboard()
    }

    override fun initializeUiState(): GasPumpUiState = GasPumpUiState()

    override suspend fun onEventReceived(event: GasPumpEvent) {
        when (event) {
            is GasPumpEvent.PumpStart -> onStartPump()
            is GasPumpEvent.PumpStop -> onStopPump()
            is GasPumpEvent.PumpPause -> onPausePump()
            is GasPumpEvent.PresetInfoSet -> onSetPreset(event)
            is GasPumpEvent.GasTypeSelect -> onSetGasType(event)
        }
    }

    private fun collectDashboard() {
        dashboard.gasAmount
            .zip(
                dashboard.payment
            ) { gasAmount, payment ->
                update { onGasAmountAndPaymentChanged(gasAmount, payment) }
            }.flowOn(dispatcher)
            .launchIn(viewModelScope)

        dashboard.lifeCycle
            .onEach { update { onLifeCycleChanged(it) } }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)

        dashboard.speed
            .onEach { update { onSpeedChanged(it) } }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)

        dashboard.presetGasAmount
            .onEach { update { onPresetInfoChanged(it) } }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)

        dashboard.gasType
            .onEach { update { onGasTypeChanged(it) } }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)
    }

    private fun onSetPreset(event: GasPumpEvent.PresetInfoSet) {
        dashboard.setPresetGasAmount(event.preset.amount)
    }

    private suspend fun onSetGasType(event: GasPumpEvent.GasTypeSelect) {
        dashboard.setGasType(event.gasType)
    }

    private suspend fun onStartPump() {
        dashboard.pumpStart()
    }

    private suspend fun onStopPump() {
        dashboard.pumpStop()
    }

    private suspend fun onPausePump() {
        dashboard.pumpPause()
    }

    override fun onCleared() {
        super.onCleared()
        dashboard.destroy()
    }
}
