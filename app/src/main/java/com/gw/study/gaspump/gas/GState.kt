package com.gw.study.gaspump.gas

import kotlinx.coroutines.flow.StateFlow

sealed class GState<out T> {
    class Value<T>(val value: T) : GState<T>()
    object Init: GState<Nothing>()
}

fun <T> GState<T>.value(): T {
    if (this is GState.Value) {
        return value
    } else {
        throw IllegalStateException()
    }
}

fun <T> StateFlow<GState<T>>.gValue(): T {
    if (this.value is GState.Value) {
        return this.value.value()
    } else {
        throw IllegalStateException()
    }
}