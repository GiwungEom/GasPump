package com.gw.study.gaspump.gasstation.pump.engine

import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.idlingresource.base.CountingBaseIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DecoratedEngine(
    idlingResource: CountingBaseIdlingResource,
    private val engine: Engine,
    receiveState: ReceiveEngineState,
    scope: CoroutineScope
): Engine {

    init {
        receiveState.getLifeCycle().onEach {
            if (it == EngineLifeCycle.Start) idlingResource.increment()
            else if (it == EngineLifeCycle.Stop) idlingResource.decrement()
        }.launchIn(scope)
    }

    override fun invoke(): Flow<Unit> = engine()

}