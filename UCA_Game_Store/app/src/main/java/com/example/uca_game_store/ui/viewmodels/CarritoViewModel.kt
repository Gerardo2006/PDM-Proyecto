package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarritoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    // Mantenemos tu nombre oficial para que tu CarritoScreen no rompa
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        cargarCarrito()
    }

    // Escucha en tiempo real los cambios del carrito de compras en Firebase
    fun cargarCarrito() {
        // Escuchamos de forma activa si el usuario inicia o cierra sesión
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                // Solo si el UID existe, activamos la escucha en Firestore
                db.collection("usuarios").document(uid).collection("carrito")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            android.util.Log.e("FirebaseCarrito", "Error al escuchar el carrito", error)
                            return@addSnapshotListener
                        }

                        val items = snapshot?.toObjects(CartItem::class.java) ?: emptyList()
                        _cartItems.value = items
                        calculateTotal(items)
                    }
            } else {
                // Si no hay usuario logueado, limpiamos el estado local
                _cartItems.value = emptyList()
                _totalPrice.value = 0.0
            }
        }
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.price }
    }

    fun agregarAlCarrito(item: CartItem) {
        val uid = auth.currentUser?.uid ?: return

        // CORREGIDO: Nos aseguramos de que guarde usando el ID correcto como nombre de documento
        db.collection("usuarios")
            .document(uid)
            .collection("carrito")
            .document(item.id)
            .set(item)
            .addOnFailureListener { exception ->
                android.util.Log.e("FirebaseCarrito", "Error al añadir artículo", exception)
            }
    }

    // OPTIMIZADO: Usa un WriteBatch para limpiar el carrito de golpe en una sola operación atómica
    fun finalizarCompra() {
        val uid = auth.currentUser?.uid ?: return
        val carritoRef = db.collection("usuarios").document(uid).collection("carrito")

        carritoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) return@addOnSuccessListener

            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit().addOnFailureListener { exception ->
                android.util.Log.e("FirebaseCarrito", "Error al vaciar el carrito en lote", exception)
            }
        }
    }
}