package com.nordeck.app.worldle

import java.util.Locale

class ConversionMath {

    companion object {

        fun metersToKilometers(meters: Int): Int = meters / 1000

        fun metersToMiles(meters: Int): Int = (meters / 1609.34).toInt()
    }
}

fun Locale.isMetric(): Boolean {
    return when (country.uppercase(this)) {
        // Only the best countries right here.
        "US", "LR", "MM" -> false
        else -> true
    }
}
