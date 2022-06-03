package com.nordeck.app.worldle

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nordeck.app.worldle.db.History
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class GameViewModel(
    context: Context,
    private val repository: Repository = Repository(context = context)
) : ViewModel() {

    private val stateLiveData = MutableLiveData<State>()
    val state: LiveData<State> = stateLiveData

    private val countries: List<Country>
    private val date: String

    init {
        countries = repository.getCountries()
        // Use the date as the seed with the number of countries.
        date = ZonedDateTime.now(ZoneId.of("US/Eastern"))
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val restoredGame = repository.getSavedGame(date)?.let {
            restoreGame(it)
        }
        updateState(
            if (restoredGame == null) {
                val seed = "$date+${countries.size}".hashCode()
                createNewGame(countries.random(Random(seed)))
            } else {
                restoredGame
            }
        )
    }

    private fun restoreGame(history: History): State? {
        val countryToGuess = countries.firstOrNull { countryToGuess ->
            countryToGuess.code.equals(history.country, true)
        }
        return countryToGuess?.let {
            val guesses = history.guesses.mapNotNull { guess ->
                countries.firstOrNull { country ->
                    guess.equals(country.code, true)
                }
            }.map { suggestion ->
                suggestion.toGuess(countryToGuess = countryToGuess)
            }
            State(
                guessInput = "",
                suggestions = emptyList(),
                countryToGuess = it,
                guesses = guesses
            )
        }
    }


    private fun createNewGame(countryToGuess: Country): State {
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
            val newState = currentState.copy(
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
            updateState(newState)
        }
    }

    fun onGuessDone() {
        stateLiveData.value?.suggestions?.firstOrNull()?.let {
            onSuggestionSelected(it)
        }
    }

    fun onSuggestionSelected(suggestion: Country) {
        stateLiveData.value?.let { currentState ->
            val newState = currentState.copy(
                guessInput = "",
                suggestions = emptyList(),
                guesses = currentState.guesses.toMutableList().apply {
                    val newGuess = suggestion.toGuess(countryToGuess = currentState.countryToGuess)
                    add(newGuess)
                }
            )
            updateState(newState)
        }
    }

    private fun Country.toGuess(countryToGuess: Country): Guess {
        val distanceFrom = this.getDistanceTo(countryToGuess)
        return Guess(
            country = this,
            distanceFromMeters = distanceFrom,
            proximityPercent = computeProximityPercent(distanceFrom),
            direction = this.getDirectionTo(countryToGuess)
        )
    }

    private fun updateState(newState: State) {
        repository.saveOrUpdate(newState.toHistory(date))
        stateLiveData.value = newState
    }

    data class State(
        val guessInput: String,
        val suggestions: List<Country>,
        val countryToGuess: Country,
        val guesses: List<Guess>
    ) {

        val hasLostGame: Boolean = guesses.size >= MAX_GUESSES

        val hasWonGame: Boolean = guesses.any { it.country == countryToGuess }

        fun toHistory(date: String): History =
            History(
                date = date,
                country = countryToGuess.code,
                guesses = guesses.map { it.country.code }
            )
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
