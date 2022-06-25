package com.nordeck.app.worldle.common

import com.nordeck.app.worldle.common.db.History
import com.nordeck.app.worldle.common.model.FileLoader
import com.nordeck.app.worldle.common.model.Repository
import com.nordeck.app.worldle.common.model.listOfStringsAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single<FileLoader> {
        AssetsFileLoader(context = androidContext())
    }

    single<HistoryDatabase> {
        val driver: SqlDriver = AndroidSqliteDriver(
            HistoryDatabase.Schema,
            androidContext(),
            "history.db"
        )
        HistoryDatabase.invoke(
            driver = driver,
            HistoryAdapter = History.Adapter(listOfStringsAdapter)
        )
    }

    single<Repository> {
        Repository(
            fileLoader = get(),
            historyDatabase = get()
        )
    }

    viewModel {
        GameViewModelAndroid(
            repository = get()
        )
    }
}