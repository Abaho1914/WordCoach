package com.abahoabbott.wordcoach.features.results

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

@Composable
fun ExpandedCardContent(
    selectedOption: String? = "communicate"
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(),
    ) {
        val options = listOf("communicate", "comprise")

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selectedOption == option) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (selectedOption == option) correctAnswerColor else wrongAnswerColor
                )
                Text(
                    text = option,
                    color = if (selectedOption == option) correctAnswerColor else wrongAnswerColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LEARN WHY",
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Definition section
        Text(
            text = "What's the definition of interact?",
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Act in such a way as to have an effect on another; act reciprocally.",
            color = Color.Gray
        )
        Text(
            text = "For example: ",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "\"all the stages in the process interact\"",
            color = Color.Gray,
            fontStyle = FontStyle.Italic
        )
        // Similar word explanation
        Text(
            text = "How is communicate similar?",
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            text = "The word communicate means share or exchange information, news, or ideas.",
            color = Color.Gray
        )
        Text(
            text = "For example: ",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "\"the prisoner was forbidden to communicate with his family\"",
            color = Color.Gray,
            fontStyle = FontStyle.Italic
        )

        // Different word explanation
        Text(
            text = "How is comprise different?",
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            text = "The word comprise means consist of; be made up of.",
            color = Color.Gray
        )
        Text(
            text = "For example: ",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "\"the country comprises twenty states\"",
            color = Color.Gray,
            fontStyle = FontStyle.Italic
        )
    }

}

@Preview
@Composable
fun ExpandedCardContentPreview() {
    ExpandedCardContent()
}