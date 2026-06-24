package com.example.test_homescreen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_homescreen.ui.theme.UcaCardBackground
import com.example.test_homescreen.ui.theme.UcaDarkBackground
import com.example.test_homescreen.ui.theme.UcaGreen
import com.example.test_homescreen.ui.theme.UcaOrange

data class SolicitudVenta(
    val id: Int,
    val juegoNombre: String,
    val vendedor: String,
    val precio: String,
    val estado: String = "Pendiente"
)

val solicitudesQuemadas = listOf(
    SolicitudVenta(1, "Elden Ring", "Juan Perez", "$45.00"),
    SolicitudVenta(2, "Hogwarts Legacy", "Maria Garcia", "$35.00"),
    SolicitudVenta(3, "Resident Evil 4", "Luis Rodriguez", "$40.00"),
    SolicitudVenta(4, "Cyberpunk 2077", "Ana Martinez", "$25.00"),
    SolicitudVenta(5, "Final Fantasy XVI", "Carlos Lopez", "$50.00")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAprobacionesScreen() {
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
                items(solicitudesQuemadas) { solicitud ->
                    SolicitudItem(solicitud)
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(solicitud: SolicitudVenta) {
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
                    onClick = { /* Lógica no funcional */ },
                    modifier = Modifier.background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                }

                // Botón Aprobar
                IconButton(
                    onClick = { /* Lógica no funcional */ },
                    modifier = Modifier.background(UcaGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Aprobar", tint = UcaGreen)
                }
            }
        }
    }
}
