package com.areyesm.upiicsaapp.model

data class DocumentModel(
    val id: String = "",
    val name: String = "",
    val fileUrl: String = "",
    val type: String = DocumentType.OTHER.name,
    val createdAt: Long = System.currentTimeMillis()
)
