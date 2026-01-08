package com.areyesm.upiicsaapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun userDoc() =
        firestore.collection("users")
            .document(
                auth.currentUser?.uid
                    ?: throw IllegalStateException("Usuario no autenticado")
            )

    fun savePinnedImage(
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        userDoc()
            .set(
                mapOf(
                    "pinnedImage" to imageUrl,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al guardar imagen fijada")
            }
    }

    fun getPinnedImage(
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        userDoc()
            .get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.getString("pinnedImage"))
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al cargar imagen fijada")
            }
    }

    fun clearPinnedImage(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        userDoc()
            .update("pinnedImage", FieldValue.delete())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al eliminar imagen fijada")
            }
    }
}
