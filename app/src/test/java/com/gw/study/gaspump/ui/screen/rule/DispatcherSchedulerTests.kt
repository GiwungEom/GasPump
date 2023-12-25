package com.gw.study.gaspump.ui.screen.rule

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class DispatcherSchedulerTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun whenMainDispatcherSet_shouldSameTestScopeScheduler() = runTest {
        Assert.assertEquals(mainDispatcherRule.testDispatcher.scheduler, testScheduler)
    }
}