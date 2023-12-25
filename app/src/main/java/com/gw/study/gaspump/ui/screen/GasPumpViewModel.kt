package com.gw.study.gaspump.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.study.gaspump.gasstation.dashboard.Dashboard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class GasPumpViewModel(
    private val dashboard: Dashboard,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val eventChannel: Channel<GasPumpEvent> = Channel(capacity = BUFFERED)
    fun sendEvent(event: GasPumpEvent) {
        viewModelScope.launch(dispatcher) {
            if (!eventChannel.isClosedForSend) {
                ensureActive()
                eventChannel.send(event)
            }
        }
    }

    private val _uiState = MutableStateFlow(GasPumpUiState())
    val uiState: StateFlow<GasPumpUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher + CoroutineName("Event Receiver")) {
            if (!eventChannel.isClosedForReceive) {
                for (event in eventChannel) {
                    launch {
                        onEventReceived(event = event)
                    }
                }
            }
        }
        collectDashboard()
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

    private fun update(state: GasPumpUiState.() -> GasPumpUiState) {
        _uiState.update(state)
    }

    private suspend fun onEventReceived(event: GasPumpEvent) {
        when (event) {
            is GasPumpEvent.PumpStart -> onStartPump()
            is GasPumpEvent.PumpStop -> onStopPump()
            is GasPumpEvent.PumpPause -> onPausePump()
            is GasPumpEvent.PresetInfo -> onSetPreset(event)
        }
    }

    private fun onSetPreset(event: GasPumpEvent.PresetInfo) {
        dashboard.setPresetGasAmount(event.amount)
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
        eventChannel.close()
        try {
            viewModelScope.cancel()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

}
