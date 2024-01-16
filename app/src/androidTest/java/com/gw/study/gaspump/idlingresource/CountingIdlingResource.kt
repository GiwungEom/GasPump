package com.gw.study.gaspump.idlingresource

import androidx.compose.ui.test.IdlingResource
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountingIdlingResource @Inject constructor(): IdlingResource {

    private val counter = AtomicInteger(0)

    override val isIdleNow: Boolean
        get() = counter.get() == 0

    fun increment() {
        counter.incrementAndGet()
    }

    fun decrement() {
        counter.getAndDecrement()
    }
}