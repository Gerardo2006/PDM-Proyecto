package com.example.uca_game_store.data.model

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val imageUrl: String = ""
)