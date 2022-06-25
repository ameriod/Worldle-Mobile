package com.nordeck.app.worldle.common

import com.nordeck.app.worldle.common.db.History
import com.nordeck.app.worldle.common.model.GameViewModel
import com.nordeck.app.worldle.common.model.listOfStringsAdapter
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.usesMetricSystem
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun isLocaleMetric(): Boolean {
    return NSLocale.currentLocale().usesMetricSystem
}

actual fun getDate(): String {
    val date = NSDate()
    val formatter = NSDateFormatter().apply {
        dateFormat = "MM-dd-yyyy"
    }
    return formatter.stringFromDate(date)
}

fun createHistoryDatabase(): HistoryDatabase {
    val driver = NativeSqliteDriver(HistoryDatabase.Schema, "history.db")
    return HistoryDatabase(
        driver = driver,
        HistoryAdapter = History.Adapter(guessesAdapter = listOfStringsAdapter)
    )
}

fun GameViewModel.getState(scope: ScopeProvider): NullableFlowWrapper<GameViewModel.State?> {
    return NullableFlowWrapper(
        flow = state,
        scope = scope
    )
}
