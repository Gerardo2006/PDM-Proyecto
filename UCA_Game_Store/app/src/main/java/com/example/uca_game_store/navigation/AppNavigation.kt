package com.example.uca_game_store.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uca_game_store.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                },
                onGameClick = { gameId -> navController.navigate("detalle/$gameId") }
            )
        }

        composable(
            route = "detalle/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
            DetalleJuegoScreen(gameId = gameId, navController = navController)
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { /* Añade tu ruta de registro aquí */ }
            )
        }
    }
}