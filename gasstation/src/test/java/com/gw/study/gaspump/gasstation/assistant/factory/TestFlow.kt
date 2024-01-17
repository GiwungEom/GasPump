package com.gw.study.gaspump.gasstation.assistant.factory

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class TestFlow {
    companion object {
        fun <T> testFlow(repeatCount: Int = 1, data: T) = flow {
            repeat(repeatCount) {
                emit(data)
            }
        }
        fun <T> testFlow(repeatCount: Int = 1, operator: () -> T) = flow {
            repeat(repeatCount) {
                emit(operator())
            }
        }
        fun <T> testFlow(repeatCount: Int = 1, data: T, delay: Long) = flow {
            repeat(repeatCount) {
                emit(data)
                delay(delay)
            }
        }
    }
}