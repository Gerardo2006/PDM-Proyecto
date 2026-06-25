package com.example.uca_game_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uca_game_store.ui.screens.CarritoScreen
import com.example.uca_game_store.ui.screens.FavoritosScreen
import com.example.uca_game_store.ui.theme.UCA_Game_StoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UCA_Game_StoreTheme {
                // Surface es el contenedor principal que usa el color de fondo de tu tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestra función de prueba
                    PantallaDePrueba()
                }
            }
        }
    }
}

@Composable
fun PantallaDePrueba() {
    // Esto controla la navegación entre pantallas
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Barra superior temporal con botones para probar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("carrito") }) {
                Text("Ver Carrito")
            }
            Button(onClick = { navController.navigate("favoritos") }) {
                Text("Ver Favoritos")
            }
        }

        HorizontalDivider()

        // --- El espacio donde se cargarán las pantallas ---
        NavHost(
            navController = navController,
            startDestination = "carrito", // Empieza mostrando el carrito por defecto
            modifier = Modifier.weight(1f)
        ) {
            composable("carrito") {
                CarritoScreen()
            }
            composable("favoritos") {
                FavoritosScreen()
            }
        }
    }
}