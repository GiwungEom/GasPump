package com.gw.study.gaspump.gasstation.flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class Trigger {
    Reset,
    None
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> resetFlow(
    flow: Flow<T>,
    resetStateFlow: StateFlow<Trigger>,
    initialValue: T
): Flow<T> {
    return channelFlow {
        launch {
            var flowJob: Job? = null
            resetStateFlow.collect { type ->
                if (type == Trigger.None) {
                    flowJob?.cancel()
                    flowJob = launch {
                        flow.collect {
                            if (!isClosedForSend && isActive) {
                                send(it)
                            }
                        }
                    }
                } else {
                    flowJob?.cancel() ?: println("flowJob is null")
                    launch {
                        if (!isClosedForSend && isActive) {
                            send(initialValue)
                        }
                    }
                }
            }
        }
    }
}