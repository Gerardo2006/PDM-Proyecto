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
import com.example.uca_game_store.ui.screens.Login
import com.example.uca_game_store.ui.screens.Register
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
            Login(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        // Pantalla de Registro
        composable("register") {
            Register(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        // Pantalla Principal
        composable("home") {
            HomeScreen(onNavigateToLogin = { navController.navigate("login") { popUpTo("home") { inclusive = true } } })
        }
    }
}