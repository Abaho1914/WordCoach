package com.abahoabbott.wordcoach.features.wod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abahoabbott.wordcoach.R
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Displays a row of social action buttons, such as sharing and saving the word.
 */
@Composable
internal fun SocialActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                painter = painterResource(R.drawable.baseline_share_24),
                contentDescription = "Share",
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                painter = painterResource(R.drawable.bookmark_add_24px),
                contentDescription = "Save",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * A button that navigates to the word game screen.
 *
 * @param onNavigateToGame Callback triggered when the button is clicked.
 */
@Composable
internal fun PlayGameButton(onNavigateToGame: () -> Unit) {
    Button(
        onClick = onNavigateToGame,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.stadia_controller_24px),
            contentDescription = null
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Play Word Game",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/**
 * Displays the header section of the Word of the Day screen, including the title and date.
 *
 * @param dateFormatter The formatter used to format the current date.
 */
@Composable
internal fun HeaderSection(dateFormatter: SimpleDateFormat) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Word of the Day",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = dateFormatter.format(Date()),
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

/**
 * Displays the pronunciation section of the Word of the Day screen, including the part of speech,
 * pronunciation text, and a button to listen to the pronunciation.
 *
 * @param wordOfTheDay The data object containing the word's pronunciation and part of speech.
 */
@Composable
internal fun ColumnScope.PronunciationSection(wordOfTheDay: WordOfTheDay) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.align(Alignment.Start)
    ) {
        Text(
            text = wordOfTheDay.definition.partOfSpeech,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Normal
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = wordOfTheDay.pronunciation,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_volume_up_24),
                contentDescription = "Listen",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Displays the definition and usage section of the Word of the Day screen. This section is expandable
 * and includes the word's definition, part of speech, and example sentences.
 *
 * @param isExpanded Whether the section is currently expanded.
 * @param wordOfTheDay The data object containing the word's definition and examples.
 */
@Composable
internal fun DefinitionAndUsageSection(
    wordOfTheDay: WordOfTheDay
) {
    var isExpanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Definition & Usage",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    painter = painterResource(
                        if (isExpanded) R.drawable.baseline_keyboard_arrow_up_24
                        else R.drawable.baseline_keyboard_arrow_down_24
                    ),
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = wordOfTheDay.definition.partOfSpeech,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = wordOfTheDay.definition.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Examples:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    wordOfTheDay.examples.forEach { example ->
                        Text(
                            text = "• $example",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}