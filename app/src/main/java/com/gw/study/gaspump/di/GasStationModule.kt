package com.gw.study.gaspump.di

import com.gw.study.gaspump.di.coroutine.DashboardScope
import com.gw.study.gaspump.gasstation.dashboard.Dashboard
import com.gw.study.gaspump.gasstation.dashboard.GasPumpDashboard
import com.gw.study.gaspump.gasstation.dashboard.preset.PresetGauge
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.gasstation.price.CumulateGasPrice
import com.gw.study.gaspump.gasstation.price.GasPrice
import com.gw.study.gaspump.gasstation.price.model.Price
import com.gw.study.gaspump.gasstation.pump.GasPump
import com.gw.study.gaspump.gasstation.pump.OnePassageGasPump
import com.gw.study.gaspump.gasstation.pump.engine.Engine
import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gasstation.pump.type.GasEngine
import com.gw.study.gaspump.gasstation.pump.type.PowerGasEngine
import com.gw.study.gaspump.gasstation.pump.type.state.ReceiveGasEngineState
import com.gw.study.gaspump.gasstation.state.BreadBoard
import com.gw.study.gaspump.gasstation.state.EngineBreadBoard
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
class GasStationModule {

    @ViewModelScoped
    @Provides
    fun providesBreadBoard(): BreadBoard {
        return EngineBreadBoard()
    }

    @Provides
    fun providesReceiveEngine(breadBoard: BreadBoard): ReceiveEngineState {
        return breadBoard
    }

    @Provides
    fun providesReceiveGasEngine(breadBoard: BreadBoard): ReceiveGasEngineState {
        return breadBoard
    }

    @GasolineGasEngine
    @Provides
    fun providesGasolineGasEngine(
        engine: Engine,
        receiveGasEngineState: ReceiveGasEngineState
    ): GasEngine {
        return PowerGasEngine(
            gas = Gas.Gasoline,
            engine = engine,
            receiveState = receiveGasEngineState
        )
    }

    @DieselGasEngine
    @Provides
    fun providesDieselGasEngine(
        engine: Engine,
        receiveGasEngineState: ReceiveGasEngineState
    ): GasEngine {
        return PowerGasEngine(
            gas = Gas.Diesel,
            engine = engine,
            receiveState = receiveGasEngineState
        )
    }

    @PremiumGasEngine
    @Provides
    fun providesPremiumGasEngine(
        engine: Engine,
        receiveGasEngineState: ReceiveGasEngineState
    ): GasEngine {
        return PowerGasEngine(
            gas = Gas.Premium,
            engine = engine,
            receiveState = receiveGasEngineState
        )
    }

    @Provides
    fun providesGasPump(
        @GasolineGasEngine gasolineGasEngine: GasEngine,
        @DieselGasEngine dieselGasEngine: GasEngine,
        @PremiumGasEngine premiumGasEngine: GasEngine
    ): GasPump {
        return OnePassageGasPump(
            gasolineGasEngine, dieselGasEngine, premiumGasEngine
        )
    }

    @GasolinePrice
    @Provides
    fun provideGasolinePrice(): Price {
        return Price(Gas.Gasoline, 50)
    }

    @DieselPrice
    @Provides
    fun provideDieselPrice(): Price {
        return Price(Gas.Diesel, 25)
    }

    @PremiumPrice
    @Provides
    fun providePremiumPrice(): Price {
        return Price(Gas.Premium, 50)
    }

    @Provides
    fun providesGasPrice(
        @GasolinePrice gasolinePrice: Price,
        @DieselPrice dieselPrice: Price,
        @PremiumPrice premiumPrice: Price
    ): GasPrice {
        return CumulateGasPrice().apply {
            addPrice(gasolinePrice)
            addPrice(dieselPrice)
            addPrice(premiumPrice)
        }
    }

    @Provides
    fun providersPresetGauge(): PresetGauge {
        return PresetGauge()
    }

    @Provides
    fun providesDashboard(
        gasPump: GasPump,
        gasPrice: GasPrice,
        presetGauge: PresetGauge,
        breadBoard: BreadBoard,
        @DashboardScope coroutineScope: CoroutineScope
    ): Dashboard {
        return GasPumpDashboard(
            gasPump = gasPump,
            gasPrice = gasPrice,
            presetGauge = presetGauge,
            engineBreadBoard = breadBoard,
            scope = coroutineScope
        )
    }
}