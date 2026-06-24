package com.example.test_homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_homescreen.ui.theme.UcaCardBackground
import com.example.test_homescreen.ui.theme.UcaDarkBackground
import com.example.test_homescreen.ui.theme.UcaOrange
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.example.test_homescreen.ui.theme.Test_homescreenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenderScreen() {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = UcaOrange,
                    contentColor = Color.White,
                    snackbarData = data
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("Publicar Nuevo Juego", color = Color.White, fontWeight = FontWeight.Bold) 
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
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del Juego", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = UcaOrange,
                    unfocusedBorderColor = Color.Gray
                ),
                singleLine = true
            )

            // Descripción
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { if (it.length <= 100) descripcion = it },
                    label = { Text("Descripción (Máx 100 caracteres)", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = UcaOrange,
                        unfocusedBorderColor = Color.Gray
                    ),
                    maxLines = 4
                )
                Text(
                    text = "${descripcion.length}/100",
                    color = if (descripcion.length >= 100) Color.Red else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }

            // Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio ($)", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = UcaOrange,
                    unfocusedBorderColor = Color.Gray
                ),
                singleLine = true
            )

            // Integración de Cámara (Simulada)
            Text(
                text = "Foto del Juego",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PhotoCamera, 
                        contentDescription = null, 
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No se ha tomado ninguna foto", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Button(
                onClick = { /* Abrir cámara (simulado) */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Tomar Foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Enviar
            Button(
                onClick = { 
                    scope.launch {
                        snackbarHostState.showSnackbar("Solicitud enviada correctamente")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UcaOrange),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Enviar Solicitud", 
                    color = Color.White, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Espacio extra al final para asegurar el scroll
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VenderScreenPreview() {
    Test_homescreenTheme {
        VenderScreen()
    }
}
