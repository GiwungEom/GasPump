package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.type.GasEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class OnePassageGasPump(
    vararg gasEngine: GasEngine
) : GasPump {

    override val gasEngines: Map<Gas, GasEngine>

    private val pump: Flow<Gas>
    override fun invoke(): Flow<Gas> = pump

    init {
        gasEngines = gasEngine.associateBy { it.gas }
        pump = gasEngines.map { it.value.invoke() }.merge()
    }
}
