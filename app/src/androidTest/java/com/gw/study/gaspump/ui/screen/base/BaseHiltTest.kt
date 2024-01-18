package com.gw.study.gaspump.ui.screen.base

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.gw.study.gaspump.DemoMainActivity
import com.gw.study.gaspump.rule.HiltInjectionRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule

open class BaseHiltTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val hiltInjectionRule = HiltInjectionRule(hiltRule)

    @get:Rule(order = 3)
    val rule = createAndroidComposeRule<DemoMainActivity>()

}