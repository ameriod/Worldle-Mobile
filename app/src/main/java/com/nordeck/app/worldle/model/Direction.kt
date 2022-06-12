package com.nordeck.app.worldle.model

import androidx.annotation.DrawableRes
import com.nordeck.app.worldle.R

private const val COMPASS_SEGMENT: Float = 22.5f

enum class Direction(
    val start: Double,
    val end: Double,
    @DrawableRes
    val drawableResId: Int = R.drawable.ic_direction,
    val rotation: Float = 0.0f
) {
    CORRECT(
        start = -1.0,
        end = -1.0,
        drawableResId = R.drawable.ic_correct
    ),
    ERROR(
        start = -1.0,
        end = -1.0,
        drawableResId = R.drawable.ic_error
    ),
    N(
        start = 348.75,
        end = 11.25,
    ),
    NNE(
        start = 11.25,
        end = 33.75,
        rotation = COMPASS_SEGMENT
    ),
    NE(
        start = 33.75,
        end = 56.25,
        rotation = COMPASS_SEGMENT.times(2)
    ),
    ENE(
        start = 56.25,
        end = 78.75,
        rotation = COMPASS_SEGMENT.times(3)
    ),
    E(
        start = 78.75,
        end = 101.25,
        rotation = COMPASS_SEGMENT.times(4)
    ),
    ESE(
        start = 101.25,
        end = 123.75,
        rotation = COMPASS_SEGMENT.times(5)
    ),
    SE(
        start = 123.75,
        end = 146.25,
        rotation = COMPASS_SEGMENT.times(6)
    ),
    SSE(
        start = 146.25,
        end = 168.75,
        rotation = COMPASS_SEGMENT.times(7)
    ),
    S(
        start = 168.75,
        end = 191.25,
        rotation = COMPASS_SEGMENT.times(8)
    ),
    SSW(
        start = 191.25,
        end = 213.75,
        rotation = COMPASS_SEGMENT.times(9)
    ),
    SW(
        start = 213.75,
        end = 236.25,
        rotation = COMPASS_SEGMENT.times(10)
    ),
    WSW(
        start = 236.25,
        end = 258.75,
        rotation = COMPASS_SEGMENT.times(11)
    ),
    W(
        start = 258.75,
        end = 281.25,
        rotation = COMPASS_SEGMENT.times(12)
    ),
    WNW(
        start = 281.25,
        end = 303.75,
        rotation = COMPASS_SEGMENT.times(13)
    ),
    NW(
        start = 303.75,
        end = 326.25,
        rotation = COMPASS_SEGMENT.times(14)
    ),
    NNW(
        start = 326.25,
        end = 348.75,
        rotation = COMPASS_SEGMENT.times(15)
    );

    fun isInRange(bearing: Double): Boolean =
        when (this) {
            // North is a special case since it's across 0 in both directions.
            N -> start.rangeTo(360.0).contains(bearing) || 0.0.rangeTo(end).contains(bearing)
            else -> start.rangeTo(end).contains(bearing)
        }

    val isDirection: Boolean = start > 0 && end > 0
}