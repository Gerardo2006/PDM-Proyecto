package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarritoViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()


    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        // TODO: En el futuro, aquí se conectará con Firebase Firestore para descargar el carrito del usuario.
        // Por ahora, cargamos datos de prueba para que Anderson pueda diseñar la pantalla.
        loadMockData()
    }

    private fun loadMockData() {
        val mockData = listOf(
            CartItem(id = "1", title = "The Legend of Zelda", price = 59.99),
            CartItem(id = "2", title = "Super Mario Odyssey", price = 49.99),
            CartItem(id = "3", title = "Hollow Knight", price = 15.00)
        )
        _cartItems.value = mockData
        calculateTotal(mockData)
    }


    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.value = items.sumOf { it.price }
    }


    fun finalizarCompra() {
        // TODO: Aquí irá la lógica para guardar la orden en Firebase
        _cartItems.value = emptyList()
        _totalPrice.value = 0.0
    }
}