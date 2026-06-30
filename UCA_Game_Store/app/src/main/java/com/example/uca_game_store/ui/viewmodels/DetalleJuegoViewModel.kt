package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.DetalleJuego
import com.example.uca_game_store.data.model.Resena
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetalleJuegoViewModel : ViewModel() {

    // Estado del juego actual
    private val _juego = MutableStateFlow<DetalleJuego?>(null)
    val juego: StateFlow<DetalleJuego?> = _juego.asStateFlow()

    // Estado de la lista de reseñas
    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas.asStateFlow()

    init {
        cargarDatosPrueba()
    }

    private fun cargarDatosPrueba() {
        _juego.value = DetalleJuego(
            id = "1",
            titulo = "Marvel: Spider Man",
            precio = 59.99,
            vendedor = "Insomniac Games",
            descripcion = "Experimenta una historia original de Spider-Man. Juega como un Peter Parker experimentado que lucha contra el crimen organizado en la Nueva York de Marvel. Al mismo tiempo, lucha por equilibrar su caótica vida personal y su carrera mientras el destino de millones de neoyorquinos descansa sobre sus hombros. El balanceo con telarañas es el mejor que se ha hecho.",
            calificacion = 4.8,
            totalResenas = 342,
            imagenUrl = "https://cdn.akamai.steamstatic.com/steam/apps/1817070/header.jpg",
            esFavorito = false
        )

        _resenas.value = listOf(
            Resena("00012324", 3, "Me gusta mas dc comics."),
            Resena("00001001", 5, "¡Increíble! El mejor juego de Spider-Man que he jugado. El balanceo es perfecto."),
            Resena("00001002", 4, "Muy bueno, aunque las misiones secundarias de crímenes son un poco repetitivas."),
            Resena("00012345", 5, "Gráficos espectaculares.")
        )
    }

    // Funciones accionables desde la interfaz
    fun alternarFavorito() {
        // Copiamos el juego actual e invertimos su valor de favorito
        _juego.value = _juego.value?.let { it.copy(esFavorito = !it.esFavorito) }
        // TODO: A futuro aquí guardarás este cambio en Firebase
    }

    fun agregarAlCarrito() {
        // TODO: Lógica para enviar este juego a la colección del carrito en Firebase
        println("Añadido al carrito: ${_juego.value?.titulo}")
    }

    fun enviarResena(estrellas: Int, comentario: String) {
        if (estrellas == 0 || comentario.isBlank()) return

        // Creamos la nueva reseña y la agregamos al inicio de la lista
        val nuevaResena = Resena("Usuario_Demo", estrellas, comentario)
        _resenas.value = listOf(nuevaResena) + _resenas.value

        // Actualizamos contador de reseñas de forma simulada
        _juego.value = _juego.value?.let {
            it.copy(totalResenas = it.totalResenas + 1)
        }
    }
}