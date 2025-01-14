package com.abahoabbott.wordcoach.features.splash.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abahoabbott.wordcoach.features.game.nav.AppDestination
import com.abahoabbott.wordcoach.features.splash.SplashScreen

fun NavGraphBuilder.splashDestination(
    navigateToGameScreen:()-> Unit
){
    composable(
        route = AppDestination.Splash.route
    ){
        SplashScreen(
            onSplashedScreenFinished = {
                navigateToGameScreen()
            }
        )
    }
}