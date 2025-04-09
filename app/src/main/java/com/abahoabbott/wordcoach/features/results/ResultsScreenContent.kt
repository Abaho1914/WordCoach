package com.abahoabbott.wordcoach.features.results

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.common.modifyExplanationQuestion
import com.abahoabbott.wordcoach.common.scoreBoardMessage
import com.abahoabbott.wordcoach.features.game.GameResult
import com.abahoabbott.wordcoach.features.game.new.GameManager
import com.abahoabbott.wordcoach.features.game.new.WordCoachQuestion
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme


@Composable
fun ResultsScreen(
    gameResult: GameResult,
    onShareResults: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val resultsUiState = ResultsUiState(
        score = gameResult.totalScore,
        correctAnswers = gameResult.passedQuestions,
        attemptedQuestions = gameResult.attemptedQuestions
    )

    LaunchedEffect(true) {
        viewModel.loadResults(resultsUiState)
    }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    ResultsScreenContent(
        resultsUiState = uiState,
        onShareResults = onShareResults,
        onPlayAgain = onPlayAgain
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ResultsScreenContent(
    resultsUiState: ResultsUiState = ResultsUiState(),
    onShareResults: () -> Unit = {},
    onPlayAgain: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = { onShareResults() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_share_24),
                            contentDescription = stringResource(R.string.share_results),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        ResultsScreenLayout(
            resultsUiState = resultsUiState,
            modifier = Modifier.padding(contentPadding),
            onPlayAgain = { onPlayAgain() }
        )
    }

}

@Composable
private fun ResultsScreenLayout(
    resultsUiState: ResultsUiState,
    modifier: Modifier = Modifier,
    onPlayAgain: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        ScoreBoard(
            resultsUiState = resultsUiState,
            correctAnswers = resultsUiState.correctAnswers
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Explanations")
            Button(
                shape = MaterialTheme.shapes.large,
                onClick = {
                    onPlayAgain()
                }
            ) {
                Text("Next round")
            }
        }


        LazyColumn {
            items(resultsUiState.attemptedQuestions) { question ->
                ExplanationCard(
                    question = GameManager.findQuestionById(question.questionId),
                    answerState = question.answerState
                )
            }

        }

    }

}


@Composable
private fun ExplanationCard(
    answerState: AnswerState = AnswerState.CORRECT,
    question: WordCoachQuestion = WordCoachQuestion()
) {

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 16.dp, vertical = 4.dp)

    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                ExplanationIcon(answerState)
                Text(
                    modifyExplanationQuestion(GameManager.generateQuestion(question)),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    modifier = Modifier.padding(),
                    onClick = {
                        isExpanded = !isExpanded
                    },
                ) {
                    Icon(
                        painter = painterResource(
                            if (isExpanded) {
                                R.drawable.baseline_keyboard_arrow_up_24
                            } else {
                                R.drawable.baseline_keyboard_arrow_down_24
                            }
                        ),
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            end = 16.dp,
                            bottom = 16.dp
                        )
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.DarkGray
                    )
                    ExpandedCardContent(
                        question= question
                    )
                }

            }
        }
    }
}


@Composable
private fun ScoreBoard(
    correctAnswers: Int,
    resultsUiState: ResultsUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        //  horizontalArrangement = Arrangement.SpaceAround
    ) {
        CustomProgressBar(
            correctAnswers = correctAnswers,
            totalQuestions = resultsUiState.totalQuestions
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center,

            ) {
            val animatedScore by animateFloatAsState(targetValue = resultsUiState.score.toFloat())
            Text(
                "Score : ${animatedScore.toInt()}",
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                scoreBoardMessage(correctAnswers),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                //  modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }


}

@PreviewLightDark
@Composable
private fun ResultsScreenPreview() {
    WordCoachTheme {
        Surface {

            val resultsUiState = ResultsUiState(
                score = 1200,
                correctAnswers = 3,
                totalQuestions = 5,
                attemptedQuestions = listOf(
                    QuestionResult(
                        22,
                        AnswerState.CORRECT
                    ),
                    QuestionResult(
                        22,
                        AnswerState.CORRECT
                    ),
                    QuestionResult(
                        21,
                        AnswerState.WRONG
                    ),
                    QuestionResult(
                        22,
                        AnswerState.UNANSWERED
                    ),
                    QuestionResult(
                        23,
                        AnswerState.CORRECT
                    )
                )
            )
            ResultsScreenContent(
                resultsUiState = resultsUiState

            )
        }
    }
}