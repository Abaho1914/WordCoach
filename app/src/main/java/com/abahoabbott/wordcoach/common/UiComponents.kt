package com.abahoabbott.wordcoach.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import com.abahoabbott.wordcoach.ui.theme.correctAnswerColor
import com.abahoabbott.wordcoach.ui.theme.wrongAnswerColor


@Composable
fun WordCoachOptionsButton(
    modifier: Modifier = Modifier,
    isAnswerCorrect: Boolean,
    onClickButton: () -> Unit = {},
    isSelected: Boolean = false,
    answerOption: String
) {

    val buttonColors = when {
        isSelected ->
            ButtonDefaults.buttonColors(
                containerColor = if (isAnswerCorrect) correctAnswerColor else wrongAnswerColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

        else -> ButtonDefaults.textButtonColors()
    }
    OutlinedButton(
        onClick = onClickButton,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        colors = buttonColors,
    ) {

        ButtonContent(
            answerOption = answerOption,
            isAnswerCorrect = isAnswerCorrect,
            isSelected = isSelected
        )
    }


}

@Composable
fun ButtonContent(answerOption: String, isAnswerCorrect: Boolean, isSelected: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            WordCoachButtonIcon(
                isAnswerCorrect = isAnswerCorrect,
            )
        }
        Text(
            text = answerOption,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun WordCoachButtonIcon(
    isAnswerCorrect: Boolean,

    ) {

    Icon(
        painter = painterResource(
            if (isAnswerCorrect) {
                R.drawable.round_check_24
            } else {
                R.drawable.baseline_close_24
            }
        ),
        contentDescription = if (isAnswerCorrect) {
            "correct answer"
        } else {
            "wrong answer"
        }
    )

}

@PreviewLightDark
@Composable
private fun WordCoachTextButtonPreview() {
    WordCoachTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WordCoachOptionsButton(
                    answerOption = "Correct Answer",
                    isAnswerCorrect = true,
                    isSelected = true,
                    onClickButton = {}
                )

                WordCoachOptionsButton(
                    answerOption = "Wrong Answer",
                    isAnswerCorrect = false,
                    isSelected = true,
                    onClickButton = {}
                )

                WordCoachOptionsButton(
                    answerOption = "Unanswered Option",
                    isAnswerCorrect = false,
                    isSelected = false,
                    onClickButton = {}
                )

            }
        }
    }
}