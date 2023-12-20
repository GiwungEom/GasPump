package com.gw.study.gaspump.gas.dashboard.preset

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val PRESET_FACTOR = 0.5F

@RunWith(MockitoJUnitRunner::class)
class PresetFactorTests {

    private val presetFactor = PresetFactor(PRESET_FACTOR)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetPaymentSmallerThanPresetFactor_shouldReturnTrue() = runTest(UnconfinedTestDispatcher()) {
        presetFactor.checkPresetFactor(10, 5) {
            Assert.assertTrue(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenPresetPaymentLargerThanPresetFactor_shouldReturnFalse() = runTest(UnconfinedTestDispatcher()) {
        presetFactor.checkPresetFactor(10, 6) {
            Assert.assertFalse(it)
        }
    }
}