package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Importes de tu arquitectura
import com.example.uca_game_store.data.model.Game
import com.example.uca_game_store.ui.viewmodels.HomeViewModel
import com.example.uca_game_store.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    // Añadimos una acción por si el usuario presiona "Login" para cerrar sesión
    onNavigateToLogin: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val games by viewModel.games.collectAsState()

    var selectedItem by remember { mutableStateOf(0) }
    val isAdmin = true // Perfil de administrador simulado

    Scaffold(
        topBar = {
            if (selectedItem == 0) { // Barra de búsqueda solo en el inicio
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(UcaGradientStart, UcaGradientEnd)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "UCA Game Store",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Buscar juegos...", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }
        },
        bottomBar = {
            UcaBottomNavigation(selectedItem, isAdmin) { index ->
                if (index == 5) {
                    onNavigateToLogin() // Acción de cerrar sesión
                } else {
                    selectedItem = index
                }
            }
        },
        containerColor = UcaDarkBackground
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // Este bloque decide qué pantalla mostrar según el botón presionado abajo
            when (selectedItem) {
                0 -> HomeContent(games) // Pasa la lista de juegos
                1 -> FavoritosScreen() // Pantalla que creamos anteriormente
                2 -> Text("Pantalla de Vender en construcción...", color = Color.White, modifier = Modifier.align(Alignment.Center)) // Reemplazar por VenderScreen() cuando me la pases
                3 -> CarritoScreen() // Pantalla que creamos anteriormente
                4 -> if (isAdmin) AdminAprobacionesScreen() else HomeContent(games)
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Pantalla en desarrollo", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(games: List<Game>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Juegos Destacados",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        FeaturedCarousel(games.take(3))

        Text(
            text = "Productos en venta",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        GameCatalog(games)
    }
}

@Composable
fun FeaturedCarousel(games: List<Game>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(games, key = { it.id }) { game ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UcaCardBackground)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (game.imageRes != null) {
                        Image(
                            painter = painterResource(id = game.imageRes),
                            contentDescription = game.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(game.title, color = UcaOrange, fontWeight = FontWeight.Bold)
                        Text(game.price, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun GameCatalog(games: List<Game>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(games, key = { it.id }) { game ->
            GameCard(game)
        }
    }
}

@Composable
fun GameCard(game: Game) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = UcaCardBackground)
    ) {
        Column {
            if (game.imageRes != null) {
                Image(
                    painter = painterResource(id = game.imageRes),
                    contentDescription = game.title,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Gray)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = game.title,
                    color = UcaOrange,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = game.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = game.price,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UcaBottomNavigation(selectedItem: Int, isAdmin: Boolean, onItemSelected: (Int) -> Unit) {
    val items = mutableListOf(
        Triple("Inicio", Icons.Default.Home, 0),
        Triple("Favorito", Icons.Default.Favorite, 1),
        Triple("Vender", Icons.Default.AddCircle, 2),
        Triple("Carrito", Icons.Default.ShoppingCart, 3)
    )

    if (isAdmin) {
        items.add(Triple("Admin", Icons.Default.Settings, 4))
    }

    items.add(Triple("Salir", Icons.Default.Person, 5))

    NavigationBar(
        containerColor = UcaCardBackground,
        contentColor = Color.White
    ) {
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = UcaOrange,
                    selectedTextColor = UcaOrange,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}