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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uca_game_store.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    // Estados para recuperación
    var showDialog by remember { mutableStateOf(false) }
    var emailRecuperacion by remember { mutableStateOf("") }
    var mensajeRecuperacion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        if (authState == "SUCCESS") {
            onLoginSuccess()
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
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Iniciar Sesión", color = Color(0xFFFF7F11), fontWeight = FontWeight.Bold)

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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.login(correo, contrasena) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Entrar")
                    }

                    // Botón para activar el diálogo de recuperación
                    TextButton(onClick = { showDialog = true }) {
                        Text("¿Olvidaste tu contraseña?", color = Color.Gray)
                    }

                    TextButton(onClick = { onNavigateToRegister() }) {
                        Text("¿No tienes cuenta? Regístrate")
                    }

                    if (authState?.startsWith("ERROR") == true) {
                        Text(text = authState!!, color = Color.Red)
                    }

                    // Mensaje de resultado de recuperación
                    mensajeRecuperacion?.let {
                        Text(text = it, color = Color.DarkGray)
                    }
                }
            }
        }
    }

    // Diálogo de recuperación de contraseña
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Recuperar contraseña") },
            text = {
                TextField(
                    value = emailRecuperacion,
                    onValueChange = { emailRecuperacion = it },
                    label = { Text("Ingresa tu correo") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetPassword(emailRecuperacion) { success, message ->
                        mensajeRecuperacion = message
                        showDialog = false
                    }
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}