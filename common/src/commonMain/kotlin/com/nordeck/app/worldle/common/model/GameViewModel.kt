package com.nordeck.app.worldle.common.model

import com.nordeck.app.worldle.common.db.History
import kotlinx.coroutines.flow.Flow

interface GameViewModel {
    val state: Flow<State?>

    fun resetGame()

    fun onGuessUpdated(input: String)

    fun onGuessDone()

    fun onSuggestionSelected(suggestion: Country)

    data class State(
        val allCountries: List<Country>,
        val guessInput: String,
        val suggestions: List<Country>,
        val countryToGuess: Country,
        val guesses: List<Guess>
    ) {

        val hasLostGame: Boolean = guesses.size >= MAX_GUESSES

        val hasWonGame: Boolean = guesses.any { it.country == countryToGuess }

        val sharePercent: Int
            get() = if (hasWonGame) {
                if (guesses.size == 1) {
                    100
                } else {
                    // We want the % left over
                    ((MAX_GUESSES - guesses.size) / MAX_GUESSES.toDouble() * 100).toInt()
                }
            } else {
                0
            }

        fun toHistory(date: String): History =
            History(
                date = date,
                country = countryToGuess.code,
                guesses = guesses.map { it.country.code }
            )

        companion object {
            private const val MAX_GUESSES = 5
        }
    }
}
