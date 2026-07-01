package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.uca_game_store.ui.viewmodels.DetalleJuegoViewModel
import com.example.uca_game_store.ui.viewmodels.ResenasViewModel

// Colores
val FondoOscuro = Color(0xFF121212)
val FondoTarjeta = Color(0xFF1E1E24)
val NaranjaUca = Color(0xFFFF8C00)
val VerdeUca = Color(0xFF00E676)
val TextoGris = Color(0xFFAAAAAA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleJuegoScreen(
    gameId: Int,
    navController: NavController,
    viewModel: DetalleJuegoViewModel = viewModel()
) {
    val juego = viewModel.juego.collectAsState().value
    val listaResenas = viewModel.resenas.collectAsState().value
    val resenasViewModel: ResenasViewModel = viewModel()

    var descripcionExpandida by remember { mutableStateOf(false) }
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    var estrellasSeleccionadas by remember { mutableIntStateOf(0) }
    var textoResena by remember { mutableStateOf("") }
    val maxCaracteres = 500

    if (juego == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NaranjaUca)
        }
        return
    }

    Scaffold(
        containerColor = FondoOscuro,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoTarjeta)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.agregarAlCarrito() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaUca, contentColor = Color.Black)
                ) {
                    Text("Al carrito", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { mostrarBottomSheet = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeUca, contentColor = Color.Black)
                ) {
                    Text("Escribir reseña", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = VerdeUca)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Volver", color = VerdeUca)
                    }
                }
            }

            item {
                AsyncImage(
                    model = juego.imagenUrl,
                    contentDescription = "Portada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = juego.titulo, color = NaranjaUca, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Por: ${juego.vendedor}", color = TextoGris, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$${juego.precio}", color = VerdeUca, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = { viewModel.alternarFavorito() }) {
                        Icon(
                            imageVector = if (juego.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (juego.esFavorito) Color.Red else TextoGris,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Surface(
                    color = FondoTarjeta,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = juego.calificacion.toString(), color = NaranjaUca, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = NaranjaUca, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "(${juego.totalResenas} reseñas)", color = TextoGris, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Descripción", color = NaranjaUca, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = juego.descripcion,
                        color = Color.White,
                        maxLines = if (descripcionExpandida) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = { descripcionExpandida = !descripcionExpandida },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (descripcionExpandida) "Leer menos" else "Leer más", color = VerdeUca)
                    }
                }
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(
                    text = "Reseñas de usuarios",
                    color = NaranjaUca,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            items(listaResenas) { resena ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoTarjeta)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Usuario: ${resena.usuarioId}", color = VerdeUca, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (index < resena.calificacion) Color.White else TextoGris,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = resena.comentario, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        if (mostrarBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { mostrarBottomSheet = false },
                containerColor = FondoTarjeta
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Escribe tu reseña", color = NaranjaUca, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { estrellasSeleccionadas = i }) {
                                Icon(Icons.Filled.Star, contentDescription = "Estrella $i", tint = if (i <= estrellasSeleccionadas) NaranjaUca else TextoGris, modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = textoResena,
                        onValueChange = { if (it.length <= maxCaracteres) textoResena = it },
                        placeholder = { Text("Escribe tu reseña aquí...", color = TextoGris) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdeUca, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        maxLines = 5
                    )

                    Button(
                        onClick = { viewModel.enviarResena(estrellasSeleccionadas, textoResena); mostrarBottomSheet = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeUca, contentColor = Color.Black)
                    ) {
                        Text("Enviar Reseña", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}