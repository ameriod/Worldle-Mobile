package com.nordeck.app.worldle.common.model

import com.nordeck.app.worldle.common.GeoMath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("code")
    val code: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("name")
    val name: String
) {
    fun getDirectionTo(dest: Country): Direction {
        return if (this == dest) {
            Direction.CORRECT
        } else {
            val bearing = GeoMath.bearing(
                lat1 = latitude,
                lng1 = longitude,
                lat2 = dest.latitude,
                lng2 = dest.longitude
            )
            // Make sure we only use real "Direction"
            Direction.values().filter { it.isDirection }
                .firstOrNull { it.isInRange(bearing) } ?: run {
                // This should never happen, but if it does, do not crash.
                Direction.ERROR
            }
        }
    }

    fun getDistanceTo(dest: Country): Int = GeoMath.distance(
        lat1 = latitude,
        lng1 = longitude,
        lat2 = dest.latitude,
        lng2 = dest.longitude
    ).toInt()
}

fun List<Country>.getByCode(code: String): Country? =
    firstOrNull { country ->
        code.equals(country.code, true)
    }
