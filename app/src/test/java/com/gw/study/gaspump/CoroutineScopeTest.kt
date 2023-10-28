package com.gw.study.gaspump

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CoroutineScopeTest {

    private lateinit var scopeHelper: CoroutineTestScopeHelper

    @Before
    fun setUp() {
        scopeHelper = CoroutineTestScopeHelper(
            StandardTestDispatcher(TestCoroutineScheduler())
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun coroutineScopeTest() = runTest(context = scopeHelper.coroutineContext) {
        delay(10000L)
        assertEquals(currentTime, 10000L)
    }

}