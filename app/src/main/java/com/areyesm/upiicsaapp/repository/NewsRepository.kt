package com.areyesm.upiicsaapp.repository

import com.google.firebase.firestore.FirebaseFirestore

class NewsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getNewsImages(
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("news")
            .get()
            .addOnSuccessListener { snapshot ->
                val images = snapshot.documents.mapNotNull {
                    it.getString("imageurl")
                }
                onSuccess(images)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error cargando noticias")
            }
    }
}
