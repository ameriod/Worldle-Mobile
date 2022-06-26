package com.nordeck.app.worldle.common.model

import com.nordeck.app.worldle.common.HistoryDatabase
import com.nordeck.app.worldle.common.db.History
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Repository(
    private val fileLoader: FileLoader,
    private val historyDatabase: HistoryDatabase
) {

    suspend fun getCountries(): List<Country> = coroutineScope {
        getCountriesInternal(fileLoader)
    }

    suspend fun getSavedGame(date: String): History? = coroutineScope {
        historyDatabase.historyQueries.selectByDate(date).executeAsOneOrNull()
    }

    suspend fun saveOrUpdate(history: History) = coroutineScope {
        historyDatabase.historyQueries.insertOrUpdate(
            guesses = history.guesses,
            country = history.country,
            date = history.date
        )
    }

    companion object {

        fun getCountriesInternal(fileLoader: FileLoader): List<Country> =
            Json.decodeFromString<List<Country>>(
                string = fileLoader.getStringFromFile("countries.json")
            ).sortedBy { it.name }
    }
}
