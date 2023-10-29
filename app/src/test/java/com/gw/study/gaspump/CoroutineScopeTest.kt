package com.gw.study.gaspump

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class CoroutineScopeTest {

    private lateinit var scopeHelper: CoroutineTestScopeHelper

    @Before
    fun setUp() {
        scopeHelper = CoroutineTestScopeHelper()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun coroutineScopeTest() = scopeHelper().runTest {
        delay(10.seconds)
        assertEquals(currentTime, 10.seconds)
    }

    @Test
    fun delaySyncTest() = scopeHelper().runTest {
        scopeHelper().launch {
            delay(3000)
            println("!")
        }

        launch {
            delay(1500)
            println("hello")
        }

        launch {
            delay(1700)
            println("world")
        }
    }
}