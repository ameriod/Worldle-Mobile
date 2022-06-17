package com.nordeck.app.worldle.ui.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.nordeck.app.worldle.common.GeoMath
import com.nordeck.app.worldle.common.model.Country
import com.nordeck.app.worldle.common.model.Direction
import timber.log.Timber

val Country.vectorAsset: String get() = "file:///android_asset/${code.lowercase()}.svg"

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
