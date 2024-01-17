package com.gw.study.gaspump.robot

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gw.study.gaspump.assistant.matcher.hasNoText

open class BaseRobot <ACTIVITY: ComponentActivity> (
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<ACTIVITY>, ACTIVITY>
) {

    fun clickButtonWithStringId(@StringRes id: Int) {
        clickButton(getText(id))
    }

    fun clickButton(text: String) {
        composeRule.onNode(hasClickAction() and hasText(text)).performClick()
    }

    fun inputText(semanticsMatcher: SemanticsMatcher, text: String) {
        composeRule.onNode(semanticsMatcher).performTextInput(text)
    }

    fun matchText(semanticsMatcher: SemanticsMatcher, text: String) {
        composeRule.onNode(semanticsMatcher).assert(hasText(text))
    }

    fun matchNoText(semanticsMatcher: SemanticsMatcher, text: String) {
        composeRule.onNode(semanticsMatcher).assert(hasNoText(text))
    }

    fun exists(semanticsMatcher: SemanticsMatcher) {
        composeRule.onNode(semanticsMatcher).assertExists()
    }

    fun assert(nodeMatcher: SemanticsMatcher, assertMatcher: SemanticsMatcher) {
        composeRule.onNode(nodeMatcher).assert(assertMatcher)
    }

    fun assertExists(nodeMatcher: SemanticsMatcher) {
        composeRule.onNode(nodeMatcher).assertExists()
    }

    fun getText(@StringRes id: Int) = composeRule.activity.getString(id)
}