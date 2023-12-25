package com.gw.study.gaspump.gasstation.pump.engine

import kotlinx.coroutines.flow.Flow

interface Engine {
    operator fun invoke(): Flow<Unit>
}