package com.gw.study.gaspump.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StandardTestDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnconfinedTestDispatcher