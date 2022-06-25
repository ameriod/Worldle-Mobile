package com.nordeck.app.worldle.common

expect class Platform() {
    val platform: String
}

expect fun isLocaleMetric(): Boolean

expect fun getDate(): String
