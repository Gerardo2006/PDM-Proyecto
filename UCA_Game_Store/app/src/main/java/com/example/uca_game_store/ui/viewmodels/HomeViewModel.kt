package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.SolicitudVenta
import com.example.uca_game_store.data.repository.VentaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val repository = VentaRepository()

    // Todos los juegos aprobados en Firebase desde la colección solicitudes_venta
    private val _juegosAprobados = MutableStateFlow<List<SolicitudVenta>>(emptyList())

    // Lista filtrada dinámicamente que va a leer la UI de la Home
    private val _juegosFiltrados = MutableStateFlow<List<SolicitudVenta>>(emptyList())
    val juegos: StateFlow<List<SolicitudVenta>> = _juegosFiltrados.asStateFlow()

    // Estado para el texto de la barra de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Tu estado original para el rol de Admin
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // NUEVO: Estado para almacenar los IDs de los juegos favoritos del usuario actual
    private val _favoritosIds = MutableStateFlow<List<String>>(emptyList())
    val favoritosIds: StateFlow<List<String>> = _favoritosIds.asStateFlow()

    // NUEVO: Estado reactivo para exponer la lista filtrada de objetos de videojuegos favoritos a la UI
    private val _juegosFavoritosObjetos = MutableStateFlow<List<SolicitudVenta>>(emptyList())
    val juegosFavoritosObjetos: StateFlow<List<SolicitudVenta>> = _juegosFavoritosObjetos.asStateFlow()

    // Estado que guardará las reseñas del juego que esté abierto en el BottomSheet
    private val _reseñasJuegoSeleccionado = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val reseñasJuegoSeleccionado: StateFlow<List<Map<String, Any>>> = _reseñasJuegoSeleccionado.asStateFlow()

    init {
        escucharJuegosAprobados()
        checkUserRole()
        escucharFavoritos()
    }

    // Tu función original corregida para verificar el rol dinámicamente desde la colección "usuarios"
    fun checkUserRole() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _isAdmin.value = false
            return
        }

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                val rol = document.getString("rol")
                _isAdmin.value = (rol == "ADMIN")
            }
            .addOnFailureListener {
                _isAdmin.value = false
            }
    }

    // Escucha los juegos aprobados en tiempo real desde el repositorio
    fun escucharJuegosAprobados() {
        repository.obtenerJuegosAprobados { lista ->
            _juegosAprobados.value = lista
            // Aplicamos el filtro de búsqueda inmediatamente con la lista actualizada
            filtrarJuegos(lista, _searchQuery.value)
            // Actualizamos los objetos de favoritos basándonos en la nueva lista de solicitudes
            actualizarObjetosFavoritos()
        }
    }

    // NUEVO: Escucha los IDs de favoritos del usuario en tiempo real
    fun escucharFavoritos() {
        repository.obtenerFavoritos { listaIds ->
            _favoritosIds.value = listaIds
            actualizarObjetosFavoritos()
        }
    }

    // NUEVO: Agrega o quita un juego de favoritos en la base de datos de manera limpia
    fun toggleFavorito(juegoId: String) {
        repository.agregarAFavoritos(juegoId) { exito ->
            // El SnapshotListener de 'escucharFavoritos' actualizará el flujo de inmediato de forma reactiva
        }
    }

    // NUEVO: Mapea y cruza los IDs favoritos con los datos reales de 'solicitudes_venta' aprobados
    private fun actualizarObjetosFavoritos() {
        val ids = _favoritosIds.value
        _juegosFavoritosObjetos.value = _juegosAprobados.value.filter { juego ->
            ids.contains(juego.id)
        }
    }

    // Se ejecuta cada vez que el usuario escribe en la barra de búsqueda
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filtrarJuegos(_juegosAprobados.value, query)
    }

    // Filtra los juegos por nombre de forma reactiva
    private fun filtrarJuegos(lista: List<SolicitudVenta>, query: String) {
        _juegosFiltrados.value = if (query.isEmpty()) {
            lista
        } else {
            lista.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }

    // Carga de forma reactiva las reseñas de un juego específico
    fun cargarReseñasDelJuego(juegoId: String) {
        repository.obtenerReseñasDelJuego(juegoId) { lista ->
            _reseñasJuegoSeleccionado.value = lista
        }
    }

    // Envía la reseña a Firestore
    fun enviarReseña(juegoId: String, rating: Int, comentario: String, onComplete: () -> Unit) {
        if (rating == 0 || comentario.trim().isEmpty()) return

        repository.guardarReseña(juegoId, rating, comentario) { exito ->
            if (exito) {
                onComplete() // Limpia los campos locales en la UI si se guardó bien
            }
        }
    }

    fun resetState() {
        _isAdmin.value = false
        _juegosAprobados.value = emptyList()
        _juegosFiltrados.value = emptyList()
        _searchQuery.value = ""
        _favoritosIds.value = emptyList()
        _juegosFavoritosObjetos.value = emptyList()
        _reseñasJuegoSeleccionado.value = emptyList()
    }
}