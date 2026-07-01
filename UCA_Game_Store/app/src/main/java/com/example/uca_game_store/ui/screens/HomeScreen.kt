package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.uca_game_store.data.model.Game
import com.example.uca_game_store.data.model.CartItem
import com.example.uca_game_store.ui.viewmodels.HomeViewModel
import com.example.uca_game_store.ui.viewmodels.AuthViewModel
import com.example.uca_game_store.ui.viewmodels.CarritoViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onGameClick: (Int) -> Unit
) {
    val games by viewModel.games.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    var selectedItem by remember { mutableIntStateOf(0) }
    var juegoSeleccionado by remember { mutableStateOf<Game?>(null) }

    Scaffold(
        bottomBar = {
            UcaBottomNavigation(selectedItem, isAdmin) { index ->
                if (index == 5) { authViewModel.signOut(); onNavigateToLogin() }
                else { selectedItem = index }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                0 -> HomeContent(games) { juegoSeleccionado = it }
                2 -> VenderScreen()
            }
        }
    }

    if (juegoSeleccionado != null) {
        FavoritosBottomSheet(
            game = juegoSeleccionado!!,
            onDismiss = { juegoSeleccionado = null },
            onComprarClick = {
                val item = CartItem(
                    id = juegoSeleccionado!!.id.toString(),
                    title = juegoSeleccionado!!.title,
                    price = juegoSeleccionado!!.price.replace("$", "").toDoubleOrNull() ?: 0.0
                )
                carritoViewModel.agregarAlCarrito(item)
                juegoSeleccionado = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosBottomSheet(game: Game, onDismiss: () -> Unit, onComprarClick: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFF1E1E24)) {
        Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
            Text(game.title, color = Color(0xFFFF8C00), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(game.description, color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onComprarClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))) {
                Text("Añadir al carrito - ${game.price}", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun HomeContent(games: List<Game>, onGameClick: (Game) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FeaturedCarousel(games.take(3), onGameClick)
        GameCatalog(games, onGameClick)
    }
}

@Composable
fun FeaturedCarousel(games: List<Game>, onGameClick: (Game) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(games, key = { it.id }) { game ->
            Card(modifier = Modifier.width(300.dp).height(180.dp).clickable { onGameClick(game) }, shape = RoundedCornerShape(16.dp)) {
                AsyncImage(model = game.imageUrl, contentDescription = game.title, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun GameCatalog(games: List<Game>, onGameClick: (Game) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(games, key = { it.id }) { game ->
            GameCard(game, onGameClick)
        }
    }
}

@Composable
fun GameCard(game: Game, onGameClick: (Game) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onGameClick(game) }, shape = RoundedCornerShape(12.dp)) {
        Column {
            AsyncImage(model = game.imageUrl, contentDescription = game.title, modifier = Modifier.fillMaxWidth().height(120.dp))
            Text(game.title, color = Color.White, modifier = Modifier.padding(8.dp))
        }
    }
}