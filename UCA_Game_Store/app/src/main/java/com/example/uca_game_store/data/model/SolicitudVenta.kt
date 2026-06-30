package com.example.uca_game_store.data.model

data class SolicitudVenta(
    val id: Int,
    val juegoNombre: String,
    val vendedor: String,
    val precio: String,
    val descripcion: String, // Campo nuevo
    val fotoUri: String?,    // Campo nuevo
    val estado: String = "Pendiente"
)