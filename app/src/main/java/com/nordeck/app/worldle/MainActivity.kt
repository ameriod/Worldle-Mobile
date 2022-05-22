package com.nordeck.app.worldle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
                        else -> GameInProgressView(state, viewModel)
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
fun GameInProgressView(state: GameViewModel.State, viewModel: GameViewModel) {
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
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
                                val parts = suggestion.name.split(Regex("((?=${suggestion.name})|(?<=${suggestion.name}))"))
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
    }
}
