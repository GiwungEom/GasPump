package com.gw.study.gaspump.scope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

class CoroutineTestScopeFactory {
    companion object {
        fun testScope(
            testDispatcher: TestDispatcher = StandardTestDispatcher(TestCoroutineScheduler()),
            name: String = "GasPumpTestScope"
        ) = TestScope(testDispatcher + CoroutineName(name))
    }
}