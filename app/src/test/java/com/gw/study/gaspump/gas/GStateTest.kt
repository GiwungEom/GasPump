package com.gw.study.gaspump.gas

import com.gw.study.gaspump.gas.GState
import com.gw.study.gaspump.gas.value
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GStateTest {

    @Test
    fun gStateTest() = runTest {
        val value = 3
        val testFlow: Flow<GState<Int>> = flow {
            emit(GState.Init)
            emit(GState.Value(value))
        }

        launch {
            assertEquals(GState.Init, testFlow.first())
            assertEquals(value, testFlow.first { it is GState.Value }.value())
        }
    }
}