package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.model.TaskFilter
import com.areyesm.upiicsaapp.model.TaskModel
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.Yellow40
import com.areyesm.upiicsaapp.ui.theme.completed
import com.areyesm.upiicsaapp.ui.theme.completedSelected
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.ui.theme.pending
import com.areyesm.upiicsaapp.ui.theme.pendingSelected
import com.areyesm.upiicsaapp.viewModel.TaskViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun Tasks(
    viewModel: TaskViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        UploadTaskButton(
            onClick = {
                viewModel.clearSelection()
                showDialog = true
            },
            loading = false
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val pendingChipColors = FilterChipDefaults.filterChipColors(
                containerColor = pending,
                selectedContainerColor = pendingSelected,
                labelColor = Color.Gray,
                selectedLabelColor = Color.Black
            )

            val pendingChipBorder = FilterChipDefaults.filterChipBorder(
                borderColor = pending,
                selectedBorderColor = pending,
                borderWidth = 1.dp,
                enabled = true,
                selected = true
            )

            val completedChipColors = FilterChipDefaults.filterChipColors(
                containerColor = completed,
                selectedContainerColor = completedSelected,
                labelColor = Color.Gray,
                selectedLabelColor = Color.Black
            )

            val completedChipBorder = FilterChipDefaults.filterChipBorder(
                borderColor = completed,
                selectedBorderColor = completed,
                borderWidth = 1.dp,
                enabled = true,
                selected = true
            )
            FilterChip(
                colors = pendingChipColors,
                border = pendingChipBorder,
                selected = viewModel.currentFilter == TaskFilter.PENDING,
                onClick = { viewModel.setFilter(TaskFilter.PENDING) },
                label = { Text("Pendientes") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                colors = completedChipColors,
                border = completedChipBorder,
                selected = viewModel.currentFilter == TaskFilter.COMPLETED,
                onClick = { viewModel.setFilter(TaskFilter.COMPLETED) },
                label = { Text("Completadas") }
            )
        }

        when (viewModel.currentFilter) {

            TaskFilter.PENDING -> {
                if (viewModel.pendingTasks.isEmpty()) {
                    Text(
                        text = "¡No Tienes Tareas Pendientes!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    TaskList(
                        tasks = viewModel.filteredTasks,
                        onEdit = {
                            viewModel.selectTask(it)
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteTask(it) },
                        onToggleComplete = { task ->
                            viewModel.setCompleted(task, true)
                        }
                    )
                }
            }

            TaskFilter.COMPLETED -> {
                if (viewModel.filteredTasks.isEmpty()) {
                    Text(
                        text = "¡Registra Tus Tareas!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    CompletedTasksSection(
                        tasks = viewModel.filteredTasks,
                        onRestore = { task ->
                            viewModel.setCompleted(task, false)
                        },
                        onDelete = { viewModel.deleteTask(it) },
                    )
                }
            }
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }

    if (showDialog) {
        AddEditTaskDialog(
            viewModel = viewModel,
            onDismiss = { showDialog = false }
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit
) {
    val task = viewModel.selectedTask

    var name by remember(task?.id) {
        mutableStateOf(task?.name ?: "")
    }

    var selectedDate by remember(task?.id) {
        mutableStateOf(
            task?.dueDate?.let {
                Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
        )
    }

    var details by remember(task?.id) {
        mutableStateOf(task?.details ?: "")
    }


    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gray100
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = if (task == null) "Nueva tarea" else "Editar tarea",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                DefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    icon = Icons.Default.Create
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Detalles") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    minLines = 4,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, cursorColor = Color.Black, focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Gray, focusedLabelColor = Color.Black, unfocusedLabelColor = Color.Gray, focusedLeadingIconColor = Color.Black, unfocusedLeadingIconColor = Color.Black
                    )
                )

                DatePickerOutlinedField(
                    label = "Fecha límite",
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                        viewModel.clearSelection()
                        onDismiss()},
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    TextButton(
                        enabled = (name.isNotBlank() && selectedDate != null),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1),
                        onClick = {

                            val finalDueDate = selectedDate
                                ?.atStartOfDay(ZoneId.systemDefault())
                                ?.toInstant()
                                ?.toEpochMilli()
                                ?: System.currentTimeMillis()

                            viewModel.saveTask(name, details, finalDueDate) {
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}



@Composable
fun TaskList(
    tasks: List<TaskModel>,
    onEdit: (TaskModel) -> Unit,
    onDelete: (String) -> Unit,
    onToggleComplete: (TaskModel) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onEdit = onEdit,
                onDelete = onDelete,
                onToggleComplete = onToggleComplete
            )
        }
    }
}


@Composable
fun TaskItem(
    task: TaskModel,
    onEdit: (TaskModel) -> Unit,
    onDelete: (String) -> Unit,
    onToggleComplete: (TaskModel) -> Unit
) {
    var selectedTaskForDetails by remember { mutableStateOf<TaskModel?>(null) }

    Surface(
        modifier = Modifier.clickable{
            selectedTaskForDetails = task
        },
        shape = RoundedCornerShape(12.dp),
        color = Color.Unspecified,
        border = BorderStroke(width = 0.5.dp, color = Color.Gray),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = false,
                    onCheckedChange = { onToggleComplete(task) }
                )

                Text(task.name, fontWeight = FontWeight.Bold)

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, start = 25.dp, end = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Entrega: ${formatDate(task.dueDate)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { onEdit(task) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "edit"
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                IconButton(
                    onClick = { onDelete(task.id) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete"
                    )
                }
            }

        }
    }
    selectedTaskForDetails?.let {
        TaskDetailsDialog(
            task = it,
            onDismiss = { selectedTaskForDetails = null }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsDialog(
    task: TaskModel,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gray100
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) { Back(onDismiss) }
                Text(
                    text = task.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "Entrega: ${formatDate(task.dueDate)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = task.details.ifBlank { "Sin detalles" },
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss,  colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}


@Composable
fun UploadTaskButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = modifier.size(56.dp),
            shape = CircleShape,
            color = Yellow40,
            onClick = onClick,
            enabled = !loading
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Seleccionar imagen",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedTasksSection(
    tasks: List<TaskModel>,
    onRestore: (TaskModel) -> Unit,
    onDelete: (String) -> Unit,

) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                CompletedTaskItem(
                    task = task,
                    onRestore = onRestore,
                    onDelete = onDelete,
                )
            }
        }
    }
}


@Composable
fun CompletedTaskItem(
    task: TaskModel,
    onRestore: (TaskModel) -> Unit,
    onDelete: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.LightGray.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(task.name, fontWeight = FontWeight.Bold)

            Text(
                text = "Materia",
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                IconButton(
                    onClick = { onRestore(task) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "restore"
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                IconButton(
                    onClick = { onDelete(task.id) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete"
                    )
                }
            }

        }
    }
}


