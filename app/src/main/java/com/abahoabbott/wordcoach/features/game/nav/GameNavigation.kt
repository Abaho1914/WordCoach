package com.abahoabbott.wordcoach.features.game.nav

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abahoabbott.wordcoach.features.game.GameResult
import com.abahoabbott.wordcoach.features.game.GameScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NavController.navigateToResultsScreen(gameResult: GameResult) {
    navigate(route = AppDestination.Results.createRoute(gameResult = gameResult))
}


//Sealed class for type safe navigation
sealed class AppDestination(val route: String) {

    //Screens without parameters
    object Splash: AppDestination("Splash")
    object Game : AppDestination("Game")


    object Results: AppDestination("Results") {
        fun createRoute(gameResult: GameResult) =
            "Results/${Uri.encode(Json.encodeToString(gameResult))}"
    }

}


fun NavGraphBuilder.gameDestination(onNavigateToResultsScreen: (GameResult) -> Unit) {
    composable(
        route = AppDestination.Game.route
    ) {
        GameScreen(
            navigateToResultsScreen = {
                onNavigateToResultsScreen(it)
            }
        )
    }
}