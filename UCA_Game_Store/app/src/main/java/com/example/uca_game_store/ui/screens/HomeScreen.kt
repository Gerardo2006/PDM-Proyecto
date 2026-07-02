package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.uca_game_store.data.model.SolicitudVenta
import com.example.uca_game_store.data.model.CartItem
import com.example.uca_game_store.navigation.UcaBottomNavigation
import com.example.uca_game_store.ui.viewmodels.HomeViewModel
import com.example.uca_game_store.ui.viewmodels.AuthViewModel
import com.example.uca_game_store.ui.viewmodels.CarritoViewModel
import com.example.uca_game_store.ui.theme.*

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
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    val labels = remember(isAdmin) {
        val list = mutableListOf("Inicio", "WishList", "Vender", "Carrito")
        if (isAdmin) list.add("Admin")
        list.add("Salir")
        list
    }

    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres salir?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        authViewModel.signOut()
                        viewModel.resetState()
                        onNavigateToLogin()
                    }
                ) {
                    Text("SÍ", color = UcaOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text("NO", color = Color.Gray)
                }
            },
            containerColor = UcaCardBackground,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }

    Scaffold(
        bottomBar = {
            UcaBottomNavigation(selectedItem, isAdmin) { index ->
                val clickedLabel = labels.getOrNull(index)
                if (clickedLabel == "Salir") {
                    mostrarDialogoCerrarSesion = true
                } else {
                    selectedItem = index
                }
            }
        },
        containerColor = UcaDarkBackground
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // CABECERA CON GRADIENTE (Como en la imagen de referencia)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(UcaGradientStart, UcaGradientMid, UcaGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "UCA Game Store",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                val currentLabel = labels.getOrNull(selectedItem)
                when (currentLabel) {
                    "Inicio" -> Column {
                        // Barra de búsqueda con estilo actualizado
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            placeholder = { Text("Buscar un videojuego...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UcaOrange,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = UcaCardBackground,
                                unfocusedContainerColor = UcaCardBackground
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        HomeContent(games) { juegoSeleccionado = it }
                    }
                    "WishList" -> {
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
    }

    if (juegoSeleccionado != null) {
        DetailAndActionBottomSheet(
            game = juegoSeleccionado!!,
            onDismiss = { juegoSeleccionado = null },
            onComprarClick = {
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
    viewModel: HomeViewModel = viewModel(),
    isAdminMode: Boolean = false,
    onAprobar: (() -> Unit)? = null,
    onRechazar: (() -> Unit)? = null
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var mostrarImagenAgrandada by remember { mutableStateOf(false) }

    val listaReseñas by viewModel.reseñasJuegoSeleccionado.collectAsState()
    val favoritosIds by viewModel.favoritosIds.collectAsState()
    val esFavorito = favoritosIds.contains(game.id)

    LaunchedEffect(game.id) {
        viewModel.cargarReseñasDelJuego(game.id)
    }

    if (mostrarImagenAgrandada) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { mostrarImagenAgrandada = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = game.fotoUri,
                    contentDescription = "Imagen agrandada",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { mostrarImagenAgrandada = false },
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = UcaCardBackground) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            if (game.fotoUri.isNotEmpty()) {
                AsyncImage(
                    model = game.fotoUri,
                    contentDescription = game.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { mostrarImagenAgrandada = true },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.nombre,
                    color = UcaOrange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!isAdminMode) {
                    IconButton(onClick = { viewModel.toggleFavorito(game.id) }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "WishList",
                            tint = if (esFavorito) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(game.descripcion, color = Color.White, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (!isAdminMode) {
                Button(
                    onClick = onComprarClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UcaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Añadir al carrito - $${game.precio}", fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onRechazar?.invoke(); onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rechazar", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onAprobar?.invoke(); onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = UcaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aprobar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (!isAdminMode) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = UcaOrange
                    ),
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
                    colors = ButtonDefaults.buttonColors(containerColor = UcaOrange)
                ) {
                    Text("Publicar Reseña", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Opiniones de la comunidad:", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (listaReseñas.isEmpty()) {
                    Text("Nadie ha reseñado este juego aún. ¡Sé el primero!", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listaReseñas.forEach { res ->
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
                                    Text(autor, color = UcaOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            val featuredGames = games.filter { it.destacado }

            if (featuredGames.isNotEmpty()) {
                Text(
                    "Destacados",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                FeaturedCarousel(featuredGames, onGameClick)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                "Catálogo General",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            GameCatalog(games, onGameClick)
        }
    }
}

@Composable
fun FeaturedCarousel(games: List<SolicitudVenta>, onGameClick: (SolicitudVenta) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(games, key = { it.id }) { game ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
                    .clickable { onGameClick(game) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Imagen con menos zoom (usando Crop pero con un Box mejor definido)
                    if (game.fotoUri.isNotEmpty()) {
                        AsyncImage(
                            model = game.fotoUri,
                            contentDescription = game.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                    }

                    // Overlay de gradiente para legibilidad del texto
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                    startY = 100f
                                )
                            )
                    )

                    // Información creativa: Nombre y Precio
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = UcaOrange,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "DESTACADO",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = game.nombre,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        Text(
                            text = "$${game.precio}",
                            color = UcaGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
        colors = CardDefaults.cardColors(containerColor = UcaCardBackground)
    ) {
        Column {
            if (game.fotoUri.isNotEmpty()) {
                AsyncImage(
                    model = game.fotoUri,
                    contentDescription = game.nombre,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.DarkGray))
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = game.nombre,
                    color = UcaOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.descripcion,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${game.precio}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
