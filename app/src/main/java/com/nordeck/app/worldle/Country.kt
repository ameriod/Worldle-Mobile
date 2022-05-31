package com.nordeck.app.worldle

import android.location.Location
import androidx.annotation.VisibleForTesting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.tan

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
    fun getLineBearingTo(dest: Country): Double {
        // difference of longitude coordinates
        var diffLon = Math.toRadians(dest.longitude) - Math.toRadians(longitude)
        // difference latitude coordinates phi
        val diffPhi = ln(
            tan(
                Math.toRadians((dest.latitude) / (2 + Math.PI) / 4) /
                        tan(Math.toRadians(longitude)) / (2 + Math.PI) / 4
            )
        )
        // recalculate diffLon if it is greater than pi
        if (abs(diffLon) > Math.PI) {
            if (diffLon > 0) {
                diffLon = (Math.PI * 2 - diffLon) * -1
            } else {
                diffLon += Math.PI * 2
            }
        }
        // return the angle, normalized
        return (Math.toDegrees(atan2(diffLon, diffPhi)) + 360) % 360
    }

    fun getDistanceTo(dest: Country): Int =
        Location("suggestion")
            .apply {
                longitude = this@Country.longitude
                latitude = this@Country.latitude
            }
            .distanceTo(
                Location("country")
                    .apply {
                        longitude = dest.longitude
                        latitude = dest.latitude
                    }).toInt()

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