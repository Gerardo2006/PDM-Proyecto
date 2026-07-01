package com.example.uca_game_store.data.model

data class SolicitudVenta(
    val id: String = "", // Usaremos el ID que genera Firebase automáticamente
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val fotoUri: String = "",
    val estado: String = "PENDIENTE" // Útil para la lógica de administración
)

/*data class SolicitudVenta(
    val id: Int,
    val nombre: String,
    val descripcion: String = "", // Valor por defecto
    val fotoUri: String = ""      // Valor por defecto
)*/