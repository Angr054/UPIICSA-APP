package com.areyesm.upiicsaapp.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.DocumentModel
import com.areyesm.upiicsaapp.model.DocumentType
import com.areyesm.upiicsaapp.repository.DocumentRepository

class DocumentViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = DocumentRepository()

    var documents by mutableStateOf<List<DocumentModel>>(emptyList())
        private set

    var isUploading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDocuments()
    }

    fun uploadDocument(uri: Uri, name: String) {
        isUploading = true

        repository.uploadDocument(
            context = getApplication(), // Contexto a través de androidVM
            uri = uri,
            name = name,
            onSuccess = {
                isUploading = false
                loadDocuments()
            },
            onError = {
                errorMessage = it
                isUploading = false
            }
        )
    }

    private fun loadDocuments() {
        repository.getDocuments(
            onSuccess = { documents = it },
            onError = { errorMessage = it }
        )
    }

    fun deleteDocument(document: DocumentModel) {
        repository.deleteDocument(
            document = document,
            onSuccess = { documents = documents - document },
            onError = { errorMessage = it }
        )
    }
}


