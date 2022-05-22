package com.nordeck.app.worldle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.nordeck.app.worldle.ui.theme.WorldleAndoirdTheme

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

@Composable
fun GameView(state: GameViewModel.State, viewModel: GameViewModel) {
    Column {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(state.countryToGuess.vectorAsset)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            // That would be cheating...
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .background(Color.Red)
        )

        if (state.guesses.isNotEmpty()) {
            PreviousGuesses(
                modifier = Modifier.fillMaxWidth(),
                state = state
            )
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
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

        if (state.suggestions.isNotEmpty()) {
            SearchSuggestions(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun PreviousGuesses(
    modifier: Modifier,
    state: GameViewModel.State
) {
    Column(modifier = modifier) {
        state.guesses.forEach { guess ->
            // TODO make pretty
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {

                Text(text = guess.country.name)

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
    }

}

@Composable
private fun SearchSuggestions(
    modifier: Modifier,
    state: GameViewModel.State,
    viewModel: GameViewModel
) {
    LazyColumn(
        modifier = modifier
    ) {
        state.suggestions.forEach { suggestion ->
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onSuggestionSelected(suggestion)
                        },
                    text = buildAnnotatedString {
                        // TODO figure out how to split and keep the delimiter
                        val parts =
                            suggestion.name.split(Regex("((?=${suggestion.name})|(?<=${suggestion.name}))"))
                        parts.forEach {
                            if (it.equals(state.guessInput, true)) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.primary,
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
                )
            }
        }
    }
}
