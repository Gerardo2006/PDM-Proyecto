package com.example.uca_game_store.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado para que la vista sepa si hubo éxito o error
    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState.asStateFlow()

    // Login
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

    // Registro
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

    // Cerrar Sesión
    fun signOut() {
        auth.signOut()
        _authState.value = null
    }

    // Recuperación de Contraseña
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "El correo no puede estar vacío")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Correo de recuperación enviado exitosamente")
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}