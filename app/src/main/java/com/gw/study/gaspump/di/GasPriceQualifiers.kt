package com.gw.study.gaspump.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GasolinePrice

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DieselPrice

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PremiumPrice