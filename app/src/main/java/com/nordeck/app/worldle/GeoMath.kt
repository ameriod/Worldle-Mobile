package com.nordeck.app.worldle

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * https://github.com/jillesvangurp/geogeometry/blob/master/src/commonMain/kotlin/com/jillesvangurp/geo/GeoGeometry.kt
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
            validate(lat1, long1, false)
            validate(lat2, long2, false)

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
        fun headingFromTwoPoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val latitude1: Double = toRadians(lat1)
            val latitude2: Double = toRadians(lat2)
            val longDiff: Double = toRadians(lon2 - lon1)
            val y: Double = sin(longDiff) * cos(latitude2)
            val x: Double =
                cos(latitude1) * sin(latitude2) - sin(latitude1) * cos(
                    latitude2
                ) * cos(longDiff)
            return (fromRadians(atan2(y, x)) + 360) % 360
        }

        /**
         * Validates coordinates. Note. because of some edge cases at the extremes that I've encountered in several data sources, I've built in
         * a small tolerance for small rounding errors that allows e.g. 180.00000000000023 to validate.
         * @param latitude latitude between -90.0 and 90.0
         * @param longitude longitude between -180.0 and 180.0
         * @param strict if false, it will allow for small rounding errors. If true, it will not.
         * @throws IllegalArgumentException if the lat or lon is out of the allowed range.
         */
        fun validate(latitude: Double, longitude: Double, strict: Boolean = false) {
            var roundedLat = latitude
            var roundedLon = longitude
            if (!strict) {
                // this gets rid of rounding errors in raw data e.g. 180.00000000000023 will validate
                roundedLat = (latitude * 1000000).roundToLong() / 1000000.0
                roundedLon = (longitude * 1000000).roundToLong() / 1000000.0
            }
            if (roundedLat < -90.0 || roundedLat > 90.0) {
                throw IllegalArgumentException("Latitude $latitude is outside legal range of -90,90")
            }
            if (roundedLon < -180.0 || roundedLon > 180.0) {
                throw IllegalArgumentException("Longitude $longitude is outside legal range of -180,180")
            }
        }
    }
}