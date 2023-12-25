package com.gw.study.gaspump.ui.architecture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
abstract class BaseViewModel<EVENT: Any, UI_STATE: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    internal abstract fun initializeUiState(): UI_STATE
    internal abstract suspend fun onEventReceived(event: EVENT)

    private val eventChannel: Channel<EVENT> = Channel(capacity = Channel.BUFFERED)
    private val _uiState = MutableStateFlow(initUiState())
    val uiState: StateFlow<UI_STATE> = _uiState.asStateFlow()

    private fun initUiState(): UI_STATE {
        return initializeUiState()
    }

    fun sendEvent(event: EVENT) {
        viewModelScope.launch(dispatcher) {
            if (!eventChannel.isClosedForSend) {
                ensureActive()
                eventChannel.send(event)
            }
        }
    }

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
    }

    protected fun update(state: UI_STATE.() -> UI_STATE) {
        _uiState.update(state)
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