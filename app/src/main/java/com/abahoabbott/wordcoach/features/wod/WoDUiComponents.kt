package com.abahoabbott.wordcoach.features.wod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.network.data.Definition
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Displays a row of social action buttons, such as sharing and saving the word.
 */
@Composable
internal fun SocialActionsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialActionButton(
            icon = painterResource(R.drawable.baseline_share_24),
            label = "Share",
            onClick = { /* Share functionality */ }
        )
        var isSaved by remember { mutableStateOf(false) }

        SocialActionButton(
            icon = painterResource(
                if (isSaved) R.drawable.bookmark_add_24px
                else R.drawable.bookmark_added_24px
            ),
            label = if (isSaved) "Saved" else "Save",
            onClick = { isSaved = !isSaved }
        )
    }
}


@Composable
private fun SocialActionButton(
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    shape = CircleShape
                )
                .scale(if (isPressed) 0.92f else 1f)
                .animateContentSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            isPressed = event.type == PointerEventType.Press
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.stadia_controller_24px),
            contentDescription = "Play Game Icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Play Word Game",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
internal fun WordnikCreditSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Powered by",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Wordnik API",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


/**
 * Displays the header section of the Word of the Day screen, including the title and date.
 *
 * @param dateFormatter The formatter used to format the current date.
 */
@Composable
internal fun HeaderSection(dateFormatter: SimpleDateFormat,
                           publishedDate: String) {
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
 * @param wordOfTheDay The data object containing the word's definition and examples.
 */
@Composable
internal fun DefinitionAndUsageSection(
    wordOfTheDay: WordOfTheDay
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showAllExamples by remember { mutableStateOf(false) }
    val visibleExamples = if (showAllExamples) wordOfTheDay.examples else wordOfTheDay.examples.take(2)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding( 16.dp)) {
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
                    contentDescription = "Expand or collapse section"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {

                    // Definition
                    Text(
                        text = "Definition:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
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

                    // Word Origin
                    Text(
                        text = "Word Origin:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = wordOfTheDay.note,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Examples
                    Text(
                        text = "Examples:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(8.dp))

                    // Display visible examples
                    visibleExamples.forEach { example ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "• ${example.text}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "— ${example.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(start = 16.dp))
                        }
                    }

                    // Show More/Show Less Button
                    if (wordOfTheDay.examples.size > 2) {
                        TextButton(
                            onClick = { showAllExamples = !showAllExamples },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(
                                text = if (showAllExamples) "Show Less" else "Show More Examples",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
internal fun WordOriginSectionPreview() {
    val wordOfTheDay = WordOfTheDay(
        apiId ="",
        word = "Ephemeral",
        pronunciation = "/ɪˈfem.ər.əl/",
        definition = Definition(
            source = "Merriam-Webster",
            text = "lasting a very short time",
            note = null,
            partOfSpeech = "adjective"
        ),
        examples = emptyList(),
        publishDate = "2023-10-26",
        note = "Origin: Late Middle English: from medieval Latin ephemeralis, from Greek ephēmeros ‘lasting only one day’ (from epi ‘on, for’ + hēmera ‘day’)."
    )
    CompositionLocalProvider {
        WordOriginSection(wordOfTheDay = wordOfTheDay)
    }
}

/**
 * Displays the origin section of the Word of the Day screen. This section is expandable and includes the
 * etymology or origin information of the word.
 *
 * @param WordOfTheDay The data object containing the word's origin or etymology note.
 */


/**
 * Displays the origin section of the Word of the Day screen. This section is expandable and includes the
 * etymology or origin information of the word.
 */
@Composable
internal fun WordOriginSection(wordOfTheDay: WordOfTheDay) {
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
                    text = "Word Origin",
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
                Text(
                    text = wordOfTheDay.note,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

