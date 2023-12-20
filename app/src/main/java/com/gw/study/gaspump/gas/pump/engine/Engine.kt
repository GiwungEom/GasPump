package com.gw.study.gaspump.gas.pump.engine

import kotlinx.coroutines.flow.Flow

interface Engine {
    operator fun invoke(): Flow<Unit>
}