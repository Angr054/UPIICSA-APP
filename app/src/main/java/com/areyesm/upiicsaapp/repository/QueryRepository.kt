package com.areyesm.upiicsaapp.repository

import android.util.Log
import com.areyesm.upiicsaapp.model.QueryModel
import com.google.firebase.firestore.FirebaseFirestore

class QueryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("aulas")

    fun searchQuery(
        queryText: String,
        onResult: (List<QueryModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        collectionRef
            .get()
            .addOnSuccessListener { snapshot ->
                val allItems = snapshot.documents.map { doc ->
                    QueryModel(
                        id = doc.id,
                        materia = doc.getString("materia") ?: "",
                        secuencia = doc.getString("secuencia") ?: "",
                        profesor = doc.getString("profesor") ?: "",
                        edificio = doc.getString("edificio") ?: "",
                        aula = doc.getString("aula") ?: ""
                    )
                }

                val list = allItems.filter { item ->
                    item.materia.contains(queryText, ignoreCase = true) ||
                            item.secuencia.contains(queryText, ignoreCase = true) ||
                            item.profesor.contains(queryText, ignoreCase = true)
                }

                onResult(list)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }


}
