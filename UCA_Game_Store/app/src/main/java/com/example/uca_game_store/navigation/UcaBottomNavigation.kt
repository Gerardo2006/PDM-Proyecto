package com.example.uca_game_store.navigation

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.uca_game_store.ui.theme.UcaCardBackground
import com.example.uca_game_store.ui.theme.UcaOrange

@Composable
fun UcaBottomNavigation(selectedItem: Int, isAdmin: Boolean, onItemSelected: (Int) -> Unit) {
    // Definimos las listas dinámicamente
    val labels = mutableListOf("Inicio", "WishList", "Vender", "Carrito")
    val icons = mutableListOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.AddCircle, Icons.Default.ShoppingCart)

    if (isAdmin) {
        labels.add("Admin")
        icons.add(Icons.Default.Settings)
    }

    // Añadimos el botón de salir siempre al final
    labels.add("Salir")
    icons.add(Icons.AutoMirrored.Filled.ExitToApp)

    NavigationBar(containerColor = UcaCardBackground) {
        labels.forEachIndexed { index, label ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = label,
                        tint = if (selectedItem == index) UcaOrange else Color.White
                    )

                },
                label = { Text(label, color = Color.White) }
            )
        }
    }
}