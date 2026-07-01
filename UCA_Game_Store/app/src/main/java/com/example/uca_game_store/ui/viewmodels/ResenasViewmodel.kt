package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.uca_game_store.data.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ResenasViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Estado para manejar el éxito o error al subir
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    fun agregarResena(resena: Resena) {
        db.collection("resenas").add(resena)
            .addOnSuccessListener {
                _uploadStatus.value = "SUCCESS"
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "ERROR: ${e.message}"
            }
    }


}

