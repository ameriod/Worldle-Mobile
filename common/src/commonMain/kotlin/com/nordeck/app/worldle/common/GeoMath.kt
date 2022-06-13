package com.nordeck.app.worldle.common

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class GeoMath {
    companion object {

        /**
         * Earth's mean radius, in meters.
         *
         * see http://en.wikipedia.org/wiki/Earth%27s_radius#Mean_radii
         */
        private const val EARTH_RADIUS_METERS = 6371000.0
        private const val DEGREES_TO_RADIANS = 2.0 * PI / 360.0
        private const val RADIANS_TO_DEGREES = 1.0 / DEGREES_TO_RADIANS

        private fun toRadians(degrees: Double): Double {
            return degrees * DEGREES_TO_RADIANS
        }

        private fun fromRadians(degrees: Double): Double {
            return degrees * RADIANS_TO_DEGREES
        }

        /**
         * From https://github.com/jillesvangurp/geogeometry/blob/master/src/commonMain/kotlin/com/jillesvangurp/geo/GeoGeometry.kt
         *
         * Compute the Haversine distance between the two coordinates. Haversine is
         * one of several distance calculation algorithms that exist. It is not very
         * precise in the sense that it assumes the earth is a perfect sphere, which
         * it is not. This means precision drops over larger distances. According to
         * http://en.wikipedia.org/wiki/Haversine_formula there is a 0.5% error
         * margin given the 1% difference in curvature between the equator and the
         * poles.
         *
         * @param lat1
         * the latitude in decimal degrees
         * @param lng1
         * the longitude in decimal degrees
         * @param lat2
         * the latitude in decimal degrees
         * @param lng2
         * the longitude in decimal degrees
         * @return the distance in meters
         */
        fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val deltaLat = toRadians(lat2 - lat1)
            val deltaLon = toRadians(lng1 - lng2)

            val a =
                sin(deltaLat / 2) * sin(deltaLat / 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(
                    deltaLon / 2
                ) * sin(
                    deltaLon / 2
                )

            val c = 2 * asin(sqrt(a))

            return EARTH_RADIUS_METERS * c
        }

        fun bearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val latitude1: Double = toRadians(lat1)
            val latitude2: Double = toRadians(lat2)
            var longDiff: Double = toRadians(lng2 - lng1)

            // difference latitude coords phi
            val diffPhi =
                ln(tan(latitude2 / 2 + PI / 4) / tan(latitude1 / 2 + PI / 4))

            // recalculate diffLon if it is greater than pi
            if (abs(longDiff) > PI) {
                longDiff = if (longDiff > 0) {
                    (PI * 2 - longDiff) * -1
                } else {
                    PI * 2 + longDiff
                }
            }

            return (fromRadians(atan2(longDiff, diffPhi)) + 360) % 360
        }
    }
}
