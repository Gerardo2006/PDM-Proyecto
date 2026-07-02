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
    val destacado: Boolean = false,
    val isSubmitting: Boolean = false,
    val mostrarDialogoDestacado: Boolean = false
)

class VenderViewModel : ViewModel() {
    private val repository = VentaRepository()

    private val _uiState = MutableStateFlow(VenderUiState())
    val uiState = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    // Limites de caracteres
    private val MAX_TITULO = 70
    private val MAX_DESCRIPCION = 200

    fun onTituloChange(it: String) {
        if (it.length <= MAX_TITULO) {
            _uiState.value = _uiState.value.copy(titulo = it)
        }
    }

    fun onDescripcionChange(it: String) {
        if (it.length <= MAX_DESCRIPCION) {
            _uiState.value = _uiState.value.copy(descripcion = it)
        }
    }

    fun onPrecioChange(it: String) {
        // Filtrar caracteres no deseados
        val filtered = it.filter { char -> char.isDigit() || char == '.' }
        
        // Validar formato de decimales (máximo 2)
        if (filtered.contains(".")) {
            val parts = filtered.split(".")
            // Solo permitir un punto y máximo 2 decimales
            if (parts.size <= 2 && parts[1].length <= 2) {
                _uiState.value = _uiState.value.copy(precio = filtered)
            }
        } else {
            _uiState.value = _uiState.value.copy(precio = filtered)
        }
    }

    fun onFotoCapturada(uri: String?) { _uiState.value = _uiState.value.copy(fotoUri = uri) }
    
    fun snackbarMostrado() { _snackbarMessage.value = null }

    fun onPublicarClick() {
        val state = _uiState.value
        if (state.titulo.isBlank() || state.descripcion.isBlank() || state.precio.isBlank() || state.fotoUri == null) {
            _snackbarMessage.value = "Por favor completa todos los campos y toma una foto"
            return
        }
        
        // Abrir el diálogo de confirmación para destacados
        _uiState.value = state.copy(mostrarDialogoDestacado = true)
    }

    fun cerrarDialogo() {
        _uiState.value = _uiState.value.copy(mostrarDialogoDestacado = false)
    }

    fun enviarSolicitud(esDestacado: Boolean) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isSubmitting = true, 
            mostrarDialogoDestacado = false,
            destacado = esDestacado
        )

        val nuevaSolicitud = SolicitudVenta(
            nombre = currentState.titulo,
            descripcion = currentState.descripcion,
            precio = currentState.precio,
            fotoUri = currentState.fotoUri ?: "",
            destacado = esDestacado
        )

        repository.guardarVenta(nuevaSolicitud) { success ->
            if (success) {
                // Limpiar campos si tuvo éxito
                _uiState.value = VenderUiState()
                _snackbarMessage.value = "¡Solicitud enviada!"
            } else {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                _snackbarMessage.value = "Error al enviar la solicitud"
            }
        }
    }
}