package com.gw.study.gaspump.gasstation.state

import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.pump.engine.state.EngineLifeCycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EngineBreadBoard : BreadBoard {

    private val lifeCycleInit = EngineLifeCycle.Create
    private val speedInit = Speed.Normal
    private val gasTypeInit: Gas = Gas.Unknown

    private val engineLifeCycle = MutableStateFlow(lifeCycleInit)
    private val engineSpeed = MutableStateFlow(speedInit)
    private val gasType = MutableStateFlow(gasTypeInit)

    override fun getLifeCycle(): StateFlow<EngineLifeCycle> = engineLifeCycle.asStateFlow()

    override suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle) = engineLifeCycle.emit(lifeCycle)

    override fun getSpeed(): StateFlow<Speed> = engineSpeed.asStateFlow()

    override fun setSpeed(speed: Speed) {
        engineSpeed.value = speed
    }

    override fun getGasType(): StateFlow<Gas> = gasType.asStateFlow()

    override suspend fun sendGasType(gas: Gas) = gasType.emit(gas)

    override suspend fun reset() {
        setSpeed(speedInit)
        sendLifeCycle(lifeCycleInit)
        sendGasType(gasTypeInit)
    }
}