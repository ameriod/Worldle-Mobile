package com.nordeck.app.worldle.ui.main

import com.nordeck.app.worldle.common.ConversionMath
import com.nordeck.app.worldle.common.Country
import com.nordeck.app.worldle.common.Direction
import java.util.Locale

data class Guess(
    val country: Country,
    private val distanceFromMeters: Int,
    val proximityPercent: Int,
    val direction: Direction
) {

    fun getDistanceFrom(locale: Locale = Locale.getDefault()): String {
        return if (locale.isMetric()) {
            "${(ConversionMath.metersToKilometers(distanceFromMeters))} km"
        } else {
            "${(ConversionMath.metersToMiles(distanceFromMeters))} mi"
        }
    }
}

private fun Locale.isMetric(): Boolean {
    return when (country.uppercase(this)) {
        // Only the best countries right here.
        "US", "LR", "MM" -> false
        else -> true
    }
}
