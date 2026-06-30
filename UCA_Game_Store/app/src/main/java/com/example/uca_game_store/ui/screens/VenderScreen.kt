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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Importa tu ViewModel
import com.example.uca_game_store.viewmodels.VenderViewModel

// Importa los colores de tu tema
import com.example.uca_game_store.ui.theme.UcaCardBackground
import com.example.uca_game_store.ui.theme.UcaDarkBackground
import com.example.uca_game_store.ui.theme.UcaOrange

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

    // Variable temporal para guardar la ruta (Uri) del archivo antes de tomar la foto
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para la cámara: Se ejecuta DESPUÉS de que se da el permiso
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && uriTemporal != null) {
                // Si tomó la foto y no la canceló, la guardamos en el ViewModel
                viewModel.onFotoCapturada(uriTemporal.toString())
            }
        }
    )

    // Lanzador para pedir permisos de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Si dio permiso, creamos el archivo vacío y abrimos la cámara
                val file = context.crearArchivoDeImagen()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                uriTemporal = uri
                cameraLauncher.launch(uri)
            } else {
                // Si rechaza el permiso, le avisamos
                viewModel.onDescripcionChange(uiState.descripcion) // Hack temporal para forzar recomposición si es necesario
            }
        }
    )

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.snackbarMostrado()
        }
    }

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

            // ... (TÍTULO) ...
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
                ),
                singleLine = true
            )

            // ... (DESCRIPCIÓN) ...
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
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
                    text = "${uiState.descripcion.length}/100",
                    color = if (uiState.descripcion.length >= 100) Color.Red else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }

            // ... (PRECIO) ...
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
                ),
                singleLine = true
            )

            // ... (INTEGRACIÓN REAL DE CÁMARA) ...
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Foto del Juego (Opcional)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Si hay foto, damos la opción de eliminarla
                if (uiState.fotoUri != null) {
                    IconButton(onClick = { viewModel.onFotoCapturada(null) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar foto", tint = Color.Red)
                    }
                }
            }

            // Si el estado tiene una foto, la mostramos. Si no, mostramos la caja gris.
            if (uiState.fotoUri != null) {
                AsyncImage(
                    model = uiState.fotoUri,
                    contentDescription = "Foto del juego",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
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
                        Text(
                            text = "No se ha tomado ninguna foto",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Button(
                onClick = {
                    // Lanzamos la petición de permiso, que a su vez lanzará la cámara
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (uiState.fotoUri == null) "Tomar Foto" else "Tomar otra foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ... (BOTÓN ENVIAR) ...
            Button(
                onClick = { viewModel.enviarSolicitud() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UcaOrange,
                    disabledContainerColor = UcaOrange.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (uiState.isSubmitting) "Enviando..." else "Enviar Solicitud",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


fun Context.crearArchivoDeImagen(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        cacheDir // Lo guarda en la caché interna
    )
}