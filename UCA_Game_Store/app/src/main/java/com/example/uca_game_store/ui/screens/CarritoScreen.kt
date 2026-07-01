package com.example.uca_game_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uca_game_store.ui.viewmodels.CarritoViewModel
import com.example.uca_game_store.data.model.CartItem

val UcaDarkBackground = Color(0xFF121212)
val UcaCardBackground = Color(0xFF1E1E24)
val UcaOrange = Color(0xFFFF8C00)
val UcaGreen = Color(0xFF00E676)

@Composable
fun CarritoScreen(viewModel: CarritoViewModel = viewModel()) {
    val cartItems by viewModel.cartItems.collectAsState()
    val total by viewModel.totalPrice.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Text("Tu Carrito", color = NaranjaUca, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                CartItemRow(item)
            }
        }

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Total:", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("$${"%.2f".format(total)}", fontSize = 22.sp, color = VerdeUca, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.finalizarCompra() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeUca, contentColor = Color.Black),
            enabled = cartItems.isNotEmpty()
        ) {
            Text("Finalizar compra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = FondoTarjeta), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(item.title, color = Color.White, modifier = Modifier.weight(1f))
            Text("$${"%.2f".format(item.price)}", color = VerdeUca, fontWeight = FontWeight.Bold)
        }
    }
}