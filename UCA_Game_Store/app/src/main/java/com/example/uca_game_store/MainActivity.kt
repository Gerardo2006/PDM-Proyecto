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
import com.example.uca_game_store.ui.screens.LoginScreen
import com.example.uca_game_store.ui.screens.Register
import com.example.uca_game_store.ui.viewmodels.AuthViewModel
import com.example.uca_game_store.ui.viewmodels.HomeViewModel
import com.example.uca_game_store.ui.viewmodels.CarritoViewModel

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

    // Inicializamos los ViewModels principales de la app
    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        // 1. Pantalla de Login
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro
        composable("register") {
            Register(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla Principal (Contenedor con el Bottom Navigation e Inicio integrado)
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                authViewModel = authViewModel,
                carritoViewModel = carritoViewModel,
                onNavigateToLogin = {
                    // Al presionar Salir, limpiamos el historial por completo por seguridad
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGameClick = { _ ->
                    // Listo por si a futuro necesitas navegar a una pantalla de detalles extendida
                }
            )
        }
    }
}