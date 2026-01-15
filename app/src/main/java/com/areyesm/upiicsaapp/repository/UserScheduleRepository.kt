package com.areyesm.upiicsaapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserScheduleRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun requireUid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Usuario no autenticado")
    }

    fun saveSelectedSubjects(
        subjectIds: List<String>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (subjectIds.isEmpty()) {
            onError(IllegalArgumentException("Lista de materias vacía"))
            return
        }

        try {
            val uid = requireUid()

            db.collection("users")
                .document(uid)
                .collection("schedule")
                .document("selectedSubjects")
                .set(
                    mapOf(
                        "subjectIds" to subjectIds,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }

        } catch (e: Exception) {
            onError(e)
        }
    }

    fun loadSelectedSubjects(
        onSuccess: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val uid = requireUid()

            db.collection("users")
                .document(uid)
                .collection("schedule")
                .document("selectedSubjects")
                .get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        onSuccess(emptyList())
                        return@addOnSuccessListener
                    }

                    val ids = doc.get("subjectIds") as? List<*>
                    onSuccess(
                        ids
                            ?.filterIsInstance<String>()
                            ?: emptyList()
                    )
                }
                .addOnFailureListener { onError(it) }

        } catch (e: Exception) {
            onError(e)
        }
    }
}
