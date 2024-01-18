package com.gw.study.gaspump.idlingresource.base

import androidx.compose.ui.test.IdlingResource

interface CountingBaseIdlingResource : IdlingResource {
    fun increment(): Int

    fun decrement(): Int
}