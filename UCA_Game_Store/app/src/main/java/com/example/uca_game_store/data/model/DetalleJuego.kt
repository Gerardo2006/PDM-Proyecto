package com.example.uca_game_store.data.model

data class DetalleJuego(
    val id: String = "",
    val titulo: String,
    val precio: Double,
    val vendedor: String,
    val descripcion: String,
    val calificacion: Double,
    val totalResenas: Int,
    val imagenUrl: String,
    val esFavorito: Boolean = false
)

data class Resena(
    val usuario: String,
    val estrellas: Int,
    val comentario: String
)