package com.gw.study.gaspump.assistant.matcher

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

fun hasNoText(
    text: String
): SemanticsMatcher {
    return SemanticsMatcher(
        "SemanticsProperties.Text.name contains '$text'"
    ) {
        val isInTextValue = it.config.getOrNull(SemanticsProperties.Text)
            ?.any { item -> item.text == text } ?: false
        !isInTextValue
    }
}