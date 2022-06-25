package com.nordeck.app.worldle.common

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun isLocaleMetric(): Boolean {
    return Locale.getDefault().let {
        when (it.country.uppercase()) {
            // Only the best countries right here.
            "US", "LR", "MM" -> false
            else -> true
        }
    }
}

actual fun getDate(): String {
    // Match iOS's NSDate which is UTC/GMT
    return ZonedDateTime.now(ZoneId.of("Etc/GMT"))
        .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
}
