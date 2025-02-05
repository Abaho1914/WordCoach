package com.abahoabbott.wordcoach.features.wod.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abahoabbott.wordcoach.features.game.nav.AppDestination
import com.abahoabbott.wordcoach.features.wod.WordOfTheDayScreen
import com.abahoabbott.wordcoach.features.wod.WordOfTheDayScreenContent

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

fun NavController.navigateToWoDScreen() {
    navigate(AppDestination.WoD.route)
}