package com.gw.study.gaspump.ui.screen.assistant.factory

import kotlinx.coroutines.flow.flow

class TestFlow {
    companion object {
        fun <T> testFlow(repeatCount: Int = 1, data: T) = flow {
            repeat(repeatCount) {
                emit(data)
            }
        }
    }
}