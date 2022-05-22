package com.nordeck.app.worldle

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.math.floor


const val MAX_GUESSES = 5
const val MAX_DISTANCE_ON_EARTH = 20000000

class GameViewModel(context: Context) : ViewModel() {

    private val stateChannel = MutableLiveData<State>()
    val state: LiveData<State> = stateChannel

    private val countries: List<Country>

    init {
        countries =
            Json.decodeFromStream<List<Country>>(stream = context.assets.open("countries.json"))
                .sortedBy { it.name }

        stateChannel.value = State(
            guessInput = "",
            suggestions = emptyList(),
            countryToGuess = countries.first(),
            guesses = emptyList()
        )
    }

    private fun computeProximityPercent(distance: Int): Int {
        val proximity = (MAX_DISTANCE_ON_EARTH - distance).toDouble()
        return floor((proximity / MAX_DISTANCE_ON_EARTH) * 100).toInt()
    }

    fun onGuessUpdated(input: String) {
        stateChannel.value?.let { currentState ->
            stateChannel.value = currentState.copy(
                guessInput = input,
                suggestions = if (input.isEmpty()) emptyList() else countries.filter {
                    it.name.contains(input, true)
                }
            )
        }
    }

    fun onGuessDone() {
        stateChannel.value?.suggestions?.firstOrNull()?.let {
            onSuggestionSelected(it)
        }
    }

    fun onSuggestionSelected(suggestion: Country) {
        stateChannel.value?.let { currentState ->
            stateChannel.value = currentState.copy(
                guessInput = "",
                suggestions = emptyList(),
                guesses = currentState.guesses.toMutableList().apply {
                    val newGuess = Guess(
                        country = suggestion,
                        // TODO
                        distanceFrom = 0,
                        // TODO
                        proximityPercent = 0,
                        // TODO
                        direction = Guess.Direction.N
                    )
                    add(newGuess)
                }
            )
        }
    }

    data class State(
        val guessInput: String,
        val suggestions: List<Country>,
        val countryToGuess: Country,
        val guesses: List<Guess>
    ) {

        val isGameOver: Boolean = guesses.size > MAX_GUESSES
    }
}

@Serializable
data class Country(
    @SerialName("code")
    val code: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("name")
    val name: String
) {
    val vectorAsset = "file:///android_asset/${code.lowercase()}/vector.svg"
}

data class Guess(
    val country: Country,
    val distanceFrom: Int,
    val proximityPercent: Int,
    val direction: Direction
) {

    enum class Direction(
        val start: Double,
        val end: Double
    ) {
        CORRECT(
            start = 0.0,
            end = 0.0
        ),
        N(
            start = 348.75,
            end = 11.25,
        ),
        NNE(
            start = 11.25,
            end = 33.75,
        ),
        NE(
            start = 33.75,
            end = 56.25,
        ),
        ENE(
            start = 56.25,
            end = 78.75,
        ),
        E(
            start = 78.75,
            end = 101.25,
        ),
        ESE(
            start = 101.25,
            end = 123.75,
        ),
        SE(
            start = 123.75,
            end = 146.25,
        ),
        SSE(
            start = 146.25,
            end = 168.75,
        ),
        S(
            start = 168.75,
            end = 191.25,
        ),
        SSW(
            start = 191.25,
            end = 213.75,
        ),
        SW(
            start = 213.75,
            end = 236.25,
        ),
        WSW(
            start = 236.25,
            end = 258.75,
        ),
        W(
            start = 258.75,
            end = 281.25,
        ),
        WNW(
            start = 281.25,
            end = 303.75,
        ),
        NW(
            start = 303.75,
            end = 326.25,
        ),
        NNW(
            start = 326.25,
            end = 348.75,
        ),
    }
}
