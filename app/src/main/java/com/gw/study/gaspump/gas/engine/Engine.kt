package com.gw.study.gaspump.gas.engine

import com.gw.study.gaspump.gas.engine.model.EngineLifeCycle
import com.gw.study.gaspump.gas.engine.model.Speed
import com.gw.study.gaspump.gas.engine.model.SpeedConfig
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class Engine(
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L)
) {
    lateinit var lifeCycleState: Flow<EngineLifeCycle>
    lateinit var speedState: Flow<Speed>

    operator fun invoke(): Flow<Unit> {
        return fEngine.filter {
            lifeCycleState.first() == EngineLifeCycle.Start
        }
    }

    private suspend fun getSpeedFrame(): Long
        = if (speedState.first() == Speed.Normal) speedConfig.normal else speedConfig.slow

    private val fEngine: Flow<Unit> = flow {
        while (true) {
            currentCoroutineContext().ensureActive()
            emit(Unit)
            delay(getSpeedFrame())
        }
    }
}
