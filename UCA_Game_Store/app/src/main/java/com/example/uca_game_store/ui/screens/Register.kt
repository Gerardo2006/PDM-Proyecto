package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uca_game_store.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit // <--- Añadimos este parámetro
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Reacciona cuando el registro es exitoso
    LaunchedEffect(authState) {
        if (authState == "SUCCESS") {
            onRegisterSuccess()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Registro UCA") }) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Crear Cuenta", fontWeight = FontWeight.Bold, color = Color(0xFFFF7F11))

            TextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") })

            Button(onClick = { viewModel.register(correo, contrasena) }) {
                Text("Registrarse")
            }

            // Mostramos el mensaje de error si ocurre
            if (authState?.startsWith("ERROR") == true) {
                Text(text = authState!!, color = Color.Red)
            }
        }
    }
}