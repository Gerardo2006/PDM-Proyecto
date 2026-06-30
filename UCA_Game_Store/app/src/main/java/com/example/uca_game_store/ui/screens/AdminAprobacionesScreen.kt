package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uca_game_store.data.model.SolicitudVenta
import com.example.uca_game_store.ui.viewmodels.AdminAprobacionesViewModel

// Asegúrate de tener estos colores en tu archivo ui/theme/Color.kt
import com.example.uca_game_store.ui.theme.UcaCardBackground
import com.example.uca_game_store.ui.theme.UcaDarkBackground
import com.example.uca_game_store.ui.theme.UcaGreen
import com.example.uca_game_store.ui.theme.UcaOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAprobacionesScreen(
    viewModel: AdminAprobacionesViewModel = viewModel()
) {
    // Escuchamos la lista de solicitudes desde el ViewModel
    val solicitudes by viewModel.solicitudes.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Panel de Administración", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = UcaCardBackground
                )
            )
        },
        containerColor = UcaDarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Solicitudes de Venta Pendientes",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(solicitudes, key = { it.id }) { solicitud ->
                    SolicitudItem(
                        solicitud = solicitud,
                        onAprobar = { viewModel.aprobarSolicitud(solicitud.id) },
                        onRechazar = { viewModel.rechazarSolicitud(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(
    solicitud: SolicitudVenta,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = UcaCardBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = solicitud.juegoNombre,
                    color = UcaOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Vendedor: ${solicitud.vendedor}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = solicitud.precio,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Botón Rechazar
                IconButton(
                    onClick = onRechazar,
                    modifier = Modifier.background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                }

                // Botón Aprobar
                IconButton(
                    onClick = onAprobar,
                    modifier = Modifier.background(UcaGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Aprobar", tint = UcaGreen)
                }
            }
        }
    }
}