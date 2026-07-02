package com.example.uca_game_store.data.model

data class Resena(
    val id: String = "",
    val juegoId: Int,
    val usuarioId: String = "",
    val comentario: String = "",
    val calificacion: Int = 0
)

