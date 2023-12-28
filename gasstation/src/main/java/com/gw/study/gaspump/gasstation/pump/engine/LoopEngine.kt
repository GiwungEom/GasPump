package com.gw.study.gaspump.gasstation.pump.engine

import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.model.SpeedConfig
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

class LoopEngine(
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L),
    receiveState: ReceiveEngineState,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Engine {

    private val lifeCycleState: Flow<EngineLifeCycle> = receiveState.getLifeCycle()
    private val speedState: Flow<Speed> = receiveState.getSpeed()

    override operator fun invoke(): Flow<Unit> {
        return fEngine.filter {
            lifeCycleState.first() == EngineLifeCycle.Start
        }
    }

    private suspend fun getSpeedFrame(): Long
        = if (speedState.first() == Speed.Normal) speedConfig.normal else speedConfig.slow

    private val fEngine: SharedFlow<Unit> = flow {
        while (true) {
            currentCoroutineContext().ensureActive()
            emit(Unit)
            delay(getSpeedFrame())
        }
    }.shareIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(0),
        replay = 0
    )
}
