package com.areyesm.upiicsaapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.TaskModel
import com.areyesm.upiicsaapp.repository.TaskRepository

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    var tasks by mutableStateOf<List<TaskModel>>(emptyList())
        private set

    var selectedTask by mutableStateOf<TaskModel?>(null)
        private set

    init {
        repository.observeTasks {
            tasks = it
        }
    }

    fun selectTask(task: TaskModel) {
        selectedTask = task
    }

    fun clearSelection() {
        selectedTask = null
    }

    fun saveTask(title: String, description: String) {
        if (title.isBlank()) return

        val task = selectedTask?.copy(
            title = title,
            description = description
        ) ?: TaskModel(
            title = title,
            description = description
        )

        if (selectedTask == null) {
            repository.addTask(task)
        } else {
            repository.updateTask(task)
        }

        clearSelection()
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }
}
