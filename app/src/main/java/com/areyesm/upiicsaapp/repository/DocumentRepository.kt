package com.areyesm.upiicsaapp.repository

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.DocumentModel
import com.areyesm.upiicsaapp.model.DocumentType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.UUID


class DocumentRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage
    private val firestore = Firebase.firestore

    private fun uid(): String? = auth.currentUser?.uid

    private fun storageRef(userId: String) =
        storage.reference
            .child("backpack_docs")
            .child(userId)

    private fun firestoreRef(userId: String) =
        firestore.collection("documents")
            .document(userId)
            .collection("user_docs")

    fun uploadDocument(
        context: Context,
        uri: Uri,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid()
        if (userId == null) {
            onError("Usuario no autenticado")
            return
        }

        val mimeType = context.contentResolver.getType(uri)
        val documentType = mimeTypeToDocumentType(mimeType)

        val docId = UUID.randomUUID().toString()
        val extension = mimeType?.substringAfter("/") ?: "bin"

        val fileRef = storageRef(userId).child("$docId.$extension")

        fileRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                fileRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val document = DocumentModel(
                    id = docId,
                    name = name,
                    fileUrl = downloadUrl.toString(),
                    type = documentType.name,
                    createdAt = System.currentTimeMillis()
                )

                firestoreRef(userId).document(docId)
                    .set(document)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Error guardando metadata")
                    }
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error subiendo archivo")
            }
    }

    fun getDocuments(
        onSuccess: (List<DocumentModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid()
        if (userId == null) {
            onError("Usuario no autenticado")
            return
        }

        firestoreRef(userId)
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.toObjects(DocumentModel::class.java))
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error cargando documentos")
            }
    }

    fun deleteDocument(
        document: DocumentModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid()
        if (userId == null) {
            onError("Usuario no autenticado")
            return
        }

        Firebase.storage.getReferenceFromUrl(document.fileUrl)
            .delete()
            .continueWithTask {
                firestoreRef(userId).document(document.id).delete()
            }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error eliminando documento")
            }
    }

    fun mimeTypeToDocumentType(mimeType: String?): DocumentType {
        return when {
            mimeType == null -> DocumentType.OTHER
            mimeType == "application/pdf" -> DocumentType.PDF
            mimeType.contains("word") -> DocumentType.WORD
            mimeType.contains("excel") ||
                    mimeType.contains("spreadsheet") -> DocumentType.EXCEL
            mimeType.startsWith("image/") -> DocumentType.IMAGE
            else -> DocumentType.OTHER
        }
    }
}


