package com.areyesm.upiicsaapp.model

data class TaskModel(
    val id: String = "",
    val name: String = "",
    val details: String = "",
    val dueDate: Long = 0L,
    val completed: Boolean = false,
    val createdAt: Long = 0L
)


