package com.nordeck.app.worldle.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nordeck.app.worldle.AppApplication
import com.nordeck.app.worldle.BuildConfig
import com.nordeck.app.worldle.ui.theme.WorldleAndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = GameViewModel(
            repository = (applicationContext as AppApplication).repository
        )

        setContent {
            WorldleAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    when (val state = viewModel.state.observeAsState().value) {
                        null -> GameLoadingView()
                        else -> GameView(state, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun GameLoadingView() {
    Text(text = "Loading")
}

private val COUNTRY_HEIGHT = 200.dp

@Composable
fun GameView(state: GameViewModel.State, viewModel: GameViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.countryToGuess.vectorAsset)
                        .build(),
                    // That would be cheating...
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(COUNTRY_HEIGHT)
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .padding(16.dp)
                )
            }
        }

        state.guesses.forEach { guess ->
            item {
                GuessView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    guess = guess
                )
            }
        }

        when {
            state.hasWonGame || state.hasLostGame -> {
                item {
                    if (state.hasLostGame) {
                        Snackbar(
                            Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.weight(1.0f),
                                    textAlign = TextAlign.Center,
                                    text = state.countryToGuess.name
                                )
                            }
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        onClick = {
                            // TODO share logic
                        }
                    ) {
                        Text(text = "Share")
                    }
                    if (BuildConfig.DEBUG) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            onClick = {
                                viewModel.resetGame()
                            }
                        ) {
                            Text("Reset")
                        }
                    }
                }
            }
            else -> {
                item {
                    val highlightColor = MaterialTheme.colors.primary
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent {
                                if (it.hasFocus && state.guesses.isNotEmpty()) {
                                    coroutineScope.launch {
                                        // Scroll down
                                        listState.animateScrollToItem(
                                            0,
                                            COUNTRY_HEIGHT.value.toInt()
                                        )
                                    }
                                }
                            },
                        value = state.guessInput,
                        placeholder = {
                            Text(text = "Country, territory...")
                        },
                        onValueChange = {
                            viewModel.onGuessUpdated(it, highlightColor)
                        },
                        keyboardActions = KeyboardActions {
                            viewModel.onGuessDone()
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = if (state.guessInput.isEmpty()) ImeAction.None else ImeAction.Done
                        ),
                        singleLine = true,
                    )
                }
            }
        }

        state.suggestions.forEach { suggestion ->
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onSuggestionSelected(suggestion)
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    text = suggestion.displayText
                )
            }
        }
    }
}

@Composable
private fun GuessView(modifier: Modifier = Modifier, guess: Guess) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {

        Text(
            modifier = Modifier.weight(1.0f),
            text = guess.country.name
        )

        Text(text = guess.getDistanceFrom())

        Image(
            modifier = Modifier
                .size(24.dp)
                .rotate(guess.direction.rotation),
            painter = painterResource(id = guess.direction.drawableResId),
            contentDescription = guess.direction.name,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )

        Text(text = "${guess.proximityPercent}%")
    }
}
