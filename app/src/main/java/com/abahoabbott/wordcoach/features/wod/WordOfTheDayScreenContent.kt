package com.abahoabbott.wordcoach.features.wod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.features.wod.work.NewViewModel
import com.abahoabbott.wordcoach.network.data.Definition
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun WordOfTheDayScreen(
    onNavigateToGame: () -> Unit,
    viewModel: NewViewModel = hiltViewModel()
) {
    val state by viewModel.wordOfTheDayState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = state) {
            is WordOfTheDayState.Loading -> LoadingScreen()
            is WordOfTheDayState.Success -> WordOfTheDayScreenContent(
                wordOfTheDay = currentState.wordOfTheDay,
                onNavigateToGame = onNavigateToGame
            )

            is WordOfTheDayState.Error -> ErrorScreen(
                message = currentState.message,
                onRetry = { }
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = CenterHorizontally) {

            CircularProgressIndicator()
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Fetching Today's Special Word",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Did you know? We add new words every day!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun WordOfTheDayScreenContent(
    wordOfTheDay: WordOfTheDay,
    onNavigateToGame: () -> Unit = {}
) {

    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
            .safeDrawingPadding()
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                initialAlpha = 0.3f
            ) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                ) {
                    // Header
                    HeaderSection(dateFormatter)
                    Spacer(Modifier.height(32.dp))
                    // Word Section
                    Text(
                        text = wordOfTheDay.word.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Pronunciation
                    PronunciationSection(wordOfTheDay)

                    Spacer(Modifier.height(32.dp))

                    DefinitionAndUsageSection(wordOfTheDay)

                    Spacer(Modifier.height(24.dp))

                    // Game Button
                    PlayGameButton(onNavigateToGame)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Social Actions
        SocialActionsRow()

    }

    WordnikCreditSection(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
    )
}
}



// Preview with sample data
@PreviewLightDark
@Composable
private fun WordOfTheDayScreenPreview() {
    WordCoachTheme {
        Surface {
            WordOfTheDayScreenContent(
                wordOfTheDay = WordOfTheDay(
                    word = "stultify",
                    pronunciation = "[ stuhl-tvh-fahy ]",
                    definition = Definition(
                        "word",
                        "sample definition",
                        "",
                        "verb"
                    ) ,
                    examples = listOf(
                        "The repetitive tasks stultified the team's creativity.",
                        "Bureaucratic red tape can stultify innovation."
                    )
                ),
                onNavigateToGame = {}
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.error_24px),
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Unable to Load Word",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),

        ) {
            Text("Retry", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingScreenPreview() {
    WordCoachTheme {
        Surface {

            LoadingScreen()
        }


    }
}

@PreviewLightDark
@Composable
private fun ErrorScreenPreview() {
    WordCoachTheme {
        Surface {

            ErrorScreen(
                message = "Sample error message"
            ) { }
        }


    }
}