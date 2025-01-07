package com.abahoabbott.wordcoach.features.results

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.common.modifyExplanationQuestion
import com.abahoabbott.wordcoach.common.scoreBoardMessage
import com.abahoabbott.wordcoach.features.game.GameResult
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme


@Composable
fun ResultsScreen(
    gameResult: GameResult,
    onShareResults: () -> Unit = {},
    onPlayAgain: () -> Unit = {}
) {
    val resultsUiState = ResultsUiState(
        score = gameResult.totalScore,
        correctAnswers = gameResult.passedQuestions,
        attemptedQuestions = gameResult.attemptedQuestions
    )



    ResultsScreenContent(
        resultsUiState = resultsUiState,
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
                    question = question.question,
                    answerState = question.answerState
                )
            }

        }

    }

}


@Composable
private fun ExplanationCard(
    answerState: AnswerState = AnswerState.CORRECT,
    question: String = ""
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            ExplanationIcon(answerState)
            Text(
                modifyExplanationQuestion(question),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                modifier = Modifier.padding(),
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                    contentDescription = null
                )
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
            Text(
                "Score : ${resultsUiState.score}",
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

            ResultsScreenContent(
            )
        }
    }
}