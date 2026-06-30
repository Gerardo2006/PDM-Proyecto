package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.R
import com.example.uca_game_store.data.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    // Estado para la barra de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Estado para la lista de juegos
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    init {
        cargarJuegos()
    }

    private fun cargarJuegos() {
        // Mantenemos tus datos de prueba exactamente igual
        _games.value = listOf(
            Game(1, "Spider-Man", "Sony Interactive Entertainment, Insomniac Games and Marvel have joined to create...", "$29.99", R.drawable.spiderman),
            Game(2, "Detroit: Become Human", "Set in the year 2036, the city of Detroit has been revitalized thanks to...", "$14.99", R.drawable.detroit),
            Game(3, "Minecraft", "In Minecraft, your adventure starts with your imagination. Build everything you...", "$15.99", R.drawable.minecraft),
            Game(4, "The Witcher 3", "Become a professional monster slayer and embark on an adventure of epic proportions.", "$19.99", R.drawable.thewitcher),
            Game(5, "God of War", "His vengeance against the Gods of Olympus years behind him, Kratos now lives...", "$19.99", R.drawable.gow),
            Game(6, "Horizon Zero Dawn", "In an era where Machines roam the land and mankind is no longer the dominant...", "$9.99", R.drawable.horizon)
        )
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        // TODO: A futuro aquí agregaremos la lógica para filtrar la lista de juegos según lo que se escriba
    }
}