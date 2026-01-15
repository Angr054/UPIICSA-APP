package com.areyesm.upiicsaapp.repository

import com.areyesm.upiicsaapp.model.SubjectModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SubjectRepository {

    private val collection =
        Firebase.firestore.collection("horarios")

    fun getSubjects(
        onSuccess: (List<SubjectModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                onSuccess(
                    result.documents.mapNotNull {
                        it.toObject(SubjectModel::class.java)
                            ?.copy(id = it.id)
                    }
                )
            }
            .addOnFailureListener {
                onError(it.message ?: "Error")
            }
    }
}

