package com.example.uca_game_store.data.repository

import com.example.uca_game_store.data.model.SolicitudVenta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class VentaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val coleccionVentas = db.collection("solicitudes_venta")
    private val storage = FirebaseStorage.getInstance().reference

    fun guardarVenta(solicitud: SolicitudVenta, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val fotoOriginal = solicitud.fotoUri.trim()

        // LÓGICA INTELIGENTE: Si ya es un link de internet, lo guardamos directo sin subir nada a Storage
        if (fotoOriginal.startsWith("http://") || fotoOriginal.startsWith("https://")) {
            val datosVenta = hashMapOf(
                "nombre" to solicitud.nombre,
                "descripcion" to solicitud.descripcion,
                "precio" to solicitud.precio,
                "fotoUri" to fotoOriginal, // Se guarda el link de internet directamente
                "vendedorId" to uid,
                "estado" to "PENDIENTE",
                "destacado" to solicitud.destacado
            )

            coleccionVentas.add(datosVenta)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        } else if (fotoOriginal.isNotEmpty()) {
            // Si no es un link, significa que es una foto capturada por la cámara (URI local)
            try {
                val fotoLocalUri = Uri.parse(fotoOriginal)
                val nombreArchivo = "juegos/${uid}_${System.currentTimeMillis()}.jpg"
                val referenciaFoto = storage.child(nombreArchivo)

                // Subimos el archivo local a Firebase Storage
                referenciaFoto.putFile(fotoLocalUri)
                    .addOnSuccessListener {
                        // Al subir con éxito, obtenemos su URL pública de descarga
                        referenciaFoto.downloadUrl.addOnSuccessListener { urlPublica ->
                            val datosVenta = hashMapOf(
                                "nombre" to solicitud.nombre,
                                "descripcion" to solicitud.descripcion,
                                "precio" to solicitud.precio,
                                "fotoUri" to urlPublica.toString(), // Guardamos la URL pública de Storage
                                "vendedorId" to uid,
                                "estado" to "PENDIENTE",
                                "destacado" to solicitud.destacado
                            )

                            coleccionVentas.add(datosVenta)
                                .addOnSuccessListener { onResult(true) }
                                .addOnFailureListener { onResult(false) }
                        }
                    }
                    .addOnFailureListener { exception ->
                        android.util.Log.e("VentaRepository", "Error al subir a Storage", exception)
                        onResult(false)
                    }
            } catch (e: Exception) {
                android.util.Log.e("VentaRepository", "Error al procesar URI local", e)
                onResult(false)
            }
        } else {
            // Si no puso ninguna foto ni link, puedes decidir si dejarlo pasar o retornar falso
            onResult(false)
        }
    }

    fun obtenerSolicitudesPendientes(onResult: (List<SolicitudVenta>) -> Unit) {
        coleccionVentas.whereEqualTo("estado", "PENDIENTE")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    val solicitud = doc.toObject(SolicitudVenta::class.java)
                    solicitud?.copy(id = doc.id)
                } ?: emptyList()
                onResult(lista)
            }
    }

    fun actualizarEstadoSolicitud(idDocumento: String, nuevoEstado: String) {
        coleccionVentas.document(idDocumento)
            .update("estado", nuevoEstado)
            .addOnFailureListener { exception ->
                println("Error en Repositorio al cambiar estado a $nuevoEstado: ${exception.message}")
            }
    }

    fun obtenerJuegosAprobados(onResult: (List<SolicitudVenta>) -> Unit) {
        coleccionVentas.whereEqualTo("estado", "APROBADO")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    val juego = doc.toObject(SolicitudVenta::class.java)
                    juego?.copy(id = doc.id)
                } ?: emptyList()
                onResult(lista)
            }
    }

    fun guardarReseña(juegoId: String, rating: Int, comentario: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val usuarioEmail = auth.currentUser?.email ?: "Usuario Anónimo"

        val datosReseña = hashMapOf(
            "usuarioId" to uid,
            "usuario" to usuarioEmail,
            "calificacion" to rating,
            "comentario" to comentario,
            "fecha" to com.google.firebase.Timestamp.now()
        )

        db.collection("solicitudes_venta")
            .document(juegoId)
            .collection("resenas")
            .add(datosReseña)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // CORREGIDO: Eliminamos el .orderBy que causaba errores de índices en Firebase y provocaba que no cargaran las opiniones
    fun obtenerReseñasDelJuego(juegoId: String, onResult: (List<Map<String, Any>>) -> Unit) {
        db.collection("solicitudes_venta")
            .document(juegoId)
            .collection("resenas")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.data
                } ?: emptyList()
                onResult(lista)
            }
    }

    // CORREGIDO: Ahora implementa la lógica de Toggle (Si existe, lo borra; si no, lo agrega) para que la estrella reaccione al click
    fun agregarAFavoritos(juegoId: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val docRef = db.collection("favoritos").document("${uid}_${juegoId}")

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Si ya lo tiene en favoritos, lo removemos
                docRef.delete().addOnSuccessListener { onResult(true) }.addOnFailureListener { onResult(false) }
            } else {
                // Si no existe, lo creamos
                val datos = hashMapOf(
                    "usuarioId" to uid,
                    "juegoId" to juegoId,
                    "fecha" to com.google.firebase.Timestamp.now()
                )
                docRef.set(datos).addOnSuccessListener { onResult(true) }.addOnFailureListener { onResult(false) }
            }
        }.addOnFailureListener { onResult(false) }
    }

    fun obtenerFavoritos(onResult: (List<String>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(emptyList())
        db.collection("favoritos")
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { snapshot, _ ->
                val listaIds = snapshot?.documents?.mapNotNull { doc ->
                    doc.getString("juegoId")
                } ?: emptyList()
                onResult(listaIds)
            }
    }
}
