package com.gw.study.gaspump.idlingresource

import com.gw.study.gaspump.idlingresource.base.CountingBaseIdlingResource
import java.util.concurrent.atomic.AtomicInteger

internal open class CountingIdlingResource : CountingBaseIdlingResource {

    private val counter = AtomicInteger(0)

    override val isIdleNow: Boolean
        get() = counter.get() == 0

    override fun increment(): Int = counter.incrementAndGet()

    override fun decrement(): Int = counter.decrementAndGet()
}