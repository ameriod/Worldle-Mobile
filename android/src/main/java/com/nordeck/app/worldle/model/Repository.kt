package com.nordeck.app.worldle.model

import androidx.annotation.VisibleForTesting
import com.nordeck.app.worldle.common.HistoryDatabase
import com.nordeck.app.worldle.common.db.History
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber

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

        @VisibleForTesting
        fun getCountriesInternal(fileLoader: FileLoader): List<Country> {
            val assets = fileLoader.getAllFilesInPath("")
                .map {
                    // In tests this returns the files, on the device this just returns the directories?
                    it.replace("/vector.svg", "")
                }
            return Json.decodeFromStream<List<Country>>(
                stream = fileLoader.getInputStreamFromFile("countries.json")
            ).filter { country ->
                // Only use countries with images
                if (assets.firstOrNull { it.equals(country.code, true) } != null) {
                    true
                } else {
                    Timber.d("ERROR: ${country.code}")
                    false
                }
            }.sortedBy { it.name }
        }
    }
}