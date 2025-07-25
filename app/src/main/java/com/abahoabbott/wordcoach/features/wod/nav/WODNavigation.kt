package com.abahoabbott.wordcoach.features.wod.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abahoabbott.wordcoach.features.game.nav.AppDestination
import com.abahoabbott.wordcoach.features.wod.WordOfTheDayScreen

fun NavGraphBuilder.wordOfTheDayDestination(
    navigateToGameScreen: () -> Unit
) {
    composable(
        route = AppDestination.WoD.route
    ) {
        WordOfTheDayScreen(
            onNavigateToGame = {
                navigateToGameScreen()
            }
        )
    }
}

/**
 * Navigates the user to the "Word of the Day" (WoD) screen.
 */
fun NavController.navigateToWoDScreen() {
    navigate(AppDestination.WoD.route) {
        popUpTo(AppDestination.Splash.route) {
            inclusive = true
        }
    }
}