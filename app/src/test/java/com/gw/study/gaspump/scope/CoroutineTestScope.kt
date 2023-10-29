package com.gw.study.gaspump.scope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

class CoroutineTestScopeHelper(
    name: String = "GasPumpTestScope",
    testDispatcher: TestDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
) {

    operator fun invoke(): TestScope {
        return scope
    }

    private val scope: TestScope

    init {
        scope = TestScope(testDispatcher + CoroutineName(name))
    }
}