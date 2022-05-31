package com.nordeck.app.worldle

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber

class GameViewModel(context: Context) : ViewModel() {

    private val stateLiveData = MutableLiveData<State>()
    val state: LiveData<State> = stateLiveData

    private val countries: List<Country>

    init {
        val assets = context.assets.list("")!!
        countries = Json.decodeFromStream<List<Country>>(
            stream = context.assets.open("countries.json")
        ).filter { country ->
            // Only use countries with images
            assets.firstOrNull { it.equals(country.code, true) } != null
        }.sortedBy { it.name }

        stateLiveData.value = createNewGame()
    }

    private fun createNewGame(countryToGuess: Country = countries.random()): State {
        Timber.d("New game: $countryToGuess")
        return State(
            guessInput = "",
            suggestions = emptyList(),
            countryToGuess = countryToGuess,
            guesses = emptyList()
        )
    }

    fun onGuessUpdated(input: String) {
        stateLiveData.value?.let { currentState ->
            stateLiveData.value = currentState.copy(
                guessInput = input,
                suggestions = if (input.isEmpty()) {
                    emptyList()
                } else {
                    countries.filter { country ->
                        country.name.contains(input, true) &&
                                // Do not show an already selected country.
                                !currentState.guesses.any { it.country == country }
                    }
                }
            )
        }
    }

    fun onGuessDone() {
        stateLiveData.value?.suggestions?.firstOrNull()?.let {
            onSuggestionSelected(it)
        }
    }

    fun onSuggestionSelected(suggestion: Country) {
        stateLiveData.value?.let { currentState ->
            val distanceFrom = suggestion.getDistanceTo(currentState.countryToGuess)

            val newState = currentState.copy(
                guessInput = "",
                suggestions = emptyList(),
                guesses = currentState.guesses.toMutableList().apply {
                    val newGuess = Guess(
                        country = suggestion,
                        distanceFromMeters = distanceFrom,
                        proximityPercent = computeProximityPercent(distanceFrom),
                        direction = suggestion.getDirectionTo(currentState.countryToGuess)
                    )
                    add(newGuess)
                }
            )
            stateLiveData.value = newState
        }
    }

    data class State(
        val guessInput: String,
        val suggestions: List<Country>,
        val countryToGuess: Country,
        val guesses: List<Guess>
    ) {

        val hasLostGame: Boolean = guesses.size >= MAX_GUESSES

        val hasWonGame: Boolean = guesses.any { it.country == countryToGuess }
    }

    companion object {

        private const val MAX_GUESSES = 5
        private const val MAX_DISTANCE_ON_EARTH = 20000000

        private fun computeProximityPercent(distance: Int): Int {
            val proximity = (MAX_DISTANCE_ON_EARTH - distance).toDouble()
            return ((proximity / MAX_DISTANCE_ON_EARTH) * 100).toInt()
        }
    }
}
