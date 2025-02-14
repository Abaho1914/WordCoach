package com.abahoabbott.wordcoach.features.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashedScreenFinished: () -> Unit = {}
) {
    val isVisible = remember { mutableStateOf(false) }

    // Scale animation for logo
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(true) {
        delay(300) // Short delay before animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        isVisible.value = true
        delay(2500) // Total splash duration
        onSplashedScreenFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo with Animation
            Image(
                painter = painterResource(id = R.drawable.word_coach_logo),
                contentDescription = "Word Coach Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated App Name
            AnimatedVisibility(visible = isVisible.value) {
                Text(
                    text = "Word Coach",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@PreviewLightDark
@Composable
private fun SplashScreenPreview() {

    WordCoachTheme {
        Surface {
            SplashScreen()
        }
    }

}