package com.gw.study.gaspump

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

class CoroutineTestScopeHelper(
    testDispatcher: TestDispatcher
) {

    private val scope: TestScope
    val coroutineContext: CoroutineContext
        get() = scope.coroutineContext.minusKey(CoroutineExceptionHandler)

    init {
        scope = TestScope(testDispatcher + CoroutineName("GasPumpTestScope"))
    }

}