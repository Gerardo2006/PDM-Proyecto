package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.uca_game_store.data.model.SolicitudVenta
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
    onGameClick: (Int) -> Unit = {}
) {
    // Escucha de estados persistentes y roles dinámicos al montar o actualizar la pantalla
    LaunchedEffect(Unit) {
        viewModel.checkUserRole()
        viewModel.escucharJuegosAprobados()
        viewModel.escucharFavoritos()
    }

    val games by viewModel.juegos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    var selectedItem by remember { mutableIntStateOf(0) }
    var juegoSeleccionado by remember { mutableStateOf<SolicitudVenta?>(null) }

    val labels = remember(isAdmin) {
        val list = mutableListOf("Inicio", "Favorito", "Vender", "Carrito")
        if (isAdmin) list.add("Admin")
        list.add("Salir")
        list
    }

    Scaffold(
        bottomBar = {
            UcaBottomNavigation(selectedItem, isAdmin) { index ->
                val clickedLabel = labels.getOrNull(index)
                if (clickedLabel == "Salir") {
                    authViewModel.signOut()
                    viewModel.resetState()
                    onNavigateToLogin()
                } else {
                    selectedItem = index
                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val currentLabel = labels.getOrNull(selectedItem)
            when (currentLabel) {
                "Inicio" -> Column {
                    // Barra de búsqueda integrada
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        placeholder = { Text("Buscar un videojuego...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF8C00),
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    HomeContent(games) { juegoSeleccionado = it }
                }
                "Favorito" -> {
                    // CORREGIDO: Integración real de los videojuegos guardados en favoritos de solicitudes_venta
                    val favoritosUsuario by viewModel.juegosFavoritosObjetos.collectAsState()

                    if (favoritosUsuario.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No tienes videojuegos en favoritos", color = Color.Gray, fontSize = 16.sp)
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text(
                                text = "Mis Favoritos",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // Desplegamos el grid de juegos usando únicamente los favoritos cruzados del usuario
                            GameCatalog(games = favoritosUsuario) { juegoSeleccionado = it }
                        }
                    }
                }
                "Vender" -> VenderScreen()
                "Carrito" -> CarritoScreen(carritoViewModel)
                "Admin" -> AdminAprobacionesScreen()
            }
        }
    }

    if (juegoSeleccionado != null) {
        DetailAndActionBottomSheet(
            game = juegoSeleccionado!!,
            onDismiss = { juegoSeleccionado = null },
            onComprarClick = {
                // CORREGIDO: Sanitización profunda de strings de precio para evitar fallos de parseo numérico
                val precioFormateado = juegoSeleccionado!!.precio
                    .replace("$", "")
                    .replace(" ", "")
                    .trim()
                val item = CartItem(
                    id = juegoSeleccionado!!.id,
                    title = juegoSeleccionado!!.nombre,
                    price = precioFormateado.toDoubleOrNull() ?: 0.0
                )
                carritoViewModel.agregarAlCarrito(item)
                juegoSeleccionado = null
            },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAndActionBottomSheet(
    game: SolicitudVenta,
    onDismiss: () -> Unit,
    onComprarClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    val listaReseñas by viewModel.reseñasJuegoSeleccionado.collectAsState()
    val favoritosIds by viewModel.favoritosIds.collectAsState()
    val esFavorito = favoritosIds.contains(game.id)

    LaunchedEffect(game.id) {
        viewModel.cargarReseñasDelJuego(game.id)
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFF1E1E24)) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Fila Superior: Información del juego + Control de Favoritos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.nombre,
                    color = Color(0xFFFF8C00),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.toggleFavorito(game.id) }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(game.descripcion, color = Color.White, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onComprarClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))) {
                Text("Añadir al carrito - $${game.precio}", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            // SECCIÓN: AÑADIR NUEVA RESEÑA A FIRESTORE
            Text("Calificar este juego:", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrella $i",
                        tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(26.dp).clickable { rating = i }
                    )
                }
            }

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                placeholder = { Text("Escribe una reseña o comentario...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.enviarReseña(game.id, rating, reviewText) {
                        rating = 0
                        reviewText = ""
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
            ) {
                Text("Publicar Reseña", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Opiniones de la comunidad:", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // SECCIÓN: LISTADO DE OPINIONES DE LA COMUNIDAD EN VIVO
            Box(modifier = Modifier.weight(1f, fill = false).heightIn(max = 200.dp)) {
                if (listaReseñas.isEmpty()) {
                    Text("Nadie ha reseñado este juego aún. ¡Sé el primero!", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listaReseñas) { res ->
                            val autor = res["usuario"] as? String ?: "Anónimo"
                            val estrellas = (res["calificacion"] as? Long)?.toInt() ?: 0
                            val comentario = res["comentario"] as? String ?: ""

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2A2A32), shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(autor, color = Color(0xFFFF8C00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Row {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (i <= estrellas) Color(0xFFFFD700) else Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comentario, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(games: List<SolicitudVenta>, onGameClick: (SolicitudVenta) -> Unit) {
    if (games.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay videojuegos disponibles", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Destacados", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            FeaturedCarousel(games.take(3), onGameClick)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Catálogo General", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            GameCatalog(games, onGameClick)
        }
    }
}

@Composable
fun FeaturedCarousel(games: List<SolicitudVenta>, onGameClick: (SolicitudVenta) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(games, key = { it.id }) { game ->
            Card(
                modifier = Modifier.width(300.dp).height(160.dp).clickable { onGameClick(game) },
                shape = RoundedCornerShape(16.dp)
            ) {
                if (game.fotoUri.isNotEmpty()) {
                    AsyncImage(model = game.fotoUri, contentDescription = game.nombre, modifier = Modifier.fillMaxSize())
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                }
            }
        }
    }
}

@Composable
fun GameCatalog(games: List<SolicitudVenta>, onGameClick: (SolicitudVenta) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games, key = { it.id }) { game ->
            GameCard(game, onGameClick)
        }
    }
}

@Composable
fun GameCard(game: SolicitudVenta, onGameClick: (SolicitudVenta) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onGameClick(game) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
    ) {
        Column {
            if (game.fotoUri.isNotEmpty()) {
                AsyncImage(model = game.fotoUri, contentDescription = game.nombre, modifier = Modifier.fillMaxWidth().height(120.dp))
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.DarkGray))
            }
            Text(game.nombre, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            Text("$${game.precio}", color = Color(0xFF00E676), fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
        }
    }
}