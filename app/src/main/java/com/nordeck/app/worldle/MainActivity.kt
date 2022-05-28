package com.nordeck.app.worldle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nordeck.app.worldle.ui.theme.WorldleAndoirdTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = GameViewModel(applicationContext)
        setContent {
            WorldleAndoirdTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    when (val state = viewModel.state.observeAsState().value) {
                        null -> GameLoadingView()
                        // TODO game over view
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(state.countryToGuess.vectorAsset)
                    .build(),
                // That would be cheating...
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(COUNTRY_HEIGHT)
                    .fillMaxWidth()
                    .background(Color.Blue)
            )
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

        item {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent {
                        if (it.hasFocus && state.guesses.isNotEmpty()) {
                            coroutineScope.launch {
                                // Scroll down
                                listState.animateScrollToItem(0, COUNTRY_HEIGHT.value.toInt())
                            }
                        }
                    },
                value = state.guessInput,
                onValueChange = {
                    viewModel.onGuessUpdated(it)
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
                    text = suggestion.highlightGuess(
                        input = state.guessInput,
                        highlightColor = MaterialTheme.colors.primary
                    )
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

        Text(text = "${guess.proximityPercent}%")

        Image(
            modifier = Modifier
                .size(24.dp)
                .rotate(guess.direction.rotation),
            painter = painterResource(id = guess.direction.drawableResId),
            contentDescription = guess.direction.name,
            colorFilter = ColorFilter.tint(Color.Blue)
        )
    }
}

private fun Country.highlightGuess(input: String, highlightColor: Color): AnnotatedString =
    buildAnnotatedString {
        // TODO this is not working
        val parts = name.split(
            Regex(
                pattern = "((?=${name})|(?<=${name}))"
            )
        )
        parts.forEach {
            if (it.equals(input, true)) {
                withStyle(
                    style = SpanStyle(
                        color = highlightColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(it)
                }
            } else {
                append(it)
            }
        }
    }