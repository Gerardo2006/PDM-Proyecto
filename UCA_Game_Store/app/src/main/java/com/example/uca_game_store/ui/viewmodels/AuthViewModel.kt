package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    // Instancia de Firebase Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado para que la vista sepa si hubo éxito o error
    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState.asStateFlow()

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _authState.value = "ERROR: Ingresa correo y contraseña"
            return
        }
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = "SUCCESS"
                } else {
                    _authState.value = "ERROR: ${task.exception?.message}"
                }
            }
    }

    fun register(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _authState.value = "ERROR: Ingresa correo y contraseña"
            return
        }
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = "SUCCESS"
                } else {
                    _authState.value = "ERROR: ${task.exception?.message}"
                }
            }
    }
}