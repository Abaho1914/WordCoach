package com.abahoabbott.wordcoach.features.game

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.common.WordCoachOptionsButton
import com.abahoabbott.wordcoach.data.GameUiState
import com.abahoabbott.wordcoach.features.game.new.GameManager
import com.abahoabbott.wordcoach.features.game.new.Option
import com.abahoabbott.wordcoach.features.game.new.WordCoachQuestion
import com.abahoabbott.wordcoach.nav.NavEvent
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    navigateToResultsScreen: (GameResult) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
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
    ) { innerPadding ->
        GameScreenContentColumn(
            modifier.fillMaxSize(),
            innerPadding,
            uiState,
            onOptionSelected,
            progress,
            onSkip
        )
    }
}

@Composable
private fun GameScreenContentColumn(
    modifier: Modifier,
    innerPadding: PaddingValues,
    uiState: GameUiState,
    onOptionSelected: (Int, Boolean) -> Unit,
    progress: Float,
    onSkip: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            GameLayout(
                currentQuestion = uiState.questionState.currentQuestion ,
                selectedOptionId = uiState.questionState.selectedOptionId,
                onOptionSelected = onOptionSelected
            )
        }
        GameProgressBar(
            progress = progress,
            onSkip = onSkip
        )
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

            ScoredDisplay(score)


        },

        )
}

@Composable
private fun ScoredDisplay(score: Int) {
    var previousScore by remember { mutableIntStateOf(score) }
    val scale = remember { Animatable(1f) }
    LaunchedEffect(score) {
        if (score != previousScore) {
            launch {
                scale.animateTo(
                    targetValue = 1.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                previousScore = score
            }
        } else {
            previousScore = score
        }

    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Text(stringResource(R.string.score_label))
            Text(
                text = score.toString(),
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .scale(
                        scale.value
                    )

            )
        }
    }
}


@Composable
private fun GameProgressBar(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
    progress: Float,
    animationDurationMillis: Int = 300
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = animationDurationMillis),
            label = "Progress Animation"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = modifier
                .weight(1f)
                .padding(end = 8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
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
    currentQuestion: WordCoachQuestion,
    selectedOptionId: Int?,
    onOptionSelected: (optionId: Int, isCorrect: Boolean) -> Unit,
) {
    val isAnswered = selectedOptionId != null
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val options = currentQuestion.options
        QuestionText(
            question = currentQuestion
        )
        OptionsList(options, selectedOptionId, isAnswered, onOptionSelected)

    }
}

@Composable
private fun OptionsList(
    options: Pair<Option, Option>,
    selectedOptionId: Int?,
    isAnswered: Boolean,
    onOptionSelected: (Int, Boolean) -> Unit
) {
    options.toList().forEachIndexed { index, option ->
        if (index > 0) {
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        WordCoachOptionsButton(
            answerOption = option.word,
            isAnswerCorrect = option.optionId == selectedOptionId,
            isSelected = selectedOptionId == option.optionId,
            onClickButton = {
                if (!isAnswered) {
                    onOptionSelected(option.optionId, option.optionId == selectedOptionId)
                }
            },
            isClickable = !isAnswered
        )
    }
}

@Composable
private fun QuestionText(question: WordCoachQuestion) {

    val myQuestion = GameManager.generateQuestion(question)

    val annotatedString = buildAnnotatedString {
        val words = myQuestion.split(" ")
        words.forEachIndexed { index, word ->
            val style = when {
                word.equals(
                    "opposite",
                    ignoreCase = true
                ) -> SpanStyle(fontStyle = FontStyle.Italic)

                word.equals("similar", ignoreCase = true) -> SpanStyle(fontStyle = FontStyle.Italic)
                else -> SpanStyle()
            }
            withStyle(style) {
                append(word)
            }
            if (index < words.size - 1) {
                append(" ")
            }
        }
    }
    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = annotatedString,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
}

@PreviewLightDark
@Composable
private fun GameScreenPreview() {
    WordCoachTheme {
        Surface {
            GameScreenContent(
                uiState = GameUiState(
                    questionState = QuestionState(
                        currentQuestion = GameManager.listOfQuestions.random()
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