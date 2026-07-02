package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.uca_game_store.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit // <--- Añadimos este parámetro para volver al login
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    // Reacciona cuando el registro es exitoso
    LaunchedEffect(authState) {
        if (authState == "SUCCESS") {
            onRegisterSuccess()
        }
    }

    val backgroundGradient = Brush.verticalGradient(colors = listOf(Color(0xFF2D2D2D), Color(0xFF121212)))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundGradient),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear Cuenta",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF7F11),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val imagen = if (contrasenaVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val descripcion = if (contrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                                Icon(imageVector = imagen, contentDescription = descripcion)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.register(correo, contrasena) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { onNavigateToLogin() }) {
                        Text("¿Ya tienes cuenta? Inicia Sesión")
                    }

                    // Mostramos el mensaje de error si ocurre
                    if (authState?.startsWith("ERROR") == true) {
                        Text(
                            text = authState!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}