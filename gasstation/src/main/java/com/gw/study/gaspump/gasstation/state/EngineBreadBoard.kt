package com.gw.study.gaspump.gasstation.gas.state

import com.gw.study.gaspump.gasstation.gas.model.Gas
import com.gw.study.gaspump.gasstation.gas.pump.engine.model.Speed
import com.gw.study.gaspump.gasstation.gas.pump.engine.state.EngineLifeCycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EngineBreadBoard : BreadBoard {

    private val engineLifeCycle = MutableStateFlow(EngineLifeCycle.Create)
    private val engineSpeed = MutableStateFlow(Speed.Normal)
    private val gasType: MutableStateFlow<Gas> = MutableStateFlow(Gas.Unknown)

    override fun getLifeCycle(): StateFlow<EngineLifeCycle> = engineLifeCycle.asStateFlow()

    override suspend fun sendLifeCycle(lifeCycle: EngineLifeCycle) = engineLifeCycle.emit(lifeCycle)

    override fun getSpeed(): StateFlow<Speed> = engineSpeed.asStateFlow()

    override fun setSpeed(speed: Speed) {
        engineSpeed.value = speed
    }

    override fun getGasType(): StateFlow<Gas> = gasType.asStateFlow()

    override suspend fun sendGasType(gas: Gas) = gasType.emit(gas)
}