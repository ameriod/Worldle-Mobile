package com.nordeck.app.worldle

import android.content.Context
import com.nordeck.app.worldle.db.History
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class Repository(
    private val context: Context,
    private val historyDatabase: HistoryDatabase = (context as AppApplication).historyDatabase
) {

    fun getCountries(): List<Country> {
        val assets = context.assets.list("")!!
        return Json.decodeFromStream<List<Country>>(
            stream = context.assets.open("countries.json")
        ).filter { country ->
            // Only use countries with images
            assets.firstOrNull { it.equals(country.code, true) } != null
        }.sortedBy { it.name }
    }

    fun getSavedGame(date: String): History? =
        historyDatabase.historyQueries.selectByDate(date).executeAsOneOrNull()

    fun saveOrUpdate(history: History) {
        historyDatabase.historyQueries.insertOrUpdate(
            guesses = history.guesses,
            country = history.country,
            date = history.date
        )
    }
}