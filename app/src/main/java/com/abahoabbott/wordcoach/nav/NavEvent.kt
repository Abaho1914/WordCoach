package com.abahoabbott.wordcoach.nav

import com.abahoabbott.wordcoach.features.game.GameResult

sealed class NavEvent {
    data class NavigateToResultsScreen(val gameResult: GameResult) : NavEvent()
    data class ShowNavError(val message: String) : NavEvent()
}