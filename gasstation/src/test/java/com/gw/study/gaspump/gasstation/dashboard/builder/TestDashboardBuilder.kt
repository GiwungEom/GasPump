package com.gw.study.gaspump.gasstation.dashboard.builder

import com.gw.study.gaspump.gasstation.dashboard.GasPumpDashboard
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.state.BreadBoard
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

typealias Stub = () -> Unit

/**
 * Dashboard 의존성 객체 및 stub 관리 간소화
 */
class TestDashboardBuilder {

    private val stubs: MutableMap<KClass<out Any>, () -> Unit> = mutableMapOf()

    private lateinit var gasPump: GasPump
    private lateinit var gasPrice: GasPrice
    private lateinit var engineBreadBoard: BreadBoard
    private lateinit var presetGauge: PresetGauge
    private lateinit var scope: CoroutineScope

    fun setGasPump(gasPump: GasPump): TestDashboardBuilder {
        this.gasPump = gasPump
        return this
    }

    fun setGasPrice(gasPrice: GasPrice): TestDashboardBuilder {
        this.gasPrice = gasPrice
        return this
    }

    fun setEngineBreadBoard(engineBreadBoard: BreadBoard): TestDashboardBuilder {
        this.engineBreadBoard = engineBreadBoard
        return this
    }

    fun setPresetGauge(presetGauge: PresetGauge): TestDashboardBuilder {
        this.presetGauge = presetGauge
        return this
    }

    fun setScope(scope: CoroutineScope): TestDashboardBuilder {
        this.scope = scope
        return this
    }

    fun addStubs(vararg stubs: Pair<KClass<out Any>, Stub>): TestDashboardBuilder {
        this.stubs.putAll(stubs.toMap())
        return this
    }

    @Throws(UninitializedPropertyAccessException::class)
    fun build(): GasPumpDashboard {
        if (::gasPump.isInitialized &&
            ::gasPrice.isInitialized &&
            ::engineBreadBoard.isInitialized &&
            ::presetGauge.isInitialized &&
            ::scope.isInitialized) {
            stubs.forEach { it.value() }
            return GasPumpDashboard(gasPump, gasPrice, engineBreadBoard, presetGauge, scope)
        } else {
            throw UninitializedPropertyAccessException()
        }
    }
}