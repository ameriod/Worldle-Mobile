package com.nordeck.app.worldle.common

class ConversionMath {

    companion object {

        fun metersToKilometers(meters: Int): Int = meters / 1000

        fun metersToMiles(meters: Int): Int = (meters / 1609.34).toInt()
    }
}
