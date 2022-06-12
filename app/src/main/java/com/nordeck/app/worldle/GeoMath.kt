package com.nordeck.app.worldle

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * see https://github.com/jillesvangurp/geogeometry/blob/master/src/commonMain/kotlin/com/jillesvangurp/geo/GeoGeometry.kt
 */
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
         * @param long1
         * the longitude in decimal degrees
         * @param lat2
         * the latitude in decimal degrees
         * @param long2
         * the longitude in decimal degrees
         * @return the distance in meters
         */
        fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
            val deltaLat = toRadians(lat2 - lat1)
            val deltaLon = toRadians(long2 - long1)

            val a =
                sin(deltaLat / 2) * sin(deltaLat / 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(
                    deltaLon / 2
                ) * sin(
                    deltaLon / 2
                )

            val c = 2 * asin(sqrt(a))

            return EARTH_RADIUS_METERS * c
        }

        /**
         * Returns the heading from one LatLng to another LatLng as a compass direction.
         *
         * @see https://stackoverflow.com/questions/9457988/bearing-from-one-coordinate-to-another
         *
         * @return The heading in degrees clockwise from north.
         */
        fun bearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val latitude1: Double = toRadians(lat1)
            val latitude2: Double = toRadians(lat2)
            var longDiff: Double = toRadians(lng2 - lng1)

            // difference latitude coords phi
            val diffPhi =
                ln(tan(latitude2 / 2 + Math.PI / 4) / tan(latitude1 / 2 + Math.PI / 4))

            // recalculate diffLon if it is greater than pi
            if (abs(longDiff) > Math.PI) {
                longDiff = if (longDiff > 0) {
                    (Math.PI * 2 - longDiff) * -1
                } else {
                    Math.PI * 2 + longDiff
                }
            }

            return (fromRadians(atan2(longDiff, diffPhi)) + 360) % 360
        }
    }
}
