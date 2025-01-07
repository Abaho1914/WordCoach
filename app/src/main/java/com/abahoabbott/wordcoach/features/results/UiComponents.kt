package com.abahoabbott.wordcoach.features.results

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abahoabbott.wordcoach.R
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

@Composable
fun CustomProgressBar(
    correctAnswers: Int = 4,
    totalQuestions: Int = 5
) {

    val myProgress = remember(correctAnswers, totalQuestions) {
        correctAnswers.toFloat() / totalQuestions
    }
    val animatedProgress by animateFloatAsState(
        targetValue = myProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )


    Box(
        modifier = Modifier.padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(120.dp),
            progress = {
                animatedProgress
            },
            color = Color(0xFF4CAF50),
            strokeWidth = 8.dp
        )
        Text(
          //  modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            text = "$correctAnswers/$totalQuestions"
        )
    }


}

@Composable
fun ExplanationIcon(answerState: AnswerState) {
    val icon = when (answerState) {
        AnswerState.CORRECT -> Icon(
            painter = painterResource(R.drawable.round_check_24),
            contentDescription = null,
            tint = correctAnswerColor

        )

        AnswerState.WRONG -> Icon(
            painter = painterResource(R.drawable.baseline_clear_24),
            contentDescription = null,
            tint = wrongAnswerColor
        )

        AnswerState.UNANSWERED -> Icon(
            painter = painterResource(R.drawable.baseline_question_mark_24),
            contentDescription = null,
            tint = Color.Gray
        )
    }
    return icon
}