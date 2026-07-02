package com.example.uca_game_store.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.uca_game_store.viewmodels.VenderViewModel
import com.example.uca_game_store.ui.theme.UcaCardBackground
import com.example.uca_game_store.ui.theme.UcaDarkBackground
import com.example.uca_game_store.ui.theme.UcaOrange
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenderScreen(
    viewModel: VenderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && uriTemporal != null) {
                viewModel.onFotoCapturada(uriTemporal.toString())
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = context.crearArchivoDeImagen()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                uriTemporal = uri
                cameraLauncher.launch(uri)
            }
        }
    )

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.snackbarMostrado()
        }
    }

    // Diálogo de Destacado
    if (uiState.mostrarDialogoDestacado) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogo() },
            title = { Text("Juego Destacado") },
            text = { Text("¿Quieres colocar tu juego en destacados? Esto tiene una tarifa de $1.99") },
            confirmButton = {
                TextButton(onClick = { viewModel.enviarSolicitud(true) }) {
                    Text("SÍ", color = UcaOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.enviarSolicitud(false) }) {
                    Text("NO", color = Color.Gray)
                }
            },
            containerColor = UcaCardBackground,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            Column {
                OutlinedTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    label = { Text("Título del Juego", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = UcaOrange,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Text(
                    text = "${uiState.titulo.length} / 70",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            Column {
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = { Text("Descripción", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = UcaOrange,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Text(
                    text = "${uiState.descripcion.length} / 200",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            OutlinedTextField(
                value = uiState.precio,
                onValueChange = { viewModel.onPrecioChange(it) },
                label = { Text("Precio ($)", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = UcaOrange,
                    unfocusedBorderColor = Color.Gray
                )
            )

            if (uiState.fotoUri != null) {
                AsyncImage(
                    model = uiState.fotoUri,
                    contentDescription = "Foto del juego",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(if (uiState.fotoUri == null) "Tomar Foto" else "Cambiar Foto")
            }

            Button(
                onClick = { viewModel.onPublicarClick() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = UcaOrange)
            ) {
                Text(if (uiState.isSubmitting) "Enviando..." else "Publicar Venta")
            }
        }
    }
}

// Función auxiliar necesaria
fun Context.crearArchivoDeImagen(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", cacheDir)
}