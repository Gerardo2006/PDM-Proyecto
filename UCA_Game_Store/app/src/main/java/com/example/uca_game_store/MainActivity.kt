package com.example.uca_game_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uca_game_store.ui.screens.HomeScreen
import com.example.uca_game_store.ui.screens.LoginScreen // CORREGIDO: Ajusta según el nombre de tu archivo
import com.example.uca_game_store.ui.screens.Register // CORREGIDO: Ajusta según el nombre de tu archivo
import com.example.uca_game_store.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Pantalla de Login
        composable("login") {
            LoginScreen( // CORREGIDO el nombre de la función
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        // Pantalla de Registro
        composable("register") {
            Register( // CORREGIDO el nombre de la función
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        // Pantalla Principal
        composable("home") {
            HomeScreen(
                onNavigateToLogin = { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                onGameClick = { gameId -> /* Aquí puedes manejar la navegación a detalle si lo necesitas */ }
            )
        }
    }
}