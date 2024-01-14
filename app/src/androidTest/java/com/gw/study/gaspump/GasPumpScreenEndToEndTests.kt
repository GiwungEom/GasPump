package com.gw.study.gaspump

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import com.gw.study.gaspump.gasstation.model.Gas
import com.gw.study.gaspump.tag.TestTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class GasPumpScreenEndToEndTests {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val rule = createAndroidComposeRule<DemoMainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenGasTypeSelected_withGasoline_shouldShowGasolineInInfo() {
        val actual = Gas.Gasoline.toString()
        rule.onNode(hasClickAction() and hasText(actual)).performClick()
        rule.onNode(hasTestTag(TestTag.GAS_TYPE)).assert(hasText(actual))
    }
}