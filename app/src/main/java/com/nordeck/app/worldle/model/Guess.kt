package com.nordeck.app.worldle.model

import com.nordeck.app.worldle.ConversionMath
import com.nordeck.app.worldle.isMetric
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
