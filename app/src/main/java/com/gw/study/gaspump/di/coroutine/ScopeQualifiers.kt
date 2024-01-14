package com.gw.study.gaspump.di.coroutine

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DashboardScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EngineScope