package com.gw.study.gaspump.gas

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow

class PumpEngine(
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L)
) {
    data class SpeedConfig(
        val normal: Long,
        val slow: Long
    )

    enum class Speed {
        Normal,
        Slow
    }

    enum class LifeCycle {
        Create,
        Start,
        Paused,
        Destroy,
    }

    operator fun invoke(): Flow<Unit> {
        return fEngine.filter {
            lifecycle.value == LifeCycle.Start
        }
    }

    private val _fSpeedState = MutableStateFlow(Speed.Normal)

    private fun getSpeedFrame() =
        if (_fSpeedState.value == Speed.Normal) speedConfig.normal else speedConfig.slow

    var speed: Speed
        get() = _fSpeedState.value
        set(value) {
            _fSpeedState.value = value
        }

    private val _lifecycle = MutableStateFlow(LifeCycle.Create)
    val lifecycle: StateFlow<LifeCycle> = _lifecycle.asStateFlow()

    private val fEngine: Flow<Unit> = flow {
        while (true) {
            currentCoroutineContext().ensureActive()
            emit(Unit)
            delay(getSpeedFrame())
        }
    }

    fun start() {
        _lifecycle.value = LifeCycle.Start
    }

    fun pause() {
        _lifecycle.value = LifeCycle.Paused
    }

    fun stop() {
        _lifecycle.value = LifeCycle.Destroy
    }
}
