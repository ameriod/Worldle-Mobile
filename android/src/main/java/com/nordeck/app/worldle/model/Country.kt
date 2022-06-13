package com.nordeck.app.worldle.model

import com.nordeck.app.worldle.common.Country
import com.nordeck.app.worldle.common.Direction
import com.nordeck.app.worldle.common.GeoMath
import timber.log.Timber

val Country.vectorAsset: String get() = "file:///android_asset/${code.lowercase()}/vector.svg"

fun Country.getDistanceTo(dest: Country): Int = GeoMath.distance(
    lat1 = latitude,
    lng1 = longitude,
    lat2 = dest.latitude,
    lng2 = dest.longitude
).toInt()

fun Country.getDirectionTo(dest: Country): Direction {
    return if (this == dest) {
        Direction.CORRECT
    } else {
        val bearing = GeoMath.bearing(
            lat1 = latitude,
            lng1 = longitude,
            lat2 = dest.latitude,
            lng2 = dest.longitude
        )
        Timber.d("Bearing: $bearing")
        // Make sure we only use real "Direction"
        Direction.values().filter { it.isDirection }
            .firstOrNull { it.isInRange(bearing) } ?: run {
            // This should never happen, but if it does, do not crash.
            Timber.e("Bearing not in range: $bearing dest: $dest to: $this")
            Direction.ERROR
        }
    }
}

fun List<Country>.getByCode(code: String): Country? =
    firstOrNull { country ->
        code.equals(country.code, true)
    }
