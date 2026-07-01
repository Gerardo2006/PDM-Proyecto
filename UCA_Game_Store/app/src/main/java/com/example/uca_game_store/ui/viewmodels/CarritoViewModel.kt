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
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        cargarCarrito()
    }

    private fun cargarCarrito() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).collection("carrito")
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.toObjects(CartItem::class.java) ?: emptyList()
                _cartItems.value = items
                calculateTotal(items)
            }
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.price }
    }

    fun agregarAlCarrito(item: CartItem) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).collection("carrito").document(item.id).set(item)
    }

    fun finalizarCompra() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).collection("carrito").get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) doc.reference.delete()
            }
    }
}