package com.nordeck.app.worldle

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.nordeck.app.worldle.common.AssetsFileLoader
import com.nordeck.app.worldle.common.HistoryDatabase
import com.nordeck.app.worldle.common.db.History
import com.nordeck.app.worldle.common.model.FileLoader
import com.nordeck.app.worldle.common.model.Repository
import com.nordeck.app.worldle.common.model.listOfStringsAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import timber.log.Timber

class AppApplication : Application() {

    private val historyDatabase: HistoryDatabase by lazy(LazyThreadSafetyMode.NONE) {
        val driver: SqlDriver = AndroidSqliteDriver(HistoryDatabase.Schema, this, "history.db")
        HistoryDatabase.invoke(
            driver = driver,
            HistoryAdapter = History.Adapter(listOfStringsAdapter)
        )
    }

    private val fileLoader: FileLoader by lazy(LazyThreadSafetyMode.NONE) {
        AssetsFileLoader(
            context = applicationContext
        )
    }

    val repository: Repository by lazy(LazyThreadSafetyMode.NONE) {
        Repository(
            fileLoader = fileLoader,
            historyDatabase = historyDatabase
        )
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
        )
    }
}
