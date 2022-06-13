package com.nordeck.app.worldle.common.model

data class Guess(
    val country: Country,
    val distanceFromMeters: Int,
    val proximityPercent: Int,
    val direction: Direction
)
