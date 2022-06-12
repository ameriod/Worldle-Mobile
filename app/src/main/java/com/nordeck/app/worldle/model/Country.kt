package com.nordeck.app.worldle.model

import androidx.annotation.VisibleForTesting
import com.nordeck.app.worldle.GeoMath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

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
    val vectorAsset = "file:///android_asset/${code.lowercase()}/vector.svg"

    @VisibleForTesting
    fun getLineBearingTo(dest: Country): Double = GeoMath.headingFromTwoPoints(
        lat1 = latitude,
        lon1 = longitude,
        lat2 = dest.latitude,
        lon2 = dest.longitude
    )

    fun getDistanceTo(dest: Country): Int = GeoMath.distance(
        lat1 = latitude,
        long1 = longitude,
        lat2 = dest.latitude,
        long2 = dest.longitude
    ).toInt()

    fun getDirectionTo(dest: Country): Direction {
        return if (this == dest) {
            Direction.CORRECT
        } else {
            val bearing = getLineBearingTo(dest)
            Timber.d("Bearing: $bearing")
            // Make sure we only use real "Direction"
            Direction.values().filter { it.isDirection }
                .firstOrNull { it.isInRange(bearing) } ?: run {
                // This should never happen, but if it does, do not crash.
                Timber.e("Bearing not in range: $bearing dest: $dest to: ${this@Country}")
                Direction.ERROR
            }
        }
    }
}
