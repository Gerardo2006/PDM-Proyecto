package com.example.uca_game_store.data.repository

import com.example.uca_game_store.data.model.SolicitudVenta
import com.google.firebase.firestore.FirebaseFirestore

class VentaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val coleccionVentas = db.collection("ventas")

    fun guardarVenta(solicitud: SolicitudVenta, onResult: (Boolean) -> Unit) {
        coleccionVentas.add(solicitud)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun obtenerVentas(onResult: (List<SolicitudVenta>) -> Unit) {
        coleccionVentas.get().addOnSuccessListener { snapshot ->
            val lista = snapshot.toObjects(SolicitudVenta::class.java)
            onResult(lista)
        }
    }
}