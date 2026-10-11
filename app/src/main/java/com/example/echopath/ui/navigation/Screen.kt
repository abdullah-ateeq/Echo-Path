package com.example.echopath.ui.navigation

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object Game : Screen()
    object Result : Screen()
    object Settings : Screen()
    object About : Screen()
}
