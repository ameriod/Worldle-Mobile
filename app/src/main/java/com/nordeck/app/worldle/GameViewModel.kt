package com.nordeck.app.worldle

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.util.*
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

private fun Locale.isMetric(): Boolean {
    return when (country.uppercase(this)) {
        "US", "LR", "MM" -> false
        else -> true
    }
}

private const val COMPASS_SEGMENT: Float = 22.5f

data class Guess(
    val country: Country,
    /**
     * Distance in meters
     */
    private val distanceFrom: Int,
    val proximityPercent: Int,
    val direction: Direction
) {

    fun getDistanceFrom(locale: Locale = Locale.getDefault()): String {
        return if (locale.isMetric()) {
            "${(distanceFrom / 1000)} km"
        } else {
            "${(distanceFrom * 0.621371).toInt()} mi"
        }
    }

    // TODO add name res id for content description
    enum class Direction(
        val start: Double,
        val end: Double,
        val rotation: Float = 0.0f
    ) {
        CORRECT(
            start = 0.0,
            end = 0.0,
        ),
        N(
            start = 348.75,
            end = 11.25,
        ),
        NNE(
            start = 11.25,
            end = 33.75,
            rotation = COMPASS_SEGMENT
        ),
        NE(
            start = 33.75,
            end = 56.25,
            rotation = COMPASS_SEGMENT.times(2)
        ),
        ENE(
            start = 56.25,
            end = 78.75,
            rotation = COMPASS_SEGMENT.times(3)
        ),
        E(
            start = 78.75,
            end = 101.25,
            rotation = COMPASS_SEGMENT.times(4)
        ),
        ESE(
            start = 101.25,
            end = 123.75,
            rotation = COMPASS_SEGMENT.times(5)
        ),
        SE(
            start = 123.75,
            end = 146.25,
            rotation = COMPASS_SEGMENT.times(6)
        ),
        SSE(
            start = 146.25,
            end = 168.75,
            rotation = COMPASS_SEGMENT.times(7)
        ),
        S(
            start = 168.75,
            end = 191.25,
            rotation = COMPASS_SEGMENT.times(8)
        ),
        SSW(
            start = 191.25,
            end = 213.75,
            rotation = COMPASS_SEGMENT.times(9)
        ),
        SW(
            start = 213.75,
            end = 236.25,
            rotation = COMPASS_SEGMENT.times(10)
        ),
        WSW(
            start = 236.25,
            end = 258.75,
            rotation = COMPASS_SEGMENT.times(11)
        ),
        W(
            start = 258.75,
            end = 281.25,
            rotation = COMPASS_SEGMENT.times(12)
        ),
        WNW(
            start = 281.25,
            end = 303.75,
            rotation = COMPASS_SEGMENT.times(13)
        ),
        NW(
            start = 303.75,
            end = 326.25,
            rotation = COMPASS_SEGMENT.times(14)
        ),
        NNW(
            start = 326.25,
            end = 348.75,
            rotation = COMPASS_SEGMENT.times(15)
        );

        val drawableResId: Int
            @DrawableRes get() =
                when (this) {
                    CORRECT -> R.drawable.ic_correct
                    else -> R.drawable.ic_direction
                }
    }
}
