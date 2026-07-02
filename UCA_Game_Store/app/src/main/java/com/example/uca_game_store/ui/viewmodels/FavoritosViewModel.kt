package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.FavoriteGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritosViewModel : ViewModel() {

    private val _favoriteGames = MutableStateFlow<List<FavoriteGame>>(emptyList())
    val favoriteGames: StateFlow<List<FavoriteGame>> = _favoriteGames.asStateFlow()

    init {
        // TODO: Aquí se conectará con Firebase Firestore para descargar los favoritos reales
        loadMockFavorites()
    }


    private fun loadMockFavorites() {
        _favoriteGames.value = listOf(
            FavoriteGame(id = "1", title = "Elden Ring", price = 49.99),
            FavoriteGame(id = "2", title = "Red Dead Redemption 2", price = 29.99),
            FavoriteGame(id = "3", title = "Stardew Valley", price = 14.99)
        )
    }


    fun removeFavorite(gameId: String) {
        // TODO: Agregar lógica para eliminar el documento en Firebase


        _favoriteGames.value = _favoriteGames.value.filter { it.id != gameId }
    }
}