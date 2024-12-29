package com.abahoabbott.wordcoach.features.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abahoabbott.wordcoach.common.WordCoachOptionsButton
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import com.abahoabbott.wordcoach.ui.theme.correctAnswerColor


data class GameUiState(
    val question: String = "",
    val options: List<GameOption> = emptyList(),
    val score: Int = 0,
    val progress: Float = 0f,
    val isLoading: Boolean = false,
    val selectedOptionId: Int? = null
)

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    GameScreenContent(
        modifier = modifier,
        uiState = uiState,
        onOptionSelected = viewModel::onOptionSelected,
        onSkip = viewModel::onSkip
    )
}



@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    onOptionSelected: (Int) -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameScreenAppBar(score = uiState.score)
        GameLayout(
            question = uiState.question,
            options = uiState.options,
            selectedOptionId = uiState.selectedOptionId,
            onOptionSelected = onOptionSelected
        )
        GameProgressBar(
            progress = uiState.progress,
            onSkip = onSkip
        )
    }

}

class GameOption(
    val id: Int,
    val text: String,
    val isCorrect: Boolean
)

@Composable
private fun GameProgressBar(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
    progress: Float,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier.weight(1f)
        )
        TextButton(
            onClick = {
                onSkip()
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("SKIP")
        }
    }

}

@Composable
fun GameLayout(
    modifier: Modifier = Modifier,
    question: String,
    options: List<GameOption>,
    selectedOptionId: Int?,
    onOptionSelected: (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            WordCoachOptionsButton(
                answerOption = option.text,
                isAnswerCorrect = option.isCorrect,
                isSelected = selectedOptionId == option.id,
                onClickButton = { onOptionSelected(option.id) }
            )
        }

    }
}

@Composable
fun GameScreenAppBar(
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            "WORD COACH",
            style = MaterialTheme.typography.titleLarge
        )
        GameScore(score = score)
    }
}

@Composable
private fun GameScore(
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            "Score:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = score.toString(),
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    color = correctAnswerColor
                )
        )
    }

}

@PreviewLightDark
@Composable
private fun GameScreenPreview() {
    WordCoachTheme {
        Surface {
            GameScreenContent(
                uiState = GameUiState(
                    question = "Which word is similar to move?",
                    options = listOf(
                        GameOption(1, "Travel", true),
                        GameOption(2, "Stay", false)
                    ),
                    score = 120,
                    progress = 0.7f
                ),
                onOptionSelected = {},
                onSkip = {}
            )

        }
    }
}