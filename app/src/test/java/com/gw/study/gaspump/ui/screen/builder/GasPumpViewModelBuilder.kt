package com.gw.study.gaspump.ui.screen.builder

import com.gw.study.gaspump.gasstation.dashboard.Dashboard
import com.gw.study.gaspump.ui.screen.GasPumpViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

typealias Stub = () -> Unit

class GasPumpViewModelBuilder {

    private lateinit var dashboard: Dashboard
    private lateinit var dispatcher: CoroutineDispatcher
    private val stubs = mutableMapOf<KClass<out Any>, Stub>()

    fun setDashboard(dashboard: Dashboard) {
        this.dashboard = dashboard
    }

    fun setDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    fun addStub(vararg stub: Pair<KClass<out Any>, Stub>): GasPumpViewModelBuilder {
        stubs.putAll(stub)
        return this
    }

    fun build(): GasPumpViewModel {
        stubs.forEach { (_, stub) -> stub() }
        return GasPumpViewModel(
            dashboard = dashboard,
            dispatcher = dispatcher
        )
    }
}