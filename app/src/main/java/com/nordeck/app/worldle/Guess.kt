package com.nordeck.app.worldle

import java.util.*

data class Guess(
    val country: Country,
    private val distanceFromMeters: Int,
    val proximityPercent: Int,
    val direction: Direction
) {

    fun getDistanceFrom(locale: Locale = Locale.getDefault()): String {
        val kilometers = distanceFromMeters / 1000
        return if (locale.isMetric()) {
            "${(kilometers)} km"
        } else {
            "${(kilometers * 0.621371).toInt()} mi"
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

