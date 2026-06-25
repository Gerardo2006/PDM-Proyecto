package com.example.uca_game_store.ui.theme.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uca_game_store.ui.viewmodels.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(viewModel: AuthViewModel) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    val backgroundGradient = Brush.horizontalGradient(colors = listOf(Color(0xFFFF7F11), Color(0xFF00BFA6)))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("UCA Game Store") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(backgroundGradient),
            verticalArrangement = Arrangement.Center
        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Iniciar Sesión", color = Color(0xFFFF7F11), fontWeight = FontWeight.Bold)

                    TextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") })

                    Button(onClick = { viewModel.login(correo, contrasena) }) {
                        Text("Entrar")
                    }

                    if (authState?.startsWith("ERROR") == true) {
                        Text(text = authState!!, color = Color.Red)
                    }
                }
            }
        }
    }
}