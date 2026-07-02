package com.example.uca_game_store.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.SolicitudVenta
import com.example.uca_game_store.data.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado simple para la UI
data class VenderUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val fotoUri: String? = null,
    val isSubmitting: Boolean = false
)

class VenderViewModel : ViewModel() {
    private val repository = VentaRepository()

    private val _uiState = MutableStateFlow(VenderUiState())
    val uiState = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun onTituloChange(it: String) { _uiState.value = _uiState.value.copy(titulo = it) }
    fun onDescripcionChange(it: String) { _uiState.value = _uiState.value.copy(descripcion = it) }
    fun onPrecioChange(it: String) { _uiState.value = _uiState.value.copy(precio = it) }
    fun onFotoCapturada(uri: String?) { _uiState.value = _uiState.value.copy(fotoUri = uri) }
    fun snackbarMostrado() { _snackbarMessage.value = null }

    fun enviarSolicitud() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isSubmitting = true)

        val nuevaSolicitud = SolicitudVenta(
            nombre = currentState.titulo,
            descripcion = currentState.descripcion,
            precio = currentState.precio,
            fotoUri = currentState.fotoUri ?: ""
        )

        repository.guardarVenta(nuevaSolicitud) { success ->
            _uiState.value = currentState.copy(isSubmitting = false)
            _snackbarMessage.value = if (success) "¡Solicitud enviada!" else "Error al enviar"
        }
    }
}