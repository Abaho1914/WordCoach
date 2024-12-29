package com.abahoabbott.wordcoach.nav

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.abahoabbott.wordcoach.features.game.GameScreen
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme

@Composable
fun WordCoachApp(){
    WordCoachTheme {
        Surface {
            GameScreen()
        }
    }
}