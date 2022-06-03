package com.nordeck.app.worldle

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.nordeck.app.worldle.db.History
import com.nordeck.app.worldle.db.listOfStringsAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import timber.log.Timber

class AppApplication : Application() {

    val historyDatabase: HistoryDatabase by lazy(LazyThreadSafetyMode.NONE) {
        val driver: SqlDriver = AndroidSqliteDriver(HistoryDatabase.Schema, this, "history.db")
        HistoryDatabase.invoke(
            driver = driver,
            HistoryAdapter = History.Adapter(listOfStringsAdapter)
        )
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        Coil.setImageLoader(ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build())
    }
}