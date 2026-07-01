package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.SolicitudVenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminAprobacionesViewModel : ViewModel() {

    // Estado de la lista de solicitudes
    private val _solicitudes = MutableStateFlow<List<SolicitudVenta>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudVenta>> = _solicitudes.asStateFlow()

    init {
        cargarSolicitudes()
    }

    private fun cargarSolicitudes() {
        _solicitudes.value = listOf(
            SolicitudVenta("1", "Elden Ring", "Juan Perez", "45.00", ),
            SolicitudVenta("2", "Hogwarts Legacy", "Maria Garcia", "$35.00"),
            SolicitudVenta("3", "Resident Evil 4", "Luis Rodriguez", "$40.00"),
            SolicitudVenta("4", "Cyberpunk 2077", "Ana Martinez", "$25.00"),
            SolicitudVenta("5", "Final Fantasy XVI", "Carlos Lopez", "$50.00")
        )
    }

    // Funciones que se llaman al tocar los botones
    fun aprobarSolicitud(id: String) {
        // TODO: A futuro, aquí se actualizará el estado en Firebase
        _solicitudes.value = _solicitudes.value.filter { it.id != id }
    }

    fun rechazarSolicitud(id: String) {
        // TODO: A futuro, aquí se eliminará de Firebase
        _solicitudes.value = _solicitudes.value.filter { it.id != id }
    }
}