package com.gw.study.gaspump.gasstation.gas.dashboard.preset

import com.gw.study.gaspump.gasstation.assistant.factory.TestFlow
import com.gw.study.gaspump.gasstation.gas.dashboard.preset.model.PresetType
import com.gw.study.gaspump.gasstation.gas.dashboard.preset.state.Gauge
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val PRESET_FACTOR = 0.5F

@RunWith(MockitoJUnitRunner::class)
class PresetGaugeTests {

    private val presetGauge = PresetGauge(PRESET_FACTOR)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetPaymentSmallerThanPresetFactor_shouldReturnTrue() = runTest(UnconfinedTestDispatcher()) {
        presetGauge.checkPresetFactor(10, 5) {
            Assert.assertTrue(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetPaymentLargerThanPresetFactor_shouldReturnFalse() = runTest(UnconfinedTestDispatcher()) {
        presetGauge.checkPresetFactor(10, 6) {
            Assert.assertFalse(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetPaymentNotSetYet_shouldReturnTrue() = runTest(UnconfinedTestDispatcher()) {
        presetGauge.checkPresetFactor(0, 6) {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun whenInitialized_shouldBeEmptyGaugeStateAndAmountZero() = runTest {
        val expected = Gauge.Empty
        val actual = presetGauge.getGauge(
            TestFlow.testFlow(1, 1),
            TestFlow.testFlow(1, 1)
        ).single()
        Assert.assertEquals(expected, actual)
        Assert.assertEquals(0, presetGauge.presetAmount.value.amount)
    }

    @Test
    fun whenPresetAmountChanged_shouldAmountChanged() = runTest {
        val expected = 10
        presetGauge.setPreset(expected, PresetType.Payment)
        Assert.assertEquals(expected, presetGauge.presetAmount.value.amount)
    }

    @Test
    fun whenGasAmountChanged_withGasType_shouldNotBeEmptyGaugeState() = runTest {
        presetGauge.setPreset(10, PresetType.Payment)
        val gaugeState = presetGauge.getGauge(
            TestFlow.testFlow(1, 1),
            TestFlow.testFlow(1, 1),
        ).single()
        Assert.assertNotEquals(Gauge.Empty, gaugeState)
    }

    @Test
    fun whenGasAmountChanged_shouldBeGaugeStateChanged() = runTest {
        presetGauge.setPreset(10, PresetType.Payment)
        val input = listOf(0, 2, 4, 6, 10)
        var index = 0
        val actual = mutableListOf<Gauge>()
        val expected = listOf(Gauge.Empty, Gauge.Spare, Gauge.Middle, Gauge.Almost, Gauge.Full)
        presetGauge.getGauge(
            TestFlow.testFlow(input.size, 1),
            TestFlow.testFlow(input.size) { input[index++] },
        ).toList(actual)
        Assert.assertEquals(expected.joinToString(), actual.joinToString())
    }
}