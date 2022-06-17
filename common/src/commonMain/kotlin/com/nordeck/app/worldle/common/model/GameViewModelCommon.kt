package com.nordeck.app.worldle.common.model

import com.nordeck.app.worldle.common.GeoMath
import com.nordeck.app.worldle.common.db.History
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModelCommon(
    private val repository: Repository,
    private val scope: CoroutineScope,
    private val date: String
) : GameViewModel {

    private val stateMutableFlow = MutableSharedFlow<GameViewModel.State?>(1)
    override val state: Flow<GameViewModel.State?> = stateMutableFlow.asSharedFlow()
    private var currentState: GameViewModel.State? = null

    private fun getRandom(countries: List<Country>): Random {
        val seed = "$date+${countries.size}".hashCode()
        return Random(seed)
    }

    init {
        scope.launch {
            val countries = repository.getCountries()
            val state = repository.getSavedGame(date)
                ?.let {
                    restoreGame(countries, it)
                } ?: createNewGame(countries, countries.random(getRandom(countries)))
            updateState(state)
        }
    }

    override fun resetGame() {
        currentState?.allCountries?.run {
            val seed = "$date+${this.size}".hashCode()
            updateState(createNewGame(this, this.random(Random(seed))))
        }
    }

    private fun restoreGame(countries: List<Country>, history: History): GameViewModel.State? {
        val countryToGuess = countries.getByCode(history.country)
        return countryToGuess?.let {
            val guesses = history.guesses.mapNotNull { guess ->
                // The guess is the country code
                countries.getByCode(guess)
            }.map { suggestion ->
                suggestion.toGuess(countryToGuess = countryToGuess)
            }
            GameViewModel.State(
                allCountries = countries,
                guessInput = "",
                suggestions = emptyList(),
                countryToGuess = it,
                guesses = guesses
            )
        }
    }

    private fun createNewGame(
        allCountries: List<Country>,
        countryToGuess: Country
    ): GameViewModel.State {
        return GameViewModel.State(
            allCountries = allCountries,
            guessInput = "",
            suggestions = emptyList(),
            countryToGuess = countryToGuess,
            guesses = emptyList()
        )
    }

    override fun onGuessUpdated(input: String) {
        currentState?.let { currentState ->
            val newState = currentState.copy(
                guessInput = input,
                suggestions = if (input.isEmpty()) {
                    emptyList()
                } else {
                    currentState.allCountries.filter { country ->
                        country.name.contains(input, true) &&
                            // Do not show an already selected country.
                            !currentState.guesses.any { it.country == country }
                    }
                }
            )
            updateState(newState)
        }
    }

    override fun onGuessDone() {
        currentState?.suggestions?.firstOrNull()?.let {
            onSuggestionSelected(it)
        }
    }

    override fun onSuggestionSelected(suggestion: Country) {
        currentState?.let { currentState ->
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
            proximityPercent = GeoMath.computeProximityPercent(distanceFrom),
            direction = this.getDirectionTo(countryToGuess)
        )
    }

    private fun updateState(newState: GameViewModel.State) {
        scope.launch {
            repository.saveOrUpdate(newState.toHistory(date))
            currentState = newState
            stateMutableFlow.tryEmit(newState)
        }
    }
}
