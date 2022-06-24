package com.nordeck.app.worldle.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordeck.app.worldle.common.model.Country
import com.nordeck.app.worldle.common.model.GameViewModel
import com.nordeck.app.worldle.common.model.GameViewModelCommon
import com.nordeck.app.worldle.common.model.Repository
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GameViewModelAndroid(
    private val repository: Repository,
) : ViewModel(), GameViewModel {

    private val common = GameViewModelCommon(
        repository = repository,
        scope = viewModelScope,
        date = ZonedDateTime.now(ZoneId.of("US/Eastern"))
            .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
    )

    override val state: Flow<GameViewModel.State?> = common.state

    override fun resetGame() {
        common.resetGame()
    }

    override fun onGuessUpdated(input: String) {
        common.onGuessUpdated(input)
    }

    override fun onGuessDone() {
        common.onGuessDone()
    }

    override fun onSuggestionSelected(suggestion: Country) {
        common.onSuggestionSelected(suggestion)
    }
}