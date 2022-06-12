package com.nordeck.app.worldle.ui.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordeck.app.worldle.db.History
import com.nordeck.app.worldle.model.Country
import com.nordeck.app.worldle.model.Repository
import com.nordeck.app.worldle.model.getByCode
import com.nordeck.app.worldle.model.getDirectionTo
import com.nordeck.app.worldle.model.getDistanceTo
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class GameViewModel(
    private val repository: Repository
) : ViewModel() {

    private val stateLiveData = MutableLiveData<State>()
    val state: LiveData<State> = stateLiveData
    private val date: String by lazy(LazyThreadSafetyMode.NONE) {
        ZonedDateTime.now(ZoneId.of("US/Eastern"))
            .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
    }

    private fun getRandom(countries: List<Country>): Random {
        val seed = "$date+${countries.size}".hashCode()
        return Random(seed)
    }

    init {
        viewModelScope.launch {
            val countries = repository.getCountries()
            val state = repository.getSavedGame(date)
                ?.let {
                    restoreGame(countries, it)
                } ?: createNewGame(countries, countries.random(getRandom(countries)))
            updateState(state)
        }
    }

    fun resetGame() {
        state.value?.allCountries?.run {
            val seed = "$date+${this.size}".hashCode()
            updateState(createNewGame(this, this.random(Random(seed))))
        }
    }

    private fun restoreGame(countries: List<Country>, history: History): State? {
        val countryToGuess = countries.getByCode(history.country)
        return countryToGuess?.let {
            val guesses = history.guesses.mapNotNull { guess ->
                // The guess is the country code
                countries.getByCode(guess)
            }.map { suggestion ->
                suggestion.toGuess(countryToGuess = countryToGuess)
            }
            State(
                allCountries = countries,
                guessInput = "",
                suggestions = emptyList(),
                countryToGuess = it,
                guesses = guesses
            )
        }
    }

    private fun createNewGame(allCountries: List<Country>, countryToGuess: Country): State {
        Timber.d("New game: $countryToGuess")

        return State(
            allCountries = allCountries,
            guessInput = "",
            suggestions = emptyList(),
            countryToGuess = countryToGuess,
            guesses = emptyList()
        )
    }

    fun onGuessUpdated(input: String, highlightColor: Color) {
        stateLiveData.value?.let { currentState ->
            val newState = currentState.copy(
                guessInput = input,
                suggestions = if (input.isEmpty()) {
                    emptyList()
                } else {
                    currentState.allCountries.filter { country ->
                        country.name.contains(input, true) &&
                            // Do not show an already selected country.
                            !currentState.guesses.any { it.country == country }
                    }.map {
                        Suggestion(
                            country = it,
                            input = input,
                            highlightColor = highlightColor
                        )
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

    fun onSuggestionSelected(suggestion: Suggestion) {
        stateLiveData.value?.let { currentState ->
            val newState = currentState.copy(
                guessInput = "",
                suggestions = emptyList(),
                guesses = currentState.guesses.toMutableList().apply {
                    val newGuess =
                        suggestion.country.toGuess(countryToGuess = currentState.countryToGuess)
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
        viewModelScope.launch {
            repository.saveOrUpdate(newState.toHistory(date))
        }
        stateLiveData.value = newState
    }

    data class State(
        val allCountries: List<Country>,
        val guessInput: String,
        val suggestions: List<Suggestion>,
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
