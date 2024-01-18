package com.gw.study.gaspump.idlingresource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class UriIdlingResource(
    private val idleTime: Long,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : CountingIdlingResource() {

    private var isIdle: Boolean = true
    private lateinit var idleJob: Job

    override fun increment(): Int {
        var counter: Int
        synchronized(this) {
            counter = super.increment()
            isIdle = false
            cancelIdle()
        }
        return counter
    }

    override fun decrement(): Int {
        var counter: Int
        synchronized(this) {
            counter = super.decrement()
            if (counter <= 0) {
                cancelIdle()
                startIdling()
            }
        }
        return counter
    }

    private fun cancelIdle() {
        if (::idleJob.isInitialized && idleJob.isActive) {
            idleJob.cancel()
        }
    }

    private fun startIdling() {
        idleJob = coroutineScope.launch {
            delay(idleTime)
            isIdle = true
        }
    }

    override val isIdleNow: Boolean
        get() = isIdle

}