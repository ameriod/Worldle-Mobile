package com.nordeck.app.worldle.ui.main

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.nordeck.app.worldle.common.model.Country

data class Suggestion(
    val country: Country,
    val displayText: AnnotatedString
) {
    constructor(country: Country, input: String, highlightColor: Color) : this(
        country,
        country.highlightGuess(input, highlightColor)
    )
}

@VisibleForTesting
fun Country.highlightGuess(input: String, highlightColor: Color): AnnotatedString =
    buildAnnotatedString {
        val parts = name.split(Regex("(?i)((?=${input})|(?i)(?<=${input}))")) // ktlint-disable
        parts.forEach {
            if (it.equals(input, true)) {
                withStyle(
                    style = SpanStyle(
                        color = highlightColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(it)
                }
            } else {
                append(it)
            }
        }
    }
