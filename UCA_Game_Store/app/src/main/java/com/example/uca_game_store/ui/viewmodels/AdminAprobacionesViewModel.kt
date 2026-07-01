package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.SolicitudVenta
import com.example.uca_game_store.data.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminAprobacionesViewModel : ViewModel() {
    private val repository = VentaRepository()

    private val _solicitudes = MutableStateFlow<List<SolicitudVenta>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudVenta>> = _solicitudes.asStateFlow()

    init {
        cargarSolicitudesRealtime()
    }

    private fun cargarSolicitudesRealtime() {
        // Escucha cambios directos en Firestore de manera reactiva
        repository.obtenerSolicitudesPendientes { lista ->
            _solicitudes.value = lista
        }
    }

    fun aprobarSolicitud(id: String) {
        // Cambia el estado a APROBADO para que pase al catálogo general de la Home
        repository.actualizarEstadoSolicitud(id, "APROBADO")
    }

    fun rechazarSolicitud(id: String) {
        // Cambia el estado a RECHAZADO (o podrías eliminarlo si prefieres)
        repository.actualizarEstadoSolicitud(id, "RECHAZADO")
    }
}