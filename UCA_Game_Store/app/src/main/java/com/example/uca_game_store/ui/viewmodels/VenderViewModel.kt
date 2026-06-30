package com.example.uca_game_store.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uca_game_store.data.model.SolicitudVenta
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de "Vender / Publicar juego".
 */
data class VenderUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val fotoUri: String? = null,
    val isSubmitting: Boolean = false
)

class VenderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VenderUiState())
    val uiState: StateFlow<VenderUiState> = _uiState.asStateFlow()

    // Mensaje puntual para el Snackbar (null = nada que mostrar)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onTituloChange(value: String) {
        _uiState.value = _uiState.value.copy(titulo = value)
    }

    fun onDescripcionChange(value: String) {
        if (value.length <= 100) {
            _uiState.value = _uiState.value.copy(descripcion = value)
        }
    }

    fun onPrecioChange(value: String) {
        _uiState.value = _uiState.value.copy(precio = value)
    }

    fun onFotoCapturada(uri: String?) {
        _uiState.value = _uiState.value.copy(fotoUri = uri)
    }

    fun enviarSolicitud() {
        viewModelScope.launch {
            val estado = _uiState.value

            // Ahora validamos también la descripción
            if (estado.titulo.isBlank() || estado.precio.isBlank() || estado.descripcion.isBlank()) {
                _snackbarMessage.value = "Completa el título, precio y descripción"
                return@launch
            }

            val usuarioActual = FirebaseAuth.getInstance().currentUser
            val nombreVendedor = usuarioActual?.displayName
                ?: usuarioActual?.email
                ?: "Usuario desconocido"

            _uiState.value = estado.copy(isSubmitting = true)

            // Creamos la solicitud incluyendo los nuevos campos
            val solicitud = SolicitudVenta(
                id = System.currentTimeMillis().toInt(),
                juegoNombre = estado.titulo,
                vendedor = nombreVendedor,
                precio = estado.precio,
                descripcion = estado.descripcion, // Incluido
                fotoUri = estado.fotoUri,         // Incluido
                estado = "Pendiente"
            )

            // TODO: aquí se conecta con GameRepository, ej:
            //gameRepository.crearSolicitudVenta(solicitud)

            _uiState.value = VenderUiState() // limpia el formulario
            _snackbarMessage.value = "Solicitud enviada correctamente"
        }
    }

    fun snackbarMostrado() {
        _snackbarMessage.value = null
    }
}