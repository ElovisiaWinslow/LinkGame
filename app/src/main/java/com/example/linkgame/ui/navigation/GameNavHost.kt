package com.example.linkgame.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.linkgame.game.model.GameMode
import com.example.linkgame.ui.screen.GameScreen
import com.example.linkgame.ui.screen.LeaderboardScreen
import com.example.linkgame.ui.screen.StartScreen
import java.util.UUID

@Composable
fun GameNavHost(onExit: () -> Unit) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Start) }
    var pendingGameMode by remember { mutableStateOf<GameMode?>(null) }
    var gameKey by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        Screen.Start -> {
            StartScreen(
                onStartChallenge = {
                    pendingGameMode = GameMode.Challenge
                    gameKey = UUID.randomUUID().toString()
                    currentScreen = Screen.Game
                },
                onStartEndless = { difficulty ->
                    pendingGameMode = GameMode.Endless(difficulty)
                    gameKey = UUID.randomUUID().toString()
                    currentScreen = Screen.Game
                },
                onShowLeaderboard = { currentScreen = Screen.Leaderboard },
                onExit = onExit
            )
        }
        Screen.Game -> {
            if (pendingGameMode != null && gameKey != null) {
                GameScreen(
                    mode = pendingGameMode!!,
                    onReturnToStart = {
                        currentScreen = Screen.Start
                        pendingGameMode = null
                        gameKey = null
                    },
                    key = gameKey!!
                )
            }
        }
        Screen.Leaderboard -> {
            LeaderboardScreen(onBack = { currentScreen = Screen.Start })
        }
    }
}

sealed class Screen {
    object Start : Screen()
    object Game : Screen()
    object Leaderboard : Screen()
}