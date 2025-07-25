package com.abahoabbott.wordcoach.nav

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.abahoabbott.wordcoach.features.game.nav.AppDestination
import com.abahoabbott.wordcoach.features.game.nav.gameDestination
import com.abahoabbott.wordcoach.features.game.nav.navigateToGameDestination
import com.abahoabbott.wordcoach.features.results.nav.navigateToResultsScreen
import com.abahoabbott.wordcoach.features.results.nav.resultsDestination
import com.abahoabbott.wordcoach.features.splash.nav.splashDestination
import com.abahoabbott.wordcoach.features.wod.nav.navigateToWoDScreen
import com.abahoabbott.wordcoach.features.wod.nav.wordOfTheDayDestination
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme
import timber.log.Timber

@Composable
fun WordCoachApp() {
    WordCoachTheme(
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {

            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = AppDestination.Splash.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                splashDestination {
                    navController.navigateToWoDScreen()

                }

                wordOfTheDayDestination {
                    navController.navigateToGameDestination()
                }

                gameDestination(
                    onNavigateToResultsScreen = { gameResult ->
                        Timber.tag("Sunflower:Nav").i(gameResult.toString())
                        navController.navigateToResultsScreen(
                            gameResult
                        )
                    }
                )
                resultsDestination(
                    navigateToGameScreen = { navController.navigateToGameDestination() }
                )

            }
        }
    }
}