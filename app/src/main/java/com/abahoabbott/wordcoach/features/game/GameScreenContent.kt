package com.abahoabbott.wordcoach.features.game

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.common.WordCoachOptionsButton
import com.abahoabbott.wordcoach.data.GameUiState
import com.abahoabbott.wordcoach.nav.NavEvent
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    navigateToResultsScreen: (GameResult) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NavEvent.NavigateToResultsScreen -> {
                    navigateToResultsScreen(
                        event.gameResult
                    )
                }

                is NavEvent.ShowNavError -> {
                    Log.i("Sunflower:Game Screen", event.message)
                }
            }
        }
    }



    GameScreenContent(
        uiState = uiState,
        onOptionSelected = { id, isCorrect -> viewModel.onOptionSelected(id, isCorrect) },
        onSkip = { viewModel.onSkip() },
        progress = uiState.gameProgress.progressFraction
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreenContent(
    modifier: Modifier = Modifier,
    uiState: GameUiState = GameUiState(),
    onOptionSelected: (optionId: Int, isCorrect: Boolean) -> Unit,
    onSkip: () -> Unit,
    progress: Float = 0.0f
) {
    Scaffold(
        topBar = {
            GameTopBar(uiState.scoreState.score)
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameLayout(
                question = uiState.questionState.currentQuestion,
                selectedOptionId = uiState.questionState.selectedOptionId,
                onOptionSelected = onOptionSelected
            )
            GameProgressBar(
                progress = progress,
                onSkip = onSkip
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GameTopBar(score: Int) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(stringResource(R.string.score_label))
                    Text(
                        text = score.toString(),
                        color = Color(0xFF4CAF50)
                    )
                }
            }


        },

        )
}


@Composable
private fun GameProgressBar(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
    progress: Float,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier.weight(1f),
        )
        TextButton(
            onClick = {
                onSkip()
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.skip_button))
        }
    }

}

@Composable
private fun GameLayout(
    modifier: Modifier = Modifier,
    question: WordQuestion,
    selectedOptionId: Int?,
    onOptionSelected: (optionId: Int, isCorrect: Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val options = question.options
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = question.question,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Text(
                    text = stringResource(R.string.or),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            WordCoachOptionsButton(
                answerOption = option.text,
                isAnswerCorrect = option.isCorrect,
                isSelected = selectedOptionId == option.optionId,
                onClickButton = {
                    onOptionSelected(option.optionId, option.isCorrect)
                }
            )
        }

    }
}

@PreviewLightDark
@Composable
private fun GameScreenPreview() {
    WordCoachTheme {
        Surface {
            GameScreenContent(
                uiState = GameUiState(
                    questionState = QuestionState(
                        currentQuestion = allQuestions.random()
                    ),
                    scoreState = ScoreState(
                        score = 360,
                    ),
                    gameProgress = GameProgress(
                        answeredQuestions = 3,
                        totalQuestions = 5

                    )
                ),
                onOptionSelected = { _, _ -> },
                onSkip = {}
            )

        }
    }
}