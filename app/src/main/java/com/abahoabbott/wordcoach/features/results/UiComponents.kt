package com.abahoabbott.wordcoach.features.results

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.features.game.new.Category
import com.abahoabbott.wordcoach.features.game.new.GameManager
import com.abahoabbott.wordcoach.features.game.new.Option
import com.abahoabbott.wordcoach.features.game.new.WordCoachQuestion
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import com.abahoabbott.wordcoach.ui.theme.correctAnswerColor
import com.abahoabbott.wordcoach.ui.theme.wrongAnswerColor


@Preview
@Composable
private fun CustomProgressBarPreview() {
    WordCoachTheme {
        Surface {
            CustomProgressBar()
        }
    }
}

/**
 * A circular progress indicator that displays the user's score.
 *
 * @param correctAnswers Number of correctly answered questions
 * @param totalQuestions Total number of questions in the quiz
 * @param modifier Optional modifier for styling/layout
 * @param progressColor Color for the progress indicator (defaults to correct answer color)
 * @param textColor Color for the score text (defaults to onSurface)
 */
@Composable
fun CustomProgressBar(
    correctAnswers: Int = 4,
    totalQuestions: Int = 5,
    modifier: Modifier = Modifier,
    progressColor: Color = correctAnswerColor,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val progress = remember(correctAnswers, totalQuestions) {
        if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "progressAnimation"
    )

    Box(
        modifier = modifier
            .padding(vertical = 24.dp)
            .size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            color = progressColor,
            strokeWidth = 8.dp,
            modifier = Modifier.fillMaxSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$correctAnswers/$totalQuestions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

/**
 * Displays an icon indicating answer state (correct, wrong, or unanswered).
 *
 * @param answerState The state of the answer (CORRECT, WRONG, UNANSWERED)
 * @param modifier Optional modifier for styling/layout
 * @param size Size of the icon (default 24dp)
 */
@Composable
fun ExplanationIcon(
    answerState: AnswerState,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val (iconRes, tint) = when (answerState) {
        AnswerState.CORRECT -> R.drawable.round_check_24 to correctAnswerColor
        AnswerState.WRONG -> R.drawable.baseline_clear_24 to wrongAnswerColor
        AnswerState.UNANSWERED -> R.drawable.baseline_question_mark_24 to Color.Gray
    }

    Icon(
        painter = painterResource(iconRes),
        contentDescription = when (answerState) {
            AnswerState.CORRECT -> "Correct answer"
            AnswerState.WRONG -> "Wrong answer"
            AnswerState.UNANSWERED -> "Unanswered question"
        },
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Composable
fun ExpandedCardContent(
    modifier: Modifier = Modifier,
    question: WordCoachQuestion = WordCoachQuestion(),
    ) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        val text = if (question.category == Category.OPPOSITE) "opposite of" else "similar to"
        Text(
            text = "Word $text ${question.questionWord.word}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        val options = question.options.toList()
        // Options list
        options.forEach { option ->
            val isCorrect = option.optionId == 21

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isCorrect) R.drawable.round_check_24 else R.drawable.baseline_clear_24
                    ),
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = if (isCorrect) correctAnswerColor else wrongAnswerColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = option.word,
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (isCorrect) correctAnswerColor else wrongAnswerColor,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Learn why section
        Text(
            text = "LEARN WHY",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        //Definition of main word
        Text(
            text = "What is the definition of ${question.questionWord.word}?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        WordDefinitionSection(question.questionWord)


        // Correct answer explanation
        Text(
            text = "How is ${options.first().word} similar?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        WordDefinitionSection(options.first())

        // Incorrect options explanations

        Text(
            text = "How is ${options.last().word} different?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        WordDefinitionSection(options.last())
    }
}

@Composable
private fun WordDefinitionSection(
    option: Option
) {
    Text(
        text = option.definition,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
    )
    Text(
        text = "For example: ${option.example}",
        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Helper function to get examples (would come from your data source)
@Preview
@Composable
fun ExpandedCardContentPreview() {
    WordCoachTheme {
        Surface {
            ExpandedCardContent()
        }
    }
}