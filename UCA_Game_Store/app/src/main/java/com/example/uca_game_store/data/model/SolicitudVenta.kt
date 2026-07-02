package com.example.uca_game_store.data.model

data class SolicitudVenta(
    val id: String = "", // <-- AGREGAMOS ESTO para guardar el ID de Firebase y solucionar el error del Repositorio
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val fotoUri: String = "",
    val vendedorId: String = "",
    val estado: String = "PENDIENTE"
)
/*data class SolicitudVenta(
    val id: Int,
    val nombre: String,
    val descripcion: String = "", // Valor por defecto
    val fotoUri: String = ""      // Valor por defecto
)*/