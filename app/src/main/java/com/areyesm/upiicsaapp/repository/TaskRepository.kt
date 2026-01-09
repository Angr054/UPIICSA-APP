package com.areyesm.upiicsaapp.repository

import com.areyesm.upiicsaapp.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun tasksRef(): CollectionReference {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Usuario no autenticado")

        return db
            .collection("users")
            .document(uid)
            .collection("tasks")
    }

    fun observeTasks(onResult: (List<TaskModel>) -> Unit) {
        tasksRef().addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val tasks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TaskModel::class.java)
                    ?.copy(id = doc.id)
            }
            onResult(tasks)
        }
    }

    fun addTask(task: TaskModel) {
        tasksRef().add(task)
    }

    fun updateTask(task: TaskModel) {
        tasksRef().document(task.id).set(task)
    }

    fun deleteTask(taskId: String) {
        tasksRef().document(taskId).delete()
    }
}

