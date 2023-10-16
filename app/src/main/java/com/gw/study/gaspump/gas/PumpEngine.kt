package com.gw.study.gaspump.gas

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive

class PumpEngine(
    private val speedConfig: SpeedConfig = SpeedConfig(50L, 100L),
    private val cScope: CoroutineScope = CoroutineScope(CoroutineName("Engine"))
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

    init {
        println("PumpEngine $this")
    }

    operator fun invoke(): Flow<Unit> {
        return fEngine.filter {
            lifecycle.value == LifeCycle.Start
        }
    }

    private val _fSpeedState = MutableStateFlow(Speed.Normal)
    private val fFrame = _fSpeedState
        .map { if (it == Speed.Normal) speedConfig.normal else speedConfig.slow }
        .stateIn(
            cScope,
            SharingStarted.Eagerly,
            speedConfig.normal
        )

    var speed: Speed
        get() = _fSpeedState.value
        set(value) {
            _fSpeedState.value = value
        }

    private val _lifecycle = MutableStateFlow(LifeCycle.Create)
    val lifecycle: StateFlow<LifeCycle> = _lifecycle.asStateFlow()

    private val fEngine: SharedFlow<Unit> = flow {
        while (true) {
            currentCoroutineContext().ensureActive()
            emit(Unit)
            delay(fFrame.value)
        }
    }.shareIn(
        cScope,
        SharingStarted.WhileSubscribed(),
        0
    )

    fun start() {
        _lifecycle.value = LifeCycle.Start
    }

    fun pause() {
        _lifecycle.value = LifeCycle.Paused
    }

    fun destroy() {
        if (cScope.isActive && (cScope.coroutineContext[Job]?.children?.count() ?: 0) > 0) {
            cScope.coroutineContext[Job]?.children?.forEach { it.cancel() }
        }
        _lifecycle.value = LifeCycle.Destroy
    }
}
