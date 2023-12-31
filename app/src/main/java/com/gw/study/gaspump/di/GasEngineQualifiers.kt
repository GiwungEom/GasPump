package com.gw.study.gaspump.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GasolineGasEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DieselGasEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PremiumGasEngine