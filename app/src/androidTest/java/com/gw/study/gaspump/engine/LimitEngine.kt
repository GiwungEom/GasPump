package com.gw.study.gaspump.engine

import com.gw.study.gaspump.gasstation.pump.engine.Engine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LimitEngine(
    private val count: Int = 5
) : Engine {
    override fun invoke(): Flow<Unit> =
        flow {
            repeat(count) {
                println("LimitEngine : $it")
                emit(Unit)
            }
        }
}