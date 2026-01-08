package com.areyesm.upiicsaapp.repository

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.UUID

class BackpackRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage

    // Identificar Usuario
    private fun userImagesRef(): StorageReference {
        val uid = auth.currentUser?.uid

        return storage.reference
            .child("backpack_images")
            .child(uid.toString())
    }
    fun uploadImage(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageRef = userImagesRef()
            .child("${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al subir imagen")
            }
    }
    fun getImages(
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        userImagesRef().listAll()
            .addOnSuccessListener { result ->
                val tasks = result.items.map { it.downloadUrl }

                Tasks.whenAllSuccess<Uri>(tasks)
                    .addOnSuccessListener { uris ->
                        onSuccess(uris.map { it.toString() })
                    }
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al cargar imágenes")
            }
    }
    fun deleteImage(
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = Firebase.storage.getReferenceFromUrl(imageUrl)

        ref.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error al eliminar imagen")
            }
    }
}