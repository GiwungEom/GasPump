package com.gw.study.gaspump.gasstation.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ResetFlowTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenResetCalled_shouldEmitInitialValue() = runTest {
        val initialValue = -1
        val expected = listOf(0, 1, 2, 3, initialValue)
        val resetType = MutableStateFlow(Trigger.None)

        val loopFlow = flow {
            var count = 0
            while (true) {
                emit(count++)
                delay(1)
            }
        }

        launch {
            val actual = resetFlow(loopFlow, resetType, initialValue).take(10).toList()
            println("actual : $actual")
            Assert.assertTrue(actual.all { expected.contains(it) })
        }

        launch {
            repeat(10) {
                resetType.value =
                if (it != 0 && it % 3 == 0) {
                    Trigger.Reset
                } else {
                    Trigger.None
                }
                delay(1)
            }
        }

        advanceUntilIdle()
    }
}