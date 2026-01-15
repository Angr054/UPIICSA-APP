package com.areyesm.upiicsaapp.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.TaskFilter
import com.areyesm.upiicsaapp.model.TaskModel
import com.areyesm.upiicsaapp.repository.TaskRepository

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    var tasks by mutableStateOf<List<TaskModel>>(emptyList())
        private set

    var selectedTask by mutableStateOf<TaskModel?>(null)
        private set

    val pendingTasks: List<TaskModel>
        get() = tasks.filter { !it.completed }

    val completedTasks: List<TaskModel>
        get() = tasks.filter { it.completed }


    var errorMessage by mutableStateOf<String?>(null)
        private set

    var showCompleted by mutableStateOf(false)
        private set

    var currentFilter by mutableStateOf(TaskFilter.PENDING)
        private set

    val filteredTasks: List<TaskModel>
        get() = when (currentFilter) {
            TaskFilter.PENDING -> tasks.filter { !it.completed }
            TaskFilter.COMPLETED -> tasks.filter { it.completed }
        }

    init {
        loadTasks()
    }

    private fun loadTasks() {
        repository.getTasks(
            onSuccess = { tasks = it },
            onError = { errorMessage = it }
        )
    }

    fun selectTask(task: TaskModel) {
        selectedTask = task
    }

    fun clearSelection() {
        selectedTask = null
    }

    fun saveTask(
        name: String,
        details: String,
        dueDate: Long,
        onComplete: () -> Unit
    ) {
        val task = selectedTask

        if (task == null) {
            repository.addTask(
                name = name,
                details = details,
                dueDate = dueDate,
                onSuccess = {
                    loadTasks()
                    onComplete()
                },
                onError = { errorMessage = it }
            )
        } else {
            repository.updateTask(
                taskId = task.id,
                name = name,
                details = details,
                dueDate = dueDate,
                onSuccess = {
                    loadTasks()
                    clearSelection()
                    onComplete()
                },
                onError = { errorMessage = it }
            )
        }
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(
            taskId = taskId,
            onSuccess = { tasks = tasks.filterNot { it.id == taskId } },
            onError = { errorMessage = it }
        )
    }

    fun toggleCompletedFilter() {
        showCompleted = !showCompleted
    }


    fun setFilter(filter: TaskFilter) {
        currentFilter = filter
    }

    fun setCompleted(task: TaskModel, completed: Boolean) {
        repository.setTaskCompleted(
            taskId = task.id,
            completed = completed,
            onSuccess = {
                tasks = tasks.map {
                    if (it.id == task.id) it.copy(completed = completed)
                    else it
                }
            },
            onError = { /* manejar error */ }
        )
    }
}

