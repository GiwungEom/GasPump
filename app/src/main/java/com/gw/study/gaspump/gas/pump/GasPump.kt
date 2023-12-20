package com.gw.study.gaspump.gas.pump

import com.gw.study.gaspump.gas.model.Gas
import com.gw.study.gaspump.gas.pump.engine.type.GasEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class GasPump(vararg gasEngine: GasEngine) {

    private val types: Map<Gas, GasEngine>
    private val pump: Flow<Gas>

    operator fun invoke() = pump


    init {
        types = gasEngine.associateBy { it.gas }
        pump = types.map { it.value.invoke() }.merge()
    }

}
