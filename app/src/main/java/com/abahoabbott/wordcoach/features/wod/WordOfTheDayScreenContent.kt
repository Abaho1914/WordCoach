package com.abahoabbott.wordcoach.features.wod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.network.data.ContentProvider
import com.abahoabbott.wordcoach.network.data.Definition
import com.abahoabbott.wordcoach.network.data.Example
import com.abahoabbott.wordcoach.network.data.WordOfTheDayResponse
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun WordOfTheDayScreen(
    onNavigateToGame: () -> Unit,
    viewModel: WordOfTheDayViewModel = hiltViewModel()
) {
    val state by viewModel.wordOfTheDayState.collectAsStateWithLifecycle()

    when (state) {
        is WordOfTheDayState.Error -> ErrorScreen(message = (state as WordOfTheDayState.Error).message)
        WordOfTheDayState.Loading

            -> LoadingScreen()

        is WordOfTheDayState.Success -> WordOfTheDayScreenContent(
            wordOfTheDay = (state as WordOfTheDayState.Success).wordOfTheDay,
            onNavigateToGame = onNavigateToGame
        )
    }


}

@Composable
fun LoadingScreen() {
    // State to control animation
    var progress by remember { mutableFloatStateOf(0.1f) }

    // Animate the progress value
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    // LaunchEffect to start the animation loop
    LaunchedEffect(Unit) {
        while (true) {
            progress = if (progress == 1f) 0.1f else 1f
            delay(1000)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
fun WordOfTheDayScreenContent(
    wordOfTheDay: WordOfTheDay,
    onNavigateToGame: () -> Unit = {}
) {


    var isExpanded by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        // Blue border container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                .padding(16.dp)

        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Word of the Day",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.baseline_volume_up_24),
                        contentDescription = "Listen to pronunciation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                TODO("Add pronunciation playback logic")
                            }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Word of the day
                Text(
                    text = wordOfTheDay.word,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),

                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Pronunciation
                Text(
                    text = wordOfTheDay.pronunciation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Meaning and examples (expandable)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Meaning & Examples",
                        // modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(
                            if (isExpanded) R.drawable.baseline_keyboard_arrow_up_24 else R.drawable.baseline_keyboard_arrow_down_24
                        ),
                        contentDescription = "Expand",
                        // modifier = Modifier.weight(1f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                AnimatedVisibility(isExpanded) {

                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Definition
                        Text(
                            text = wordOfTheDay.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Examples
                        wordOfTheDay.examples.forEach { example ->
                            Text(
                                text = "- $example",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Button to navigate to the game screen
        Button(
            onClick = onNavigateToGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Play Game")
        }
    }

}

@PreviewLightDark
@Composable
private fun WordOfTheDayScreenPreview() {
    WordCoachTheme {
        Surface {
            val wordOfTheDayResponse = WordOfTheDayResponse(
                _id = "6771e763f6dc0d7129949664",
                word = "balzarine",
                contentProvider = ContentProvider(
                    name = "wordnik",
                    id = 711
                ),
                definitions = listOf(
                    Definition(
                        source = "century",
                        text = "A light mixed fabric of cotton and wool for women's dresses, commonly used for summer gowns before the introduction of barege.",
                        note = null,
                        partOfSpeech = "noun"
                    )
                ),
                publishDate = "2025-01-31T03:00:00.000Z",
                examples = listOf(
                    Example(
                        url = "http://docsouth.unc.edu/fpn/felton/felton.html",
                        title = "Country life in Georgia in the days of my youth,",
                        text = "The underskirts were starched as stiff as possible, and I remember hearing a friend of my mother say she had on at that time eight petticoats beside the outside frock made of \"balzarine.\"",
                        id = 1040081023
                    ),
                    Example(
                        url = "http://www.gutenberg.org/dirs/2/8/5/0/28503/28503-8.txt",
                        title = "The Wit of Women Fourth Edition",
                        text = "On _condition_ that you are under twenty-five and that you will wear a rose (recognizably) in your bodice the first time you appear in Broadway with the hat and _balzarine_, we will pay the bills.",
                        id = 1151999862
                    )
                ),
                pdd = "2025-01-31",
                htmlExtra = null,
                note = "The word 'balzarine' comes from French."
            )

            val wordOfTheDay = wordOfTheDayResponse.toWordOfTheDay()
            WordOfTheDayScreenContent(
                wordOfTheDay = wordOfTheDay,
                onNavigateToGame = {}
            )
        }
    }
}