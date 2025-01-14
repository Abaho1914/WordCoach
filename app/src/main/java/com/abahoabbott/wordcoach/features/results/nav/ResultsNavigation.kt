package com.abahoabbott.wordcoach.features.results.nav

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.features.game.GameResult
import com.abahoabbott.wordcoach.features.game.nav.AppDestination
import com.abahoabbott.wordcoach.features.results.ResultsScreen
import kotlinx.serialization.json.Json


fun NavController.navigateToGameDestination() {
    navigate(AppDestination.Game.route)

}
private fun shareResults(context: Context,subject: String,summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT,summary)

    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_results)
        )
    )
}

fun NavGraphBuilder.resultsDestination(
    navigateToGameScreen: () -> Unit
) {
    composable(
        route = "${AppDestination.Results.route}/{gameResult}",
        arguments = listOf(
            navArgument("gameResult") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val gameResultsJson = navBackStackEntry.arguments?.getString("gameResult")
        val gameResults = gameResultsJson?.let { string ->
            Json.decodeFromString<GameResult>(Uri.decode(string))
        } ?: GameResult(
            resultId = 0,
            totalScore = 0,
            passedQuestions = 0
        )

        val context = LocalContext.current
        val summary = """
            I just scored ${gameResults.totalScore} points on Word Coach.
            See if you can beat my score!
            https://abahobbott.com
        """.trimIndent()

        ResultsScreen(
            gameResult = gameResults,
            onShareResults = {
                shareResults(context,summary = summary, subject = "Word Coach")
                //navigateToGameScreen()
            }
            ,
            onPlayAgain = {navigateToGameScreen()}
        )

    }
}