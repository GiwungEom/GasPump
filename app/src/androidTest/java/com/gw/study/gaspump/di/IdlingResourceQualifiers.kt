package com.gw.study.gaspump.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CountingIdleResource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UriIdleResource