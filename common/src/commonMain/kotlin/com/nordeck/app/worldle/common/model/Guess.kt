package com.nordeck.app.worldle.common.model

import com.nordeck.app.worldle.common.ConversionMath
import com.nordeck.app.worldle.common.isLocaleMetric

data class Guess(
    val country: Country,
    val distanceFromMeters: Int,
    val proximityPercent: Int,
    val direction: Direction
) {
    fun getDistanceFrom(): String {
        return if (isLocaleMetric()) {
            "${(ConversionMath.metersToKilometers(distanceFromMeters))} km"
        } else {
            "${(ConversionMath.metersToMiles(distanceFromMeters))} mi"
        }
    }
}
