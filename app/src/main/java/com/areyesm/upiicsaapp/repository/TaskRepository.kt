package com.areyesm.upiicsaapp.repository

import com.areyesm.upiicsaapp.model.TaskModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.UUID

class TaskRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private fun uid(): String? = auth.currentUser?.uid

    private fun taskRef(userId: String) =
        firestore.collection("tasks")
            .document(userId)
            .collection("user_tasks")

    fun getTasks(
        onSuccess: (List<TaskModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid() ?: return onError("Usuario no autenticado")

        taskRef(userId)
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener {
                onSuccess(it.toObjects(TaskModel::class.java))
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error cargando tareas")
            }
    }

    fun addTask(
        name: String,
        details: String,
        dueDate: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid() ?: return onError("Usuario no autenticado")

        val id = UUID.randomUUID().toString()
        val task = TaskModel(
            id = id,
            name = name,
            details = details,
            dueDate = dueDate,
            createdAt = System.currentTimeMillis()
        )

        taskRef(userId).document(id)
            .set(task)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error guardando tarea")
            }
    }

    fun deleteTask(
        taskId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid() ?: return onError("Usuario no autenticado")

        taskRef(userId).document(taskId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error eliminando tarea")
            }
    }

    fun updateTask(
        taskId: String,
        name: String,
        details: String,
        dueDate: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid() ?: return onError("Usuario no autenticado")

        taskRef(userId).document(taskId)
            .update(
                mapOf(
                    "name" to name,
                    "dueDate" to dueDate
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error actualizando tarea")
            }
    }

    fun setTaskCompleted(
        taskId: String,
        completed: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid() ?: return onError("Usuario no autenticado")

        taskRef(userId).document(taskId)
            .update("completed", completed)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error actualizando tarea")
            }
    }


}
